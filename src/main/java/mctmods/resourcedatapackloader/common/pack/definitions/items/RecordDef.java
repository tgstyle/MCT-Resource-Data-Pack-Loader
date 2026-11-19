package mctmods.resourcedatapackloader.common.pack.definitions.items;

import net.minecraft.resources.ResourceLocation;

public record RecordDef(ResourceLocation sound, int ticks, int comparator) {
    public static final int UNENDING_TICKS = 20000000;
    public static final int MOST_TICKS = 20000000;
    public static final int DEFAULT_COMPARATOR = 1;

    public float seconds() { return ticks / 20.0F; }

    public int level() { return comparator < 0 ? DEFAULT_COMPARATOR : comparator; }
}
