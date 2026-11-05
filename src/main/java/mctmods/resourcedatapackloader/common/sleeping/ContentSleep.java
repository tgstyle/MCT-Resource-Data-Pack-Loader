package mctmods.resourcedatapackloader.common.sleeping;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.rules.ContentGameRules;
import mctmods.resourcedatapackloader.core.network.BedStateMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ContentSleep {
    private static final String BED_SETS_SPAWN = "bedSetsSpawn";
    private static final String BED_SPAWN_BY_DAY = "bedSpawnByDay";
    private static final String SLEEP_SHARE = "sleepShare";
    private static final String LEAVES_OUT_BELOW = "sleepLeavesOutBelow";
    private static final String SLEEP_SAYS = "sleepSays";
    private static final String WAKE_SAYS = "wakeSays";
    private static final String MORNING_SAYS = "morningSays";
    private static final String SAYS_IN = "sleepSaysIn";
    private static final String CHAT = "chat";
    private static final String BAR = "bar";
    private static final int UNSET = -1;
    private static final int WHOLE_SHARE = 100;
    private static final Map<UUID, Byte> STATES = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> SLEEPERS = new HashMap<>();
    private static final Set<ResourceKey<Level>> SKIPPED = new HashSet<>();
    private static boolean bedSetsSpawn = true;
    private static boolean bedSpawnByDay;
    private static int share = UNSET;
    @Nullable private static Integer leavesOutBelow;
    @Nullable private static String sleepSays;
    @Nullable private static String wakeSays;
    @Nullable private static String morningSays;
    private static boolean inBar;
    private static boolean pressing;

    private ContentSleep() {}

    private record Count(int asleep, int deep, int counted, int leftOut) {}

    public static void load() {
        bedSetsSpawn = true;
        bedSpawnByDay = false;
        share = UNSET;
        leavesOutBelow = null;
        sleepSays = null;
        wakeSays = null;
        morningSays = null;
        inBar = false;
        Json.eachFile(PackManager.SLEEPING, "sleeping file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "sleeping file");
            if (json != null) { read("Sleeping file " + key, json); }
        });
        String where = inBar ? BAR : CHAT;
        if (!bedSetsSpawn) { Summary.info("sleeping", "Sleeping in a bed keeps a spawn point the player already has; the in-bed screen offers a Set spawn button"); }
        if (!bedSetsSpawn && bedSpawnByDay) { Summary.info("sleeping", "Using a bed by day sets the spawn point there unless the player sneaks"); }
        if (share != UNSET) { Summary.info("sleeping", "The night is skipped once " + share + "% of the counted players sleep"); }
        if (leavesOutBelow != null) { Summary.info("sleeping", "Awake players below Y " + leavesOutBelow + " are left out of the sleep count"); }
        if (sleepSays != null) { Summary.info("sleeping", "Falling asleep says '" + sleepSays + "' in the " + where); }
        if (wakeSays != null) { Summary.info("sleeping", "Leaving bed at night says '" + wakeSays + "' in the " + where); }
        if (morningSays != null) { Summary.info("sleeping", "Sleeping through the night says '" + morningSays + "' in the " + where); }
    }

    private static void read(String owner, JsonObject json) {
        if (json.has(BED_SETS_SPAWN)) {
            bedSetsSpawn = Json.getBoolean(json, BED_SETS_SPAWN, true);
            if (!bedSetsSpawn) { VanillaClients.clientDrawn(owner, BED_SETS_SPAWN); }
        }
        if (json.has(BED_SPAWN_BY_DAY)) { bedSpawnByDay = Json.getBoolean(json, BED_SPAWN_BY_DAY, false); }
        if (json.has(SLEEP_SHARE)) { share = Math.round(Json.ranged(json, SLEEP_SHARE, WHOLE_SHARE, 0.0F, WHOLE_SHARE, owner)); }
        if (json.has(LEAVES_OUT_BELOW)) { leavesOutBelow = Json.getInt(json, LEAVES_OUT_BELOW, 0); }
        sleepSays = said(json, SLEEP_SAYS, sleepSays);
        wakeSays = said(json, WAKE_SAYS, wakeSays);
        morningSays = said(json, MORNING_SAYS, morningSays);
        if (json.has(SAYS_IN)) { inBar = BAR.equals(Json.choice(json, SAYS_IN, CHAT, owner, List.of(CHAT, BAR))); }
    }

    @Nullable private static String said(JsonObject json, String member, @Nullable String held) {
        if (!json.has(member)) { return held; }
        String text = Json.text(json, member, "");
        return text.isEmpty() ? null : text;
    }

    private static boolean talks() { return sleepSays != null || wakeSays != null || morningSays != null; }

    public static boolean leavesOut() { return leavesOutBelow != null; }

    public static boolean enoughAsleep(ServerLevel level, int percent) {
        Count count = count(level);
        return count.deep() >= Math.max(1, Mth.ceil(count.counted() * percent / 100.0F));
    }

    private static Count count(ServerLevel level) {
        int asleep = 0;
        int deep = 0;
        int counted = 0;
        int leftOut = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) { continue; }
            if (!player.isSleeping() && leavesOutBelow != null && player.getY() < leavesOutBelow) {
                leftOut++;
                continue;
            }
            counted++;
            if (player.isSleeping()) { asleep++; }
            if (player.isSleepingLongEnough()) { deep++; }
        }
        return new Count(asleep, deep, counted, leftOut);
    }

    private static boolean noSettableBed(ServerLevel level, BlockPos pos) { return !(level.getBlockState(pos).getBlock() instanceof BedBlock && BedBlock.canSetSpawn(level)); }

    private static boolean keptSpawn(ServerPlayer player) {
        BlockPos pos = player.getRespawnPosition();
        ServerLevel level = player.server.getLevel(player.getRespawnDimension());
        if (pos == null || level == null) { return false; }
        if (player.isRespawnForced()) { return true; }
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof RespawnAnchorBlock) { return state.getValue(RespawnAnchorBlock.CHARGE) > 0 && RespawnAnchorBlock.canSetSpawn(level); }
        return state.getBlock() instanceof BedBlock && BedBlock.canSetSpawn(level);
    }

    public static void onSetSpawn(PlayerSetSpawnEvent event) {
        if (bedSetsSpawn || pressing || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        UUID id = player.getUUID();
        STATES.remove(id);
        BlockPos pos = event.getNewSpawn();
        ServerLevel level = player.server.getLevel(event.getSpawnLevel());
        if (event.isForced() || pos == null || level == null || noSettableBed(level, pos)) { return; }
        if (pos.equals(player.getRespawnPosition()) && event.getSpawnLevel().equals(player.getRespawnDimension())) {
            STATES.put(id, BedStateMessage.CURRENT);
            return;
        }
        if (!keptSpawn(player)) {
            STATES.put(id, BedStateMessage.NONE);
            return;
        }
        if (bedSpawnByDay && level.isDay() && !player.isShiftKeyDown()) { return; }
        event.setCanceled(true);
        STATES.put(id, BedStateMessage.OTHER);
    }

    public static void onCanSleep(CanPlayerSleepEvent event) {
        Byte state = STATES.remove(event.getEntity().getUUID());
        if (bedSetsSpawn || event.getProblem() != null) { return; }
        RDPLNetwork.sendBedState(event.getEntity(), state == null ? BedStateMessage.HIDDEN : state);
    }

    public static void setSpawnHere(ServerPlayer player) {
        if (bedSetsSpawn || !player.isSleeping()) { return; }
        BlockPos pos = player.getSleepingPos().orElse(null);
        ServerLevel level = player.serverLevel();
        if (pos == null || noSettableBed(level, pos)) { return; }
        pressing = true;
        try { player.setRespawnPosition(level.dimension(), pos, player.getYRot(), false, false); }
        finally { pressing = false; }
        if (pos.equals(player.getRespawnPosition()) && level.dimension().equals(player.getRespawnDimension())) {
            RDPLNetwork.sendBedState(player, BedStateMessage.UPDATED);
        }
    }

    public static void onWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (!bedSetsSpawn) { RDPLNetwork.sendBedState(player, BedStateMessage.HIDDEN); }
        if (talks() && !event.wakeImmediately() && !event.updateLevel()) { SKIPPED.add(player.level().dimension()); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { STATES.remove(event.getEntity().getUUID()); }

    public static void onServerStarted(ServerStartedEvent event) {
        STATES.clear();
        SLEEPERS.clear();
        SKIPPED.clear();
        if (share == UNSET) { return; }
        MinecraftServer server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            if (!ContentGameRules.names(level, GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE)) { level.getGameRules().getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE).set(share, server); }
        }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!talks() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        ResourceKey<Level> dimension = level.dimension();
        Set<UUID> was = SLEEPERS.computeIfAbsent(dimension, key -> new HashSet<>());
        Set<UUID> now = new HashSet<>();
        for (ServerPlayer player : level.players()) { if (player.isSleeping()) { now.add(player.getUUID()); } }
        boolean skipped = SKIPPED.remove(dimension);
        if (!skipped && now.equals(was)) { return; }
        Count count = count(level);
        for (UUID sleeper : now) { if (!was.contains(sleeper)) { say(level, sleepSays, sleeper, count); } }
        if (!skipped && !level.isDay()) {
            for (UUID sleeper : was) { if (!now.contains(sleeper)) { say(level, wakeSays, sleeper, count); } }
        }
        if (skipped) { say(level, morningSays, was.isEmpty() ? null : was.iterator().next(), new Count(was.size(), was.size(), count.counted(), count.leftOut())); }
        SLEEPERS.put(dimension, now);
    }

    private static void say(ServerLevel level, @Nullable String says, @Nullable UUID who, Count count) {
        if (says == null) { return; }
        ServerPlayer named = who == null ? null : level.getServer().getPlayerList().getPlayer(who);
        String name = named == null ? "" : named.getGameProfile().getName();
        for (ServerPlayer player : level.players()) { player.displayClientMessage(line(player, says, name, count.asleep(), count.counted(), count.leftOut()), inBar); }
    }

    private static Component line(ServerPlayer player, String key, Object... args) { return Component.translatableWithFallback(key, Lang.tr(player, key), args); }
}
