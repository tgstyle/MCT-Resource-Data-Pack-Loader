package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentVein implements IContentShape {
    private static final int MOST_BLOCKS = 42;
    private static final int SCATTER_BELOW = 4;
    private static final float HALF_TURN = (float) Math.PI;
    private final ContentPlacer placer;
    private final AmountDef size;
    private final boolean sparse;

    public ContentVein(ContentPlacer placer, AmountDef size, boolean sparse) {
        this.placer = placer;
        this.size = size;
        this.sparse = sparse;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        float turn = random.nextFloat() * HALF_TURN;
        double lowY = origin.getY() + random.nextInt(3) - 2;
        double highY = origin.getY() + random.nextInt(3) - 2;

        int blocks = MathHelper.clamp(size.pick(random), 1, MOST_BLOCKS);
        if (sparse) { blocks = thinned(blocks, turn, lowY > highY); }
        else if (blocks < SCATTER_BELOW) { return scatter(world, random, blocks, origin); }

        double reach = MathHelper.sin(turn) * blocks / 8.0D;
        double drift = MathHelper.cos(turn) * blocks / 8.0D;
        double lowX = origin.getX() + reach;
        double highX = origin.getX() - reach;
        double lowZ = origin.getZ() + drift;
        double highZ = origin.getZ() - drift;

        boolean placed = false;
        for (int step = 0; step <= blocks; step++) {
            double along = (double) step / blocks;
            double girth = (MathHelper.sin(HALF_TURN * (float) along) + 1.0D) * (random.nextDouble() * blocks / 16.0D) + 1.0D;
            placed |= blob(world, random,
                    lowX + (highX - lowX) * along,
                    lowY + (highY - lowY) * along,
                    lowZ + (highZ - lowZ) * along,
                    girth * 0.5D);
        }
        return placed;
    }

    private static int thinned(int blocks, float turn, boolean flipped) {
        if (blocks == 1 && flipped) { return 2; }
        if (blocks == 2 && turn > HALF_TURN * 0.5F) { return 3; }
        return blocks;
    }

    private boolean blob(World world, Random random, double centreX, double centreY, double centreZ, double radius) {
        if (radius <= 0.0D) { return false; }

        boolean placed = false;
        for (int x = MathHelper.floor(centreX - radius); x <= MathHelper.floor(centreX + radius); x++) {
            double offX = away(x, centreX, radius);
            if (offX >= 1.0D) { continue; }

            for (int y = MathHelper.floor(centreY - radius); y <= MathHelper.floor(centreY + radius); y++) {
                double offY = offX + away(y, centreY, radius);
                if (offY >= 1.0D) { continue; }

                for (int z = MathHelper.floor(centreZ - radius); z <= MathHelper.floor(centreZ + radius); z++) {
                    if (offY + away(z, centreZ, radius) >= 1.0D) { continue; }

                    placed |= placer.place(world, random, x, y, z);
                }
            }
        }
        return placed;
    }

    private static double away(int block, double centre, double radius) {
        double offset = (block + 0.5D - centre) / radius;
        return offset * offset;
    }

    private boolean scatter(World world, Random random, int blocks, BlockPos origin) {
        boolean placed = placer.place(world, random, origin.getX(), origin.getY(), origin.getZ());
        for (int step = 1; step < blocks; step++) {
            placed |= placer.place(world, random,
                    origin.getX() + random.nextInt(2),
                    origin.getY() + random.nextInt(2),
                    origin.getZ() + random.nextInt(2));
        }
        return placed;
    }
}
