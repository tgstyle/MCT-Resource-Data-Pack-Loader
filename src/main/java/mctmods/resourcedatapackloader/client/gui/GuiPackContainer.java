package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.gui.ContainerPack;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public class GuiPackContainer extends GuiContainer {
    private static final ResourceLocation VANILLA = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int EDGE = 7;
    private static final int HEADER = 17;
    private static final int SLOT = 18;
    private static final int FOOT = 96;
    private static final int FOOT_SOURCE = 126;
    private static final int PLAYER_WIDE = 162;
    private static final int PLAIN_X = 8;
    private static final int PLAIN_Y = 5;
    private static final int PLAIN_W = 16;
    private static final int PLAIN_H = 10;
    private final IInventory held;
    private final int rows;
    private final int columns;
    @Nullable private final ResourceLocation texture;

    public GuiPackContainer(InventoryPlayer player, TileEntityPackContainer tile, @Nullable ContainerDef def) {
        this(new ContainerPack(player, tile), tile, Math.max(1, tile.rows()), Math.max(1, tile.columns()), def);
    }

    @Override protected void keyTyped(char typed, int code) throws java.io.IOException {
        if (code != org.lwjgl.input.Keyboard.KEY_NONE && code == mctmods.resourcedatapackloader.client.PouchKey.code()
                && inventorySlots instanceof mctmods.resourcedatapackloader.content.gui.ContainerPouch
                && ((mctmods.resourcedatapackloader.content.gui.ContainerPouch) inventorySlots).worn() >= 0) {
            mctmods.resourcedatapackloader.network.RDPLNetwork.openWornPouch();
            return;
        }
        super.keyTyped(typed, code);
    }

    public GuiPackContainer(net.minecraft.inventory.Container container, IInventory inventory, int rows, int columns, @Nullable ContainerDef def) {
        super(container);
        this.held = inventory;
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        this.texture = def == null ? null : def.guiTexture;
        this.xSize = def != null && def.guiTexture != null && def.guiWidth > 0 ? def.guiWidth : ContainerPack.width(columns);
        this.ySize = def != null && def.guiTexture != null && def.guiHeight > 0 ? def.guiHeight : ContainerPack.height(rows);
        this.allowUserInput = false;
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = held.getDisplayName().getUnformattedText();
        fontRenderer.drawString(title, 8, 6, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
    }

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
        if (columns <= 9 && rows <= 6) {
            drawTexturedModalRect(x, y, 0, 0, xSize, rows * SLOT + HEADER);
            drawTexturedModalRect(x, y + rows * SLOT + HEADER, 0, FOOT_SOURCE, xSize, FOOT);
            return;
        }
        band(x, y, 0, HEADER);
        for (int row = 0; row < rows; row++) { band(x, y + HEADER + row * SLOT, HEADER, SLOT); }
        foot(x, y + HEADER + rows * SLOT);
    }

    private void band(int x, int y, int source, int tall) {
        drawTexturedModalRect(x, y, 0, source, EDGE, tall);
        for (int column = 0; column < columns; column++) { drawTexturedModalRect(x + EDGE + column * SLOT, y, EDGE, source, SLOT, tall); }
        drawTexturedModalRect(x + xSize - EDGE, y, 169, source, EDGE, tall);
    }

    private void foot(int x, int y) {
        for (int down = 0; down < FOOT; down += PLAIN_H) {
            int tall = Math.min(PLAIN_H, FOOT - down);
            for (int across = EDGE; across < xSize - EDGE; across += PLAIN_W) {
                int wide = Math.min(PLAIN_W, xSize - EDGE - across);
                drawTexturedModalRect(x + across, y + down, PLAIN_X, PLAIN_Y, wide, tall);
            }
        }
        drawTexturedModalRect(x, y, 0, FOOT_SOURCE, EDGE, FOOT);
        drawTexturedModalRect(x + xSize - EDGE, y, 169, FOOT_SOURCE, EDGE, FOOT);
        drawTexturedModalRect(x + (xSize - PLAYER_WIDE) / 2, y, EDGE, FOOT_SOURCE, PLAYER_WIDE, FOOT);
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partial) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partial);
        renderHoveredToolTip(mouseX, mouseY);
    }
}
