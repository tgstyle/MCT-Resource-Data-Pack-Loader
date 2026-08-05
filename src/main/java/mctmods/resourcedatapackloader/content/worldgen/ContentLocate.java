package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import java.util.ArrayList;
import java.util.List;

public class ContentLocate extends WorldSavedData {
    private static final String NAME = "RDPLLocate";
    private NBTTagCompound placed = new NBTTagCompound();

    public ContentLocate() { super(NAME); }

    @SuppressWarnings("unused") public ContentLocate(String name) { super(name); }

    @Override public void readFromNBT(NBTTagCompound nbt) { placed = nbt.getCompoundTag("Placed"); }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("Placed", placed);
        return nbt;
    }

    private static ContentLocate of(World world) {
        ContentLocate held = (ContentLocate) world.getPerWorldStorage().getOrLoadData(ContentLocate.class, NAME);
        if (held == null) {
            held = new ContentLocate();
            world.getPerWorldStorage().setData(NAME, held);
        }
        return held;
    }

    public static void record(World world, String name, BlockPos at) {
        ContentLocate held = of(world);
        long[] known = readLongs(held.placed, name);
        long[] grown = new long[known.length + 1];
        System.arraycopy(known, 0, grown, 0, known.length);
        grown[known.length] = at.toLong();
        writeLongs(held.placed, name, grown);
        held.markDirty();
    }

    public static List<String> names(World world) { return new ArrayList<>(of(world).placed.getKeySet()); }

    public static BlockPos nearest(World world, String name, BlockPos from) {
        long[] known = readLongs(of(world).placed, name);
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        for (long packed : known) {
            BlockPos at = BlockPos.fromLong(packed);
            double away = at.distanceSq(from);
            if (away < closest) {
                closest = away;
                best = at;
            }
        }
        return best;
    }

    private static long[] readLongs(NBTTagCompound from, String name) {
        int[] halves = from.getIntArray(name);
        long[] out = new long[halves.length / 2];
        for (int i = 0; i < out.length; i++) { out[i] = ((long) halves[i * 2] << 32) | (halves[i * 2 + 1] & 0xFFFFFFFFL); }
        return out;
    }

    private static void writeLongs(NBTTagCompound into, String name, long[] values) {
        int[] halves = new int[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            halves[i * 2] = (int) (values[i] >> 32);
            halves[i * 2 + 1] = (int) values[i];
        }
        into.setIntArray(name, halves);
    }
}
