package mctmods.resourcedatapackloader.common.util.world;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.util.Constants;

public final class TemplateBlocks {
    private TemplateBlocks() {}

    public static final class Entry {
        public final BlockPos pos;
        @Nullable public final NBTTagCompound nbt;
        private final List<IBlockState> palette;
        private final int index;

        private Entry(BlockPos pos, List<IBlockState> palette, int index, @Nullable NBTTagCompound nbt) {
            this.pos = pos;
            this.palette = palette;
            this.index = index;
            this.nbt = nbt;
        }

        public IBlockState state() { return palette.get(index); }
    }

    public static List<IBlockState> palette(Template held) { return palette(held.writeToNBT(new NBTTagCompound())); }

    private static List<IBlockState> palette(NBTTagCompound saved) {
        NBTTagList tag = saved.getTagList("palette", Constants.NBT.TAG_COMPOUND);
        List<IBlockState> states = new ArrayList<>();
        for (int i = 0; i < tag.tagCount(); i++) { states.add(NBTUtil.readBlockState(tag.getCompoundTagAt(i))); }
        return states;
    }

    public static List<Entry> blocks(Template held) {
        NBTTagCompound saved = held.writeToNBT(new NBTTagCompound());
        List<IBlockState> palette = palette(saved);
        NBTTagList list = saved.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            NBTTagList at = entry.getTagList("pos", Constants.NBT.TAG_INT);
            entries.add(new Entry(new BlockPos(at.getIntAt(0), at.getIntAt(1), at.getIntAt(2)), palette, entry.getInteger("state"), entry.hasKey("nbt") ? entry.getCompoundTag("nbt") : null));
        }
        return entries;
    }

    public static List<NBTTagCompound> entities(Template held) {
        NBTTagList list = held.writeToNBT(new NBTTagCompound()).getTagList("entities", Constants.NBT.TAG_COMPOUND);
        List<NBTTagCompound> entities = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) { entities.add(list.getCompoundTagAt(i).getCompoundTag("nbt")); }
        return entities;
    }
}
