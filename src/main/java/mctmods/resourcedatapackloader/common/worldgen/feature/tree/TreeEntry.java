package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class TreeEntry {
    public static final byte PHASE_FELLED = 2;
    public static final byte KIND_STRUCTURE = 0;

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
    public final Map<Integer, BlockState> saplings;
    public final IntSet owned;
    public final IntSet gone;
    public final int hash;
    public final List<BlockState> palette;
    public final int[] blocks;
    public final int[] states;
    public final int[] under;
    public final Map<Integer, CompoundTag> blockEntities;

    public TreeEntry(long pos, String rule, byte phase, int[] release, int pick, byte kind, String source, byte turn, byte mirror, long seed, int step, int steps, long started, long seen, long progress, Map<Integer, BlockState> saplings, IntSet owned, IntSet gone, int hash, List<BlockState> palette, int[] blocks, int[] states, int[] under, Map<Integer, CompoundTag> blockEntities) {
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

    public static int pack(int dx, int dy, int dz) { return (dx + 24) | ((dz + 24) << 6) | ((dy + 16) << 12); }

    public static int unpackX(int packed) { return (packed & 63) - 24; }

    public static int unpackY(int packed) { return ((packed >> 12) & 63) - 16; }

    public static int unpackZ(int packed) { return ((packed >> 6) & 63) - 24; }

    public static TreeEntry read(HolderGetter<Block> blocks, CompoundTag tag) {
        return new TreeEntry(tag.getLongOr("Pos", 0L), tag.getStringOr("Rule", ""), tag.getByteOr("Phase", (byte) 0), tag.getIntArray("Release").orElse(new int[0]), tag.getIntOr("Pick", 0), tag.getByteOr("Kind", (byte) 0), tag.getStringOr("Source", ""), tag.getByteOr("Turn", (byte) 0), tag.getByteOr("Mirror", (byte) 0), tag.getLongOr("Seed", 0L), tag.getIntOr("Step", 0), tag.getIntOr("Steps", 0), tag.getLongOr("Started", 0L), tag.getLongOr("Seen", 0L), tag.getLongOr("Progress", 0L), readSaplings(blocks, tag.getListOrEmpty("Saplings")), new IntOpenHashSet(tag.getIntArray("Owned").orElse(new int[0])), new IntOpenHashSet(tag.getIntArray("Gone").orElse(new int[0])), tag.getIntOr("Hash", 0), readPalette(blocks, tag.getListOrEmpty("Palette")), tag.getIntArray("Blocks").orElse(new int[0]), tag.getIntArray("States").orElse(new int[0]), tag.getIntArray("Under").orElse(new int[0]), readBlockEntities(tag.getListOrEmpty("BlockEntities")));
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Pos", pos);
        tag.putByte("Phase", phase);
        if (release.length > 0) { tag.putIntArray("Release", release); }
        if (phase == PHASE_FELLED) { return tag; }
        tag.putString("Rule", rule);
        tag.putInt("Pick", pick);
        tag.putByte("Kind", kind);
        tag.putString("Source", source);
        tag.putByte("Turn", turn);
        tag.putByte("Mirror", mirror);
        tag.putLong("Seed", seed);
        tag.putInt("Step", step);
        tag.putInt("Steps", steps);
        tag.putLong("Started", started);
        tag.putLong("Seen", seen);
        tag.putLong("Progress", progress);
        tag.put("Saplings", writeSaplings());
        tag.putIntArray("Owned", sorted(owned));
        if (!gone.isEmpty()) { tag.putIntArray("Gone", sorted(gone)); }
        if (kind == KIND_STRUCTURE) { tag.putInt("Hash", hash); }
        if (kind != KIND_STRUCTURE) {
            tag.put("Palette", writePalette());
            tag.putIntArray("Blocks", blocks);
            tag.putIntArray("States", states);
            tag.putIntArray("Under", under);
            tag.put("BlockEntities", writeBlockEntities());
        }
        return tag;
    }

    private static Map<Integer, BlockState> readSaplings(HolderGetter<Block> blocks, ListTag list) {
        Map<Integer, BlockState> saplings = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag sapling = list.getCompoundOrEmpty(i);
            saplings.put(sapling.getIntOr("P", 0), NbtUtils.readBlockState(blocks, sapling.getCompoundOrEmpty("S")));
        }
        return saplings;
    }

    private static List<BlockState> readPalette(HolderGetter<Block> blocks, ListTag list) {
        List<BlockState> palette = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) { palette.add(NbtUtils.readBlockState(blocks, list.getCompoundOrEmpty(i))); }
        return palette;
    }

    private static Map<Integer, CompoundTag> readBlockEntities(ListTag list) {
        Map<Integer, CompoundTag> blockEntities = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompoundOrEmpty(i);
            blockEntities.put(entry.getIntOr("I", 0), entry.getCompoundOrEmpty("Data"));
        }
        return blockEntities;
    }

    private ListTag writeSaplings() {
        ListTag list = new ListTag();
        for (Map.Entry<Integer, BlockState> sapling : saplings.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("P", sapling.getKey());
            tag.put("S", NbtUtils.writeBlockState(sapling.getValue()));
            list.add(tag);
        }
        return list;
    }

    private ListTag writePalette() {
        ListTag list = new ListTag();
        for (BlockState state : palette) { list.add(NbtUtils.writeBlockState(state)); }
        return list;
    }

    private ListTag writeBlockEntities() {
        ListTag list = new ListTag();
        for (Map.Entry<Integer, CompoundTag> entry : blockEntities.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("I", entry.getKey());
            tag.put("Data", entry.getValue());
            list.add(tag);
        }
        return list;
    }

    private static int[] sorted(IntSet set) {
        int[] array = set.toIntArray();
        Arrays.sort(array);
        return array;
    }
}
