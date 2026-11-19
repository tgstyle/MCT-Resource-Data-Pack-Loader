package mctmods.resourcedatapackloader.common.blocks.shape;

import mctmods.resourcedatapackloader.common.blocks.BlockVariants;
import mctmods.resourcedatapackloader.common.blocks.ContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation") public class ContentCarpetBlock extends ContentBlock {
    private static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

    public static ContentCarpetBlock create(BlockDef def) { return BlockVariants.construct(def, ContentCarpetBlock::new); }

    protected ContentCarpetBlock(BlockDef def) { super(def); }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) { return CARPET_AABB; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return false; }

    @Override public int getLightOpacity(@Nonnull IBlockState state) { return 0; }

    @Override public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) { return super.canPlaceBlockAt(world, pos) && canStay(world, pos); }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos) {
        if (canStay(world, pos)) { return; }
        dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
    }

    @Override @SideOnly(Side.CLIENT) public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        if (side == EnumFacing.UP) { return true; }
        return world.getBlockState(pos.offset(side)).getBlock() == this || super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override @Nonnull public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED; }

    private static boolean canStay(World world, BlockPos pos) { return !world.isAirBlock(pos.down()); }
}
