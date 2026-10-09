package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import java.util.function.Function;

public final class BlockVariants {
    private static final ThreadLocal<BlockDef> CONSTRUCTING = new ThreadLocal<>();
    private static final ThreadLocal<PropertyVariant> PROPERTY = new ThreadLocal<>();

    private BlockVariants() {}

    public static <T extends Block> T construct(BlockDef def, Function<BlockDef, T> factory) {
        CONSTRUCTING.set(def);
        PROPERTY.set(new PropertyVariant(ContentSetup.names(def)));
        try { return factory.apply(def); }
        finally {
            CONSTRUCTING.remove();
            PROPERTY.remove();
        }
    }

    public static BlockDef def() { return CONSTRUCTING.get(); }

    public static PropertyVariant property() { return PROPERTY.get(); }

    public static BlockStateContainer container(Block block) { return new BlockStateContainer(block, property()); }

    public static boolean sustains(BlockDef def, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) { return def.sustains(plantable.getPlantType(world, pos.offset(direction))); }

    public static AxisAlignedBB bounds(BlockDef def, AxisAlignedBB fallback) { return def.bounds == null ? fallback : def.bounds; }

    public static void subBlocks(Block block, BlockDef def, NonNullList<ItemStack> list) { for (BlockVariant value : def.visible) { list.add(new ItemStack(block, 1, value.meta)); } }
}
