package mctmods.resourcedatapackloader.common.scripts;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ScriptEventHandlers {
    private static final String BLOCK_PLACE = "block_place";
    private static final String BLOCK_BREAK = "block_break";
    private static final String ENTITY_HURT = "entity_hurt";
    private static final String ENTITY_SPAWN = "entity_spawn";
    private static final String ENTITY_DESPAWN = "entity_despawn";
    private static final String PLAYER_LOGIN = "player_login";
    private static final String PLAYER_TICK = "player_tick";
    private static final String SERVER_TICK = "server_tick";
    private static final String DIMENSION_CHANGE = "dimension_change";
    private static final String LOOT_DROP = "loot_drop";
    private static final String ENTITY_TRAVEL = "entity_travel";
    private static final String SPAWNED = "rdplScriptSpawned";

    private ScriptEventHandlers() {}

    @SuppressWarnings("deprecation") @SubscribeEvent(priority = EventPriority.LOW) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!ScriptEvents.has(BLOCK_PLACE) || event.getWorld().isRemote) { return; }
        Entity placer = event.getEntity();
        EntityPlayer player = placer instanceof EntityPlayer ? (EntityPlayer) placer : null;
        Map<String, Object> bindings = bind((WorldServer) event.getWorld(), player);
        bindings.put("pos", event.getPos());
        bindings.put("state", event.getPlacedBlock());
        bindings.put("entity", placer);
        ItemStack stack = event instanceof BlockEvent.PlaceEvent && player != null ? player.getHeldItem(((BlockEvent.PlaceEvent) event).getHand()) : held(placer);
        if (ScriptEvents.fire(BLOCK_PLACE, event.getWorld(), event.getPos(), event.getPlacedBlock(), placer, stack, bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onBreak(BlockEvent.BreakEvent event) {
        if (!ScriptEvents.has(BLOCK_BREAK) || event.getWorld().isRemote) { return; }
        EntityPlayer player = event.getPlayer();
        Map<String, Object> bindings = bind((WorldServer) event.getWorld(), player);
        bindings.put("pos", event.getPos());
        bindings.put("state", event.getState());
        bindings.put("player", player);
        if (ScriptEvents.fire(BLOCK_BREAK, event.getWorld(), event.getPos(), event.getState(), player, held(player), bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onHurt(LivingAttackEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (!ScriptEvents.has(ENTITY_HURT) || victim.world.isRemote) { return; }
        DamageSource source = event.getSource();
        if (victim.isEntityInvulnerable(source) || victim.getHealth() <= 0.0F || source.isFireDamage() && victim.isPotionActive(MobEffects.FIRE_RESISTANCE)) { return; }
        Entity attacker = event.getSource().getTrueSource();
        EntityPlayer player = attacker instanceof EntityPlayer ? (EntityPlayer) attacker : null;
        Map<String, Object> bindings = bind((WorldServer) victim.world, player);
        bindings.put("entity", victim);
        bindings.put("attacker", attacker);
        bindings.put("damageType", event.getSource().getDamageType());
        bindings.put("amount", event.getAmount());
        if (ScriptEvents.fire(ENTITY_HURT, victim.world, victim.getPosition(), null, victim, held(attacker), bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onSpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLiving) || event.getWorld().isRemote || entity.getEntityData().getBoolean(SPAWNED)) { return; }
        entity.getEntityData().setBoolean(SPAWNED, true);
        if (!ScriptEvents.has(ENTITY_SPAWN)) { return; }
        Map<String, Object> bindings = bind((WorldServer) event.getWorld(), null);
        bindings.put("entity", entity);
        if (ScriptEvents.fire(ENTITY_SPAWN, event.getWorld(), entity.getPosition(), null, entity, ItemStack.EMPTY, bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (!ScriptEvents.has(PLAYER_LOGIN) || player.world.isRemote) { return; }
        Map<String, Object> bindings = bind((WorldServer) player.world, player);
        bindings.put("player", player);
        ScriptEvents.fire(PLAYER_LOGIN, player.world, player.getPosition(), null, player, held(player), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!ScriptEvents.has(PLAYER_TICK) || event.phase != TickEvent.Phase.END || player.world.isRemote || ScriptEvents.idle(PLAYER_TICK, player.world.getTotalWorldTime())) { return; }
        Map<String, Object> bindings = bind((WorldServer) player.world, player);
        bindings.put("player", player);
        bindings.put("pos", player.getPosition());
        ScriptEvents.fire(PLAYER_TICK, player.world, player.getPosition(), null, player, held(player), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!ScriptEvents.has(SERVER_TICK) || event.phase != TickEvent.Phase.END) { return; }
        WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        long time = world.getTotalWorldTime();
        if (ScriptEvents.idle(SERVER_TICK, time)) { return; }
        Map<String, Object> bindings = bind(world, null);
        bindings.put("tick", time);
        ScriptEvents.fire(SERVER_TICK, world, BlockPos.ORIGIN, null, null, ItemStack.EMPTY, bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        if (!ScriptEvents.has(DIMENSION_CHANGE) || player.world.isRemote) { return; }
        Map<String, Object> bindings = bind((WorldServer) player.world, player);
        bindings.put("player", player);
        bindings.put("from", event.fromDim);
        bindings.put("to", event.toDim);
        ScriptEvents.fire(DIMENSION_CHANGE, player.world, player.getPosition(), null, player, held(player), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDrops(LivingDropsEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (!ScriptEvents.has(LOOT_DROP) || victim.world.isRemote) { return; }
        Entity attacker = event.getSource().getTrueSource();
        EntityPlayer player = attacker instanceof EntityPlayer ? (EntityPlayer) attacker : null;
        Map<String, Object> bindings = bind((WorldServer) victim.world, player);
        bindings.put("entity", victim);
        bindings.put("attacker", attacker);
        bindings.put("damageType", event.getSource().getDamageType());
        bindings.put("drops", new ScriptDrops(event.getDrops(), victim));
        ScriptEvents.fire(LOOT_DROP, victim.world, victim.getPosition(), null, victim, held(attacker), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!ScriptEvents.has(ENTITY_TRAVEL) || entity.world.isRemote) { return; }
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        Map<String, Object> bindings = bind((WorldServer) entity.world, player);
        bindings.put("entity", entity);
        bindings.put("from", entity.world.provider.getDimension());
        bindings.put("to", event.getDimension());
        if (ScriptEvents.fire(ENTITY_TRAVEL, entity.world, entity.getPosition(), null, entity, held(entity), bindings)) { event.setCanceled(true); }
    }

    public static void despawn(EntityLiving entity) {
        World world = entity.world;
        if (entity.isDead || world.isRemote || !ScriptEvents.has(ENTITY_DESPAWN)) { return; }
        Map<String, Object> bindings = bind((WorldServer) world, null);
        bindings.put("entity", entity);
        ScriptEvents.fire(ENTITY_DESPAWN, world, entity.getPosition(), null, entity, ItemStack.EMPTY, bindings);
    }

    private static Map<String, Object> bind(WorldServer world, @Nullable EntityPlayer player) {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("rdpl", new RdplApi(world, player));
        return bindings;
    }

    private static ItemStack held(@Nullable Entity entity) { return entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY; }
}
