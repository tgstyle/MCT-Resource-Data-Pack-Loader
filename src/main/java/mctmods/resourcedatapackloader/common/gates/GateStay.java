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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

public final class GateStay {
    public static final GateStay NONE = new GateStay(0, "", Collections.emptyList(), "", true, "", "");
    private static final String NEVER = "never";
    private static final String DAY = "day";
    private static final String VISIT = "visit";
    private static final List<String> RESETS = Collections.unmodifiableList(Arrays.asList(NEVER, DAY, VISIT));
    private static final List<Integer> DEFAULT_WARN = Collections.unmodifiableList(Arrays.asList(60, 30, 10, 5, 4, 3, 2, 1));
    private static final List<String> EXPIRE_KEYS = Collections.unmodifiableList(Arrays.asList("exit", "function", "message"));
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

    public static GateStay parse(JsonObject json, ResourceLocation key, int dimension) {
        if (!json.has("stayMinutes")) { return NONE; }
        if (dimension == GateDef.NO_DIMENSION) {
            ContentLog.LOGGER.error("Gate {} sets stayMinutes without dimension, so it has no time budget", key);
            return NONE;
        }
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

    static boolean spent(EntityPlayer player, GateDef def) {
        GateStay stay = def.stay;
        if (!stay.active() || VISIT.equals(stay.reset)) { return false; }
        NBTTagCompound entry = PlayerPersisted.read(player, SECTION).getCompoundTag(def.getKey());
        if (DAY.equals(stay.reset) && entry.getLong(DAY_KEY) != today()) { return false; }
        return entry.getInteger(USED) >= stay.limit();
    }

    static void refused(EntityPlayer player, GateDef def) { Says.bar(player, CardIds.GATE_EXPIRED, TextFormatting.RED, def.stay.expired.replace("%dim%", def.name)); }

    static void tick(MinecraftServer server) {
        List<GateDef> gates = ContentGates.staying();
        if (gates.isEmpty()) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            for (GateDef def : gates) {
                if (player.dimension == def.dimension) { count(server, player, def); }
                else if (VISIT.equals(def.stay.reset)) { forget(player, def); }
            }
        }
    }

    private static void count(MinecraftServer server, EntityPlayerMP player, GateDef def) {
        GateStay stay = def.stay;
        NBTTagCompound entry = entry(player, def);
        if (DAY.equals(stay.reset) && entry.getLong(DAY_KEY) != today()) {
            entry.setInteger(USED, 0);
            entry.setLong(DAY_KEY, today());
        }
        int limit = stay.limit();
        int used = entry.getInteger(USED);
        if (used >= limit) { return; }
        used = Math.min(limit, used + STEP);
        entry.setInteger(USED, used);
        if (used >= limit) {
            expire(server, player, def);
            return;
        }
        int seconds = (limit - used) / STEP;
        if (stay.warn.contains(seconds)) {
            Says.bar(player, CardIds.GATE_STAY, TextFormatting.GOLD, stay.message.replace("%dim%", def.name).replace("%time%", String.format("%d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE)));
        }
    }

    private static void expire(MinecraftServer server, EntityPlayerMP player, GateDef def) {
        GateStay stay = def.stay;
        if (stay.exit) {
            GateEvents.sendOut(server, player, 0);
            if (player.dimension == def.dimension) { ContentLog.LOGGER.warn("Gate {} could not send {} out of dimension {}", def.getKey(), player.getName(), def.dimension); }
            else { ContentLog.LOGGER.info("Gate {} sent {} out of dimension {}, the {} minute budget is used", def.getKey(), player.getName(), def.dimension, stay.minutes); }
        }
        refused(player, def);
        if (!stay.function.isEmpty()) { Functions.runAs(player, stay.function, "Gate " + def.getKey()); }
    }

    private static NBTTagCompound entry(EntityPlayer player, GateDef def) {
        NBTTagCompound section = PlayerPersisted.section(player, SECTION);
        if (!section.hasKey(def.getKey(), Constants.NBT.TAG_COMPOUND)) { section.setTag(def.getKey(), new NBTTagCompound()); }
        return section.getCompoundTag(def.getKey());
    }

    private static void forget(EntityPlayer player, GateDef def) {
        NBTTagCompound section = PlayerPersisted.read(player, SECTION);
        if (section.getCompoundTag(def.getKey()).getInteger(USED) > 0) { section.removeTag(def.getKey()); }
    }

    private static long today() {
        World overworld = DimensionManager.getWorld(0);
        return overworld == null ? 0L : CardPlace.day(overworld);
    }
}
