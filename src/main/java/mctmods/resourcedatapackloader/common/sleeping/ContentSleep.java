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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.server.ServerStartedEvent;

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
    private static final Map<ResourceKey<Level>, Set<UUID>> SLEEPING = new HashMap<>();
    private static final Set<ResourceKey<Level>> SKIPPED = new HashSet<>();
    private static boolean bedSetsSpawn = true;
    private static boolean bedSpawnByDay;
    private static int share = UNSET;
    private static boolean leavesOut;
    private static int below;
    private static String sleepSays = "";
    private static String wakeSays = "";
    private static String morningSays = "";
    private static String saysIn = CHAT;
    private static boolean pressing;

    private ContentSleep() {}

    private record Count(int asleep, int deep, int counted, int leftOut) {}

    public static void load() {
        bedSetsSpawn = true;
        bedSpawnByDay = false;
        share = UNSET;
        leavesOut = false;
        below = 0;
        sleepSays = "";
        wakeSays = "";
        morningSays = "";
        saysIn = CHAT;
        Json.eachFile(PackManager.SLEEPING, "sleeping file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "sleeping file");
            if (json != null) { read("Sleeping file " + key, json); }
        });
        if (!bedSetsSpawn) { Summary.info("sleeping." + BED_SETS_SPAWN, "Sleeping in a bed keeps a spawn point the player already has; the in-bed screen offers a Set spawn button"); }
        if (!bedSetsSpawn && bedSpawnByDay) { Summary.info("sleeping." + BED_SPAWN_BY_DAY, "Using a bed by day sets the spawn there unless the player sneaks"); }
        if (share != UNSET) { Summary.info("sleeping." + SLEEP_SHARE, share + "% of the counted players must sleep to skip the night"); }
        if (leavesOut) { Summary.info("sleeping." + LEAVES_OUT_BELOW, "Awake players below Y " + below + " are left out of the sleep count"); }
        if (!sleepSays.isEmpty()) { Summary.info("sleeping." + SLEEP_SAYS, "Falling asleep says '" + sleepSays + "' in the " + saysIn); }
        if (!wakeSays.isEmpty()) { Summary.info("sleeping." + WAKE_SAYS, "Leaving bed at night says '" + wakeSays + "' in the " + saysIn); }
        if (!morningSays.isEmpty()) { Summary.info("sleeping." + MORNING_SAYS, "Sleeping through the night says '" + morningSays + "' in the " + saysIn); }
    }

    private static void read(String owner, JsonObject json) {
        if (json.has(BED_SETS_SPAWN)) {
            bedSetsSpawn = Json.getBoolean(json, BED_SETS_SPAWN, true);
            if (!bedSetsSpawn) { VanillaClients.clientDrawn(owner, BED_SETS_SPAWN); }
        }
        if (json.has(BED_SPAWN_BY_DAY)) { bedSpawnByDay = Json.getBoolean(json, BED_SPAWN_BY_DAY, false); }
        float asked = Json.bounded(json, SLEEP_SHARE, 0.0F, 100.0F, owner);
        if (!Float.isNaN(asked)) { share = Math.round(asked); }
        if (json.has(LEAVES_OUT_BELOW)) {
            below = Json.getInt(json, LEAVES_OUT_BELOW, below);
            leavesOut = true;
        }
        if (json.has(SLEEP_SAYS)) { sleepSays = Json.text(json, SLEEP_SAYS, ""); }
        if (json.has(WAKE_SAYS)) { wakeSays = Json.text(json, WAKE_SAYS, ""); }
        if (json.has(MORNING_SAYS)) { morningSays = Json.text(json, MORNING_SAYS, ""); }
        saysIn = Json.choice(json, SAYS_IN, saysIn, owner, List.of(CHAT, BAR));
    }

    public static boolean leavesOut() { return leavesOut; }

    private static boolean says() { return !sleepSays.isEmpty() || !wakeSays.isEmpty() || !morningSays.isEmpty(); }

    private static boolean noSpawnBed(Level level, BlockPos pos) { return !(level.getBlockState(pos).getBlock() instanceof BedBlock && BedBlock.canSetSpawn(level)); }

    private static boolean spawnLost(ServerPlayer player) {
        BlockPos at = player.getRespawnPosition();
        ServerLevel level = at == null ? null : player.server.getLevel(player.getRespawnDimension());
        if (level == null) { return true; }
        if (player.isRespawnForced()) { return false; }
        BlockState state = level.getBlockState(at);
        if (state.getBlock() instanceof BedBlock) { return !BedBlock.canSetSpawn(level); }
        return !(state.getBlock() instanceof RespawnAnchorBlock && state.getValue(RespawnAnchorBlock.CHARGE) > 0 && RespawnAnchorBlock.canSetSpawn(level));
    }

    private static boolean isSpawn(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) { return pos.equals(player.getRespawnPosition()) && dimension.equals(player.getRespawnDimension()); }

    public static void onSetSpawn(PlayerSetSpawnEvent event) {
        if (bedSetsSpawn || pressing || event.isForced() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        BlockPos pos = event.getNewSpawn();
        ServerLevel level = pos == null ? null : player.server.getLevel(event.getSpawnLevel());
        if (level == null || noSpawnBed(level, pos) || isSpawn(player, event.getSpawnLevel(), pos) || spawnLost(player)) { return; }
        if (bedSpawnByDay && level.isDay() && !player.isShiftKeyDown()) { return; }
        event.setCanceled(true);
    }

    public static void onSleepInBed(PlayerSleepInBedEvent event) {
        if (bedSetsSpawn || event.getResultStatus() != null || !(event.getEntity() instanceof ServerPlayer player) || !RDPLNetwork.reaches(player)) { return; }
        BlockPos pos = event.getPos();
        ServerLevel level = player.serverLevel();
        byte state;
        if (pos == null || noSpawnBed(level, pos)) { state = BedStateMessage.HIDDEN; }
        else if (isSpawn(player, level.dimension(), pos)) { state = BedStateMessage.CURRENT; }
        else if (spawnLost(player)) { state = BedStateMessage.NONE; }
        else { state = BedStateMessage.OTHER; }
        RDPLNetwork.sendBedState(player, state);
    }

    public static void setSpawnHere(ServerPlayer player) {
        if (bedSetsSpawn || !player.isSleeping()) { return; }
        BlockPos pos = player.getSleepingPos().orElse(null);
        ServerLevel level = player.serverLevel();
        if (pos == null || noSpawnBed(level, pos)) { return; }
        pressing = true;
        try { player.setRespawnPosition(level.dimension(), pos, player.getYRot(), false, false); }
        finally { pressing = false; }
        if (isSpawn(player, level.dimension(), pos)) { RDPLNetwork.sendBedState(player, BedStateMessage.UPDATED); }
    }

    public static void onWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (!bedSetsSpawn) { RDPLNetwork.sendBedState(player, BedStateMessage.HIDDEN); }
        if (!event.wakeImmediately() && !event.updateLevel() && says()) { SKIPPED.add(player.level().dimension()); }
    }

    public static void onServerStarted(ServerStartedEvent event) {
        SLEEPING.clear();
        SKIPPED.clear();
        if (share == UNSET) { return; }
        MinecraftServer server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) { if (!ContentGameRules.names(level, GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE)) { level.getGameRules().getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE).set(share, server); } }
    }

    private static Count count(ServerLevel level) {
        int asleep = 0;
        int deep = 0;
        int counted = 0;
        int leftOut = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) { continue; }
            if (player.isSleeping()) {
                asleep++;
                if (player.isSleepingLongEnough()) { deep++; }
            }
            else if (leavesOut && player.getY() < below) {
                leftOut++;
                continue;
            }
            counted++;
        }
        return new Count(asleep, deep, counted, leftOut);
    }

    public static boolean enoughAsleep(ServerLevel level, int percent) {
        Count count = count(level);
        return count.deep >= Math.max(1, (int) Math.ceil(count.counted * percent / 100.0D));
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !says() || !(event.level instanceof ServerLevel level)) { return; }
        ResourceKey<Level> dimension = level.dimension();
        Set<UUID> before = SLEEPING.getOrDefault(dimension, Set.of());
        Set<UUID> now = new HashSet<>();
        for (ServerPlayer player : level.players()) { if (player.isSleeping() && !player.isSpectator()) { now.add(player.getUUID()); } }
        boolean skipped = SKIPPED.remove(dimension);
        if (now.isEmpty() && before.isEmpty() && !skipped) { return; }
        Count count = count(level);
        for (ServerPlayer player : level.players()) {
            UUID id = player.getUUID();
            if (now.contains(id) && !before.contains(id)) { say(level, sleepSays, player.getDisplayName(), count.asleep, count); }
            else if (before.contains(id) && !now.contains(id) && !skipped && !level.isDay()) { say(level, wakeSays, player.getDisplayName(), count.asleep, count); }
        }
        if (skipped) {
            Component name = Component.empty();
            for (UUID id : before) {
                ServerPlayer one = level.getServer().getPlayerList().getPlayer(id);
                if (one != null) { name = one.getDisplayName(); break; }
            }
            say(level, morningSays, name, before.size(), count);
        }
        if (now.isEmpty()) { SLEEPING.remove(dimension); }
        else { SLEEPING.put(dimension, now); }
    }

    private static void say(ServerLevel level, String line, Component name, int asleep, Count count) {
        if (line.isEmpty()) { return; }
        boolean bar = BAR.equals(saysIn);
        for (ServerPlayer player : level.players()) { player.displayClientMessage(Component.translatableWithFallback(line, Lang.tr(player, line), name, asleep, count.counted, count.leftOut), bar); }
    }
}
