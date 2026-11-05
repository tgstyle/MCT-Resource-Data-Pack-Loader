package mctmods.resourcedatapackloader.common.sleeping;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.rules.ContentGameRules;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ContentSleep {
    public static final int HIDDEN = 0;
    public static final int NONE = 1;
    public static final int CURRENT = 2;
    public static final int OTHER = 3;
    public static final int UPDATED = 4;
    private static final String SETS_SPAWN = "bedSetsSpawn";
    private static final String BY_DAY = "bedSpawnByDay";
    private static final String SHARE = "sleepShare";
    private static final String BELOW = "sleepLeavesOutBelow";
    private static final String SAYS_IN = "sleepSaysIn";
    private static final String CHAT = "chat";
    private static final String BAR = "bar";
    private static final int UNSET = -1;
    private static final Map<UUID, Integer> DECIDED = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> SEEN = new HashMap<>();
    private static final Set<ResourceKey<Level>> SKIPPED = new HashSet<>();
    private static boolean setsSpawn = true;
    private static boolean byDay;
    private static int share = UNSET;
    @Nullable private static Integer below;
    @Nullable private static String sleepSays;
    @Nullable private static String wakeSays;
    @Nullable private static String morningSays;
    private static boolean overlay;
    private static boolean pressing;

    private ContentSleep() {}

    private record Count(int counted, int leftOut) {}

    public static void load() {
        setsSpawn = true;
        byDay = false;
        share = UNSET;
        below = null;
        sleepSays = null;
        wakeSays = null;
        morningSays = null;
        overlay = false;
        DECIDED.clear();
        SEEN.clear();
        SKIPPED.clear();
        Json.eachFile(PackManager.SLEEPING, "sleeping file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "sleeping file");
            if (json != null) { read("Sleeping file " + key, json); }
        });
        if (!setsSpawn) { Summary.info("sleeping", "Sleeping in a bed keeps a spawn point the player already has, and the in-bed screen offers Set spawn"); }
        if (!setsSpawn && byDay) { Summary.info("sleeping", "Using a bed by day sets the spawn point there unless the player sneaks"); }
        if (share != UNSET) { Summary.info("sleeping", "The night is skipped when " + share + "% of the players are asleep"); }
        if (below != null) { Summary.info("sleeping", "Awake players below Y " + below + " are left out of the sleep count"); }
        if (says()) { Summary.info("sleeping", "Sleep lines go to the " + (overlay ? "action bar" : "chat")); }
    }

    private static void read(String owner, JsonObject json) {
        if (json.has(SETS_SPAWN)) {
            setsSpawn = Json.getBoolean(json, SETS_SPAWN, setsSpawn);
            if (!setsSpawn) { VanillaClients.clientDrawn(owner, SETS_SPAWN); }
        }
        byDay = Json.getBoolean(json, BY_DAY, byDay);
        if (json.has(SHARE)) {
            float asked = Json.ranged(json, SHARE, Float.NaN, 0.0F, 100.0F, owner);
            if (!Float.isNaN(asked)) { share = Math.round(asked); }
        }
        if (json.has(BELOW)) {
            int asked = Json.getInt(json, BELOW, Integer.MIN_VALUE);
            if (asked != Integer.MIN_VALUE) { below = asked; }
        }
        sleepSays = said(json, "sleepSays", sleepSays);
        wakeSays = said(json, "wakeSays", wakeSays);
        morningSays = said(json, "morningSays", morningSays);
        overlay = BAR.equals(Json.choice(json, SAYS_IN, overlay ? BAR : CHAT, owner, List.of(CHAT, BAR)));
    }

    @Nullable private static String said(JsonObject json, String key, @Nullable String held) {
        String text = Json.text(json, key, held == null ? "" : held);
        return text.isEmpty() ? null : text;
    }

    private static boolean says() { return sleepSays != null || wakeSays != null || morningSays != null; }

    public static boolean leavesOut() { return below != null; }

    private static boolean leftOut(ServerPlayer player) { return below != null && !player.isSleeping() && player.getY() < below; }

    public static boolean enoughAsleep(ServerLevel level, int percentage) {
        int counted = 0;
        int deep = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || leftOut(player)) { continue; }
            counted++;
            if (player.isSleepingLongEnough()) { deep++; }
        }
        return deep >= Math.max(1, Mth.ceil(counted * percentage / 100.0F));
    }

    private static Count count(ServerLevel level) {
        int counted = 0;
        int leftOut = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) { continue; }
            if (leftOut(player)) { leftOut++; }
            else { counted++; }
        }
        return new Count(counted, leftOut);
    }

    private static boolean keptSpawn(ServerPlayer player) {
        ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
        if (respawn == null) { return false; }
        ServerLevel level = player.level().getServer().getLevel(respawn.respawnData().dimension());
        if (level == null) { return false; }
        if (respawn.forced()) { return true; }
        BlockPos pos = respawn.respawnData().pos();
        if (ContentSleepBeds.setsSpawn(level, pos)) { return true; }
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof RespawnAnchorBlock && state.getValue(RespawnAnchorBlock.CHARGE) > 0 && RespawnAnchorBlock.canSetSpawn(level, pos);
    }

    private static boolean listed(ServerPlayer player) { return player.level().getServer().getPlayerList().getPlayer(player.getUUID()) == player; }

    public static void onSetSpawn(PlayerSetSpawnEvent event) {
        if (setsSpawn || pressing || event.isForced() || !(event.getEntity() instanceof ServerPlayer player) || !listed(player)) { return; }
        BlockPos pos = event.getNewSpawn();
        ServerLevel level = player.level().getServer().getLevel(event.getSpawnLevel());
        if (pos == null || level == null || !ContentSleepBeds.setsSpawn(level, pos)) { return; }
        ServerPlayer.RespawnConfig current = player.getRespawnConfig();
        if (current != null && current.respawnData().dimension().equals(event.getSpawnLevel()) && pos.equals(current.respawnData().pos())) {
            DECIDED.put(player.getUUID(), CURRENT);
            return;
        }
        if (!keptSpawn(player)) {
            DECIDED.put(player.getUUID(), NONE);
            return;
        }
        if (byDay && level.isBrightOutside() && !player.isShiftKeyDown()) {
            DECIDED.put(player.getUUID(), CURRENT);
            return;
        }
        DECIDED.put(player.getUUID(), OTHER);
        event.setCanceled(true);
    }

    public static void onCanSleep(CanPlayerSleepEvent event) {
        if (setsSpawn) { return; }
        ServerPlayer player = event.getEntity();
        Integer decided = DECIDED.remove(player.getUUID());
        if (event.getProblem() == null) { RDPLNetwork.sendBedState(player, decided == null ? HIDDEN : decided); }
    }

    public static void setSpawnHere(ServerPlayer player) {
        if (setsSpawn || !player.isSleeping()) { return; }
        BlockPos pos = player.getSleepingPos().orElse(null);
        ServerLevel level = player.level();
        if (pos == null || !ContentSleepBeds.setsSpawn(level, pos)) { return; }
        pressing = true;
        try { player.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(level.dimension(), pos, player.getYRot(), player.getXRot()), false), false); }
        finally { pressing = false; }
        ServerPlayer.RespawnConfig now = player.getRespawnConfig();
        if (now != null && now.respawnData().dimension().equals(level.dimension()) && pos.equals(now.respawnData().pos())) { RDPLNetwork.sendBedState(player, UPDATED); }
    }

    public static void onWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (says() && !event.wakeImmediately() && !event.updateLevel()) { SKIPPED.add(player.level().dimension()); }
        if (!setsSpawn) { RDPLNetwork.sendBedState(player, HIDDEN); }
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (share == UNSET) { return; }
        MinecraftServer server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) { if (!ContentGameRules.names(level, GameRules.PLAYERS_SLEEPING_PERCENTAGE)) { level.getGameRules().set(GameRules.PLAYERS_SLEEPING_PERCENTAGE, share, server); } }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!says() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        ResourceKey<Level> dimension = level.dimension();
        Set<UUID> seen = SEEN.get(dimension);
        boolean skipped = SKIPPED.remove(dimension);
        boolean sleeping = false;
        for (ServerPlayer player : level.players()) { sleeping |= player.isSleeping(); }
        if (!sleeping && !skipped && (seen == null || seen.isEmpty())) { return; }
        if (seen == null) {
            seen = new HashSet<>();
            SEEN.put(dimension, seen);
        }
        Set<UUID> now = new HashSet<>();
        for (ServerPlayer player : level.players()) { if (player.isSleeping()) { now.add(player.getUUID()); } }
        Count count = count(level);
        for (UUID id : now) { if (!seen.contains(id)) { say(level, sleepSays, name(level, Set.of(id)), now.size(), count); } }
        if (!skipped && !level.isBrightOutside()) { for (UUID id : seen) { if (!now.contains(id)) { say(level, wakeSays, name(level, Set.of(id)), now.size(), count); } } }
        if (skipped) { say(level, morningSays, name(level, seen), seen.size(), count); }
        seen.clear();
        seen.addAll(now);
    }

    @Nullable private static Component name(ServerLevel level, Set<UUID> ids) {
        for (UUID id : ids) {
            Player found = level.getPlayerByUUID(id);
            if (found != null) { return found.getDisplayName(); }
        }
        return null;
    }

    private static void say(ServerLevel level, @Nullable String said, @Nullable Component name, int asleep, Count count) {
        if (said == null) { return; }
        for (ServerPlayer player : level.players()) { player.sendSystemMessage(Component.translatableWithFallback(said, Lang.tr(player, said), name == null ? Component.empty() : name, asleep, count.counted(), count.leftOut()), overlay); }
    }
}
