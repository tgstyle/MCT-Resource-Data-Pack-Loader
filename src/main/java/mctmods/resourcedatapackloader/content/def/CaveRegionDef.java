package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record CaveRegionDef(ResourceLocation key, int weight, int minHeight, int maxHeight, List<String> dimensions, String floorCover, float floorChance, String ceilingCover,
        float ceilingChance, List<BlockMatchDef> coverReplace, List<BiomeSpawnDef> spawns, boolean keepDefaultSpawns, List<PickDef> structures, float structureChance,
        String structureLoot, @Nullable ResourceLocation biome, List<String> requires) {
    public static final int WORLD_FLOOR = Integer.MIN_VALUE;

    public boolean hasStructures() { return !structures.isEmpty(); }
}
