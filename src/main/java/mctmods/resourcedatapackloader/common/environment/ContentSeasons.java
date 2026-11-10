package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SeasonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ContentSeasons {
    private static final String SEASONS = "seasons";
    private static final int INTERVAL = 20;
    private static final int CLOCK_INTERVAL = 200;
    private static final Map<ResourceLocation, SeasonDef> BY_DIMENSION = new HashMap<>();
    private static final Map<ResourceLocation, Integer> APPLIED = new HashMap<>();
    private static int ticks;

    private ContentSeasons() {}

    public static void parse(ResourceLocation key, JsonObject json) {
        if (!json.has(SEASONS)) { return; }
        String owner = "Dimension " + key + " seasons";
        if (ContentEnvironments.names(key).isEmpty()) {
            ContentLog.LOGGER.error("{} needs environments in the same dimension file, ignoring it", owner);
            return;
        }
        if (!json.get(SEASONS).isJsonObject()) {
            ContentLog.LOGGER.error("{} is not an object of days and order, ignoring it", owner);
            return;
        }
        JsonObject seasons = json.getAsJsonObject(SEASONS);
        int days = Json.getInt(seasons, "days", 0);
        if (days <= 0) {
            ContentLog.LOGGER.error("{} gives days {}, which is not a whole number above 0, ignoring it", owner, days);
            return;
        }
        if (!seasons.has("order") || !seasons.get("order").isJsonArray() || seasons.getAsJsonArray("order").isEmpty()) {
            ContentLog.LOGGER.error("{} needs order, a list of environment names or lists of names, ignoring it", owner);
            return;
        }
        List<List<String>> order = new ArrayList<>();
        for (JsonElement entry : seasons.getAsJsonArray("order")) {
            List<String> group = group(owner, key, entry);
            if (group == null) { return; }
            order.add(List.copyOf(group));
        }
        BY_DIMENSION.put(key, new SeasonDef(days, List.copyOf(order)));
    }

    @Nullable private static List<String> group(String owner, ResourceLocation key, JsonElement entry) {
        List<String> names = new ArrayList<>();
        if (entry.isJsonArray()) {
            for (JsonElement element : entry.getAsJsonArray()) {
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                    ContentLog.LOGGER.error("{} order has an entry that is not an environment name, ignoring seasons", owner);
                    return null;
                }
                names.add(Settings.lower(element.getAsString()));
            }
        }
        else if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) { names.add(Settings.lower(entry.getAsString())); }
        else {
            ContentLog.LOGGER.error("{} order has an entry that is not an environment name or a list of names, ignoring seasons", owner);
            return null;
        }
        if (names.isEmpty()) {
            ContentLog.LOGGER.error("{} order has an empty list, ignoring seasons", owner);
            return null;
        }
        int whole = 0;
        for (String name : names) {
            if (!ContentEnvironments.names(key).contains(name)) {
                ContentLog.LOGGER.error("{} order names '{}', which is not an environment of the dimension, ignoring seasons", owner, name);
                return null;
            }
            if (!ContentEnvironments.local(key, name)) { whole++; }
        }
        if (whole > 1) {
            ContentLog.LOGGER.error("{} order has an entry with {} whole environments, and at most one may lack biomes, ignoring seasons", owner, whole);
            return null;
        }
        return names;
    }

    public static void onServerTick(ServerTickEvent.Post event) { tick(event.getServer()); }

    private static void tick(MinecraftServer server) {
        if (BY_DIMENSION.isEmpty()) { return; }
        ticks++;
        if (ticks % CLOCK_INTERVAL == 0) { sendClock(server); }
        if (ticks % INTERVAL != 0) { return; }
        long day = CardPlace.day(server.overworld());
        for (Map.Entry<ResourceLocation, SeasonDef> entry : BY_DIMENSION.entrySet()) {
            int index = entry.getValue().index(day);
            Integer applied = APPLIED.get(entry.getKey());
            if (applied != null && applied == index) { continue; }
            APPLIED.put(entry.getKey(), index);
            enter(server, entry.getKey(), entry.getValue(), index, day);
            sendClock(server);
        }
    }

    @Nullable public static SeasonState state(ResourceLocation dimension) {
        SeasonDef def = BY_DIMENSION.get(dimension);
        if (def == null) { return null; }
        long clock = SeasonClock.ticks();
        return new SeasonState(def, def.index(clock / DimensionTraitsDef.VANILLA_DAY), clock);
    }

    @Nullable public static SeasonState state(@Nullable ResourceLocation dimension, ResourceLocation own) { return state(dimension == null ? own : dimension); }

    public static void sendClock(ServerPlayer player) {
        if (!BY_DIMENSION.isEmpty()) { RDPLNetwork.sendSeasonClock(player, SeasonClock.ticks(player.server)); }
    }

    private static void sendClock(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { sendClock(player); }
    }

    private static void enter(MinecraftServer server, ResourceLocation key, SeasonDef seasons, int index, long day) {
        List<String> target = seasons.order().get(index);
        List<String> seasonal = seasons.seasonal();
        List<String> before = ContentEnvironments.active(key);
        for (String name : before.subList(1, before.size())) {
            if (seasonal.contains(name) && !target.contains(name)) { ContentEnvironments.change(server, key, name, false); }
        }
        String whole = null;
        for (String name : target) {
            if (!ContentEnvironments.local(key, name)) { whole = name; }
        }
        String current = before.getFirst();
        if (whole != null && !whole.equals(current)) { ContentEnvironments.change(server, key, whole, true); }
        else if (whole == null && seasonal.contains(current)) { ContentEnvironments.change(server, key, ContentEnvironments.DEFAULT, true); }
        for (String name : target) {
            if (ContentEnvironments.local(key, name) && !ContentEnvironments.active(key).contains(name)) { ContentEnvironments.change(server, key, name, true); }
        }
        if (!before.equals(ContentEnvironments.active(key))) {
            ContentEnvironments.announce(server, key, before);
            ContentLog.LOGGER.info("Dimension {} enters the season '{}' on day {}", key, target.getFirst(), day);
        }
    }

    public static void forget() {
        APPLIED.clear();
        ticks = 0;
    }
}
