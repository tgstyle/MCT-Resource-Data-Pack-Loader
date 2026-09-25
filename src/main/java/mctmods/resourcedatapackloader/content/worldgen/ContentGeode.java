package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public final class ContentGeode implements IContentShape {
    private static final int LEAST_BUBBLES = 4;
    private static final int MOST_EXTRA_BUBBLES = 4;
    private static final double CRACK_REACH = 1.5D;
    private final ShapeDef shape;
    private final BlockState outline;
    @Nullable private final BlockState fill;
    @Nullable private final BlockState middle;
    @Nullable private final BlockState budding;
    @Nullable private final BlockState crystal;

    public ContentGeode(ShapeDef shape, BlockState outline, @Nullable BlockState fill, @Nullable BlockState middle, @Nullable BlockState budding, @Nullable BlockState crystal) {
        this.shape = shape;
        this.outline = outline;
        this.fill = fill;
        this.middle = middle;
        this.budding = budding;
        this.crystal = crystal;
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int width = Mth.clamp(shape.width().pick(random), 3, ShapeDef.MOST_REACH * 2);
        int height = Math.max(3, shape.height().pick(random));
        int baseY = origin.getY() - height / 2;
        if (baseY <= placer.floorY()) { return false; }
        int baseX = origin.getX() - width / 2;
        int baseZ = origin.getZ() - width / 2;
        int cells = width * width * height;
        boolean[] body = new boolean[cells];
        boolean[] hollow = new boolean[cells];
        boolean[] mid = new boolean[cells];
        boolean[] cracked = new boolean[cells];
        carve(random, width, height, body, hollow);
        if (middle != null) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        int at = index(x, z, y, width, height);
                        mid[at] = !body[at] && touches(body, x, z, y, width, height);
                    }
                }
            }
        }
        if (fill != null && shape.crack() > 0.0F && random.nextFloat() < shape.crack()) { crack(random, width, height, cracked); }
        if (blocked(placer, baseX, baseY, baseZ, width, height, body, hollow, mid)) { return false; }
        boolean placed = false;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    if (!body[at] || cracked[at]) { continue; }
                    if (placer.place(random, baseX + x, baseY + y, baseZ + z)) { placed = true; }
                    else { body[at] = false; }
                }
            }
        }
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    int placeX = baseX + x;
                    int placeY = baseY + y;
                    int placeZ = baseZ + z;
                    if (fill != null && (hollow[at] || cracked[at] && (body[at] || mid[at] || shell(body, mid, x, z, y, width, height)))) { placed |= placer.placeExactly(fill, placeX, placeY, placeZ); }
                    else if (middle != null && !cracked[at] && mid[at]) { placed |= placer.placeExactly(middle, placeX, placeY, placeZ); }
                    else if (!cracked[at] && shell(body, mid, x, z, y, width, height)) { placed |= placer.placeExactly(outline, placeX, placeY, placeZ); }
                }
            }
        }
        if (budding != null && fill != null) { placed |= bud(placer, random, baseX, baseY, baseZ, width, height, body, hollow, cracked, budding); }
        return placed;
    }

    private boolean bud(ContentPlacer placer, RandomSource random, int baseX, int baseY, int baseZ, int width, int height, boolean[] body, boolean[] hollow, boolean[] cracked, BlockState buds) {
        boolean placed = false;
        boolean[] budded = new boolean[body.length];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    if (!body[at] || hollow[at] || cracked[at] || !touches(hollow, x, z, y, width, height) || random.nextFloat() >= shape.buddingChance()) { continue; }
                    budded[at] = placer.placeExactly(buds, baseX + x, baseY + y, baseZ + z);
                    placed |= budded[at];
                }
            }
        }
        if (crystal == null) { return placed; }
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    if (!hollow[at] || cracked[at] || !touches(budded, x, z, y, width, height) || random.nextFloat() >= shape.crystalChance()) { continue; }
                    placed |= placer.placeExactly(crystal, baseX + x, baseY + y, baseZ + z);
                }
            }
        }
        return placed;
    }

    private void carve(RandomSource random, int width, int height, boolean[] body, boolean[] hollow) {
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

    private static void crack(RandomSource random, int width, int height, boolean[] cracked) {
        double centerX = width / 2.0D;
        double centerY = height / 2.0D;
        double centerZ = width / 2.0D;
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double tilt = (random.nextDouble() - 0.5D) * Math.PI * 0.5D;
        double dirX = Math.cos(angle) * Math.cos(tilt);
        double dirY = Math.sin(tilt);
        double dirZ = Math.sin(angle) * Math.cos(tilt);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    double offX = x + 0.5D - centerX;
                    double offY = y + 0.5D - centerY;
                    double offZ = z + 0.5D - centerZ;
                    double along = offX * dirX + offY * dirY + offZ * dirZ;
                    if (along <= 0.0D) { continue; }
                    double sideX = offX - along * dirX;
                    double sideY = offY - along * dirY;
                    double sideZ = offZ - along * dirZ;
                    if (sideX * sideX + sideY * sideY + sideZ * sideZ <= CRACK_REACH * CRACK_REACH) { cracked[index(x, z, y, width, height)] = true; }
                }
            }
        }
    }

    private boolean blocked(ContentPlacer placer, int baseX, int baseY, int baseZ, int width, int height, boolean[] body, boolean[] hollow, boolean[] mid) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    boolean wanted = body[at] || mid[at] || (fill != null && hollow[at]) || shell(body, mid, x, z, y, width, height);
                    if (wanted && placer.occupied(baseX + x, baseY + y, baseZ + z)) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean shell(boolean[] body, boolean[] mid, int x, int z, int y, int width, int height) {
        int at = index(x, z, y, width, height);
        return !body[at] && !mid[at] && (touches(body, x, z, y, width, height) || touches(mid, x, z, y, width, height));
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
