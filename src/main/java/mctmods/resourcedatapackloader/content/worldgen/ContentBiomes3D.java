package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.world.Biomes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentBiomes3D {
    private static final int CELLS = 4;
    private static final int CELL_SIZE = 4;
    private static final Map<String, Object> RESOLVED = new ConcurrentHashMap<>();
    private static final Object MISSING = new Object();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static List<Band> bands = Collections.emptyList();

    private ContentBiomes3D() {}

    private static final class Band {
        final BiomeDef def;
        final Set<String> replaces;

        Band(BiomeDef def) {
            this.def = def;
            this.replaces = def.replaces.isEmpty() ? Collections.emptySet() : Settings.lower(def.replaces.toArray(new String[0]));
        }

        boolean accepts(int y, @Nullable String under) {
            if (y < def.minHeight || y > def.maxHeight) { return false; }
            return replaces.isEmpty() || (under != null && replaces.contains(under));
        }
    }

    public static void apply(ICube cube, World world) {
        List<Band> banded = banded();
        boolean caves = ContentCaveRegions.any();
        if (!caves && banded.isEmpty()) { return; }
        Biome[] cells = new Biome[CELLS * CELLS * CELLS];
        Biome[] unders = new Biome[CELLS * CELLS];
        boolean any = false;
        int top = ((IMinMaxHeight) world).rdpl$getMaxHeight() - 1;
        int baseX = cube.getX() << 4;
        int baseY = cube.getY() << 4;
        int baseZ = cube.getZ() << 4;
        Chunk column = cube.getColumn();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int cellX = 0; cellX < CELLS; cellX++) {
            for (int cellZ = 0; cellZ < CELLS; cellZ++) {
                int x = baseX + cellX * CELL_SIZE + 1;
                int z = baseZ + cellZ * CELL_SIZE + 1;
                Biome under = columnBiome(column, world, x, z, pos);
                unders[cellX * CELLS + cellZ] = under;
                String underName = banded.isEmpty() ? null : named(under);
                for (int cellY = 0; cellY < CELLS; cellY++) {
                    int y = Math.min(baseY + cellY * CELL_SIZE + 1, top);
                    Biome found = caves ? fromCave(world, x, y, z) : null;
                    if (found == null && !banded.isEmpty()) { found = fromBand(banded, y, underName); }
                    if (found == null) { continue; }
                    cells[index(cellX, cellY, cellZ)] = found;
                    any = true;
                }
            }
        }
        if (!any) { return; }
        for (int cellX = 0; cellX < CELLS; cellX++) {
            for (int cellZ = 0; cellZ < CELLS; cellZ++) {
                Biome under = unders[cellX * CELLS + cellZ];
                for (int cellY = 0; cellY < CELLS; cellY++) {
                    Biome found = cells[index(cellX, cellY, cellZ)];
                    cube.setBiome(cellX, cellY, cellZ, found == null ? under : found);
                }
            }
        }
    }

    private static int index(int cellX, int cellY, int cellZ) { return AddressTools.getBiomeAddress3d(cellX, cellY, cellZ); }

    private static Biome columnBiome(Chunk column, World world, int x, int z, BlockPos.MutableBlockPos pos) { return column.getBiome(pos.setPos(x, 0, z), world.getBiomeProvider()); }

    @Nullable private static Biome fromCave(World world, int x, int y, int z) {
        CaveRegionDef region = ContentCaveRegions.regionAt(world, x, y, z);
        return region == null || !region.hasBiome() ? null : biome(region.biome);
    }

    @Nullable private static Biome fromBand(List<Band> banded, int y, @Nullable String under) {
        for (Band band : banded) {
            if (!band.accepts(y, under)) { continue; }
            Biome made = biome(band.def.registryName.toString());
            if (made != null) { return made; }
        }
        return null;
    }

    @Nullable private static Biome biome(String named) {
        Object held = RESOLVED.get(named);
        if (held != null) { return held == MISSING ? null : (Biome) held; }
        Biome found = Biomes.byName(named);
        if (found == null) { ContentLog.LOGGER.error("A pack asks for the 3D biome '{}', which is not registered, so nothing is written there", named); }
        RESOLVED.put(named, found == null ? MISSING : found);
        return found;
    }

    public static boolean anyShapesSky() {
        for (Band band : banded()) {
            if (band.def.shapesSky()) { return true; }
        }
        return false;
    }

    @Nullable public static BiomeDef shapesSkyAt(Biome column, int y) {
        String under = null;
        for (Band band : banded()) {
            if (!band.def.shapesSky()) { continue; }
            if (!band.replaces.isEmpty() && under == null) { under = named(column); }
            if (band.accepts(y, under)) { return band.def; }
        }
        return null;
    }

    @Nullable public static Biome registered(BiomeDef def) { return biome(def.registryName.toString()); }

    @Nullable public static Biome named(String registryName) { return registryName.isEmpty() ? null : biome(registryName); }

    private static String named(Biome biome) {
        ResourceLocation named = biome.getRegistryName();
        return named == null ? "" : named.toString().toLowerCase(Locale.ROOT);
    }

    private static List<Band> banded() {
        if (!GENERATION.stale()) { return bands; }
        RESOLVED.clear();
        List<Band> made = new ArrayList<>();
        for (BiomeDef def : ContentBiomes.defs()) {
            if (def.banded) { made.add(new Band(def)); }
        }
        bands = made;
        return made;
    }
}
