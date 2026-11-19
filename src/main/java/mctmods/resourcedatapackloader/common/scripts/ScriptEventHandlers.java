package mctmods.resourcedatapackloader.common.scripts;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
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

    private ScriptEventHandlers() {}

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!ScriptEvents.has(BLOCK_PLACE) || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Entity placer = event.getEntity();
        Player player = placer instanceof Player who ? who : null;
        Map<String, Object> bindings = bind(level, player);
        bindings.put("pos", event.getPos());
        bindings.put("state", event.getPlacedBlock());
        bindings.put("entity", placer);
        ItemStack offhand = player == null ? ItemStack.EMPTY : player.getOffhandItem();
        if (ScriptEvents.fire(BLOCK_PLACE, level, event.getPlacedBlock(), placer, held(placer), offhand, bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onBreak(BreakBlockEvent event) {
        if (!ScriptEvents.has(BLOCK_BREAK) || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Player player = event.getPlayer();
        Map<String, Object> bindings = bind(level, player);
        bindings.put("pos", event.getPos());
        bindings.put("state", event.getState());
        bindings.put("player", player);
        if (ScriptEvents.fire(BLOCK_BREAK, level, event.getState(), player, held(player), bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onHurt(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (!ScriptEvents.has(ENTITY_HURT) || !(victim.level() instanceof ServerLevel level)) { return; }
        Entity attacker = event.getSource().getEntity();
        Player player = attacker instanceof Player who ? who : null;
        Map<String, Object> bindings = bind(level, player);
        bindings.put("entity", victim);
        bindings.put("attacker", attacker);
        bindings.put("damageType", event.getSource().getMsgId());
        bindings.put("amount", event.getAmount());
        if (ScriptEvents.fire(ENTITY_HURT, level, null, victim, held(attacker), bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob) || !ScriptEvents.has(ENTITY_SPAWN) || event.loadedFromDisk() || !(event.getLevel() instanceof ServerLevel level) || entity.getPersistentData().getBooleanOr(SPAWNED, false)) { return; }
        entity.getPersistentData().putBoolean(SPAWNED, true);
        Map<String, Object> bindings = bind(level, null);
        bindings.put("entity", entity);
        if (ScriptEvents.fire(ENTITY_SPAWN, level, null, entity, ItemStack.EMPTY, bindings)) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!ScriptEvents.has(PLAYER_LOGIN) || !(player.level() instanceof ServerLevel level)) { return; }
        Map<String, Object> bindings = bind(level, player);
        bindings.put("player", player);
        ScriptEvents.fire(PLAYER_LOGIN, level, null, player, held(player), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!ScriptEvents.has(PLAYER_TICK) || !(player.level() instanceof ServerLevel level) || ScriptEvents.idle(PLAYER_TICK, level.getGameTime())) { return; }
        Map<String, Object> bindings = bind(level, player);
        bindings.put("player", player);
        bindings.put("pos", player.blockPosition());
        ScriptEvents.fire(PLAYER_TICK, level, null, player, held(player), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onServerTick(ServerTickEvent.Post event) {
        if (!ScriptEvents.has(SERVER_TICK)) { return; }
        ServerLevel level = event.getServer().overworld();
        long time = level.getGameTime();
        if (ScriptEvents.idle(SERVER_TICK, time)) { return; }
        Map<String, Object> bindings = bind(level, null);
        bindings.put("tick", time);
        ScriptEvents.fire(SERVER_TICK, level, null, null, ItemStack.EMPTY, bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!ScriptEvents.has(DIMENSION_CHANGE) || !(player.level() instanceof ServerLevel level)) { return; }
        Map<String, Object> bindings = bind(level, player);
        bindings.put("player", player);
        bindings.put("from", event.getFrom().identifier().toString());
        bindings.put("to", event.getTo().identifier().toString());
        ScriptEvents.fire(DIMENSION_CHANGE, level, null, player, held(player), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (!ScriptEvents.has(LOOT_DROP) || !(victim.level() instanceof ServerLevel level)) { return; }
        Entity attacker = event.getSource().getEntity();
        Player player = attacker instanceof Player who ? who : null;
        Map<String, Object> bindings = bind(level, player);
        bindings.put("entity", victim);
        bindings.put("attacker", attacker);
        bindings.put("damageType", event.getSource().getMsgId());
        bindings.put("drops", new ScriptDrops(event.getDrops(), victim));
        ScriptEvents.fire(LOOT_DROP, level, null, victim, held(attacker), bindings);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!ScriptEvents.has(ENTITY_TRAVEL) || !(entity.level() instanceof ServerLevel level)) { return; }
        Player player = entity instanceof Player who ? who : null;
        Map<String, Object> bindings = bind(level, player);
        bindings.put("entity", entity);
        bindings.put("from", level.dimension().identifier().toString());
        bindings.put("to", event.getDimension().identifier().toString());
        if (ScriptEvents.fire(ENTITY_TRAVEL, level, null, entity, held(entity), bindings)) { event.setCanceled(true); }
    }

    public static void despawn(Mob entity) {
        if (entity.isRemoved() || !(entity.level() instanceof ServerLevel level) || !ScriptEvents.has(ENTITY_DESPAWN)) { return; }
        Map<String, Object> bindings = bind(level, null);
        bindings.put("entity", entity);
        ScriptEvents.fire(ENTITY_DESPAWN, level, null, entity, ItemStack.EMPTY, bindings);
    }

    private static Map<String, Object> bind(ServerLevel level, @Nullable Player player) {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("rdpl", new RdplApi(level, player));
        return bindings;
    }

    private static ItemStack held(@Nullable Entity entity) { return entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY; }
}
