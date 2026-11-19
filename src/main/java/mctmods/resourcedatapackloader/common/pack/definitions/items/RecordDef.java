package mctmods.resourcedatapackloader.common.pack.definitions.items;

import net.minecraft.resources.ResourceLocation;

public record RecordDef(ResourceLocation sound, int ticks, int comparator) {
    public int length() { return ticks < 1 ? Integer.MAX_VALUE : ticks; }

    public int level() { return comparator < 0 ? 1 : comparator; }
}
