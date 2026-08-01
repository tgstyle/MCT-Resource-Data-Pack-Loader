package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ContentVillagePlacement {
    private static final List<Biome> VANILLA = new ArrayList<>(MapGenVillage.VILLAGE_SPAWN_BIOMES);
    private static boolean applied;

    private ContentVillagePlacement() {}

    public static int spacing(int fallback) {
        if (ContentControl.off(ContentControl.VILLAGES)) { return fallback; }

        int wanted = ContentControl.number(ContentControl.VILLAGES, "villageSpacing", Config.worldgen.villageSpacing);
        if (wanted <= 0) { return fallback; }

        return Math.max(9, wanted);
    }

    public static boolean farEnoughFromSpawn(World world, int chunkX, int chunkZ) {
        if (world == null || ContentControl.off(ContentControl.VILLAGES)) { return true; }

        int least = ContentControl.number(ContentControl.VILLAGES, "villageMinDistanceFromSpawn", Config.worldgen.villageMinDistanceFromSpawn);
        if (least <= 0) { return true; }

        BlockPos spawn = world.getSpawnPoint();
        double dx = (chunkX * 16 + 8) - spawn.getX();
        double dz = (chunkZ * 16 + 8) - spawn.getZ();
        return dx * dx + dz * dz >= (double) least * least;
    }

    public static void applyBiomes() {
        if (applied || ContentControl.off(ContentControl.VILLAGES)) { return; }
        applied = true;

        Set<String> names = Names.lower(ContentControl.list(ContentControl.VILLAGES, "villageBiomes", Config.worldgen.villageBiomes));
        Set<String> types = Names.lower(ContentControl.list(ContentControl.VILLAGES, "villageBiomeTypes", Config.worldgen.villageBiomeTypes));
        if (names.isEmpty() && types.isEmpty()) { return; }

        boolean blacklist = ContentControl.flag(ContentControl.VILLAGES, "villageBiomesAreBlacklist", Config.worldgen.villageBiomesAreBlacklist);
        List<Biome> wanted = new ArrayList<>();
        for (Biome biome : blacklist ? VANILLA : all()) {
            if (matches(biome, names, types) != blacklist) { wanted.add(biome); }
        }

        if (wanted.isEmpty()) {
            ContentLog.LOGGER.warn("Village biome settings leave no biome at all, so villages would never generate. Leaving the vanilla list alone");
            return;
        }

        MapGenVillage.VILLAGE_SPAWN_BIOMES = wanted;
        Summary.info("villages.biomes", "Villages generate in " + wanted.size() + " biome(s)");
    }

    public static void reload() { applied = false; }

    private static List<Biome> all() {
        List<Biome> found = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES) { found.add(biome); }
        return found;
    }

    private static boolean matches(Biome biome, Set<String> names, Set<String> types) {
        ResourceLocation name = biome.getRegistryName();
        if (name != null && names.contains(name.toString().toLowerCase(Locale.ROOT))) { return true; }
        if (names.contains(biome.getBiomeName().toLowerCase(Locale.ROOT))) { return true; }

        for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
            if (types.contains(type.getName().toLowerCase(Locale.ROOT))) { return true; }
        }
        return false;
    }
}
