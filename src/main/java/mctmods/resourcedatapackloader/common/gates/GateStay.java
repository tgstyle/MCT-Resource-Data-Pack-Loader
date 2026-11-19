package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
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
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class GateStay {
    public static final GateStay NONE = new GateStay(0, "", List.of(), "", true, "", "");
    private static final String NEVER = "never";
    private static final String DAY = "day";
    private static final String VISIT = "visit";
    private static final List<String> RESETS = List.of(NEVER, DAY, VISIT);
    private static final List<Integer> DEFAULT_WARN = List.of(60, 30, 10, 5, 4, 3, 2, 1);
    private static final String SECTION = "rdplGateStay";
    private static final String USED = "used";
    private static final String DAY_KEY = "day";
    private static final int STEP = 20;
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

    public static GateStay parse(JsonObject json, Identifier key, @Nullable Identifier dimension) {
        if (!json.has("stayMinutes")) { return NONE; }
        if (dimension == null) {
            ContentLog.LOGGER.error("Gate {} sets stayMinutes without dimension, so it has no time budget", key);
            return NONE;
        }
        int minutes = Json.getInt(json, "stayMinutes", 0);
        if (minutes <= 0) {
            ContentLog.LOGGER.error("Gate {} sets stayMinutes {}, which is not above 0, so it has no time budget", key, minutes);
            return NONE;
        }
        JsonObject onExpire = Json.getJsonObject(json, "onExpire", new JsonObject());
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
        CompoundTag entry = PlayerPersisted.read(player, SECTION).getCompoundOrEmpty(def.id());
        if (DAY.equals(stay.reset) && entry.getLongOr(DAY_KEY, 0L) != CardPlace.day(player.level())) { return false; }
        return entry.getIntOr(USED, 0) >= stay.limit();
    }

    static void refused(ServerPlayer player, GateDef def) { Says.bar(player, CardIds.GATE_EXPIRED, ChatFormatting.RED, def.stay().expired.replace("%dim%", def.name())); }

    static void tick(MinecraftServer server) {
        List<GateDef> gates = ContentGates.staying();
        if (gates.isEmpty()) { return; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Identifier here = player.level().dimension().identifier();
            for (GateDef def : gates) {
                if (Objects.equals(here, def.dimension())) { count(server, player, def); }
                else if (VISIT.equals(def.stay().reset)) { forget(player, def); }
            }
        }
    }

    private static void count(MinecraftServer server, ServerPlayer player, GateDef def) {
        GateStay stay = def.stay();
        CompoundTag entry = entry(player, def);
        long today = CardPlace.day(player.level());
        if (DAY.equals(stay.reset) && entry.getLongOr(DAY_KEY, 0L) != today) {
            entry.putInt(USED, 0);
            entry.putLong(DAY_KEY, today);
        }
        int limit = stay.limit();
        int used = entry.getIntOr(USED, 0);
        if (used >= limit) { return; }
        used = Math.min(limit, used + STEP);
        entry.putInt(USED, used);
        if (used >= limit) {
            expire(server, player, def);
            return;
        }
        int seconds = (limit - used) / STEP;
        if (stay.warn.contains(seconds)) {
            String time = String.format("%d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
            Says.bar(player, CardIds.GATE_STAY, ChatFormatting.GOLD, stay.message.replace("%dim%", def.name()).replace("%time%", time));
        }
    }

    private static void expire(MinecraftServer server, ServerPlayer player, GateDef def) {
        GateStay stay = def.stay();
        if (stay.exit) {
            GateEvents.sendOut(player, server.overworld());
            String name = player.getName().getString();
            if (Objects.equals(def.dimension(), player.level().dimension().identifier())) { ContentLog.LOGGER.warn("Gate {} could not send {} out of dimension {}", def.key(), name, def.dimension()); }
            else { ContentLog.LOGGER.info("Gate {} sent {} out of dimension {}, the {} minute budget is used", def.key(), name, def.dimension(), stay.minutes); }
        }
        refused(player, def);
        if (!stay.function.isEmpty()) { Functions.runAs(player, stay.function, "Gate " + def.key()); }
    }

    private static CompoundTag entry(ServerPlayer player, GateDef def) {
        CompoundTag section = PlayerPersisted.section(player, SECTION);
        if (!section.contains(def.id())) { section.put(def.id(), new CompoundTag()); }
        return section.getCompoundOrEmpty(def.id());
    }

    private static void forget(ServerPlayer player, GateDef def) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        if (section.getCompoundOrEmpty(def.id()).getIntOr(USED, 0) > 0) { section.remove(def.id()); }
    }
}
