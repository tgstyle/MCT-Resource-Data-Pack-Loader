package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record ContainerDef(int rows, int columns, String lootTable, boolean chestModel, @Nullable ResourceLocation chestTexture,
                           @Nullable ResourceLocation guiTexture, int guiWidth, int guiHeight, String curioSlot) {
    public static final int MOST_ROWS = 9;
    public static final int MOST_COLUMNS = 12;
    public static final String ANY_SLOT = "curio";
    public static final String RING_SLOT = "ring";

    public int size() { return rows * columns; }

    public ContainerDef sized(int rows, int columns) { return new ContainerDef(rows, columns, lootTable, chestModel, chestTexture, guiTexture, guiWidth, guiHeight, curioSlot); }

    public boolean worn() { return !curioSlot.isEmpty(); }
}
