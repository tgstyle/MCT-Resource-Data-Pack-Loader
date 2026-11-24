package mctmods.resourcedatapackloader.common.util.plant;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;

public final class LeafDecay {
    private LeafDecay() { }

    public static boolean decays(IBlockState state) { return state.getPropertyKeys().contains(BlockLeaves.DECAYABLE); }

    public static IBlockState loose(IBlockState state) {
        IBlockState loose = state.withProperty(BlockLeaves.DECAYABLE, true);
        return loose.getPropertyKeys().contains(BlockLeaves.CHECK_DECAY) ? loose.withProperty(BlockLeaves.CHECK_DECAY, true) : loose;
    }

    public static IBlockState checking(IBlockState state) {
        if (!decays(state) || !state.getValue(BlockLeaves.DECAYABLE) || !state.getPropertyKeys().contains(BlockLeaves.CHECK_DECAY)) { return state; }
        return state.withProperty(BlockLeaves.CHECK_DECAY, true);
    }

    public static IBlockState persistent(IBlockState state) { return decays(state) ? state.withProperty(BlockLeaves.DECAYABLE, false) : state; }
}
