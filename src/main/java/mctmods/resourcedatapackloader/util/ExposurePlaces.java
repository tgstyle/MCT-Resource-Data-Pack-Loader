package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.ExposureBandDef;
import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ExposurePlaces {
    private static final Map<ExposureDef, Boolean> CHECKED = new IdentityHashMap<>();

    private ExposurePlaces() {}

    public static int level(ExposureDef def, ServerPlayer player) {
        if (def.biomes().isEmpty() && def.structures().isEmpty() && def.caveRegions().isEmpty() && def.heights().isEmpty()) { return 0; }
        ServerLevel level = player.serverLevel();
        BlockPos feet = player.blockPosition();
        warnUnknown(def, level);
        return Math.max(Math.max(biomeLevel(def, level, feet), structureLevel(def, level, feet)), Math.max(regionLevel(def, level, feet), heightLevel(def, feet.getY())));
    }

    private static int biomeLevel(ExposureDef def, ServerLevel level, BlockPos feet) {
        if (def.biomes().isEmpty()) { return 0; }
        Holder<Biome> biome = level.getBiome(feet);
        int reached = 0;
        for (Map.Entry<String, Integer> entry : def.biomes().entrySet()) {
            if (entry.getValue() > reached && BiomeNames.matches(List.of(entry.getKey()), biome)) { reached = entry.getValue(); }
        }
        return reached;
    }

    private static int structureLevel(ExposureDef def, ServerLevel level, BlockPos feet) {
        if (def.structures().isEmpty()) { return 0; }
        int reached = 0;
        for (Map.Entry<String, Integer> entry : def.structures().entrySet()) {
            if (entry.getValue() > reached && CardPlace.inStructure(level, feet, List.of(entry.getKey()), def.range())) { reached = entry.getValue(); }
        }
        return reached;
    }

    private static int regionLevel(ExposureDef def, ServerLevel level, BlockPos feet) {
        if (def.caveRegions().isEmpty()) { return 0; }
        CaveRegionDef region = ContentCaveRegions.regionAt(level, feet);
        return region == null ? 0 : def.caveRegions().getOrDefault(region.key(), 0);
    }

    private static int heightLevel(ExposureDef def, int y) {
        int reached = 0;
        for (ExposureBandDef band : def.heights()) {
            if (band.level() > reached && y >= band.from() && y <= band.to()) { reached = band.level(); }
        }
        return reached;
    }

    private static void warnUnknown(ExposureDef def, ServerLevel level) {
        if (CHECKED.put(def, Boolean.TRUE) != null) { return; }
        for (String name : def.biomes().keySet()) {
            if (!name.startsWith("#") && !knownBiome(level, name)) { ContentLog.LOGGER.error("Exposure {} names biome {}, which is not registered, so it is ignored", def.key(), name); }
        }
    }

    private static boolean knownBiome(ServerLevel level, String name) {
        for (ResourceLocation held : level.registryAccess().registryOrThrow(Registries.BIOME).keySet()) {
            if (held.toString().equals(name) || held.getPath().equals(name)) { return true; }
        }
        return false;
    }
}
