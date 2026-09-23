package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record CaveRegionDef(ResourceLocation key, int weight, int minHeight, int maxHeight, List<String> dimensions, @Nullable BlockMatchDef floorCover, float floorChance, @Nullable BlockMatchDef ceilingCover,
        float ceilingChance, List<BlockMatchDef> coverReplace, List<BiomeSpawnDef> spawns, boolean keepDefaultSpawns, List<PickDef> structures, float structureChance,
        String structureLoot, @Nullable ResourceLocation biome, List<String> requires, String ambientSound, float soundChance, String particle, float particleChance, int waterLevel) {
    public static final int WORLD_FLOOR = Integer.MIN_VALUE;
    public static final int NO_WATER = Integer.MIN_VALUE;

    public boolean hasWater() { return waterLevel != NO_WATER; }

    public boolean hasStructures() { return !structures.isEmpty(); }

    public boolean hasAmbience() { return !ambientSound.isEmpty() && soundChance > 0.0F || !particle.isEmpty() && particleChance > 0.0F; }
}
