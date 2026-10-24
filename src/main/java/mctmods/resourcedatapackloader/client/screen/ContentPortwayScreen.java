package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import javax.annotation.Nonnull;

public final class ContentPortwayScreen extends AbstractContainerScreen<ContentPortwayMenu> {
    private static final int FIELD_WIDE = 80;
    private static final int FIELD_HIGH = 10;
    private static final int FIELD_TOP = 4;
    private static final int QUIET = 4;
    private static final int LABEL = -12566464;
    private final EditBox search;
    private int quiet = -1;

    public ContentPortwayScreen(ContentPortwayMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, ContentContainerMenu.width(ContentPortwayMenu.SHAPE), ContentContainerMenu.height(ContentPortwayMenu.SHAPE));
        search = new EditBox(Minecraft.getInstance().font, FIELD_WIDE, FIELD_HIGH, Component.translatable("rdpl.portway.search"));
        search.setMaxLength(ContentPortwayMenu.FILTER_MAX);
        search.setHint(Component.translatable("rdpl.portway.search"));
        search.setResponder(_ -> quiet = 0);
    }

    @Override protected void init() {
        super.init();
        search.setPosition(leftPos + imageWidth - 8 - FIELD_WIDE, topPos + FIELD_TOP);
        addRenderableWidget(search);
    }

    @Override protected void containerTick() {
        if (quiet >= 0 && ++quiet >= QUIET) { send(); }
    }

    private void send() {
        quiet = -1;
        RDPLNetwork.portwayFilter(search.getValue());
    }

    @Override public boolean keyPressed(@Nonnull KeyEvent event) {
        if (search.isFocused() && !event.isEscape()) {
            if (event.isConfirmation()) { send(); }
            else { search.keyPressed(event); }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean doubleClick) {
        boolean inside = search.isMouseOver(event.x(), event.y());
        if (inside && event.button() == 1) { search.setValue(""); setFocused(search); return true; }
        if (!inside) { setFocused(null); }
        return super.mouseClicked(event, doubleClick);
    }

    @Override protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, Language.getInstance().getVisualOrder(font.substrByWidth(title, imageWidth - 16 - FIELD_WIDE - 4)), titleLabelX, titleLabelY, LABEL, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL, false);
    }

    @Override public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        super.extractBackground(graphics, mouseX, mouseY, partial);
        ContentContainerScreen.frame(graphics, leftPos, topPos, imageWidth, ContentPortwayMenu.ROWS, ContentPortwayMenu.COLUMNS);
    }
}
