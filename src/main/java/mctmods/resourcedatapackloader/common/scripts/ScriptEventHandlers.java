package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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

    private static InteractionHand placingHand = InteractionHand.MAIN_HAND;

    private ScriptEventHandlers() {}

    @SubscribeEvent public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide) { placingHand = event.getHand(); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!ScriptEvents.has(BLOCK_PLACE) || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Entity placer = event.getEntity();
        if (fireWith(BLOCK_PLACE, level, event.getPlacedBlock(), placer, placing(placer), placer instanceof Player player ? player : null, ScriptEvents.POS, event.getPos(), ScriptEvents.STATE, event.getPlacedBlock(), ScriptEvents.ENTITY, placer)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onBreak(BlockEvent.BreakEvent event) {
        if (!ScriptEvents.has(BLOCK_BREAK) || ContentCarry.probing() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Player player = event.getPlayer();
        if (fireWith(BLOCK_BREAK, level, event.getState(), player, held(player), player, ScriptEvents.POS, event.getPos(), ScriptEvents.STATE, event.getState(), ScriptEvents.PLAYER, player)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onHurt(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (!ScriptEvents.has(ENTITY_HURT) || !(victim.level() instanceof ServerLevel level)) { return; }
        DamageSource source = event.getSource();
        if (victim.isInvulnerableTo(source) || victim.isDeadOrDying() || source.is(DamageTypeTags.IS_FIRE) && victim.hasEffect(MobEffects.FIRE_RESISTANCE)) { return; }
        Entity attacker = source.getEntity();
        if (fireWith(ENTITY_HURT, level, null, victim, held(attacker), attacker instanceof Player player ? player : null, ScriptEvents.ENTITY, victim, ScriptEvents.ATTACKER, attacker, ScriptEvents.DAMAGE_TYPE, event.getSource().getMsgId(), "amount", event.getAmount())) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.loadedFromDisk() || !(entity instanceof Mob) || !ScriptEvents.has(ENTITY_SPAWN) || !(event.getLevel() instanceof ServerLevel level) || entity.getPersistentData().getBoolean(SPAWNED)) { return; }
        entity.getPersistentData().putBoolean(SPAWNED, true);
        if (fireWith(ENTITY_SPAWN, level, null, entity, ItemStack.EMPTY, null, ScriptEvents.ENTITY, entity)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!ScriptEvents.has(PLAYER_LOGIN) || !(player.level() instanceof ServerLevel level)) { return; }
        fireWith(PLAYER_LOGIN, level, null, player, held(player), player, ScriptEvents.PLAYER, player);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!ScriptEvents.has(PLAYER_TICK) || !(player.level() instanceof ServerLevel level) || ScriptEvents.idle(PLAYER_TICK, level.getGameTime())) { return; }
        fireWith(PLAYER_TICK, level, null, player, held(player), player, ScriptEvents.PLAYER, player, ScriptEvents.POS, player.blockPosition());
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onServerTick(ServerTickEvent.Post event) {
        if (!ScriptEvents.has(SERVER_TICK)) { return; }
        ServerLevel level = event.getServer().overworld();
        long time = level.getGameTime();
        if (ScriptEvents.idle(SERVER_TICK, time)) { return; }
        fireWith(SERVER_TICK, level, null, null, ItemStack.EMPTY, null, "tick", time);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!ScriptEvents.has(DIMENSION_CHANGE) || !(player.level() instanceof ServerLevel level)) { return; }
        fireWith(DIMENSION_CHANGE, level, null, player, held(player), player, ScriptEvents.PLAYER, player, ScriptEvents.FROM, event.getFrom().location().toString(), ScriptEvents.TO, event.getTo().location().toString());
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (!ScriptEvents.has(LOOT_DROP) || !(victim.level() instanceof ServerLevel level)) { return; }
        Entity attacker = event.getSource().getEntity();
        fireWith(LOOT_DROP, level, null, victim, held(attacker), attacker instanceof Player player ? player : null, ScriptEvents.ENTITY, victim, ScriptEvents.ATTACKER, attacker, ScriptEvents.DAMAGE_TYPE, event.getSource().getMsgId(), ScriptEvents.DROPS, new ScriptDrops(event.getDrops(), victim));
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!ScriptEvents.has(ENTITY_TRAVEL) || !(entity.level() instanceof ServerLevel level)) { return; }
        if (fireWith(ENTITY_TRAVEL, level, null, entity, held(entity), entity instanceof Player player ? player : null, ScriptEvents.ENTITY, entity, ScriptEvents.FROM, level.dimension().location().toString(), ScriptEvents.TO, event.getDimension().location().toString())) { event.setCanceled(true); }
    }

    public static void despawn(Mob entity) {
        if (entity.isRemoved() || !(entity.level() instanceof ServerLevel level) || !ScriptEvents.has(ENTITY_DESPAWN)) { return; }
        fireWith(ENTITY_DESPAWN, level, null, entity, ItemStack.EMPTY, null, ScriptEvents.ENTITY, entity);
    }

    private static boolean fireWith(String kind, ServerLevel level, @Nullable BlockState state, @Nullable Entity entity, ItemStack item, @Nullable Player player, Object... extra) {
        Map<String, Object> bindings = ScriptEvents.bind(level, player);
        for (int i = 0; i < extra.length; i += 2) { bindings.put((String) extra[i], extra[i + 1]); }
        return ScriptEvents.fire(kind, level, state, entity, item, bindings);
    }

    private static ItemStack placing(@Nullable Entity placer) {
        if (!(placer instanceof Player player)) { return held(placer); }
        InteractionHand hand = placingHand;
        placingHand = InteractionHand.MAIN_HAND;
        return player.getItemInHand(hand);
    }

    private static ItemStack held(@Nullable Entity entity) { return entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY; }
}
