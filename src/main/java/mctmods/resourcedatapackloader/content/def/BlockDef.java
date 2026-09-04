package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record BlockDef(ResourceLocation key, String type, String material, String mapColor, String soundType, String creativeTab, String harvestTool, int harvestToolLevel, boolean silkHarvest,
                       int expDropMin, int expDropMax, float explosionResistanceDivisor, List<BlockVariant> variants, List<String> requires, String renderLayer, boolean opaque, boolean fullCube,
                       float slipperiness, @Nullable double[] bounds, int flammability, int fireSpread, String modelBlock, boolean itemModelFromFile, String torchParticle, boolean torchSmoke, int torchColor,
                       String cropSeed, String cropProduce, int cropMaxAge, @Nullable SaplingDef sapling, @Nullable GrowthDef growth, List<String> plantTypes, List<String> behavesAs, String tint,
                       String leafSapling, int leafSaplingChance, @Nullable ResourceLocation opensWith, String openSound) {
    public static final String PARTICLE_NONE = "none";
    public static final String PARTICLE_FLAME = "flame";
    public static final String PARTICLE_COLORED = "colored";

    public boolean dropsExperience() { return expDropMax > 0 || expDropMin > 0; }
}
