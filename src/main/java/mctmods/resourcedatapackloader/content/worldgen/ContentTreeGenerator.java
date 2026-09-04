package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;

public class ContentTreeGenerator extends WorldGenAbstractTree {
    private final IBlockState log;
    private final IBlockState leaves;
    private final int minHeight;
    private final Set<Block> soil;

    public ContentTreeGenerator(boolean notify, int minHeight, IBlockState log, IBlockState leaves, Set<Block> soil) {
        super(notify);
        this.minHeight = Math.max(1, minHeight);
        this.log = log;
        this.leaves = leaves;
        this.soil = soil;
    }

    @Override public boolean generate(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos position) {
        int height = random.nextInt(3) + minHeight;
        if (position.getY() < 1 || position.getY() >= world.getHeight() - height - 1) { return false; }
        if (!rooted(world, position.down())) { return false; }
        if (!clear(world, position, height)) { return false; }
        IBlockState under = world.getBlockState(position.down());
        under.getBlock().onPlantGrow(under, world, position.down(), position);
        canopy(world, random, position, height);
        trunk(world, position, height);
        return true;
    }

    private boolean rooted(World world, BlockPos below) {
        IBlockState state = world.getBlockState(below);
        if (!soil.isEmpty()) { return soil.contains(state.getBlock()); }
        return state.getBlock().canSustainPlant(state, world, below, net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling) net.minecraft.init.Blocks.SAPLING);
    }

    private boolean clear(World world, BlockPos position, int height) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = position.getY(); y <= position.getY() + 1 + height; y++) {
            int reach = 1;
            if (y == position.getY()) { reach = 0; }
            if (y >= position.getY() + 1 + height - 2) { reach = 2; }
            for (int x = position.getX() - reach; x <= position.getX() + reach; x++) {
                for (int z = position.getZ() - reach; z <= position.getZ() + reach; z++) {
                    if (y < 0 || y >= world.getHeight()) { return false; }
                    if (!isReplaceable(world, cursor.setPos(x, y, z))) { return false; }
                }
            }
        }
        return true;
    }

    private void canopy(World world, Random random, BlockPos position, int height) {
        for (int y = position.getY() - 3 + height; y <= position.getY() + height; y++) {
            int depth = y - (position.getY() + height);
            int reach = 1 - depth / 2;
            for (int x = position.getX() - reach; x <= position.getX() + reach; x++) {
                int dx = x - position.getX();
                for (int z = position.getZ() - reach; z <= position.getZ() + reach; z++) {
                    int dz = z - position.getZ();
                    if (Math.abs(dx) == reach && Math.abs(dz) == reach && (random.nextInt(2) == 0 || depth == 0)) { continue; }
                    BlockPos at = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(at);
                    if (!state.getBlock().isAir(state, world, at) && !state.getBlock().isLeaves(state, world, at) && state.getMaterial() != Material.VINE) { continue; }
                    setBlockAndNotifyAdequately(world, at, leaves);
                }
            }
        }
    }

    private void trunk(World world, BlockPos position, int height) {
        for (int y = 0; y < height; y++) {
            BlockPos at = position.up(y);
            IBlockState state = world.getBlockState(at);
            if (!state.getBlock().isAir(state, world, at) && !state.getBlock().isLeaves(state, world, at) && state.getMaterial() != Material.VINE) { continue; }
            setBlockAndNotifyAdequately(world, at, log);
        }
    }
}
