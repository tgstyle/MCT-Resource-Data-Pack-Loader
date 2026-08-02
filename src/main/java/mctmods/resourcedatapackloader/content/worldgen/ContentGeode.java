package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentGeode implements IContentShape {
    private static final int LEAST_BUBBLES = 4;
    private static final int MOST_EXTRA_BUBBLES = 4;
    private final ContentPlacer placer;
    private final ShapeDef shape;
    private final IBlockState outline;
    @Nullable private final IBlockState fill;

    public ContentGeode(ContentPlacer placer, ShapeDef shape, IBlockState outline, @Nullable IBlockState fill) {
        this.placer = placer;
        this.shape = shape;
        this.outline = outline;
        this.fill = fill;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int width = Math.min(ShapeDef.MOST_REACH * 2, Math.max(3, shape.width.pick(random)));
        int height = Math.max(3, shape.height.pick(random));
        int baseY = origin.getY() - height / 2;
        if (baseY <= 1) { return false; }

        int baseX = origin.getX() - width / 2;
        int baseZ = origin.getZ() - width / 2;

        boolean[] body = new boolean[width * width * height];
        boolean[] hollow = new boolean[width * width * height];
        carve(random, width, height, body, hollow);

        if (blocked(world, baseX, baseY, baseZ, width, height, body, hollow)) { return false; }

        boolean placed = false;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    if (body[at]) {
                        if (placer.place(world, random, baseX + x, baseY + y, baseZ + z)) { placed = true; }
                        else { body[at] = false; }
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    if (fill != null && hollow[at]) {
                        placed |= placer.placeExactly(world, fill, baseX + x, baseY + y, baseZ + z);
                    }
                    else if (!body[at] && touches(body, x, z, y, width, height)) {
                        placed |= placer.placeExactly(world, outline, baseX + x, baseY + y, baseZ + z);
                    }
                }
            }
        }
        return placed;
    }

    private void carve(Random random, int width, int height, boolean[] body, boolean[] hollow) {
        int bubbles = LEAST_BUBBLES + random.nextInt(MOST_EXTRA_BUBBLES);
        for (int bubble = 0; bubble < bubbles; bubble++) {
            double spanX = random.nextDouble() * 6.0D + 3.0D;
            double spanY = random.nextDouble() * 4.0D + 2.0D;
            double spanZ = random.nextDouble() * 6.0D + 3.0D;
            double centerX = random.nextDouble() * (width - spanX - 2.0D) + 1.0D + spanX / 2.0D;
            double centerY = random.nextDouble() * (height - spanY - 4.0D) + 2.0D + spanY / 2.0D;
            double centerZ = random.nextDouble() * (width - spanZ - 2.0D) + 1.0D + spanZ / 2.0D;
            double inner = shape.isHollow() ? random.nextGaussian() * 0.15D + 0.4D : 0.0D;

            for (int x = 1; x < width - 1; x++) {
                for (int z = 1; z < width - 1; z++) {
                    for (int y = 1; y < height - 1; y++) {
                        double reach = away(x, centerX, spanX) + away(y, centerY, spanY) + away(z, centerZ, spanZ);
                        int at = index(x, z, y, width, height);
                        if (reach < 1.0D) { body[at] = true; }
                        if (shape.isHollow() && reach <= inner) { hollow[at] = true; }
                    }
                }
            }
        }
    }

    private boolean blocked(World world, int baseX, int baseY, int baseZ, int width, int height, boolean[] body, boolean[] hollow) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    boolean wanted = body[at] || (fill != null && hollow[at]) || touches(body, x, z, y, width, height);
                    if (wanted && placer.occupied(world, baseX + x, baseY + y, baseZ + z)) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean touches(boolean[] body, int x, int z, int y, int width, int height) {
        return (x + 1 < width && body[index(x + 1, z, y, width, height)])
                || (x > 0 && body[index(x - 1, z, y, width, height)])
                || (z + 1 < width && body[index(x, z + 1, y, width, height)])
                || (z > 0 && body[index(x, z - 1, y, width, height)])
                || (y + 1 < height && body[index(x, z, y + 1, width, height)])
                || (y > 0 && body[index(x, z, y - 1, width, height)]);
    }

    private static double away(int block, double center, double span) {
        double offset = (block - center) / (span / 2.0D);
        return offset * offset;
    }

    private static int index(int x, int z, int y, int width, int height) { return (x * width + z) * height + y; }
}
