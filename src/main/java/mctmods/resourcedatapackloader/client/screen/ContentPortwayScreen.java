package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentPortwayScreen extends BackdropContainerScreen<ContentPortwayMenu> {
    private static final int FIELD_WIDE = 80;
    private static final int FIELD_HIGH = 10;
    private static final int FIELD_TOP = 4;
    private static final int QUIET = 4;
    @Nullable private EditBox search;
    private int quiet = -1;

    public ContentPortwayScreen(ContentPortwayMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = ContentContainerMenu.width(ContentPortwayMenu.SHAPE);
        this.imageHeight = ContentContainerMenu.height(ContentPortwayMenu.SHAPE);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override protected void init() {
        super.init();
        String kept = search == null ? "" : search.getValue();
        search = new EditBox(font, leftPos + imageWidth - 8 - FIELD_WIDE, topPos + FIELD_TOP, FIELD_WIDE, FIELD_HIGH, Component.translatable("rdpl.portway.search"));
        search.setMaxLength(ContentPortwayMenu.FILTER_MAX);
        search.setHint(Component.translatable("rdpl.portway.search").withStyle(style -> style.withColor(0x808080)));
        search.setValue(kept);
        search.setResponder(text -> quiet = 0);
        addRenderableWidget(search);
    }

    @Override protected void containerTick() {
        super.containerTick();
        if (quiet >= 0 && ++quiet >= QUIET) { send(); }
    }

    private void send() {
        quiet = -1;
        if (search != null) { RDPLNetwork.portwayFilter(search.getValue()); }
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (search != null && search.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { send(); }
            else { search.keyPressed(keyCode, scanCode, modifiers); }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (search != null) {
            boolean inside = search.isMouseOver(mouseX, mouseY);
            if (inside && button == 1) { search.setValue(""); setFocused(search); return true; }
            if (!inside) { setFocused(null); }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        FormattedText trimmed = font.substrByWidth(title, imageWidth - 16 - FIELD_WIDE - 4);
        graphics.drawString(font, Language.getInstance().getVisualOrder(trimmed), titleLabelX, titleLabelY, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override protected void renderBg(@Nonnull GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        ContentContainerScreen.frame(graphics, leftPos, topPos, imageWidth, ContentPortwayMenu.ROWS, ContentPortwayMenu.COLUMNS);
    }
}
