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
    private static final int EDGE = 7;
    private static final int HEADER = 17;
    private static final int CELL = ContentContainerMenu.SLOT;
    private static final int FOOT = 96;
    private static final int FOOT_SOURCE = 126;
    private static final int RIGHT_SOURCE = 169;
    private static final int SILL = 3;
    private static final int SILL_SOURCE = FOOT_SOURCE + FOOT - SILL;
    private static final int PLAYER_WIDE = 162;
    private static final int PLAIN_X = 8;
    private static final int PLAIN_Y = 5;
    private static final int PLAIN_WIDE = 16;
    private static final int PLAIN_TALL = 10;
    private final ContainerDef def;

    public ContentContainerScreen(ContentContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.def = menu.def();
        this.imageWidth = def.guiTexture() != null ? def.guiWidth() : ContentContainerMenu.width(def);
        this.imageHeight = def.guiTexture() != null ? def.guiHeight() : ContentContainerMenu.height(def);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partial);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(@Nonnull GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        ResourceLocation own = def.guiTexture();
        if (own != null) {
            graphics.blit(own, leftPos, topPos, 0, 0, imageWidth, imageHeight);
            return;
        }
        frame(graphics, leftPos, topPos, imageWidth, def.rows(), def.columns());
    }

    public static void frame(GuiGraphics graphics, int left, int top, int wide, int rows, int columns) {
        head(graphics, left, top, wide);
        for (int row = 0; row < rows; row++) { band(graphics, left, top + HEADER + row * CELL, wide, columns); }
        foot(graphics, left, top + HEADER + rows * CELL, wide);
    }

    private static void head(GuiGraphics graphics, int left, int y, int wide) {
        graphics.blit(SHEET, left, y, 0, 0, EDGE, HEADER);
        for (int across = EDGE; across < wide - EDGE; across += CELL) {
            graphics.blit(SHEET, left + across, y, EDGE, 0, Math.min(CELL, wide - EDGE - across), HEADER);
        }
        graphics.blit(SHEET, left + wide - EDGE, y, RIGHT_SOURCE, 0, EDGE, HEADER);
    }

    private static void band(GuiGraphics graphics, int left, int y, int wide, int columns) {
        int cells = columns * CELL;
        int start = (wide - cells) / 2;
        graphics.blit(SHEET, left, y, 0, HEADER, EDGE, CELL);
        plain(graphics, left + EDGE, y, start - EDGE, CELL);
        for (int column = 0; column < columns; column++) { graphics.blit(SHEET, left + start + column * CELL, y, EDGE, HEADER, CELL, CELL); }
        plain(graphics, left + start + cells, y, wide - EDGE - start - cells, CELL);
        graphics.blit(SHEET, left + wide - EDGE, y, RIGHT_SOURCE, HEADER, EDGE, CELL);
    }

    private static void foot(GuiGraphics graphics, int left, int y, int wide) {
        plain(graphics, left + EDGE, y, wide - EDGE - EDGE, FOOT);
        sill(graphics, left, y + FOOT - SILL, wide);
        graphics.blit(SHEET, left, y, 0, FOOT_SOURCE, EDGE, FOOT);
        graphics.blit(SHEET, left + wide - EDGE, y, RIGHT_SOURCE, FOOT_SOURCE, EDGE, FOOT);
        graphics.blit(SHEET, left + (wide - PLAYER_WIDE) / 2, y, EDGE, FOOT_SOURCE, PLAYER_WIDE, FOOT);
    }

    private static void sill(GuiGraphics graphics, int left, int y, int wide) {
        for (int across = EDGE; across < wide - EDGE; across += CELL) {
            graphics.blit(SHEET, left + across, y, EDGE, SILL_SOURCE, Math.min(CELL, wide - EDGE - across), SILL);
        }
    }

    private static void plain(GuiGraphics graphics, int x, int y, int wide, int tall) {
        for (int down = 0; down < tall; down += PLAIN_TALL) {
            int high = Math.min(PLAIN_TALL, tall - down);
            for (int across = 0; across < wide; across += PLAIN_WIDE) {
                graphics.blit(SHEET, x + across, y + down, PLAIN_X, PLAIN_Y, Math.min(PLAIN_WIDE, wide - across), high);
            }
        }
    }
}
