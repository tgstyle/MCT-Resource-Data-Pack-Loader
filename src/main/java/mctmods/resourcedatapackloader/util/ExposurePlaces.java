package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.ExposureBandDef;
import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.util.world.BiomeNames;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ExposurePlaces {
    private static final Map<ExposureDef, Boolean> CHECKED = new IdentityHashMap<>();

    private ExposurePlaces() {}

    public static int level(ExposureDef def, EntityPlayer player) {
        if (def.biomes.isEmpty() && def.structures.isEmpty() && def.caveRegions.isEmpty() && def.heights.isEmpty()) { return 0; }
        warnUnknown(def);
        BlockPos feet = new BlockPos(player.posX, player.posY, player.posZ);
        return Math.max(Math.max(biomeLevel(def, player, feet), structureLevel(def, player, feet)), Math.max(regionLevel(def, player, feet), heightLevel(def, feet.getY())));
    }

    private static int biomeLevel(ExposureDef def, EntityPlayer player, BlockPos feet) {
        if (def.biomes.isEmpty()) { return 0; }
        Biome biome = player.world.getBiome(feet);
        int level = 0;
        for (Map.Entry<String, Integer> entry : def.biomes.entrySet()) {
            if (entry.getValue() > level && BiomeNames.matches(Collections.singletonList(entry.getKey()), biome)) { level = entry.getValue(); }
        }
        return level;
    }

    private static int structureLevel(ExposureDef def, EntityPlayer player, BlockPos feet) {
        if (def.structures.isEmpty()) { return 0; }
        int level = 0;
        for (Map.Entry<String, Integer> entry : def.structures.entrySet()) {
            if (entry.getValue() > level && CardPlace.inStructure(player.world, feet, Collections.singletonList(entry.getKey()), def.range)) { level = entry.getValue(); }
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
            if (!name.startsWith("#") && !knownBiome(name)) { ContentLog.LOGGER.error("Exposure {} names biome {}, which is not registered, so it is ignored", def.registryName, name); }
        }
    }

    private static boolean knownBiome(String name) {
        ResourceLocation key = new ResourceLocation(name);
        if (ForgeRegistries.BIOMES.containsKey(key)) { return true; }
        for (ResourceLocation held : ForgeRegistries.BIOMES.getKeys()) {
            if (held.getPath().equals(name)) { return true; }
        }
        return false;
    }
}
