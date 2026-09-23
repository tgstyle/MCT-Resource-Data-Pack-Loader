package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentStructureMost {
    private static final String KEY = "structureMost";
    private static final String VILLAGES = "villages";
    private static final ResourceLocation CITY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE);
    private static final List<String> RAW = new ArrayList<>();
    @Nullable private static Map<ResourceLocation, Integer> caps;
    private static final Set<ResourceLocation> SILENCED = ConcurrentHashMap.newKeySet();

    private ContentStructureMost() {}

    static void load(List<String> entries, List<String> touched) {
        synchronized (RAW) {
            RAW.clear();
            RAW.addAll(entries);
            caps = null;
            SILENCED.clear();
        }
        for (String entry : entries) {
            String[] parts = ContentStructureControl.split(entry, KEY);
            if (parts != null && villages(parts[0])) { touched.add(parts[0].trim() + " at most " + parts[1].trim()); }
        }
    }

    private static boolean villages(String name) {
        String named = name.trim().toLowerCase(Locale.ROOT);
        return VILLAGES.equals(named) || ContentCity.STRUCTURE.equals(named);
    }

    private static Map<ResourceLocation, Integer> caps() {
        synchronized (RAW) {
            if (caps != null) { return caps; }
            Map<ResourceLocation, Integer> made = new LinkedHashMap<>();
            for (String entry : RAW) {
                String[] parts = ContentStructureControl.split(entry, KEY);
                if (parts == null || !VILLAGES.equals(parts[0].trim().toLowerCase(Locale.ROOT))) { continue; }
                int most;
                try { most = Integer.parseInt(parts[1].trim()); }
                catch (NumberFormatException ex) {
                    ContentLog.LOGGER.error("{} entry '{}' does not end in a number, ignoring it", KEY, entry);
                    continue;
                }
                if (most <= 0) { continue; }
                for (ResourceLocation id : ContentStructureControl.structures(VILLAGES)) { made.put(id, most); }
            }
            caps = made;
            return made;
        }
    }

    public static boolean refuses(Structure structure, StructureManager manager, RegistryAccess registries, ChunkPos chunk) {
        if (RAW.isEmpty() && !ContentDimensions.anyStructuresOff()) { return false; }
        ServerLevel level = level(manager);
        if (level == null) { return false; }
        if (ContentDimensions.structuresOff(level)) {
            if (SILENCED.add(level.dimension().location())) { ContentLog.LOGGER.debug("Dimension {} allows no structures, so none is founded there (first refused at chunk {})", level.dimension().location(), chunk); }
            return true;
        }
        if (RAW.isEmpty()) { return false; }
        if (structure instanceof ContentCityStructure) { return cityRefused(level, chunk); }
        Registry<Structure> registry = registries.registryOrThrow(Registries.STRUCTURE);
        ResourceLocation id = registry.getKey(structure);
        Integer most = id == null ? null : caps().get(id);
        if (most == null || ContentStructureSpread.pinned(chunk)) { return false; }
        int founded = villagesFounded(level);
        if (founded < most) { return false; }
        ContentLog.LOGGER.debug("Villages and cities have been founded {} time(s) in {}, their structureMost, so chunk {} founds no {}", founded, level.dimension().location(), chunk, id);
        return true;
    }

    private static int cityMost() {
        synchronized (RAW) {
            for (String entry : RAW) {
                String[] parts = ContentStructureControl.split(entry, KEY);
                if (parts == null || !villages(parts[0])) { continue; }
                try { return Integer.parseInt(parts[1].trim()); }
                catch (NumberFormatException ex) { return 0; }
            }
            return 0;
        }
    }

    private static int villagesFounded(ServerLevel level) { return ContentStructureCounts.total(level, caps().keySet()) + ContentStructureCounts.count(level, CITY); }

    @Nullable private static ResourceLocation cityMarker(ServerLevel level, ChunkPos chunk) {
        int[] center = CityPlanTowns.cityCenter(CityGround.of(level), CityPlan.districtOf(chunk.getMinBlockX(), true), CityPlan.districtOf(chunk.getMinBlockZ(), false));
        return center == null ? null : ResourceLocation.fromNamespaceAndPath(CITY.getNamespace(), CITY.getPath() + "/" + center[0] + "_" + center[1]);
    }

    private static boolean cityRefused(ServerLevel level, ChunkPos chunk) {
        if (mayFound(level, chunk)) { return false; }
        ContentLog.LOGGER.debug("{} villages and cities have been founded in {}, the most structureMost allows, so the city at chunk {} is not", villagesFounded(level), level.dimension().location(), chunk);
        return true;
    }

    public static boolean mayFound(ServerLevel level, ChunkPos chunk) {
        int most = cityMost();
        if (most <= 0 || ContentStructureSpread.pinned(chunk)) { return true; }
        ResourceLocation marker = cityMarker(level, chunk);
        if (marker == null || ContentStructureCounts.count(level, marker) > 0) { return true; }
        return villagesFounded(level) < most;
    }

    public static void founded(Structure structure, StructureManager manager, RegistryAccess registries, ChunkPos chunk) {
        if (RAW.isEmpty()) { return; }
        if (structure instanceof ContentCityStructure) {
            ServerLevel level = level(manager);
            ResourceLocation marker = level == null || cityMost() <= 0 ? null : cityMarker(level, chunk);
            if (marker != null && ContentStructureCounts.count(level, marker) == 0) {
                ContentStructureCounts.add(level, marker);
                ContentStructureCounts.add(level, CITY);
            }
            return;
        }
        Registry<Structure> registry = registries.registryOrThrow(Registries.STRUCTURE);
        ResourceLocation id = registry.getKey(structure);
        Integer most = id == null ? null : caps().get(id);
        if (most == null) { return; }
        ServerLevel level = level(manager);
        if (level == null) { return; }
        ContentStructureCounts.add(level, id);
        ContentLog.LOGGER.debug("Structure {} founded at chunk {}, village {} of at most {} in {}", id, chunk, villagesFounded(level), most, level.dimension().location());
    }

    @Nullable private static ServerLevel level(StructureManager manager) {
        LevelAccessor held = ((IStructureManager) manager).rdpl$level();
        return held instanceof ServerLevelAccessor accessor ? accessor.getLevel() : null;
    }
}
