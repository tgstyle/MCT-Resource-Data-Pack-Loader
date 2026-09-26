package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParserWorlds;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.Json;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentDimensions {
    private static final Map<ResourceLocation, DimensionDef> DEFS = new LinkedHashMap<>();
    private static final Map<Integer, DimensionDef> BY_ID = new LinkedHashMap<>();
    private static final Map<Integer, DimensionType> TYPES = new LinkedHashMap<>();
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
        List<String> registered = new ArrayList<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : DEFS.entrySet()) {
            DimensionDef def = entry.getValue();
            if (!ContentRegistry.available(def.requires, entry.getKey())) { continue; }
            if (def.galacticraft == null) {
                if (register(def, ContentWorldProvider.class)) { registered.add(entry.getKey() + " as " + def.id); }
            }
            else if (ContentCelestial.orbits(def)) { ContentCelestial.defer(def); }
            else { ContentLog.LOGGER.error("Dimension {} is a Galacticraft body and Galacticraft is not loaded, so it is not registered", entry.getKey()); }
        }
        if (!registered.isEmpty()) { Summary.info("dimensions", "Registered " + registered.size() + " dimension(s): " + registered); }
    }

    public static boolean register(DimensionDef def, Class<? extends WorldProvider> provider) {
        if (DimensionManager.isDimensionRegistered(def.id)) {
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

    @Nullable public static DimensionDef byId(int dimension) { return BY_ID.get(dimension); }

    public static DimensionType typeFor(int dimension) {
        DimensionType type = TYPES.get(dimension);
        return type == null ? DimensionType.OVERWORLD : type;
    }

    public static Map<ResourceLocation, DimensionDef> all() {
        parse();
        return Collections.unmodifiableMap(DEFS);
    }
}
