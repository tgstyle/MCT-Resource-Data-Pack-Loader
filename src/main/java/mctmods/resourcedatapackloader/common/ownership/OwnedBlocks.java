package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.NbtStrings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public final class OwnedBlocks {
    public static final String TAG = "rdpl_owned";
    private static final int VERSION = 1;
    private static final String BLOCKS = "blocks";
    private static final String POS = "pos";
    private static final String BLOCK = "block";
    private static final String OWNER = "owner";
    private static final String EPOCH = "epoch";
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

    public NBTTagCompound write() {
        NBTTagCompound out = new NBTTagCompound();
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
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("v", VERSION);
        tag.setTag(BLOCKS, NbtStrings.write(palette));
        tag.setIntArray(POS, pos);
        tag.setIntArray(BLOCK, block);
        tag.setIntArray(OWNER, owner);
        tag.setInteger(EPOCH, epoch);
        out.setTag(TAG, tag);
        return out;
    }

    public void read(NBTTagCompound in) {
        palette.clear();
        entries.clear();
        epoch = 0;
        if (!in.hasKey(TAG, NBT.TAG_COMPOUND)) { return; }
        NBTTagCompound tag = in.getCompoundTag(TAG);
        epoch = tag.getInteger(EPOCH);
        NbtStrings.read(tag.getTagList(BLOCKS, NBT.TAG_STRING), palette);
        int[] pos = tag.getIntArray(POS);
        int[] block = tag.getIntArray(BLOCK);
        int[] owner = tag.getIntArray(OWNER);
        int count = Math.min(pos.length, Math.min(block.length, owner.length));
        for (int i = 0; i < count; i++) {
            if (block[i] >= 0 && block[i] < palette.size()) { entries.put(pos[i], new Entry(block[i], owner[i])); }
        }
    }
}
