package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.interfaces.IChestPartner;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import javax.annotation.Nullable;

public final class ContentChests {
    public static final String PARTNER = "rdplPartner";
    public static final int UNKEYED = -2;
    public static final int SINGLE = -1;
    private static final ThreadLocal<Placing> PLACING = new ThreadLocal<>();

    private ContentChests() {}

    public static void beginUse(EntityPlayer player, World world, BlockPos clicked, EnumFacing side, Block block) {
        if (!Config.tweaks.modernChestPlacement || !(block instanceof BlockChest)) { return; }
        BlockPos at = world.getBlockState(clicked).getBlock().isReplaceable(world, clicked) ? clicked : clicked.offset(side);
        EnumFacing facing = player.getHorizontalFacing().getOpposite();
        int partner = SINGLE;
        if (player.isSneaking() && side.getAxis().isHorizontal()) {
            EnumFacing toward = side.getOpposite();
            EnumFacing theirs = candidate(world, at, toward, block);
            if (theirs != null && theirs.getAxis() != side.getAxis()) {
                facing = theirs;
                partner = toward.getIndex();
            }
        }
        if (partner == SINGLE && !player.isSneaking()) {
            if (facing == candidate(world, at, facing.rotateY(), block)) { partner = facing.rotateY().getIndex(); }
            else if (facing == candidate(world, at, facing.rotateYCCW(), block)) { partner = facing.rotateYCCW().getIndex(); }
        }
        PLACING.set(new Placing(at.toImmutable(), facing, partner));
    }

    public static void endUse() { PLACING.remove(); }

    public static boolean placing(BlockPos pos) {
        Placing placing = PLACING.get();
        return placing != null && placing.at.equals(pos);
    }

    public static void placed(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        Placing placing = PLACING.get();
        world.setBlockState(pos, state.withProperty(BlockChest.FACING, placing.facing), 3);
        TileEntity chest = world.getTileEntity(pos);
        if (chest instanceof IChestPartner) { ((IChestPartner) chest).rdpl$setPartner(placing.partner); }
        if (stack.hasDisplayName() && chest instanceof TileEntityChest) { ((TileEntityChest) chest).setCustomName(stack.getDisplayName()); }
        if (placing.partner != SINGLE) {
            EnumFacing toward = EnumFacing.byIndex(placing.partner);
            BlockPos beside = pos.offset(toward);
            TileEntity other = world.getTileEntity(beside);
            if (other instanceof IChestPartner) { ((IChestPartner) other).rdpl$setPartner(toward.getOpposite().getIndex()); }
            IBlockState otherState = world.getBlockState(beside);
            world.notifyBlockUpdate(beside, otherState, otherState, 3);
        }
        recheck(world, pos);
    }

    public static boolean keyed(IBlockAccess world, BlockPos pos) { return Config.tweaks.modernChestPlacement && partnerOf(world, pos) != UNKEYED; }

    public static IBlockState seen(IBlockAccess world, BlockPos origin, BlockPos queried, IBlockState state) { return !queried.equals(origin) && state.getBlock() instanceof BlockChest && apart(world, origin, queried) ? Blocks.AIR.getDefaultState() : state; }

    public static boolean apart(@Nullable IBlockAccess world, BlockPos one, BlockPos other) {
        if (!Config.tweaks.modernChestPlacement || world == null) { return false; }
        int mine = partnerOf(world, one);
        int theirs = partnerOf(world, other);
        if (mine == UNKEYED && theirs == UNKEYED) { return false; }
        EnumFacing toward = EnumFacing.getFacingFromVector(other.getX() - one.getX(), 0, other.getZ() - one.getZ());
        return mine != toward.getIndex() || theirs != toward.getOpposite().getIndex();
    }

    public static void recheck(World world, BlockPos pos) {
        TileEntity self = world.getTileEntity(pos);
        if (self != null) { self.updateContainingBlockInfo(); }
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
            BlockPos beside = pos.offset(side);
            if (!world.isBlockLoaded(beside) || !(world.getBlockState(beside).getBlock() instanceof BlockChest)) { continue; }
            TileEntity neighbor = world.getTileEntity(beside);
            if (neighbor != null) { neighbor.updateContainingBlockInfo(); }
        }
    }

    @Nullable private static EnumFacing candidate(World world, BlockPos at, EnumFacing side, Block block) {
        BlockPos beside = at.offset(side);
        IBlockState state = world.getBlockState(beside);
        if (state.getBlock() != block || pairedAt(world, beside)) { return null; }
        return state.getValue(BlockChest.FACING);
    }

    private static boolean pairedAt(World world, BlockPos pos) {
        int partner = partnerOf(world, pos);
        Block block = world.getBlockState(pos).getBlock();
        if (partner == UNKEYED) {
            for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
                BlockPos beside = pos.offset(side);
                if (world.getBlockState(beside).getBlock() == block && partnerOf(world, beside) == UNKEYED) { return true; }
            }
            return false;
        }
        if (partner == SINGLE) { return false; }
        EnumFacing toward = EnumFacing.byIndex(partner);
        BlockPos beside = pos.offset(toward);
        return world.getBlockState(beside).getBlock() == block && partnerOf(world, beside) == toward.getOpposite().getIndex();
    }

    private static int partnerOf(IBlockAccess world, BlockPos pos) {
        Placing placing = PLACING.get();
        if (placing != null && placing.at.equals(pos)) { return placing.partner; }
        TileEntity chest = world.getTileEntity(pos);
        return chest instanceof IChestPartner ? ((IChestPartner) chest).rdpl$partner() : UNKEYED;
    }

    private static final class Placing {
        private final BlockPos at;
        private final EnumFacing facing;
        private final int partner;

        private Placing(BlockPos at, EnumFacing facing, int partner) {
            this.at = at;
            this.facing = facing;
            this.partner = partner;
        }
    }
}
