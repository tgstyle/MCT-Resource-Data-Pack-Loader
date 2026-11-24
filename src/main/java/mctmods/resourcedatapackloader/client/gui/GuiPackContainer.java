package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.client.util.PouchKey;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.gui.ContainerPack;
import mctmods.resourcedatapackloader.common.gui.ContainerPouch;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

public class GuiPackContainer extends GuiContainer {
    private static final ResourceLocation VANILLA = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int TITLE_TEXT = 0x404040;
    protected static final int EDGE = 7;
    private static final int HEADER = ContainerPack.HEADER;
    private static final int SLOT = ContainerPack.SLOT;
    private static final int FOOT = 96;
    private static final int FOOT_SOURCE = 126;
    private static final int RIGHT_SOURCE = 169;
    private static final int SILL = 3;
    private static final int SILL_SOURCE = FOOT_SOURCE + FOOT - SILL;
    private static final int PLAYER_WIDE = 162;
    private static final int PLAIN_X = 8;
    private static final int PLAIN_Y = 5;
    private static final int PLAIN_W = 16;
    private static final int PLAIN_H = 10;
    private final IInventory held;
    private final int rows;
    private final int columns;
    private static final String ELLIPSIS = "...";
    private int slots = -1;
    @Nullable private String cutTitle;
    @Nullable private final ResourceLocation texture;

    public GuiPackContainer(InventoryPlayer player, TileEntityPackContainer tile, @Nullable ContainerDef def) { this(new ContainerPack(player, tile), tile, Math.max(1, tile.rows()), Math.max(1, tile.columns()), def); }

    @Override protected void keyTyped(char typed, int code) throws IOException {
        if (code != Keyboard.KEY_NONE && code == PouchKey.code()
                && inventorySlots instanceof ContainerPouch
                && ((ContainerPouch) inventorySlots).worn() >= 0) {
            RDPLNetwork.openWornPouch();
            return;
        }
        super.keyTyped(typed, code);
    }

    public GuiPackContainer(Container container, IInventory inventory, int rows, int columns, @Nullable ContainerDef def) {
        super(container);
        this.held = inventory;
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        this.texture = def == null ? null : def.guiTexture;
        this.xSize = def != null && def.guiTexture != null && def.guiWidth > 0 ? def.guiWidth : ContainerPack.width(this.columns);
        this.ySize = def != null && def.guiTexture != null && def.guiHeight > 0 ? def.guiHeight : ContainerPack.height(this.rows);
        this.allowUserInput = false;
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = held.getDisplayName().getUnformattedText();
        String shown = title;
        cutTitle = null;
        if (fontRenderer.getStringWidth(title) > titleWidth()) {
            cutTitle = title;
            shown = fontRenderer.trimStringToWidth(title, titleWidth() - fontRenderer.getStringWidth(ELLIPSIS)) + ELLIPSIS;
        }
        fontRenderer.drawString(shown, 8, 6, TITLE_TEXT);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, TITLE_TEXT);
    }

    protected int titleWidth() { return xSize - 16; }

    @Override protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        if (texture != null) {
            mc.getTextureManager().bindTexture(texture);
            drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
            return;
        }
        mc.getTextureManager().bindTexture(VANILLA);
        head(x, y);
        for (int row = 0; row < rows; row++) { band(x, y + HEADER + row * SLOT, cellsIn(row)); }
        foot(x, y + HEADER + rows * SLOT);
    }

    private void head(int x, int y) {
        drawTexturedModalRect(x, y, 0, 0, EDGE, HEADER);
        tile(x + EDGE, y, xSize - EDGE - EDGE, HEADER, EDGE, 0, SLOT, HEADER);
        drawTexturedModalRect(x + xSize - EDGE, y, RIGHT_SOURCE, 0, EDGE, HEADER);
    }

    protected final void limitSlots(int count) { this.slots = count; }

    private int cellsIn(int row) { return slots < 0 ? columns : MathHelper.clamp(slots - row * columns, 0, columns); }

    private void band(int x, int y, int count) {
        int cells = count * SLOT;
        int start = (xSize - cells) / 2;
        drawTexturedModalRect(x, y, 0, HEADER, EDGE, SLOT);
        plain(x + EDGE, y, start - EDGE, SLOT);
        for (int column = 0; column < count; column++) { drawTexturedModalRect(x + start + column * SLOT, y, EDGE, HEADER, SLOT, SLOT); }
        plain(x + start + cells, y, xSize - EDGE - start - cells, SLOT);
        drawTexturedModalRect(x + xSize - EDGE, y, RIGHT_SOURCE, HEADER, EDGE, SLOT);
    }

    private void foot(int x, int y) {
        plain(x + EDGE, y, xSize - EDGE - EDGE, FOOT);
        sill(x, y + FOOT - SILL);
        drawTexturedModalRect(x, y, 0, FOOT_SOURCE, EDGE, FOOT);
        drawTexturedModalRect(x + xSize - EDGE, y, RIGHT_SOURCE, FOOT_SOURCE, EDGE, FOOT);
        drawTexturedModalRect(x + (xSize - PLAYER_WIDE) / 2, y, EDGE, FOOT_SOURCE, PLAYER_WIDE, FOOT);
    }

    private void sill(int x, int y) { tile(x + EDGE, y, xSize - EDGE - EDGE, SILL, EDGE, SILL_SOURCE, SLOT, SILL); }

    private void plain(int x, int y, int wide, int tall) { tile(x, y, wide, tall, PLAIN_X, PLAIN_Y, PLAIN_W, PLAIN_H); }

    private void tile(int x, int y, int wide, int tall, int sourceX, int sourceY, int tileW, int tileH) {
        for (int down = 0; down < tall; down += tileH) {
            int high = Math.min(tileH, tall - down);
            for (int across = 0; across < wide; across += tileW) { drawTexturedModalRect(x + across, y + down, sourceX, sourceY, Math.min(tileW, wide - across), high); }
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partial) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partial);
        renderHoveredToolTip(mouseX, mouseY);
        int across = mouseX - guiLeft;
        int down = mouseY - guiTop;
        if (cutTitle != null && MathUtil.inside(across, down, 8, 6, titleWidth(), fontRenderer.FONT_HEIGHT)) { drawHoveringText(cutTitle, mouseX, mouseY); }
    }
}
