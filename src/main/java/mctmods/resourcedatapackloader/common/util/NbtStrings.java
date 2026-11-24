package mctmods.resourcedatapackloader.common.util;

import java.util.Collection;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public final class NbtStrings {
    private NbtStrings() {}

    public static NBTTagList write(Iterable<String> values) {
        NBTTagList list = new NBTTagList();
        for (String value : values) { list.appendTag(new NBTTagString(value)); }
        return list;
    }

    public static void read(NBTTagList list, Collection<String> into) {
        for (int i = 0; i < list.tagCount(); i++) { into.add(list.getStringTagAt(i)); }
    }
}
