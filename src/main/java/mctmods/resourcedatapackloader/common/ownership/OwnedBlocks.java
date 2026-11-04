package mctmods.resourcedatapackloader.common.ownership;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class OwnedBlocks {
    public static final String TAG = "rdpl_owned";
    private static final int VERSION = 1;
    private static final Codec<int[]> INTS = Codec.INT_STREAM.xmap(IntStream::toArray, Arrays::stream);
    private static final Codec<OwnedBlocks> BODY = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("v").orElse(VERSION).forGetter(_ -> VERSION),
            Codec.STRING.listOf().fieldOf("blocks").forGetter(held -> held.palette),
            INTS.fieldOf("pos").forGetter(OwnedBlocks::positions),
            INTS.fieldOf("block").forGetter(OwnedBlocks::blocks),
            INTS.fieldOf("owner").forGetter(OwnedBlocks::owners),
            Codec.INT.fieldOf("epoch").orElse(0).forGetter(OwnedBlocks::epoch)).apply(instance, (_, names, pos, block, owner, epoch) -> restored(names, pos, block, owner, epoch)));
    public static final MapCodec<OwnedBlocks> CODEC = BODY.fieldOf(TAG);
    private final List<String> palette = new ArrayList<>();
    private final Map<Integer, Entry> entries = new HashMap<>();
    private int epoch;

    public static final class Entry {
        private final int block;
        private final int handle;

        private Entry(int block, int handle) {
            this.block = block;
            this.handle = handle;
        }

        public int handle() { return handle; }
    }

    private static OwnedBlocks restored(List<String> names, int[] pos, int[] block, int[] owner, int epoch) {
        OwnedBlocks blocks = new OwnedBlocks();
        blocks.epoch = epoch;
        blocks.palette.addAll(names);
        int count = Math.min(pos.length, Math.min(block.length, owner.length));
        for (int i = 0; i < count; i++) {
            if (block[i] >= 0 && block[i] < blocks.palette.size()) { blocks.entries.put(pos[i], new Entry(block[i], owner[i])); }
        }
        return blocks;
    }

    public void put(int pos, String blockName, int handle) { entries.put(pos, new Entry(paletteIndex(blockName), handle)); }

    public void remove(int pos) { entries.remove(pos); }

    public Map<Integer, Entry> entries() { return Collections.unmodifiableMap(entries); }

    public boolean isEmpty() { return entries.isEmpty(); }

    public String blockName(Entry entry) { return palette.get(entry.block); }

    public int epoch() { return epoch; }

    public void setEpoch(int epoch) { this.epoch = epoch; }

    private int paletteIndex(String blockName) {
        int index = palette.indexOf(blockName);
        if (index >= 0) { return index; }
        palette.add(blockName);
        return palette.size() - 1;
    }

    private int[] positions() { return entries.keySet().stream().mapToInt(Integer::intValue).toArray(); }

    private int[] blocks() { return entries.values().stream().mapToInt(entry -> entry.block).toArray(); }

    private int[] owners() { return entries.values().stream().mapToInt(entry -> entry.handle).toArray(); }
}
