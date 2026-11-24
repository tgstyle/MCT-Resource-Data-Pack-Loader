package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
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
        ItemStack stack = event instanceof BlockEvent.PlaceEvent && player != null ? player.getHeldItem(((BlockEvent.PlaceEvent) event).getHand()) : held(placer);
        if (fireWith(BLOCK_PLACE, event.getWorld(), event.getPos(), event.getPlacedBlock(), placer, stack, player, ScriptEvents.POS, event.getPos(), ScriptEvents.STATE, event.getPlacedBlock(), ScriptEvents.ENTITY, placer)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onBreak(BlockEvent.BreakEvent event) {
        if (!ScriptEvents.has(BLOCK_BREAK) || ContentCarry.probing() || event.getWorld().isRemote) { return; }
        EntityPlayer player = event.getPlayer();
        if (fireWith(BLOCK_BREAK, event.getWorld(), event.getPos(), event.getState(), player, held(player), player, ScriptEvents.POS, event.getPos(), ScriptEvents.STATE, event.getState(), ScriptEvents.PLAYER, player)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onHurt(LivingAttackEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (!ScriptEvents.has(ENTITY_HURT) || victim.world.isRemote) { return; }
        DamageSource source = event.getSource();
        if (victim.isEntityInvulnerable(source) || victim.getHealth() <= 0.0F || source.isFireDamage() && victim.isPotionActive(MobEffects.FIRE_RESISTANCE)) { return; }
        Entity attacker = event.getSource().getTrueSource();
        EntityPlayer player = attacker instanceof EntityPlayer ? (EntityPlayer) attacker : null;
        if (fireWith(ENTITY_HURT, victim.world, victim.getPosition(), null, victim, held(attacker), player, ScriptEvents.ENTITY, victim, ScriptEvents.ATTACKER, attacker, ScriptEvents.DAMAGE_TYPE, event.getSource().getDamageType(), "amount", event.getAmount())) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onSpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLiving) || event.getWorld().isRemote || entity.getEntityData().getBoolean(SPAWNED)) { return; }
        entity.getEntityData().setBoolean(SPAWNED, true);
        if (!ScriptEvents.has(ENTITY_SPAWN)) { return; }
        if (fireWith(ENTITY_SPAWN, event.getWorld(), entity.getPosition(), null, entity, ItemStack.EMPTY, null, ScriptEvents.ENTITY, entity)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (!ScriptEvents.has(PLAYER_LOGIN) || player.world.isRemote) { return; }
        fireWith(PLAYER_LOGIN, player.world, player.getPosition(), null, player, held(player), player, ScriptEvents.PLAYER, player);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!ScriptEvents.has(PLAYER_TICK) || event.phase != TickEvent.Phase.END || player.world.isRemote || ScriptEvents.idle(PLAYER_TICK, player.world.getTotalWorldTime())) { return; }
        fireWith(PLAYER_TICK, player.world, player.getPosition(), null, player, held(player), player, ScriptEvents.PLAYER, player, ScriptEvents.POS, player.getPosition());
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!ScriptEvents.has(SERVER_TICK) || event.phase != TickEvent.Phase.END) { return; }
        WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        long time = world.getTotalWorldTime();
        if (ScriptEvents.idle(SERVER_TICK, time)) { return; }
        fireWith(SERVER_TICK, world, BlockPos.ORIGIN, null, null, ItemStack.EMPTY, null, "tick", time);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        if (!ScriptEvents.has(DIMENSION_CHANGE) || player.world.isRemote) { return; }
        fireWith(DIMENSION_CHANGE, player.world, player.getPosition(), null, player, held(player), player, ScriptEvents.PLAYER, player, ScriptEvents.FROM, event.fromDim, ScriptEvents.TO, event.toDim);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDrops(LivingDropsEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (!ScriptEvents.has(LOOT_DROP) || victim.world.isRemote) { return; }
        Entity attacker = event.getSource().getTrueSource();
        EntityPlayer player = attacker instanceof EntityPlayer ? (EntityPlayer) attacker : null;
        fireWith(LOOT_DROP, victim.world, victim.getPosition(), null, victim, held(attacker), player, ScriptEvents.ENTITY, victim, ScriptEvents.ATTACKER, attacker, ScriptEvents.DAMAGE_TYPE, event.getSource().getDamageType(), ScriptEvents.DROPS, new ScriptDrops(event.getDrops(), victim));
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!ScriptEvents.has(ENTITY_TRAVEL) || entity.world.isRemote) { return; }
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        if (fireWith(ENTITY_TRAVEL, entity.world, entity.getPosition(), null, entity, held(entity), player, ScriptEvents.ENTITY, entity, ScriptEvents.FROM, entity.world.provider.getDimension(), ScriptEvents.TO, event.getDimension())) { event.setCanceled(true); }
    }

    public static void despawn(EntityLiving entity) {
        World world = entity.world;
        if (entity.isDead || world.isRemote || !ScriptEvents.has(ENTITY_DESPAWN)) { return; }
        fireWith(ENTITY_DESPAWN, world, entity.getPosition(), null, entity, ItemStack.EMPTY, null, ScriptEvents.ENTITY, entity);
    }

    private static boolean fireWith(String kind, World world, BlockPos pos, @Nullable IBlockState state, @Nullable Entity entity, ItemStack item, @Nullable EntityPlayer player, Object... extra) {
        Map<String, Object> bindings = ScriptEvents.bind((WorldServer) world, player);
        for (int i = 0; i < extra.length; i += 2) { bindings.put((String) extra[i], extra[i + 1]); }
        return ScriptEvents.fire(kind, world, pos, state, entity, item, bindings);
    }

    private static ItemStack held(@Nullable Entity entity) { return entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY; }
}
