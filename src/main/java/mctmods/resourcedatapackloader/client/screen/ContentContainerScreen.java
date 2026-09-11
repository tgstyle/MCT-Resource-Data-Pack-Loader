package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ContentContainerScreen extends AbstractContainerScreen<ContentContainerMenu> {
    private static final ResourceLocation SHEET = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
    private static final int SHEET_WIDE = 256;
    private static final int SHEET_TALL = 256;
    private static final int EDGE = 7;
    private static final int HEADER = 17;
    private static final int CELL = ContentContainerMenu.SLOT;
    private static final int PANEL = 176;
    private static final int PLAYER_AT = 125;
    private static final int PLAYER_TALL = 97;
    private static final int PLAIN_Y = 4;
    private static final int PLAIN_TALL = 12;
    private final ContainerDef def;

    public ContentContainerScreen(ContentContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.def = menu.def();
        this.imageWidth = ContentContainerMenu.width(def);
        this.imageHeight = ContentContainerMenu.height(def);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        renderBackground(graphics, mouseX, mouseY, partial);
        super.render(graphics, mouseX, mouseY, partial);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(@Nonnull GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        ResourceLocation own = def.guiTexture();
        if (own != null) {
            graphics.blit(own, leftPos, topPos, 0, 0, imageWidth, imageHeight, def.guiWidth(), def.guiHeight());
            return;
        }
        int left = leftPos;
        int top = topPos;
        int inner = def.columns() * CELL;
        strip(graphics, left, top, 0, HEADER, inner);
        for (int row = 0; row < def.rows(); row++) { strip(graphics, left, top + HEADER + row * CELL, HEADER, CELL, inner); }
        int bottom = top + HEADER + def.rows() * CELL;
        int player = left + (imageWidth - PANEL) / 2;
        plain(graphics, left, bottom, inner);
        graphics.blit(SHEET, left, bottom, 0, PLAYER_AT, EDGE, PLAYER_TALL, SHEET_WIDE, SHEET_TALL);
        graphics.blit(SHEET, player + EDGE, bottom, EDGE, PLAYER_AT, PANEL - 2 * EDGE, PLAYER_TALL, SHEET_WIDE, SHEET_TALL);
        graphics.blit(SHEET, left + imageWidth - EDGE, bottom, PANEL - EDGE, PLAYER_AT, EDGE, PLAYER_TALL, SHEET_WIDE, SHEET_TALL);
    }

    private void plain(GuiGraphics graphics, int x, int y, int inner) {
        int down = 0;
        while (down < PLAYER_TALL) {
            int rows = Math.min(PLAIN_TALL, PLAYER_TALL - down);
            int at = 0;
            while (at < inner) {
                int wide = Math.min(CELL, inner - at);
                graphics.blit(SHEET, x + EDGE + at, y + down, EDGE, PLAIN_Y, wide, rows, SHEET_WIDE, SHEET_TALL);
                at += wide;
            }
            down += rows;
        }
    }

    private void strip(GuiGraphics graphics, int x, int y, int sheetY, int tall, int inner) {
        graphics.blit(SHEET, x, y, 0, sheetY, EDGE, tall, SHEET_WIDE, SHEET_TALL);
        int at = 0;
        while (at < inner) {
            int wide = Math.min(CELL, inner - at);
            graphics.blit(SHEET, x + EDGE + at, y, EDGE, sheetY, wide, tall, SHEET_WIDE, SHEET_TALL);
            at += wide;
        }
        graphics.blit(SHEET, x + EDGE + inner, y, PANEL - EDGE, sheetY, EDGE, tall, SHEET_WIDE, SHEET_TALL);
    }
}
