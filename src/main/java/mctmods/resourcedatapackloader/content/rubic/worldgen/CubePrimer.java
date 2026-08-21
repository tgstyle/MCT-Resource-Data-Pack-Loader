package mctmods.resourcedatapackloader.content.rubic.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import javax.annotation.Nullable;
import java.util.Arrays;

public class CubePrimer {
    public static final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
    private final char[] data;
    private IBlockState lastState;
    private int lastId;
    private byte[] extData = null;
    private Biome[] biomes3d = null;

    public boolean hasBiomes() { return biomes3d != null; }

    public CubePrimer() { this(new char[4096]); }

    protected CubePrimer(char[] data) { this.data = data; }

    @SuppressWarnings("unused") @Nullable public Biome getBiome(int localBiomeX, int localBiomeY, int localBiomeZ) {
        if (biomes3d == null) { return null; }
        int biomeX = localBiomeX * 2;
        int biomeZ = localBiomeZ * 2;
        return this.biomes3d[biomeX << 3 | biomeZ];
    }

    @SuppressWarnings("unused") public void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome) {
        if (this.biomes3d == null) { this.biomes3d = new Biome[8 * 8]; }
        int biomeX = localBiomeX * 2;
        int biomeZ = localBiomeZ * 2;
        for (int dx = 0; dx < 2; dx++) {
            for (int dz = 0; dz < 2; dz++) { this.biomes3d[(biomeX + dx) << 3 | (biomeZ + dz)] = biome; }
        }
    }

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
            if (extData == null) { extData = new byte[4096]; }
            extData[idx] = (byte) (value >>> 16);
        }
    }

    @SuppressWarnings("deprecation") private static int rdpl$idOf(IBlockState state) { return Block.BLOCK_STATE_IDS.get(state); }

    public void reset() {
        Arrays.fill(this.data, '\0');
        this.extData = null;
        this.biomes3d = null;
    }

    private static int getBlockIndex(int x, int y, int z) { return y << 8 | z << 4 | x; }
}
