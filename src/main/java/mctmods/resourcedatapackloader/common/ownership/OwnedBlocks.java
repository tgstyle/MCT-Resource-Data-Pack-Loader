package mctmods.resourcedatapackloader.common.ownership;

import com.mojang.serialization.Codec;
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
    private static final Codec<Raw> RAW = RecordCodecBuilder.<Raw>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("v").orElse(VERSION).forGetter(Raw::v),
            Codec.STRING.listOf().fieldOf("blocks").forGetter(Raw::blocks),
            INTS.fieldOf("pos").forGetter(Raw::pos),
            INTS.fieldOf("block").forGetter(Raw::block),
            INTS.fieldOf("owner").forGetter(Raw::owner),
            Codec.INT.fieldOf("epoch").orElse(0).forGetter(Raw::epoch)).apply(instance, Raw::new)).codec().fieldOf(TAG).codec();
    public static final Codec<OwnedBlocks> CODEC = RAW.xmap(OwnedBlocks::from, OwnedBlocks::raw);
    private final List<String> palette = new ArrayList<>();
    private final Map<Integer, Entry> entries = new HashMap<>();
    private int epoch;

    private record Raw(int v, List<String> blocks, int[] pos, int[] block, int[] owner, int epoch) {}

    public static final class Entry {
        private final int block;
        private final int handle;

        private Entry(int block, int handle) {
            this.block = block;
            this.handle = handle;
        }

        public int handle() { return handle; }
    }

    public void put(int pos, String blockName, int handle) { entries.put(pos, new Entry(paletteIndex(blockName), handle)); }

    public void remove(int pos) { entries.remove(pos); }

    public Map<Integer, Entry> entries() { return Collections.unmodifiableMap(entries); }

    public String blockName(Entry entry) { return palette.get(entry.block); }

    public int epoch() { return epoch; }

    public void setEpoch(int epoch) { this.epoch = epoch; }

    private int paletteIndex(String blockName) {
        int index = palette.indexOf(blockName);
        if (index >= 0) { return index; }
        palette.add(blockName);
        return palette.size() - 1;
    }

    private Raw raw() {
        int[] pos = new int[entries.size()];
        int[] block = new int[entries.size()];
        int[] owner = new int[entries.size()];
        int at = 0;
        for (Map.Entry<Integer, Entry> each : entries.entrySet()) {
            pos[at] = each.getKey();
            block[at] = each.getValue().block;
            owner[at] = each.getValue().handle;
            at++;
        }
        return new Raw(VERSION, List.copyOf(palette), pos, block, owner, epoch);
    }

    private static OwnedBlocks from(Raw raw) {
        OwnedBlocks read = new OwnedBlocks();
        read.epoch = raw.epoch();
        read.palette.addAll(raw.blocks());
        int count = Math.min(raw.pos().length, Math.min(raw.block().length, raw.owner().length));
        for (int i = 0; i < count; i++) {
            if (raw.block()[i] >= 0 && raw.block()[i] < read.palette.size()) { read.entries.put(raw.pos()[i], new Entry(raw.block()[i], raw.owner()[i])); }
        }
        return read;
    }
}
