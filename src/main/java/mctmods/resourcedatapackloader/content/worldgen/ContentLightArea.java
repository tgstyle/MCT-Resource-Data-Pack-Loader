package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.ILightAreaHolder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import java.util.Arrays;

public final class ContentLightArea {
    public static final int UNKNOWN = -1;
    public static final int NO = 0;
    public static final int YES = 1;
    private final Chunk[] nearby = new Chunk[9];
    private World inside;
    private int chunkX;
    private int chunkZ;
    private boolean ringLoaded;
    private boolean wideLoaded;

    public ContentLightArea() {}

    private static ContentLightArea of(World world) {
        ILightAreaHolder holder = (ILightAreaHolder) world;
        ContentLightArea area = holder.rdpl$lightArea();
        if (area == null) {
            area = new ContentLightArea();
            holder.rdpl$setLightArea(area);
        }
        return area;
    }

    public static void enter(World world, int x, int z) {
        ContentLightArea area = of(world);
        area.inside = world;
        area.chunkX = x;
        area.chunkZ = z;
        area.ringLoaded = square(world, x, z, 1);
        area.wideLoaded = area.ringLoaded && square(world, x, z, 2);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) { area.nearby[(dx + 1) * 3 + dz + 1] = area.ringLoaded ? world.getChunk(x + dx, z + dz) : null; }
        }
    }

    public static boolean inside(World world) { return of(world).inside != null; }

    public static void leave(World world) {
        ContentLightArea area = of(world);
        area.inside = null;
        Arrays.fill(area.nearby, null);
    }

    public static int answer(World world, BlockPos pos, int radius) {
        ContentLightArea area = ((ILightAreaHolder) world).rdpl$lightArea();
        if (area == null || area.inside != world) { return UNKNOWN; }
        int dx = (pos.getX() >> 4) - area.chunkX;
        int dz = (pos.getZ() >> 4) - area.chunkZ;
        if (dx == 0 && dz == 0) {
            if (radius <= 16) { return area.ringLoaded ? YES : NO; }
            return area.wideLoaded ? YES : UNKNOWN;
        }
        if (dx < -1 || dx > 1 || dz < -1 || dz > 1) { return UNKNOWN; }
        return radius <= 16 && area.wideLoaded ? YES : UNKNOWN;
    }

    public static Chunk at(World world, BlockPos pos) {
        ContentLightArea area = ((ILightAreaHolder) world).rdpl$lightArea();
        if (area == null || area.inside != world || !area.ringLoaded) { return null; }
        int dx = (pos.getX() >> 4) - area.chunkX;
        int dz = (pos.getZ() >> 4) - area.chunkZ;
        if (dx < -1 || dx > 1 || dz < -1 || dz > 1) { return null; }
        return area.nearby[(dx + 1) * 3 + dz + 1];
    }

    public static boolean skySettled(World world, BlockPos pos) {
        int y = pos.getY();
        if (y < 1 || y > 254) { return false; }
        Chunk here = at(world, pos);
        if (here == null) { return false; }
        int stored = here.getLightFor(EnumSkyBlock.SKY, pos);
        if (here.canSeeSky(pos)) { return stored == 15; }
        IBlockState state = here.getBlockState(pos);
        int opacity = state.getBlock().getLightOpacity(state, world, pos);
        if (opacity < 1) { opacity = 1; }
        if (opacity >= 15) { return stored == 0; }
        int raw = 0;
        BlockPos.MutableBlockPos beside = new BlockPos.MutableBlockPos();
        for (EnumFacing facing : EnumFacing.VALUES) {
            beside.setPos(pos.getX() + facing.getXOffset(), y + facing.getYOffset(), pos.getZ() + facing.getZOffset());
            Chunk next = at(world, beside);
            if (next == null) { return false; }
            int reaching = next.getLightFor(EnumSkyBlock.SKY, beside) - opacity;
            if (reaching > raw) { raw = reaching; }
            if (raw >= 14) { break; }
        }
        return raw == stored;
    }

    private static boolean square(World world, int x, int z, int reach) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                if (!world.isBlockLoaded(at.setPos((x + dx) << 4, 64, (z + dz) << 4))) { return false; }
            }
        }
        return true;
    }
}
