package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentBiomes3D {
    private static final int CELLS = 4;
    private static final int CELL_SIZE = 4;
    private static final Map<String, Object> RESOLVED = new ConcurrentHashMap<>();
    private static final Object MISSING = new Object();
    private static List<BiomeDef> bands = null;

    private ContentBiomes3D() {}

    public static void apply(ICube cube, World world) {
        List<BiomeDef> banded = banded();
        boolean caves = ContentCaveRegions.any();
        if (!caves && banded.isEmpty()) { return; }
        Biome[] cells = new Biome[CELLS * CELLS * CELLS];
        boolean any = false;
        int baseX = cube.getX() << 4;
        int baseY = cube.getY() << 4;
        int baseZ = cube.getZ() << 4;
        Chunk column = cube.getColumn();
        for (int cellX = 0; cellX < CELLS; cellX++) {
            for (int cellY = 0; cellY < CELLS; cellY++) {
                for (int cellZ = 0; cellZ < CELLS; cellZ++) {
                    int x = baseX + cellX * CELL_SIZE + 1;
                    int y = baseY + cellY * CELL_SIZE + 1;
                    int z = baseZ + cellZ * CELL_SIZE + 1;
                    Biome found = caves ? fromCave(world, x, y, z) : null;
                    if (found == null && !banded.isEmpty()) { found = fromBand(banded, column, world, x, y, z); }
                    if (found == null) { continue; }
                    cells[index(cellX, cellY, cellZ)] = found;
                    any = true;
                }
            }
        }
        if (!any) { return; }
        for (int cellX = 0; cellX < CELLS; cellX++) {
            for (int cellZ = 0; cellZ < CELLS; cellZ++) {
                Biome under = columnBiome(column, world, baseX + cellX * CELL_SIZE + 1, baseZ + cellZ * CELL_SIZE + 1);
                for (int cellY = 0; cellY < CELLS; cellY++) {
                    Biome found = cells[index(cellX, cellY, cellZ)];
                    cube.setBiome(cellX, cellY, cellZ, found == null ? under : found);
                }
            }
        }
    }

    private static int index(int cellX, int cellY, int cellZ) { return cellX << 4 | cellY << 2 | cellZ; }

    private static Biome columnBiome(Chunk column, World world, int x, int z) { return column.getBiome(new BlockPos(x, 0, z), world.getBiomeProvider()); }

    @Nullable private static Biome fromCave(World world, int x, int y, int z) {
        CaveRegionDef region = ContentCaveRegions.regionAt(world, x, y, z);
        return region == null || !region.hasBiome() ? null : biome(region.biome);
    }

    @Nullable private static Biome fromBand(List<BiomeDef> banded, Chunk column, World world, int x, int y, int z) {
        String under = null;
        for (BiomeDef def : banded) {
            if (y < def.minHeight || y > def.maxHeight) { continue; }
            if (!def.replaces.isEmpty()) {
                if (under == null) { under = name(column, world, x, z); }
                if (!matches(def.replaces, under)) { continue; }
            }
            Biome made = biome(def.registryName.toString());
            if (made != null) { return made; }
        }
        return null;
    }

    private static String name(Chunk column, World world, int x, int z) {
        ResourceLocation named = columnBiome(column, world, x, z).getRegistryName();
        return named == null ? "" : named.toString().toLowerCase(Locale.ROOT);
    }

    private static boolean matches(List<String> wanted, String under) {
        for (String one : wanted) {
            if (one.trim().toLowerCase(Locale.ROOT).equals(under)) { return true; }
        }
        return false;
    }

    @Nullable private static Biome biome(String named) {
        Object held = RESOLVED.get(named);
        if (held != null) { return held == MISSING ? null : (Biome) held; }
        Biome found = Biome.REGISTRY.getObject(new ResourceLocation(named.trim()));
        if (found == null) { ContentLog.LOGGER.error("A pack asks for the 3D biome '{}', which is not registered, so nothing is written there", named); }
        RESOLVED.put(named, found == null ? MISSING : found);
        return found;
    }

    private static List<BiomeDef> banded() {
        List<BiomeDef> held = bands;
        if (held != null) { return held; }
        List<BiomeDef> made = new ArrayList<>();
        for (BiomeDef def : ContentBiomes.defs()) {
            if (def.banded) { made.add(def); }
        }
        bands = made;
        return made;
    }
}
