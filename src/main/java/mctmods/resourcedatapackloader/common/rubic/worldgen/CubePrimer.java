package mctmods.resourcedatapackloader.common.rubic.worldgen;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.core.util.address.AddressTools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import java.util.Arrays;

public class CubePrimer {
    public static final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
    private final char[] data;
    private IBlockState lastState;
    private int lastId;
    private byte[] extData = null;

    public CubePrimer() { this(new char[ICube.VOLUME]); }

    protected CubePrimer(char[] data) { this.data = data; }

    public IBlockState getBlockState(int x, int y, int z) {
        int idx = getBlockIndex(x, y, z);
        int block = this.data[idx];
        if (extData != null) { block |= extData[idx] << 16; }
        @SuppressWarnings("deprecation") IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(block);
        return iblockstate == null ? DEFAULT_STATE : iblockstate;
    }

    public void setBlockState(int x, int y, int z, IBlockState state) {
        if (state != lastState) {
            lastState = state;
            lastId = rdpl$idOf(state);
        }
        int value = lastId;
        char lsb = (char) value;
        int idx = getBlockIndex(x, y, z);
        this.data[idx] = lsb;
        if (value > 0xFFFF) {
            if (extData == null) { extData = new byte[ICube.VOLUME]; }
            extData[idx] = (byte) (value >>> 16);
        }
        else if (extData != null) { extData[idx] = 0; }
    }

    @SuppressWarnings("deprecation") private static int rdpl$idOf(IBlockState state) { return Block.BLOCK_STATE_IDS.get(state); }

    public void reset() {
        Arrays.fill(this.data, '\0');
        this.extData = null;
    }

    private static int getBlockIndex(int x, int y, int z) { return AddressTools.getLocalAddress(x, y, z); }
}
