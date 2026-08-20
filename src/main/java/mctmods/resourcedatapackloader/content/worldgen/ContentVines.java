package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.block.ContentBlockVine;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentVines implements IContentShape {
    private static final int FLAGS = 2 | 16;
    private final ContentPlacer placer;
    private final AmountDef count;
    private final AmountDef drop;
    private final int scatterX;
    private final int scatterY;
    private final int scatterZ;

    public ContentVines(ContentPlacer placer, AmountDef count, ShapeDef shape) {
        this.placer = placer;
        this.count = count;
        this.drop = shape.stack;
        this.scatterX = shape.scatterX;
        this.scatterY = shape.scatterY;
        this.scatterZ = shape.scatterZ;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        boolean placed = false;
        int attempts = count.pick(random);
        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos start = origin.add(scatter(random, scatterX), scatter(random, scatterY), scatter(random, scatterZ));
            if (!world.isBlockLoaded(start) || !world.isAirBlock(start)) { continue; }
            EnumFacing wall = wallBeside(world, start);
            if (wall == null) { continue; }
            IBlockState state = placer.choose(random);
            if (!(state.getBlock() instanceof BlockVine)) { continue; }
            IBlockState attached = state.withProperty(BlockVine.getPropertyFor(wall), Boolean.TRUE);
            int length = drop.pick(random);
            BlockPos at = start;
            for (int step = 0; step < length; step++) {
                if (!world.isAirBlock(at) || !attachable(world, at.offset(wall), wall.getOpposite())) { break; }
                world.setBlockState(at, attached, FLAGS);
                placed = true;
                at = at.down();
            }
        }
        return placed;
    }

    @Nullable private static EnumFacing wallBeside(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            if (attachable(world, pos.offset(facing), facing.getOpposite())) { return facing; }
        }
        return null;
    }

    private static boolean attachable(World world, BlockPos pos, EnumFacing face) {
        if (!world.isBlockLoaded(pos)) { return false; }
        return ContentBlockVine.attachable(world, pos, face);
    }

    private static int scatter(Random random, int bound) {
        if (bound <= 0) { return 0; }
        return random.nextInt(bound) - random.nextInt(bound);
    }
}
