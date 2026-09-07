package mctmods.resourcedatapackloader.content.worldgen;

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
            if (parts != null) { touched.add(parts[0].trim() + " at most " + parts[1].trim()); }
        }
    }

    public static boolean any() { return !RAW.isEmpty(); }

    private static Map<ResourceLocation, Integer> caps(Registry<Structure> registry) {
        synchronized (RAW) {
            if (caps != null) { return caps; }
            Map<ResourceLocation, Integer> made = new LinkedHashMap<>();
            for (String entry : RAW) {
                String[] parts = ContentStructureControl.split(entry, KEY);
                if (parts == null) { continue; }
                int most;
                try { most = Integer.parseInt(parts[1].trim()); }
                catch (NumberFormatException ex) {
                    ContentLog.LOGGER.error("{} entry '{}' does not end in a number, ignoring it", KEY, entry);
                    continue;
                }
                if (most <= 0) { continue; }
                String name = parts[0].trim().toLowerCase(Locale.ROOT);
                ResourceLocation direct = name.contains(":") ? ResourceLocation.tryParse(name) : null;
                List<ResourceLocation> ids = direct != null && registry.containsKey(direct) ? List.of(direct) : ContentStructureControl.structures(name);
                for (ResourceLocation id : ids) { made.put(id, most); }
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
        Registry<Structure> registry = registries.registryOrThrow(Registries.STRUCTURE);
        ResourceLocation id = registry.getKey(structure);
        Integer most = id == null ? null : caps(registry).get(id);
        if (most == null || ContentStructureSpread.pinned(chunk)) { return false; }
        int founded = ContentStructureCounts.count(level, id);
        if (founded < most) { return false; }
        ContentLog.LOGGER.debug("Structure {} has been founded {} time(s) in {}, its structureMost, so chunk {} founds none", id, founded, level.dimension().location(), chunk);
        return true;
    }

    public static void founded(Structure structure, StructureManager manager, RegistryAccess registries, ChunkPos chunk) {
        if (RAW.isEmpty()) { return; }
        Registry<Structure> registry = registries.registryOrThrow(Registries.STRUCTURE);
        ResourceLocation id = registry.getKey(structure);
        Integer most = id == null ? null : caps(registry).get(id);
        if (most == null) { return; }
        ServerLevel level = level(manager);
        if (level == null) { return; }
        int now = ContentStructureCounts.add(level, id);
        ContentLog.LOGGER.debug("Structure {} founded at chunk {}, {} of at most {} in {}", id, chunk, now, most, level.dimension().location());
    }

    @Nullable private static ServerLevel level(StructureManager manager) {
        LevelAccessor held = ((IStructureManager) manager).rdpl$level();
        return held instanceof ServerLevelAccessor accessor ? accessor.getLevel() : null;
    }
}
