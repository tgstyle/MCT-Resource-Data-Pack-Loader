package mctmods.resourcedatapackloader.common.util.dimension;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureBandDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public final class ExposurePlaces {
    private static final Map<ExposureDef, Boolean> CHECKED = new IdentityHashMap<>();

    private ExposurePlaces() {}

    public static int level(ExposureDef def, EntityPlayer player) {
        if (def.biomes.isEmpty() && def.environments.isEmpty() && def.structures.isEmpty() && def.caveRegions.isEmpty() && def.heights.isEmpty()) { return 0; }
        warnUnknown(def);
        BlockPos feet = new BlockPos(player.posX, player.posY, player.posZ);
        return Math.max(Math.max(Math.max(biomeLevel(def, player, feet), environmentLevel(def, player)), structureLevel(def, player, feet)), Math.max(regionLevel(def, player, feet), heightLevel(def, feet.getY())));
    }

    private static int environmentLevel(ExposureDef def, EntityPlayer player) {
        int level = 0;
        for (String name : ContentEnvironments.active(player.dimension)) { level = Math.max(level, def.environments.getOrDefault(name, 0)); }
        return level;
    }

    private static int biomeLevel(ExposureDef def, EntityPlayer player, BlockPos feet) {
        if (def.biomes.isEmpty()) { return 0; }
        Biome biome = player.world.getBiome(feet);
        return best(def.biomes, name -> BiomeNames.matches(Collections.singletonList(name), biome));
    }

    private static int structureLevel(ExposureDef def, EntityPlayer player, BlockPos feet) {
        if (def.structures.isEmpty()) { return 0; }
        return best(def.structures, name -> CardPlace.inStructure(player.world, feet, Collections.singletonList(name), def.range));
    }

    private static int best(Map<String, Integer> levels, Predicate<String> match) {
        int level = 0;
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            if (entry.getValue() > level && match.test(entry.getKey())) { level = entry.getValue(); }
        }
        return level;
    }

    private static int regionLevel(ExposureDef def, EntityPlayer player, BlockPos feet) {
        if (def.caveRegions.isEmpty()) { return 0; }
        CaveRegionDef region = ContentCaveRegions.regionAt(player.world, feet.getX(), feet.getY(), feet.getZ());
        return region == null ? 0 : def.caveRegions.getOrDefault(region.key, 0);
    }

    private static int heightLevel(ExposureDef def, int y) {
        int level = 0;
        for (ExposureBandDef band : def.heights) {
            if (band.level > level && y >= band.from && y <= band.to) { level = band.level; }
        }
        return level;
    }

    private static void warnUnknown(ExposureDef def) {
        if (CHECKED.put(def, Boolean.TRUE) != null) { return; }
        for (String name : def.biomes.keySet()) {
            if (!IdNames.isTag(name) && !BiomeNames.known(name)) { ContentLog.LOGGER.error("Exposure {} names biome {}, which is not registered, so it is ignored", def.registryName, name); }
        }
    }
}
