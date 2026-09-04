package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentLargeVein implements IContentShape {
    private final ContentPlacer placer;
    private final AmountDef size;
    private final boolean sparse;
    private final boolean spindly;
    private final BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();

    public ContentLargeVein(ContentPlacer placer, AmountDef size, boolean sparse, boolean spindly) {
        this.placer = placer;
        this.size = size;
        this.sparse = sparse;
        this.spindly = spindly;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        int veinSize = size.pick(random);
        int branchSize = spindly ? 1 : 1 + (veinSize / 30);
        int subBranchSize = spindly ? 1 : 1 + (branchSize / 5);
        boolean placed = false;
        for (int blocksVein = 0; blocksVein <= veinSize; ) {
            int posX = x;
            int posY = y;
            int posZ = z;
            int directionChange = random.nextInt(6);
            int directionX = stride(random);
            int directionY = stride(random);
            int directionZ = stride(random);
            for (int blocksBranch = 0; blocksBranch <= branchSize; ) {
                if (directionChange != 1) { posX += random.nextInt(2) * directionX; }
                if (directionChange != 2) { posY += random.nextInt(2) * directionY; }
                if (directionChange != 3) { posZ += random.nextInt(2) * directionZ; }
                if (random.nextInt(3) == 0) {
                    int posX2 = posX;
                    int posY2 = posY;
                    int posZ2 = posZ;
                    int directionChange2 = random.nextInt(6);
                    int directionX2 = stride(random);
                    int directionY2 = stride(random);
                    int directionZ2 = stride(random);
                    for (int blocksSubBranch = 0; blocksSubBranch <= subBranchSize; ) {
                        if (directionChange2 != 0) { posX2 += random.nextInt(2) * directionX2; }
                        if (directionChange2 != 1) { posY2 += random.nextInt(2) * directionY2; }
                        if (directionChange2 != 2) { posZ2 += random.nextInt(2) * directionZ2; }
                        placed |= place(world, random, posX2, posY2, posZ2);
                        if (sparse) {
                            blocksVein++;
                            blocksBranch++;
                        }
                        blocksSubBranch++;
                    }
                }
                placed |= place(world, random, posX, posY, posZ);
                blocksBranch++;
            }
            x = x + (random.nextInt(3) - 1);
            y = y + (random.nextInt(3) - 1);
            z = z + (random.nextInt(3) - 1);
            blocksVein++;
        }
        return placed;
    }

    private boolean place(World world, Random random, int x, int y, int z) {
        if (!world.isBlockLoaded(at.setPos(x, y, z))) { return false; }
        return placer.place(world, random, x, y, z);
    }

    private static int stride(Random random) {
        int direction = -random.nextInt(2);
        return direction + (~direction >>> 31);
    }
}
