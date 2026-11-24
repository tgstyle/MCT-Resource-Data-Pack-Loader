package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public final class TreeEntry {
    public static final byte PHASE_SAPLING = 0;
    public static final byte PHASE_GROWING = 1;
    public static final byte PHASE_FELLED = 2;
    public static final byte KIND_STRUCTURE = 0;
    public static final int ORIGIN = pack(0, 0, 0);

    public final long pos;
    public final String rule;
    public byte phase;
    public int[] release;
    public int pick;
    public final byte kind;
    public final String source;
    public final byte turn;
    public final byte mirror;
    public final long seed;
    public int step;
    public int steps;
    public final long started;
    public long seen;
    public long progress;
    public final Map<Integer, IBlockState> saplings;
    public final IntSet owned;
    public final IntSet gone;
    public final int hash;
    public final List<IBlockState> palette;
    public final int[] blocks;
    public final int[] states;
    public final int[] under;
    public final Map<Integer, NBTTagCompound> blockEntities;

    public TreeEntry(long pos, String rule, byte phase, int[] release, int pick, byte kind, String source, byte turn, byte mirror, long seed, int step, int steps, long started, long seen, long progress, Map<Integer, IBlockState> saplings, IntSet owned, IntSet gone, int hash, List<IBlockState> palette, int[] blocks, int[] states, int[] under, Map<Integer, NBTTagCompound> blockEntities) {
        this.pos = pos;
        this.rule = rule;
        this.phase = phase;
        this.release = release;
        this.pick = pick;
        this.kind = kind;
        this.source = source;
        this.turn = turn;
        this.mirror = mirror;
        this.seed = seed;
        this.step = step;
        this.steps = steps;
        this.started = started;
        this.seen = seen;
        this.progress = progress;
        this.saplings = saplings;
        this.owned = owned;
        this.gone = gone;
        this.hash = hash;
        this.palette = palette;
        this.blocks = blocks;
        this.states = states;
        this.under = under;
        this.blockEntities = blockEntities;
    }

    public static TreeEntry fresh(long pos, String rule, byte phase, int pick, byte kind, String source, byte turn, byte mirror, long seed, int steps, long now, Map<Integer, IBlockState> saplings, int hash, List<IBlockState> palette, int[] blocks, int[] states, int[] under, Map<Integer, NBTTagCompound> blockEntities) {
        return new TreeEntry(pos, rule, phase, new int[0], pick, kind, source, turn, mirror, seed, 0, steps, now, now, 0L, saplings, new IntOpenHashSet(), new IntOpenHashSet(), hash, palette, blocks, states, under, blockEntities);
    }

    public static int pack(int dx, int dy, int dz) { return (dx + 24) | ((dz + 24) << 6) | ((dy + 16) << 12); }

    public static int pack(BlockPos rel) { return pack(rel.getX(), rel.getY(), rel.getZ()); }

    public static BlockPos unpack(int packed) { return new BlockPos(unpackX(packed), unpackY(packed), unpackZ(packed)); }

    public static BlockPos at(BlockPos start, int packed) { return start.add(unpackX(packed), unpackY(packed), unpackZ(packed)); }

    @Nullable public IBlockState originSapling() { return saplings.get(ORIGIN); }

    public static int unpackX(int packed) { return (packed & 63) - 24; }

    public static int unpackY(int packed) { return ((packed >> 12) & 63) - 16; }

    public static int unpackZ(int packed) { return ((packed >> 6) & 63) - 24; }

    public static TreeEntry read(NBTTagCompound tag) {
        return new TreeEntry(tag.getLong("Pos"), tag.getString("Rule"), tag.getByte("Phase"), tag.getIntArray("Release"), tag.getInteger("Pick"), tag.getByte("Kind"), tag.getString("Source"), tag.getByte("Turn"), tag.getByte("Mirror"), tag.getLong("Seed"), tag.getInteger("Step"), tag.getInteger("Steps"), tag.getLong("Started"), tag.getLong("Seen"), tag.getLong("Progress"), readSaplings(tag.getTagList("Saplings", Constants.NBT.TAG_COMPOUND)), new IntOpenHashSet(tag.getIntArray("Owned")), new IntOpenHashSet(tag.getIntArray("Gone")), tag.getInteger("Hash"), readPalette(tag.getTagList("Palette", Constants.NBT.TAG_COMPOUND)), tag.getIntArray("Blocks"), tag.getIntArray("States"), tag.getIntArray("Under"), readBlockEntities(tag.getTagList("BlockEntities", Constants.NBT.TAG_COMPOUND)));
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("Pos", pos);
        tag.setByte("Phase", phase);
        if (release.length > 0) { tag.setIntArray("Release", release); }
        if (phase == PHASE_FELLED) { return tag; }
        tag.setString("Rule", rule);
        tag.setInteger("Pick", pick);
        tag.setByte("Kind", kind);
        tag.setString("Source", source);
        tag.setByte("Turn", turn);
        tag.setByte("Mirror", mirror);
        tag.setLong("Seed", seed);
        tag.setInteger("Step", step);
        tag.setInteger("Steps", steps);
        tag.setLong("Started", started);
        tag.setLong("Seen", seen);
        tag.setLong("Progress", progress);
        tag.setTag("Saplings", writeSaplings());
        tag.setIntArray("Owned", sorted(owned));
        if (!gone.isEmpty()) { tag.setIntArray("Gone", sorted(gone)); }
        if (kind == KIND_STRUCTURE) { tag.setInteger("Hash", hash); }
        if (kind != KIND_STRUCTURE) {
            tag.setTag("Palette", writePalette());
            tag.setIntArray("Blocks", blocks);
            tag.setIntArray("States", states);
            tag.setIntArray("Under", under);
            tag.setTag("BlockEntities", writeBlockEntities());
        }
        return tag;
    }

    private static <V> Map<Integer, V> readKeyed(NBTTagList list, String key, String value, Function<NBTTagCompound, V> decode) {
        Map<Integer, V> keyed = new LinkedHashMap<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            keyed.put(entry.getInteger(key), decode.apply(entry.getCompoundTag(value)));
        }
        return keyed;
    }

    private static <V> NBTTagList writeKeyed(Map<Integer, V> map, String key, String value, Function<V, NBTBase> encode) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, V> entry : map.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(key, entry.getKey());
            tag.setTag(value, encode.apply(entry.getValue()));
            list.appendTag(tag);
        }
        return list;
    }

    private static Map<Integer, IBlockState> readSaplings(NBTTagList list) { return readKeyed(list, "P", "S", NBTUtil::readBlockState); }

    private static List<IBlockState> readPalette(NBTTagList list) {
        List<IBlockState> palette = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) { palette.add(NBTUtil.readBlockState(list.getCompoundTagAt(i))); }
        return palette;
    }

    private static Map<Integer, NBTTagCompound> readBlockEntities(NBTTagList list) { return readKeyed(list, "I", "Data", data -> data); }

    private NBTTagList writeSaplings() { return writeKeyed(saplings, "P", "S", state -> NBTUtil.writeBlockState(new NBTTagCompound(), state)); }

    private NBTTagList writePalette() {
        NBTTagList list = new NBTTagList();
        for (IBlockState state : palette) { list.appendTag(NBTUtil.writeBlockState(new NBTTagCompound(), state)); }
        return list;
    }

    private NBTTagList writeBlockEntities() { return writeKeyed(blockEntities, "I", "Data", data -> data); }

    private static int[] sorted(IntSet set) {
        int[] array = set.toIntArray();
        Arrays.sort(array);
        return array;
    }
}
