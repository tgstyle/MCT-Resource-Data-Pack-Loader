package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.def.DownDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentDowned {
    private static final Map<UUID, State> DOWNED = new HashMap<>();
    private static final int EFFECT_EVERY = 20;
    private static final int EFFECT_TICKS = 60;
    private static final double REACH_SQUARED = 4.0D;
    private static final double MOVE_SQUARED = 1.0E-4D;
    private static final double MOVE_TOLERANCE = 0.01D;
    private static boolean armed;
    private static boolean bleeding;

    private ContentDowned() {}

    private static final class State {
        final DownDef down;
        final String side;
        final UUID by;
        int left;
        int reviving;
        World world;
        double x;
        double y;
        double z;

        State(DownDef down, String side, UUID by, EntityPlayerMP player) {
            this.down = down;
            this.side = side;
            this.by = by;
            this.left = down.seconds * 20;
            pin(player);
        }

        void pin(EntityPlayerMP player) {
            this.world = player.world;
            this.x = player.posX;
            this.y = player.posY;
            this.z = player.posZ;
        }
    }

    public static void load() {
        if (armed) { return; }
        for (TeamDef def : ContentTeams.all().values()) {
            if (def.down == null) { continue; }
            MinecraftForge.EVENT_BUS.register(ContentDowned.class);
            armed = true;
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if (DOWNED.remove(player.getUniqueID()) != null || event.getSource() == DamageSource.OUT_OF_WORLD) { return; }
        TeamDef def = ContentTeams.defOf(player);
        if (def == null || def.down == null) { return; }
        event.setCanceled(true);
        player.setHealth(1.0F);
        player.dismountRidingEntity();
        Entity by = event.getSource().getTrueSource();
        State state = new State(def.down, def.name, by == null ? null : by.getUniqueID(), player);
        DOWNED.put(player.getUniqueID(), state);
        effects(player);
        ContentLog.LOGGER.info("{} is downed and bleeds out in {} seconds", player.getName(), def.down.seconds);
        Says.tell(player, CardIds.DOWNED, def.color, def.down.says.replace("{seconds}", String.valueOf(def.down.seconds)));
        CardEvents.downed(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onAttack(LivingAttackEvent event) {
        if (bleeding || DOWNED.isEmpty() || event.getEntityLiving().world.isRemote) { return; }
        Entity by = event.getSource().getTrueSource();
        if (by != null && DOWNED.containsKey(by.getUniqueID())) { event.setCanceled(true); }
        else if (event.getSource() != DamageSource.OUT_OF_WORLD && DOWNED.containsKey(event.getEntityLiving().getUniqueID())) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onInteract(PlayerInteractEvent event) {
        if (!DOWNED.isEmpty() && event.isCancelable() && DOWNED.containsKey(event.getEntityPlayer().getUniqueID())) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onStrike(AttackEntityEvent event) {
        if (!DOWNED.isEmpty() && DOWNED.containsKey(event.getEntityPlayer().getUniqueID())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onLogout(PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP && DOWNED.containsKey(event.player.getUniqueID())) { kill((EntityPlayerMP) event.player); }
    }

    @SubscribeEvent public static void onRespawn(PlayerRespawnEvent event) { DOWNED.remove(event.player.getUniqueID()); }

    @SubscribeEvent public static void onTick(TickEvent.WorldTickEvent event) {
        if (DOWNED.isEmpty() || event.side != Side.SERVER || event.phase != TickEvent.Phase.END || event.world.provider.getDimension() != 0) { return; }
        MinecraftServer server = event.world.getMinecraftServer();
        if (server == null) { return; }
        for (Map.Entry<UUID, State> one : new ArrayList<>(DOWNED.entrySet())) { step(server, one.getKey(), one.getValue()); }
    }

    private static void step(MinecraftServer server, UUID id, State state) {
        EntityPlayerMP player = online(server, id);
        if (player == null || player.isDead || player.getHealth() <= 0.0F) {
            DOWNED.remove(id);
            return;
        }
        if (state.left <= 0) {
            kill(player);
            return;
        }
        state.left--;
        if (player.getHealth() > 1.0F) { player.setHealth(1.0F); }
        hold(player, state);
        if (state.left % EFFECT_EVERY == 0) { effects(player); }
        EntityPlayerMP reviver = reviver(server, player, state);
        if (reviver == null) {
            state.reviving = 0;
            return;
        }
        state.reviving++;
        if (state.reviving >= state.down.reviveSeconds * 20) { revive(player, state, reviver); }
    }

    public static void clear(MinecraftServer server) {
        if (DOWNED.isEmpty()) { return; }
        for (UUID id : new ArrayList<>(DOWNED.keySet())) {
            DOWNED.remove(id);
            EntityPlayerMP player = online(server, id);
            if (player == null) { continue; }
            player.removePotionEffect(MobEffects.SLOWNESS);
            player.removePotionEffect(MobEffects.GLOWING);
            player.setHealth(player.getMaxHealth());
        }
    }

    @Nullable private static EntityPlayerMP online(MinecraftServer server, UUID id) {
        for (EntityPlayerMP one : server.getPlayerList().getPlayers()) {
            if (one.getUniqueID().equals(id)) { return one; }
        }
        return null;
    }

    @Nullable private static EntityPlayerMP reviver(MinecraftServer server, EntityPlayerMP down, State state) {
        for (EntityPlayerMP near : server.getPlayerList().getPlayers()) {
            if (near == down || !near.isSneaking() || near.isSpectator() || near.world != down.world || DOWNED.containsKey(near.getUniqueID()) || near.getDistanceSq(down) > REACH_SQUARED) { continue; }
            TeamDef def = ContentTeams.defOf(near);
            if (def != null && def.name.equals(state.side)) { return near; }
        }
        return null;
    }

    private static void revive(EntityPlayerMP player, State state, EntityPlayerMP reviver) {
        DOWNED.remove(player.getUniqueID());
        player.removePotionEffect(MobEffects.SLOWNESS);
        player.removePotionEffect(MobEffects.GLOWING);
        player.setHealth(player.getMaxHealth() / 2.0F);
        ContentLog.LOGGER.info("{} is revived", player.getName());
        TeamDef def = ContentTeams.named(state.side);
        Says.tell(player, CardIds.REVIVED, def == null ? TextFormatting.WHITE : def.color, state.down.revivedSays);
        ContentScoring.revived(reviver);
        CardEvents.revived(player);
    }

    private static void kill(EntityPlayerMP player) {
        State state = DOWNED.get(player.getUniqueID());
        Entity by = state == null || state.by == null ? null : player.server.getEntityFromUuid(state.by);
        DamageSource source = by == null ? DamageSource.OUT_OF_WORLD : new EntityDamageSource("outOfWorld", by).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
        bleeding = true;
        try { player.attackEntityFrom(source, Float.MAX_VALUE); }
        finally { bleeding = false; }
    }

    private static void hold(EntityPlayerMP player, State state) {
        player.dismountRidingEntity();
        if (player.world != state.world) {
            state.pin(player);
            return;
        }
        if (player.posY < state.y) { state.y = player.posY; }
        double dx = player.posX - state.x;
        double dz = player.posZ - state.z;
        if (dx * dx + dz * dz <= MOVE_SQUARED && player.posY <= state.y + MOVE_TOLERANCE) { return; }
        player.connection.setPlayerLocation(state.x, state.y, state.z, player.rotationYaw, player.rotationPitch);
        player.motionX = 0.0D;
        player.motionZ = 0.0D;
        if (player.motionY > 0.0D) { player.motionY = 0.0D; }
    }

    private static void effects(EntityPlayerMP player) {
        hold(player, MobEffects.SLOWNESS, 6);
        hold(player, MobEffects.GLOWING, 0);
    }

    private static void hold(EntityPlayerMP player, Potion potion, int amplifier) { player.addPotionEffect(new PotionEffect(potion, EFFECT_TICKS, amplifier, false, false)); }
}
