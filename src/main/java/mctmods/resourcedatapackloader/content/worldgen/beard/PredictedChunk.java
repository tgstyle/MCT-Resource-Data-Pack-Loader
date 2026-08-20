package mctmods.resourcedatapackloader.content.worldgen.beard;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import javax.annotation.Nonnull;

public final class PredictedChunk extends Chunk {
    private static PredictedChunk held;
    private final World reading;
    private final BlockPos.MutableBlockPos asked = new BlockPos.MutableBlockPos();

    private PredictedChunk(World world) {
        super(world, 0, 0);
        this.reading = world;
    }

    public static PredictedChunk of(World world) {
        PredictedChunk known = held;
        if (known != null && known.reading == world) { return known; }
        held = new PredictedChunk(world);
        return held;
    }

    public static void forget() { held = null; }

    @Override @Nonnull public IBlockState getBlockState(@Nonnull BlockPos pos) { return BeardSurface.predicted(reading, pos); }

    @Override @Nonnull public IBlockState getBlockState(int x, int y, int z) { return BeardSurface.predicted(reading, asked.setPos(x, y, z)); }
}
