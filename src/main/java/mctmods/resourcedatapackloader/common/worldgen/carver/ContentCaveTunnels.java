package mctmods.resourcedatapackloader.common.worldgen.carver;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

public final class ContentCaveTunnels extends MapGenCaves {
    private final IBlockState fill;
    private final IBlockState sea;
    private final int seaLevel;

    public ContentCaveTunnels(IBlockState fill, IBlockState sea, int seaLevel) {
        this.fill = fill;
        this.sea = sea;
        this.seaLevel = seaLevel;
    }

    @Override protected void recursiveGenerate(@Nonnull World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, @Nonnull ChunkPrimer chunkPrimerIn) {
        int systems = rand.nextInt(rand.nextInt(rand.nextInt(15) + 1) + 1);
        if (rand.nextInt(3) != 0) { systems = 0; }
        for (int system = 0; system < systems; system++) {
            double startX = chunkX * 16 + rand.nextInt(16);
            double startY = 8 + rand.nextInt(240);
            double startZ = chunkZ * 16 + rand.nextInt(16);
            int tunnels = 1;
            if (rand.nextInt(4) == 0) {
                addRoom(rand.nextLong(), originalX, originalZ, chunkPrimerIn, startX, startY, startZ);
                tunnels += rand.nextInt(4);
            }
            for (int tunnel = 0; tunnel < tunnels; tunnel++) {
                float yaw = rand.nextFloat() * (float) (Math.PI * 2);
                float pitch = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float width = rand.nextFloat() * 2.0F + rand.nextFloat();
                if (rand.nextInt(10) == 0) { width *= rand.nextFloat() * rand.nextFloat() * 3.0F + 1.0F; }
                addTunnel(rand.nextLong(), originalX, originalZ, chunkPrimerIn, startX, startY, startZ, width, yaw, pitch, 0, 0, 1.0);
            }
        }
    }

    @Override protected boolean canReplaceBlock(@Nonnull IBlockState state, @Nonnull IBlockState up) { return state == fill; }

    @Override protected boolean isOceanBlock(@Nonnull ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) { return false; }

    @Override protected void digBlock(@Nonnull ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, @Nonnull IBlockState state, @Nonnull IBlockState up) {
        if (canReplaceBlock(state, up)) { data.setBlockState(x, y, z, y < seaLevel ? sea : BLK_AIR); }
    }
}
