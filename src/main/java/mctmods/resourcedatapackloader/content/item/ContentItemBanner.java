package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.block.ContentBlockBanner;
import mctmods.resourcedatapackloader.content.block.ContentBlockBannerWall;

import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ContentItemBanner extends ItemBlock {
    private final ContentBlockBanner standing;

    public ContentItemBanner(ContentBlockBanner standing) {
        super(standing);
        this.standing = standing;
        this.maxStackSize = 16;
        setMaxDamage(0);
    }

    @Override @Nonnull public EnumActionResult onItemUse(@NotNull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        boolean replaceable = state.getBlock().isReplaceable(world, pos);
        if (facing == EnumFacing.DOWN) { return EnumActionResult.FAIL; }
        if (!state.getMaterial().isSolid() && !replaceable) { return EnumActionResult.FAIL; }
        if (replaceable && facing != EnumFacing.UP) { return EnumActionResult.FAIL; }

        BlockPos placed = pos.offset(facing);
        ItemStack held = player.getHeldItem(hand);
        if (!player.canPlayerEdit(placed, facing, held) || !standing.canPlaceBlockAt(world, placed)) { return EnumActionResult.FAIL; }
        if (world.isRemote) { return EnumActionResult.SUCCESS; }

        if (replaceable) { placed = placed.down(); }
        if (facing == EnumFacing.UP) {
            int rotation = MathHelper.floor((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
            world.setBlockState(placed, standing.getDefaultState().withProperty(BlockBanner.ROTATION, rotation), 3);
        }
        else {
            ContentBlockBannerWall wall = standing.getWall();
            if (wall == null) { return EnumActionResult.FAIL; }

            world.setBlockState(placed, wall.getDefaultState().withProperty(BlockBanner.FACING, facing), 3);
        }
        held.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
