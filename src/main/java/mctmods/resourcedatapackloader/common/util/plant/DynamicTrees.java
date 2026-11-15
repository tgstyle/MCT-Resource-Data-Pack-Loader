package mctmods.resourcedatapackloader.common.util.plant;

import com.google.common.base.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public final class DynamicTrees {
    private static final String MOD = "dynamictrees";
    private static final String SAPLING = "com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling";
    private static final String LEAVES = "com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves";
    private static volatile boolean resolved;
    @Nullable private static volatile Class<?> leaves;

    private DynamicTrees() {}

    public static boolean loaded() {
        return Loader.isModLoaded(MOD);
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
        ResourceLocation name = block.getRegistryName();
        return isSapling(block) || isLeaves(block) || name != null && Objects.equal(name.getNamespace(), MOD);
    }
}
