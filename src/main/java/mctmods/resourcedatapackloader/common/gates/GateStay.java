package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.Nbts;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class GateStay {
    public static final GateStay NONE = new GateStay(0, "", List.of(), "", true, "", "");
    private static final String NEVER = "never";
    private static final String DAY = "day";
    private static final String VISIT = "visit";
    private static final List<String> RESETS = List.of(NEVER, DAY, VISIT);
    private static final List<Integer> DEFAULT_WARN = List.of(60, 30, 10, 5, 4, 3, 2, 1);
    private static final List<String> EXPIRE_KEYS = List.of("exit", "function", "message");
    private static final String SECTION = "rdplGateStay";
    private static final String USED = "used";
    private static final String DAY_KEY = "day";
    private static final int TICKS_PER_SECOND = 20;
    private static final int TICKS_PER_MINUTE = 1200;
    private static final int SECONDS_PER_MINUTE = 60;

    private final int minutes;
    private final String reset;
    private final List<Integer> warn;
    private final String message;
    private final boolean exit;
    private final String function;
    private final String expired;

    private GateStay(int minutes, String reset, List<Integer> warn, String message, boolean exit, String function, String expired) {
        this.minutes = minutes;
        this.reset = reset;
        this.warn = warn;
        this.message = message;
        this.exit = exit;
        this.function = function;
        this.expired = expired;
    }

    public static GateStay parse(JsonObject json, ResourceLocation key, @Nullable ResourceLocation dimension) {
        if (!json.has("stayMinutes")) { return NONE; }
        if (GateDef.dimensionMissing(dimension, key, "stayMinutes", "it has no time budget")) { return NONE; }
        int minutes = Json.getInt(json, "stayMinutes", 0);
        if (minutes <= 0) {
            ContentLog.LOGGER.error("Gate {} sets stayMinutes {}, which is not above 0, so it has no time budget", key, minutes);
            return NONE;
        }
        JsonObject onExpire = Json.getJsonObject(json, "onExpire", new JsonObject());
        Json.unknownKeys(onExpire, "onExpire", EXPIRE_KEYS);
        return new GateStay(minutes, Json.choice(json, "stayReset", NEVER, "Gate " + key, RESETS), warnings(json), Json.getString(json, "stayMessage", "%dim%: %time% left"),
                Json.getBoolean(onExpire, "exit", true), Json.getString(onExpire, "function", "").trim(), Json.getString(onExpire, "message", "Your time in %dim% is up"));
    }

    private static List<Integer> warnings(JsonObject json) {
        if (!json.has("stayWarn")) { return DEFAULT_WARN; }
        List<Integer> seconds = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "stayWarn", new JsonArray())) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) { seconds.add(element.getAsInt()); }
        }
        return seconds;
    }

    public boolean active() { return minutes > 0; }

    private int limit() { return minutes * TICKS_PER_MINUTE; }

    static boolean spent(ServerPlayer player, GateDef def) {
        GateStay stay = def.stay();
        if (!stay.active() || VISIT.equals(stay.reset)) { return false; }
        CompoundTag entry = PlayerPersisted.read(player, SECTION).getCompound(def.id());
        if (DAY.equals(stay.reset) && entry.getLong(DAY_KEY) != today(player.server)) { return false; }
        return entry.getInt(USED) >= stay.limit();
    }

    static void refused(ServerPlayer player, GateDef def) { Says.bar(player, CardIds.GATE_EXPIRED, ChatFormatting.RED, Says.fill(def.stay().expired, "%dim%", def.name())); }

    static void tick(MinecraftServer server, int step) {
        List<GateDef> gates = ContentGates.staying();
        if (gates.isEmpty()) { return; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            for (GateDef def : gates) {
                if (player.level().dimension().location().equals(def.dimension())) { count(server, player, def, step); }
                else if (VISIT.equals(def.stay().reset)) { forget(player, def); }
            }
        }
    }

    private static void count(MinecraftServer server, ServerPlayer player, GateDef def, int step) {
        GateStay stay = def.stay();
        CompoundTag entry = entry(player, def);
        if (DAY.equals(stay.reset) && entry.getLong(DAY_KEY) != today(server)) {
            entry.putInt(USED, 0);
            entry.putLong(DAY_KEY, today(server));
        }
        int limit = stay.limit();
        int used = entry.getInt(USED);
        if (used >= limit) { return; }
        used = Math.min(limit, used + step);
        entry.putInt(USED, used);
        if (used >= limit) {
            expire(server, player, def);
            return;
        }
        int seconds = (limit - used) / TICKS_PER_SECOND;
        if (stay.warn.contains(seconds)) {
            String left = String.format("%d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
            Says.bar(player, CardIds.GATE_STAY, ChatFormatting.GOLD, Says.fill(stay.message, "%dim%", def.name(), "%time%", left));
        }
    }

    private static void expire(MinecraftServer server, ServerPlayer player, GateDef def) {
        GateStay stay = def.stay();
        if (stay.exit) {
            GateEvents.sendOut(server, player, server.overworld());
            if (player.level().dimension().location().equals(def.dimension())) { ContentLog.LOGGER.warn("Gate {} could not send {} out of dimension {}", def.key(), player.getName().getString(), def.dimension()); }
            else { ContentLog.LOGGER.info("Gate {} sent {} out of dimension {}, the {} minute budget is used", def.key(), player.getName().getString(), def.dimension(), stay.minutes); }
        }
        refused(player, def);
        if (!stay.function.isEmpty()) { Functions.runAs(player, stay.function, "Gate " + def.key()); }
    }

    private static CompoundTag entry(ServerPlayer player, GateDef def) {
        return Nbts.child(PlayerPersisted.section(player, SECTION), def.id());
    }

    private static void forget(ServerPlayer player, GateDef def) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        if (section.getCompound(def.id()).getInt(USED) > 0) { section.remove(def.id()); }
    }

    private static long today(MinecraftServer server) { return CardPlace.day(server.overworld()); }
}
