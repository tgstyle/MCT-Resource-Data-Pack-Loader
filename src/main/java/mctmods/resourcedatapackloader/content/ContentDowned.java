package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.def.DownDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        Level level;
        double x;
        double y;
        double z;

        State(DownDef down, String side, UUID by, ServerPlayer player) {
            this.down = down;
            this.side = side;
            this.by = by;
            this.left = down.seconds() * 20;
            pin(player);
        }

        void pin(ServerPlayer player) {
            this.level = player.level();
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
        }
    }

    public static void load() {
        if (armed) { return; }
        for (TeamDef def : ContentTeams.all().values()) {
            if (def.down() == null) { continue; }
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onDeath);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onAttack);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onStrike);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onDig);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onUse);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onItem);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onTouch);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentDowned::onTouchAt);
            NeoForge.EVENT_BUS.addListener(ContentDowned::onLogout);
            NeoForge.EVENT_BUS.addListener(ContentDowned::onRespawn);
            NeoForge.EVENT_BUS.addListener(ContentDowned::onTick);
            armed = true;
            return;
        }
    }

    private static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (DOWNED.remove(player.getUUID()) != null || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        TeamDef def = ContentTeams.defOf(player);
        if (def == null || def.down() == null) { return; }
        event.setCanceled(true);
        player.setHealth(1.0F);
        player.stopRiding();
        Entity by = event.getSource().getEntity();
        DOWNED.put(player.getUUID(), new State(def.down(), def.name(), by == null ? null : by.getUUID(), player));
        effects(player);
        ContentLog.LOGGER.info("{} is downed and bleeds out in {} seconds", player.getGameProfile().getName(), def.down().seconds());
        Says.tell(player, CardIds.DOWNED, def.color(), def.down().says().replace("{seconds}", String.valueOf(def.down().seconds())));
    }

    private static void onAttack(LivingIncomingDamageEvent event) {
        if (bleeding || DOWNED.isEmpty() || event.getEntity().level().isClientSide()) { return; }
        Entity by = event.getSource().getEntity();
        if (by != null && DOWNED.containsKey(by.getUUID())) { event.setCanceled(true); }
        else if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && DOWNED.containsKey(event.getEntity().getUUID())) { event.setCanceled(true); }
    }

    private static void onStrike(AttackEntityEvent event) { refuse(event, event.getEntity()); }

    private static void onDig(PlayerInteractEvent.LeftClickBlock event) { refuse(event, event.getEntity()); }

    private static void onUse(PlayerInteractEvent.RightClickBlock event) { refuse(event, event.getEntity()); }

    private static void onItem(PlayerInteractEvent.RightClickItem event) { refuse(event, event.getEntity()); }

    private static void onTouch(PlayerInteractEvent.EntityInteract event) { refuse(event, event.getEntity()); }

    private static void onTouchAt(PlayerInteractEvent.EntityInteractSpecific event) { refuse(event, event.getEntity()); }

    private static void refuse(ICancellableEvent event, Player player) { if (!DOWNED.isEmpty() && DOWNED.containsKey(player.getUUID())) { event.setCanceled(true); } }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && DOWNED.containsKey(player.getUUID())) { kill(player); }
    }

    private static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { DOWNED.remove(event.getEntity().getUUID()); }

    private static void onTick(ServerTickEvent.Post event) {
        if (DOWNED.isEmpty()) { return; }
        MinecraftServer server = event.getServer();
        for (Map.Entry<UUID, State> one : new ArrayList<>(DOWNED.entrySet())) { step(server, one.getKey(), one.getValue()); }
    }

    private static void step(MinecraftServer server, UUID id, State state) {
        ServerPlayer player = server.getPlayerList().getPlayer(id);
        if (player == null || !player.isAlive() || player.getHealth() <= 0.0F) {
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
        if (!attended(server, player, state)) {
            state.reviving = 0;
            return;
        }
        state.reviving++;
        if (state.reviving >= state.down.reviveSeconds() * 20) { revive(player, state); }
    }

    private static boolean attended(MinecraftServer server, ServerPlayer down, State state) {
        for (ServerPlayer near : server.getPlayerList().getPlayers()) {
            if (near == down || !near.isShiftKeyDown() || near.isSpectator() || near.level() != down.level() || DOWNED.containsKey(near.getUUID()) || near.distanceToSqr(down) > REACH_SQUARED) { continue; }
            TeamDef def = ContentTeams.defOf(near);
            if (def != null && def.name().equals(state.side)) { return true; }
        }
        return false;
    }

    private static void revive(ServerPlayer player, State state) {
        DOWNED.remove(player.getUUID());
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.removeEffect(MobEffects.GLOWING);
        player.setHealth(player.getMaxHealth() / 2.0F);
        ContentLog.LOGGER.info("{} is revived", player.getGameProfile().getName());
        TeamDef def = ContentTeams.named(state.side);
        Says.tell(player, CardIds.REVIVED, def == null ? ChatFormatting.WHITE : def.color(), state.down.revivedSays());
    }

    private static void kill(ServerPlayer player) {
        State state = DOWNED.get(player.getUUID());
        Entity by = state == null || state.by == null ? null : attacker(player.serverLevel().getServer(), state.by);
        bleeding = true;
        try { player.hurt(new DamageSource(player.damageSources().fellOutOfWorld().typeHolder(), by), Float.MAX_VALUE); }
        finally { bleeding = false; }
    }

    private static Entity attacker(MinecraftServer server, UUID id) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity found = level.getEntity(id);
            if (found != null) { return found; }
        }
        return null;
    }

    private static void hold(ServerPlayer player, State state) {
        player.stopRiding();
        player.stopFallFlying();
        if (player.level() != state.level) {
            state.pin(player);
            return;
        }
        if (player.getY() < state.y) { state.y = player.getY(); }
        double dx = player.getX() - state.x;
        double dz = player.getZ() - state.z;
        if (dx * dx + dz * dz <= MOVE_SQUARED && player.getY() <= state.y + MOVE_TOLERANCE) { return; }
        player.connection.teleport(state.x, state.y, state.z, player.getYRot(), player.getXRot());
        player.setDeltaMovement(0.0D, Math.min(player.getDeltaMovement().y, 0.0D), 0.0D);
    }

    private static void effects(ServerPlayer player) {
        hold(player, MobEffects.MOVEMENT_SLOWDOWN, 6);
        hold(player, MobEffects.GLOWING, 0);
    }

    private static void hold(ServerPlayer player, Holder<MobEffect> effect, int amplifier) { player.addEffect(new MobEffectInstance(effect, EFFECT_TICKS, amplifier, false, false)); }
}
