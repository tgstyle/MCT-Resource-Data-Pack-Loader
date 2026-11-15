package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public final class TreeBlockAccess implements IBlockAccess {
    private static final int LIGHT = 15 << 20 | 15 << 4;
    private static final IBlockState AIR = Objects.requireNonNull(Blocks.AIR.getDefaultState());
    private final BlockPos at;
    private final IBlockState state;

    public TreeBlockAccess(BlockPos at, IBlockState state) {
        this.at = at;
        this.state = state;
    }

    @Nullable @Override public TileEntity getTileEntity(@Nonnull BlockPos pos) { return null; }

    @Override public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) { return LIGHT; }

    @Nonnull @Override public IBlockState getBlockState(@Nonnull BlockPos pos) { return pos.equals(at) ? state : AIR; }

    @Override public boolean isAirBlock(@Nonnull BlockPos pos) { return getBlockState(pos).getBlock() == Blocks.AIR; }

    @Nonnull @Override public Biome getBiome(@Nonnull BlockPos pos) { return Biomes.DEFAULT; }

    @Override public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) { return 0; }

    @Nonnull @Override public WorldType getWorldType() { return WorldType.DEFAULT; }

    @Override public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean fallback) { return pos.equals(at) ? state.isSideSolid(this, pos, side) : fallback; }
}
