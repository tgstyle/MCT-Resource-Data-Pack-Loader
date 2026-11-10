package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SeasonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class ContentSeasons {
    private static final String SEASONS = "seasons";
    private static final int INTERVAL = 20;
    private static final int CLOCK_INTERVAL = 200;
    private static final Map<DimensionDef, SeasonDef> BY_DIMENSION = new IdentityHashMap<>();
    private static final Map<DimensionDef, Integer> APPLIED = new IdentityHashMap<>();
    private static int ticks;

    private ContentSeasons() {}

    public static void parse(ResourceLocation key, JsonObject json, DimensionDef base) {
        if (!json.has(SEASONS)) { return; }
        String owner = "Dimension " + key + " seasons";
        if (ContentEnvironments.names(base).isEmpty()) {
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
        if (!seasons.has("order") || !seasons.get("order").isJsonArray() || seasons.getAsJsonArray("order").size() == 0) {
            ContentLog.LOGGER.error("{} needs order, a list of environment names or lists of names, ignoring it", owner);
            return;
        }
        List<List<String>> order = new ArrayList<>();
        JsonArray entries = seasons.getAsJsonArray("order");
        for (JsonElement entry : entries) {
            List<String> group = group(owner, entry, base);
            if (group == null) { return; }
            order.add(Collections.unmodifiableList(group));
        }
        BY_DIMENSION.put(base, new SeasonDef(days, Collections.unmodifiableList(order)));
    }

    @Nullable private static List<String> group(String owner, JsonElement entry, DimensionDef base) {
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
            if (!ContentEnvironments.names(base).contains(name)) {
                ContentLog.LOGGER.error("{} order names '{}', which is not an environment of the dimension, ignoring seasons", owner, name);
                return null;
            }
            if (!ContentEnvironments.local(base, name)) { whole++; }
        }
        if (whole > 1) {
            ContentLog.LOGGER.error("{} order has an entry with {} whole environments, and at most one may lack biomes, ignoring seasons", owner, whole);
            return null;
        }
        return names;
    }

    public static void tick(MinecraftServer server) {
        if (BY_DIMENSION.isEmpty()) { return; }
        ticks++;
        if (ticks % CLOCK_INTERVAL == 0) { sendClock(server); }
        if (ticks % INTERVAL != 0) { return; }
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null) { return; }
        long day = CardPlace.day(overworld);
        for (Map.Entry<DimensionDef, SeasonDef> entry : BY_DIMENSION.entrySet()) {
            DimensionDef base = entry.getKey();
            int index = entry.getValue().index(day);
            Integer applied = APPLIED.get(base);
            if (applied != null && applied == index || ContentDimensions.byId(base.id) == null) { continue; }
            APPLIED.put(base, index);
            enter(server, base, entry.getValue(), index, day);
            sendClock(server);
        }
    }

    @Nullable public static SeasonDef def(int dimension) {
        if (ContentDimensions.byId(dimension) == null) { return null; }
        for (Map.Entry<DimensionDef, SeasonDef> entry : BY_DIMENSION.entrySet()) {
            if (entry.getKey().id == dimension) { return entry.getValue(); }
        }
        return null;
    }

    @Nullable public static SeasonState state(int dimension) {
        SeasonDef def = def(dimension);
        if (def == null) { return null; }
        long ticks = SeasonClock.ticks(FMLCommonHandler.instance().getMinecraftServerInstance());
        return new SeasonState(def, def.index(ticks / DimensionTraitsDef.VANILLA_DAY), ticks);
    }

    @Nullable public static SeasonState state(@Nullable ResourceLocation dimension, int own) {
        if (dimension == null) { return state(own); }
        DimensionDef named = ContentDimensions.all().get(dimension);
        return named == null ? null : state(named.id);
    }

    public static void sendClock(EntityPlayerMP player) {
        if (!BY_DIMENSION.isEmpty()) { RDPLNetwork.sendSeasonClock(player, SeasonClock.ticks(player.getServer())); }
    }

    private static void sendClock(MinecraftServer server) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { sendClock(player); }
    }

    private static void enter(MinecraftServer server, DimensionDef base, SeasonDef seasons, int index, long day) {
        List<String> target = seasons.order.get(index);
        List<String> before = ContentEnvironments.active(base.id);
        for (String name : before.subList(1, before.size())) {
            if (seasons.seasonal.contains(name) && !target.contains(name)) { ContentEnvironments.change(server, base.id, name, false); }
        }
        String whole = null;
        for (String name : target) {
            if (!ContentEnvironments.local(base, name)) { whole = name; }
        }
        String current = before.get(0);
        if (whole != null && !whole.equals(current)) { ContentEnvironments.change(server, base.id, whole, true); }
        else if (whole == null && seasons.seasonal.contains(current)) { ContentEnvironments.change(server, base.id, ContentEnvironments.DEFAULT, true); }
        for (String name : target) {
            if (ContentEnvironments.local(base, name) && !ContentEnvironments.active(base.id).contains(name)) { ContentEnvironments.change(server, base.id, name, true); }
        }
        if (!before.equals(ContentEnvironments.active(base.id))) {
            ContentEnvironments.announce(server, base.id, before);
            ContentLog.LOGGER.info("Dimension {} enters the season '{}' on day {}", base.registryName, target.get(0), day);
        }
    }

    public static void forget() {
        APPLIED.clear();
        ticks = 0;
    }
}
