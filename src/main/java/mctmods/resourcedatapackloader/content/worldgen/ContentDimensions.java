package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
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

    private ContentDimensions() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.load || !Config.content.dimensions) { return; }
        PackManager.get().forEach(PackManager.DIMENSIONS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                DimensionDef def = ContentParser.dimension(key, contents);
                if (def != null) { DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in dimension definition {}, ignoring it: {}", key, ex.getMessage()); }
        });
        List<String> registered = new ArrayList<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : DEFS.entrySet()) {
            DimensionDef def = entry.getValue();
            if (!ContentRegistry.available(def.requires, entry.getKey())) { continue; }
            if (DimensionManager.isDimensionRegistered(def.id)) {
                ContentLog.LOGGER.error("Dimension {} wants id {}, which is already registered by something else. Change the id or remove the conflicting mod", entry.getKey(), def.id);
                continue;
            }
            DimensionType type = DimensionType.register(def.getName(), def.suffix, def.id, ContentWorldProvider.class, def.keepLoaded);
            TYPES.put(def.id, type);
            DimensionManager.registerDimension(def.id, type);
            BY_ID.put(def.id, def);
            registered.add(entry.getKey() + " as " + def.id);
        }
        if (!registered.isEmpty()) { Summary.info("dimensions", "Registered " + registered.size() + " dimension(s): " + registered); }
    }

    @Nullable public static DimensionDef byId(int dimension) { return BY_ID.get(dimension); }

    public static DimensionType typeFor(int dimension) {
        DimensionType type = TYPES.get(dimension);
        return type == null ? DimensionType.OVERWORLD : type;
    }

    public static Map<ResourceLocation, DimensionDef> all() { return Collections.unmodifiableMap(DEFS); }
}
