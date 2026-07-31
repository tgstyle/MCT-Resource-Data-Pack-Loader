package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

public final class ContentTree implements IContentShape {
    private final AmountDef count;
    private final AmountDef height;
    private final Set<Block> surface;
    private final int scatterX;
    private final int scatterZ;
    private final int drift;
    @Nullable private final IBlockState log;
    @Nullable private final IBlockState leaves;

    public ContentTree(AmountDef count, ShapeDef shape, Set<Block> surface, ResourceLocation key) {
        this.count = count;
        this.height = shape.height;
        this.surface = surface;
        this.scatterX = shape.scatterX;
        this.scatterZ = shape.scatterZ;
        this.drift = Math.max(1, shape.scatterY);
        this.log = ContentStates.parse(shape.log, key);
        this.leaves = ContentStates.parse(shape.leaves, key);
        if (this.log == null || this.leaves == null) { ContentLog.LOGGER.error("Worldgen {} grows a tree but its log '{}' or leaves '{}' are not registered, so nothing generates", key, shape.log, shape.leaves); }
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        if (log == null || leaves == null) { return false; }

        boolean placed = false;
        int attempts = count.pick(random);
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = origin.getX() + scatter(random, scatterX);
            int z = origin.getZ() + scatter(random, scatterZ);

            BlockPos top = world.getHeight(new BlockPos(x, 0, z));
            if (!world.isAreaLoaded(top, 3)) { continue; }
            if (Math.abs(top.getY() - origin.getY()) > drift) { continue; }
            if (!surface.isEmpty() && !surface.contains(world.getBlockState(top.down()).getBlock())) { continue; }
            if (!world.isAirBlock(top)) { continue; }

            ContentTreeGenerator tree = new ContentTreeGenerator(true, Math.max(1, height.pick(random)), log, leaves, surface);
            placed |= tree.generate(world, random, top);
        }
        return placed;
    }

    private static int scatter(Random random, int bound) {
        if (bound <= 0) { return 0; }
        return random.nextInt(bound) - random.nextInt(bound);
    }
}
