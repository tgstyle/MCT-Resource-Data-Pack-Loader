package mctmods.resourcedatapackloader.common.util.plant;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public final class DynamicTrees {
    private static final String MOD = "dynamictrees";
    private static final String SAPLING = "com.dtteam.dynamictrees.block.sapling.DynamicSaplingBlock";
    private static final String LEAVES = "com.dtteam.dynamictrees.block.leaves.DynamicLeavesBlock";
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
        return isSapling(block) || isLeaves(block) || MOD.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace());
    }
}
