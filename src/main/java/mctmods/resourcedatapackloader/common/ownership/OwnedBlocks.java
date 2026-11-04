package mctmods.resourcedatapackloader.common.ownership;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class OwnedBlocks {
    public static final String TAG = "rdpl_owned";
    private static final int VERSION = 1;
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

    public CompoundTag write() {
        CompoundTag out = new CompoundTag();
        if (entries.isEmpty()) { return out; }
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
        ListTag names = new ListTag();
        for (String name : palette) { names.add(StringTag.valueOf(name)); }
        CompoundTag tag = new CompoundTag();
        tag.putInt("v", VERSION);
        tag.put("blocks", names);
        tag.putIntArray("pos", pos);
        tag.putIntArray("block", block);
        tag.putIntArray("owner", owner);
        tag.putInt("epoch", epoch);
        out.put(TAG, tag);
        return out;
    }

    public void read(CompoundTag in) {
        palette.clear();
        entries.clear();
        epoch = 0;
        if (!in.contains(TAG, Tag.TAG_COMPOUND)) { return; }
        CompoundTag tag = in.getCompound(TAG);
        epoch = tag.getInt("epoch");
        ListTag names = tag.getList("blocks", Tag.TAG_STRING);
        for (int i = 0; i < names.size(); i++) { palette.add(names.getString(i)); }
        int[] pos = tag.getIntArray("pos");
        int[] block = tag.getIntArray("block");
        int[] owner = tag.getIntArray("owner");
        int count = Math.min(pos.length, Math.min(block.length, owner.length));
        for (int i = 0; i < count; i++) {
            if (block[i] >= 0 && block[i] < palette.size()) { entries.put(pos[i], new Entry(block[i], owner[i])); }
        }
    }
}
