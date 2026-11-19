package mctmods.resourcedatapackloader.common.pack.definitions.items;

import net.minecraft.util.ResourceLocation;

public final class RecordDef {
    public final ResourceLocation sound;
    public final int comparator;

    public RecordDef(ResourceLocation sound, int comparator) {
        this.sound = sound;
        this.comparator = comparator;
    }
}
