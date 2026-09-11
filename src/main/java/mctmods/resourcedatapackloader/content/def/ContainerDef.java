package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record ContainerDef(int rows, int columns, String lootTable, boolean chestModel, @Nullable ResourceLocation chestTexture,
                           @Nullable ResourceLocation guiTexture, int guiWidth, int guiHeight, String curioSlot) {
    public static final int MOST_ROWS = 9;
    public static final int MOST_COLUMNS = 12;

    public int size() { return rows * columns; }

    public boolean worn() { return !curioSlot.isEmpty(); }
}
