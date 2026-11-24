package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PlacedBlock {
    private PlacedBlock() {}

    public static void announce(World world, BlockPos pos, IBlockState placed) {
        world.notifyBlockUpdate(pos, placed, placed, BlockFlags.UPDATE_ALL);
        world.notifyNeighborsOfStateChange(pos, placed.getBlock(), false);
        SoundType sound = placed.getBlock().getSoundType(placed, world, pos, null);
        world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }
}
