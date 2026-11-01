package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record BlockDef(Identifier key, String type, String material, String mapColor, String soundType, String creativeTab, String harvestTool, int harvestToolLevel, boolean silkHarvest,
                       int expDropMin, int expDropMax, float explosionResistanceDivisor, List<BlockVariant> variants, List<String> requires, String renderLayer, boolean opaque, boolean fullCube, int lightOpacity,
                       float slipperiness, @Nullable double[] bounds, int flammability, int fireSpread, String modelBlock, boolean itemModelFromFile, String torchParticle, boolean torchSmoke, int torchColor,
                       String cropSeed, String cropProduce, int cropMaxAge, @Nullable SaplingDef sapling, @Nullable GrowthDef growth, List<String> behavesAs, String tint,
                       String leafSapling, int leafSaplingChance, @Nullable Identifier opensWith, String openSound, @Nullable PortalDef portal, @Nullable ContainerDef container, @Nullable BellDef bell, List<String> spreadsOnto, @Nullable PortwayDef portway) {
    public static final String PARTICLE_NONE = "none";
    public static final String PARTICLE_FLAME = "flame";
    public static final String PARTICLE_COLORED = "colored";

    public boolean dropsExperience() { return expDropMax > 0 || expDropMin > 0; }

    public boolean chested() { return container != null && container.chestModel(); }
}
