package mctmods.resourcedatapackloader.common.pack.definitions.containers;

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
    public final boolean keepContents;
    public final boolean ender;
    public final boolean enderAutomation;

    public static ContainerDef plain(int rows) { return new ContainerDef(rows, 9, "", false, null, null, 0, 0, false); }

    public ContainerDef(int rows, int columns, String lootTable, boolean chestModel, @Nullable ResourceLocation chestTexture, @Nullable ResourceLocation guiTexture, int guiWidth, int guiHeight, boolean keepContents) { this(rows, columns, lootTable, chestModel, chestTexture, guiTexture, guiWidth, guiHeight, keepContents, false, false); }

    public ContainerDef(int rows, int columns, String lootTable, boolean chestModel, @Nullable ResourceLocation chestTexture, @Nullable ResourceLocation guiTexture, int guiWidth, int guiHeight, boolean keepContents, boolean ender, boolean enderAutomation) {
        this.rows = rows;
        this.columns = columns;
        this.lootTable = lootTable;
        this.chestModel = chestModel;
        this.chestTexture = chestTexture;
        this.guiTexture = guiTexture;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.keepContents = keepContents;
        this.ender = ender;
        this.enderAutomation = enderAutomation;
    }

    public int size() { return rows * columns; }
}
