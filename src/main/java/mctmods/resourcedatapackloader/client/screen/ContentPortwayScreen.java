package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;

import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentPortwayScreen extends BackdropContainerScreen<ContentPortwayMenu> {
    private static final int FIELD_WIDE = 80;
    private static final int FIELD_HIGH = 10;
    private static final int FIELD_TOP = 4;
    private static final int QUIET = 4;
    private static final int LABEL = 0x404040;
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
        EditBox box = new EditBox(font, leftPos + imageWidth - 8 - FIELD_WIDE, topPos + FIELD_TOP, FIELD_WIDE, FIELD_HIGH, Component.translatable("rdpl.portway.search"));
        box.setMaxLength(ContentPortwayMenu.FILTER_MAX);
        box.setValue(kept);
        box.setHint(Component.translatable("rdpl.portway.search").withStyle(style -> style.withColor(0x808080)));
        box.setResponder(text -> quiet = 0);
        search = box;
        addRenderableWidget(box);
    }

    @Override protected void containerTick() {
        super.containerTick();
        if (search != null) { search.tick(); }
        if (quiet >= 0 && ++quiet >= QUIET) { send(); }
    }

    private void send() {
        quiet = -1;
        if (search != null) { RDPLNetwork.portwayFilter(search.getValue()); }
    }

    @Override public boolean keyPressed(int key, int scan, int modifiers) {
        if (search != null && search.isFocused() && key != GLFW.GLFW_KEY_ESCAPE) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) { send(); }
            else { search.keyPressed(key, scan, modifiers); }
            return true;
        }
        return super.keyPressed(key, scan, modifiers);
    }

    @Override public boolean mouseClicked(double x, double y, int button) {
        boolean inside = search != null && search.isMouseOver(x, y);
        if (inside && button == 1) {
            search.setValue("");
            setFocused(search);
            return true;
        }
        if (search != null && !inside) { setFocused(null); }
        return super.mouseClicked(x, y, button);
    }

    @Override protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        int wide = imageWidth - 16 - FIELD_WIDE - 4;
        graphics.drawString(font, Language.getInstance().getVisualOrder(font.substrByWidth(title, wide)), titleLabelX, titleLabelY, LABEL, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL, false);
    }

    @Override protected void renderBg(@Nonnull GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        ContentContainerScreen.frame(graphics, leftPos, topPos, imageWidth, ContentPortwayMenu.ROWS, ContentPortwayMenu.COLUMNS);
    }
}
