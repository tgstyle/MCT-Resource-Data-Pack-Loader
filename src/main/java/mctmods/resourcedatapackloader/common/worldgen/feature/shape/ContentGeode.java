package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ContentGeode extends ContentShapeBase {
    private static final int LEAST_BUBBLES = 4;
    private static final int MOST_EXTRA_BUBBLES = 4;
    private static final double CRACK_REACH = 1.5D;
    private final IBlockState outline;
    @Nullable private final IBlockState fill;
    @Nullable private final IBlockState middle;
    @Nullable private final IBlockState budding;
    @Nullable private final IBlockState crystal;

    public ContentGeode(ContentPlacer placer, ShapeDef shape, IBlockState outline, @Nullable IBlockState fill, ResourceLocation key) {
        super(placer, shape);
        this.outline = outline;
        this.fill = fill;
        this.middle = layer(key, shape.middle, "middle");
        this.budding = layer(key, shape.budding, "budding");
        this.crystal = layer(key, shape.crystal, "crystal");
    }

    @Nullable private static IBlockState layer(ResourceLocation key, String name, String what) {
        if (name.isEmpty()) { return null; }
        IBlockState state = ContentStates.parse(name, key);
        if (state == null) { ContentLog.LOGGER.error("Worldgen {} names block {} as its geode {}, which is not registered, leaving that layer out", key, name, what); }
        return state;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int width = radius(random, shape.width, 3, ShapeDef.MOST_REACH * 2);
        int height = rise(random, 3);
        int baseY = origin.getY() - height / 2;
        if (baseY <= ContentPlacer.floorY(world)) { return false; }
        int baseX = origin.getX() - width / 2;
        int baseZ = origin.getZ() - width / 2;
        int cells = width * width * height;
        boolean[] body = new boolean[cells];
        boolean[] hollow = new boolean[cells];
        boolean[] mid = new boolean[cells];
        boolean[] cracked = new boolean[cells];
        carve(random, width, height, body, hollow);
        if (middle != null) {
            forEachCell(width, height, 0, (x, z, y, at) -> {
                mid[at] = !body[at] && touches(body, x, z, y, width, height);
                return false;
            });
        }
        if (fill != null && shape.crack > 0.0F && random.nextFloat() < shape.crack) { crack(random, width, height, cracked); }
        if (blocked(world, baseX, baseY, baseZ, width, height, body, hollow, mid)) { return false; }
        boolean placed = forEachCell(width, height, 0, (x, z, y, at) -> {
            if (!body[at] || cracked[at]) { return false; }
            if (placer.place(world, random, baseX + x, baseY + y, baseZ + z)) { return true; }
            body[at] = false;
            return false;
        });
        placed |= forEachCell(width, height, 0, (x, z, y, at) -> {
            int placeX = baseX + x;
            int placeY = baseY + y;
            int placeZ = baseZ + z;
            if (fill != null && (hollow[at] || cracked[at] && (body[at] || mid[at] || shell(body, mid, x, z, y, width, height)))) { return placer.placeExactly(world, fill, placeX, placeY, placeZ); }
            if (!cracked[at] && mid[at]) { return placer.placeExactly(world, middle, placeX, placeY, placeZ); }
            return !cracked[at] && shell(body, mid, x, z, y, width, height) && placer.placeExactly(world, outline, placeX, placeY, placeZ);
        });
        if (budding != null && fill != null) { placed |= bud(world, random, baseX, baseY, baseZ, width, height, body, hollow, cracked); }
        return placed;
    }

    private boolean bud(World world, Random random, int baseX, int baseY, int baseZ, int width, int height, boolean[] body, boolean[] hollow, boolean[] cracked) {
        boolean[] budded = new boolean[body.length];
        boolean placed = forEachCell(width, height, 0, (x, z, y, at) -> {
            if (!body[at] || hollow[at] || cracked[at] || !touches(hollow, x, z, y, width, height) || random.nextFloat() >= shape.buddingChance) { return false; }
            budded[at] = placer.placeExactly(world, budding, baseX + x, baseY + y, baseZ + z);
            return budded[at];
        });
        if (crystal == null) { return placed; }
        placed |= forEachCell(width, height, 0, (x, z, y, at) -> {
            if (!hollow[at] || cracked[at] || !touches(budded, x, z, y, width, height) || random.nextFloat() >= shape.crystalChance) { return false; }
            return placer.placeExactly(world, crystal, baseX + x, baseY + y, baseZ + z);
        });
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
            forEachCell(width, height, 1, (x, z, y, at) -> {
                double reach = away(x, centerX, spanX) + away(y, centerY, spanY) + away(z, centerZ, spanZ);
                if (reach < 1.0D) { body[at] = true; }
                if (shape.isHollow() && reach <= inner) { hollow[at] = true; }
                return false;
            });
        }
    }

    private static void crack(Random random, int width, int height, boolean[] cracked) {
        double centerX = width / 2.0D;
        double centerY = height / 2.0D;
        double centerZ = width / 2.0D;
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double tilt = (random.nextDouble() - 0.5D) * Math.PI * 0.5D;
        double dirX = Math.cos(angle) * Math.cos(tilt);
        double dirY = Math.sin(tilt);
        double dirZ = Math.sin(angle) * Math.cos(tilt);
        forEachCell(width, height, 0, (x, z, y, at) -> {
            double offX = x + 0.5D - centerX;
            double offY = y + 0.5D - centerY;
            double offZ = z + 0.5D - centerZ;
            double along = offX * dirX + offY * dirY + offZ * dirZ;
            if (along <= 0.0D) { return false; }
            double sideX = offX - along * dirX;
            double sideY = offY - along * dirY;
            double sideZ = offZ - along * dirZ;
            if (sideX * sideX + sideY * sideY + sideZ * sideZ <= CRACK_REACH * CRACK_REACH) { cracked[at] = true; }
            return false;
        });
    }

    private boolean blocked(World world, int baseX, int baseY, int baseZ, int width, int height, boolean[] body, boolean[] hollow, boolean[] mid) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    int at = index(x, z, y, width, height);
                    boolean wanted = body[at] || mid[at] || (fill != null && hollow[at]) || shell(body, mid, x, z, y, width, height);
                    if (wanted && placer.occupied(world, baseX + x, baseY + y, baseZ + z)) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean forEachCell(int width, int height, int inset, CellVisitor visit) {
        boolean result = false;
        for (int x = inset; x < width - inset; x++) {
            for (int z = inset; z < width - inset; z++) {
                for (int y = inset; y < height - inset; y++) { result |= visit.visit(x, z, y, index(x, z, y, width, height)); }
            }
        }
        return result;
    }

    private interface CellVisitor {
        boolean visit(int x, int z, int y, int at);
    }

    private static boolean shell(boolean[] body, boolean[] mid, int x, int z, int y, int width, int height) {
        int at = index(x, z, y, width, height);
        return !body[at] && !mid[at] && (touches(body, x, z, y, width, height) || touches(mid, x, z, y, width, height));
    }

    private static boolean touches(boolean[] body, int x, int z, int y, int width, int height) {
        for (EnumFacing face : EnumFacing.VALUES) {
            int nx = x + face.getXOffset();
            int ny = y + face.getYOffset();
            int nz = z + face.getZOffset();
            if (nx >= 0 && nx < width && nz >= 0 && nz < width && ny >= 0 && ny < height && body[index(nx, nz, ny, width, height)]) { return true; }
        }
        return false;
    }

    private static double away(int block, double center, double span) { return MathUtil.away(block, center, span / 2.0D, 0.0D); }

    private static int index(int x, int z, int y, int width, int height) { return (x * width + z) * height + y; }
}
