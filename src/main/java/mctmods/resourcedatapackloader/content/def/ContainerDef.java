package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public final class ContainerDef {
    public static final int MOST_ROWS = 9;
    public static final int MOST_COLUMNS = 12;
    public final int rows;
    public final int columns;
    public final String lootTable;
    public final boolean chestModel;
    @Nullable public final ResourceLocation chestTexture;
    @Nullable public final ResourceLocation guiTexture;
    public final int guiWidth;
    public final int guiHeight;
    public final String bauble;

    public ContainerDef(int rows, int columns, String lootTable, boolean chestModel, @Nullable ResourceLocation chestTexture, @Nullable ResourceLocation guiTexture, int guiWidth, int guiHeight, String bauble) {
        this.rows = rows;
        this.columns = columns;
        this.lootTable = lootTable;
        this.chestModel = chestModel;
        this.chestTexture = chestTexture;
        this.guiTexture = guiTexture;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.bauble = bauble;
    }

    public int size() { return rows * columns; }

    public boolean drawn() { return guiTexture == null; }
}
