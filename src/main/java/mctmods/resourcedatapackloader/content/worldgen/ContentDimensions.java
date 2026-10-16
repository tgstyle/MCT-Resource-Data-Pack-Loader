package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParserWorlds;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.Json;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentDimensions {
    private static final Map<ResourceLocation, DimensionDef> DEFS = new LinkedHashMap<>();
    private static final Map<Integer, DimensionDef> BY_ID = new LinkedHashMap<>();
    private static final Map<Integer, DimensionType> TYPES = new LinkedHashMap<>();
    private static final Map<Integer, DimensionDef> OVERLAYS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, DimensionDef> PENDING = new LinkedHashMap<>();
    private static final Set<Integer> STATION_TYPES = new HashSet<>();
    private static boolean loaded;
    private static boolean parsed;

    private ContentDimensions() {}

    public static void parse() {
        if (parsed) { return; }
        parsed = true;
        if (!Config.registersToClients() || !Config.content.dimensions) { return; }
        Json.eachFile(PackManager.DIMENSIONS, "dimension definition", (key, contents) -> {
            DimensionDef def = ContentParserWorlds.dimension(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
    }

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        parse();
        if (!Config.registersToClients() || !Config.content.dimensions) { return; }
        for (Map.Entry<ResourceLocation, DimensionDef> entry : DEFS.entrySet()) {
            DimensionDef def = entry.getValue();
            if (!ContentRegistry.available(def.requires, entry.getKey())) { continue; }
            if (def.galacticraft == null) { PENDING.put(entry.getKey(), def); }
            else if (ContentCelestial.orbits(def)) { ContentCelestial.defer(def); }
            else { ContentLog.LOGGER.error("Dimension {} is a Galacticraft body and Galacticraft is not loaded, so it is not registered", entry.getKey()); }
        }
        if (!ContentCelestial.present()) { settle(); }
    }

    public static void settle() {
        List<String> registered = new ArrayList<>();
        List<String> overlaid = new ArrayList<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : PENDING.entrySet()) {
            DimensionDef def = entry.getValue();
            if (!BY_ID.containsKey(def.id) && exists(def.id)) {
                claimExisting(def);
                overlaid.add(entry.getKey() + " on " + def.id);
            }
            else if (register(def, ContentWorldProvider.class)) { registered.add(entry.getKey() + " as " + def.id); }
        }
        PENDING.clear();
        if (!registered.isEmpty()) { Summary.info("dimensions", "Registered " + registered.size() + " dimension(s): " + registered); }
        if (!overlaid.isEmpty()) { Summary.info("dimensions.existing", "Applied " + overlaid.size() + " dimension file(s) to existing dimensions: " + overlaid); }
    }

    private static boolean exists(int dimension) { return DimensionManager.isDimensionRegistered(dimension) || ContentCelestial.holds(dimension); }

    private static void claimExisting(DimensionDef def) {
        BY_ID.put(def.id, def);
        OVERLAYS.put(def.id, def);
        if (ContentCelestial.stationType(def.id)) { STATION_TYPES.add(def.id); }
        if (def.gives("keepLoaded")) { DimensionManager.keepDimensionLoaded(def.id, def.keepLoaded); }
        if (def.gives("terrain") && !def.gives("terrain.type")) { ContentLog.LOGGER.error("Dimension {} sets terrain options for dimension {}, which already exists, without a terrain type, so it keeps its own generator", def.registryName, def.id); }
        if (def.gives("suffix")) { ContentLog.LOGGER.error("Dimension {} names suffix '{}' for dimension {}, which already exists, so its save folder stays as it is", def.registryName, def.suffix, def.id); }
    }

    @Nullable public static DimensionDef overlay(int dimension) { return held(OVERLAYS, dimension); }

    @Nullable public static DimensionDef overlay(World world) { return overlay(world.provider.getDimension()); }

    @Nullable public static DimensionDef of(@Nullable World world) { return world == null ? null : byId(world.provider.getDimension()); }

    @Nullable public static RainDef rain(World world) {
        DimensionDef def = of(world);
        return def == null ? null : def.traits.rain;
    }

    @Nullable private static DimensionDef held(Map<Integer, DimensionDef> defs, int dimension) {
        DimensionDef def = defs.get(dimension);
        if (def != null || STATION_TYPES.isEmpty() || !DimensionManager.isDimensionRegistered(dimension)) { return def; }
        int type = DimensionManager.getProviderType(dimension).getId();
        return STATION_TYPES.contains(type) ? defs.get(type) : null;
    }

    public static boolean register(DimensionDef def, Class<? extends WorldProvider> provider) {
        if (exists(def.id)) {
            ContentLog.LOGGER.error("Dimension {} wants id {}, which is already registered by something else. Change the id or remove the conflicting mod", def.registryName, def.id);
            return false;
        }
        DimensionType type = DimensionType.register(def.getName(), def.suffix, def.id, provider, def.keepLoaded);
        TYPES.put(def.id, type);
        DimensionManager.registerDimension(def.id, type);
        BY_ID.put(def.id, def);
        return true;
    }

    public static void claim(DimensionDef def) { BY_ID.put(def.id, def); }

    @Nullable public static DimensionDef byId(int dimension) { return held(BY_ID, dimension); }

    public static DimensionType typeFor(int dimension) {
        DimensionType type = TYPES.get(dimension);
        return type == null ? DimensionType.OVERWORLD : type;
    }

    public static Map<ResourceLocation, DimensionDef> all() {
        parse();
        return Collections.unmodifiableMap(DEFS);
    }
}
