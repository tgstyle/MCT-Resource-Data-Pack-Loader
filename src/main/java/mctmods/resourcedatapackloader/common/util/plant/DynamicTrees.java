package mctmods.resourcedatapackloader.common.util.plant;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public final class DynamicTrees {
    private static final String MOD = "dynamictrees";
    private static final String SAPLING = "com.ferreusveritas.dynamictrees.block.DynamicSaplingBlock";
    private static final String LEAVES = "com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock";
    private static volatile boolean resolved;
    @Nullable private static volatile Class<?> leaves;

    private DynamicTrees() {}

    public static boolean loaded() {
        return ModList.get().isLoaded(MOD);
    }

    public static boolean isSapling(Block block) {
        return block.getClass().getName().startsWith(SAPLING);
    }

    public static boolean isLeaves(Block block) {
        if (!resolved) {
            try { leaves = Class.forName(LEAVES, false, DynamicTrees.class.getClassLoader()); }
            catch (ClassNotFoundException | LinkageError absent) { leaves = null; }
            resolved = true;
        }
        Class<?> type = leaves;
        return type != null && type.isInstance(block);
    }

    public static boolean owns(Block block) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
        return isSapling(block) || isLeaves(block) || name != null && MOD.equals(name.getNamespace());
    }
}
