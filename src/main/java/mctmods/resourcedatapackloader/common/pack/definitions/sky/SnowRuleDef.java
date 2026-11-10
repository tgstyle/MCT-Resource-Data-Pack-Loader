package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record SnowRuleDef(int place, int freeze, boolean whenDry, boolean melt, float meltChance, Set<ResourceLocation> meltBlocks) {
    public static final int DEFAULT = 0;
    public static final int BIOME = 1;
    public static final int ANY = 2;
    public static final int NEVER = 3;
    public static final List<String> MODES = List.of("default", "biome", "any", "never");
    public static final float MELT_CHANCE = 0.0625F;
    public static final Set<ResourceLocation> MELT_BLOCKS = Set.of(ResourceLocation.parse("minecraft:snow"), ResourceLocation.parse("minecraft:ice"));
    public static final SnowRuleDef NONE = new SnowRuleDef(DEFAULT, DEFAULT, false, false, MELT_CHANCE, MELT_BLOCKS);
}
