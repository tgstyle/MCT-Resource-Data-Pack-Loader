package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.world.SavedData;
import mctmods.resourcedatapackloader.util.Longs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContentLocate extends WorldSavedData {
    private static final String NAME = "RDPLLocate";
    private NBTTagCompound placed = new NBTTagCompound();

    public ContentLocate(String name) { super(name); }

    @Override public void readFromNBT(NBTTagCompound nbt) { placed = nbt.getCompoundTag("Placed"); }

    @Override @Nonnull public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("Placed", placed);
        return nbt;
    }

    private static ContentLocate of(World world) { return SavedData.get(world.getPerWorldStorage(), ContentLocate.class, NAME, ContentLocate::new); }

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

    public static BlockPos nearest(World world, String name, BlockPos from) { return nearest(world, name, from, 0.0D); }

    public static BlockPos nearest(World world, String name, BlockPos from, double beyond) {
        long[] known = readLongs(of(world).placed, name);
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        for (long packed : known) {
            BlockPos at = BlockPos.fromLong(packed);
            double away = at.distanceSq(from);
            if (away < beyond * beyond) { continue; }
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
        for (int i = 0; i < out.length; i++) { out[i] = Longs.pack(halves[i * 2], halves[i * 2 + 1]); }
        return out;
    }

    private static void writeLongs(NBTTagCompound into, String name, long[] values) {
        int[] halves = new int[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            halves[i * 2] = Longs.high(values[i]);
            halves[i * 2 + 1] = Longs.low(values[i]);
        }
        into.setIntArray(name, halves);
    }
}
