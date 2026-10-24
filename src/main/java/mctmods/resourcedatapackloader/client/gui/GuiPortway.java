package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.content.def.PortwayMenu;
import mctmods.resourcedatapackloader.content.gui.ContainerPortway;
import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiPortway extends GuiPackContainer {
    private static final int FIELD_WIDE = 80;
    private static final int FIELD_HIGH = 10;
    private static final int FIELD_TOP = 4;
    private static final int QUIET = 4;
    private static final int HINT = 0x808080;
    @Nullable private GuiTextField search;
    private int quiet = -1;

    public GuiPortway(ContainerPortway container) { super(container, container.icons(), ContainerPortway.ROWS, ContainerPortway.COLUMNS, null); }

    @Override public void initGui() {
        super.initGui();
        String kept = search == null ? "" : search.getText();
        search = new GuiTextField(0, fontRenderer, guiLeft + xSize - 8 - FIELD_WIDE, guiTop + FIELD_TOP, FIELD_WIDE, FIELD_HIGH);
        search.setMaxStringLength(ContainerPortway.FILTER_MAX);
        search.setEnableBackgroundDrawing(true);
        search.setText(kept);
        Keyboard.enableRepeatEvents(true);
    }

    @Override public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override public void updateScreen() {
        super.updateScreen();
        if (search != null) { search.updateCursorCounter(); }
        if (quiet >= 0 && ++quiet >= QUIET) { send(); }
    }

    private void send() {
        quiet = -1;
        if (search != null) { RDPLNetwork.portwayFilter(search.getText()); }
    }

    @Override protected void keyTyped(char typed, int code) throws IOException {
        if (search != null && search.isFocused() && code != Keyboard.KEY_ESCAPE) {
            if (code == Keyboard.KEY_RETURN || code == Keyboard.KEY_NUMPADENTER) {
                send();
                return;
            }
            String before = search.getText();
            search.textboxKeyTyped(typed, code);
            if (!search.getText().equals(before)) { quiet = 0; }
            return;
        }
        super.keyTyped(typed, code);
    }

    @Override protected void mouseClicked(int x, int y, int button) throws IOException {
        if (search != null) {
            boolean inside = x >= search.x && x < search.x + search.width && y >= search.y && y < search.y + search.height;
            if (inside && button == 1) {
                search.setText("");
                quiet = 0;
            }
            search.mouseClicked(x, y, button);
        }
        super.mouseClicked(x, y, button);
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partial, mouseX, mouseY);
        if (search != null) { search.drawTextBox(); }
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (search != null && search.getText().isEmpty() && !search.isFocused()) {
            fontRenderer.drawString(I18n.format("rdpl.portway.search"), xSize - 8 - FIELD_WIDE + 4, FIELD_TOP + (FIELD_HIGH - 8) / 2, HINT);
        }
    }

    @Override protected int titleWidth() { return xSize - 16 - FIELD_WIDE - 4; }

    @Override protected void renderToolTip(@Nonnull ItemStack stack, int x, int y) {
        NBTTagCompound nav = stack.getSubCompound(ContainerPortway.NAV);
        if (nav != null) {
            String kind = nav.getString(ContainerPortway.KIND);
            if ("sort".equals(kind)) {
                String order = nav.getString(ContainerPortway.ORDER);
                drawHoveringText(Arrays.asList(I18n.format("rdpl.portway.sort", I18n.format("rdpl.portway.order." + order)),
                        TextFormatting.GRAY + I18n.format("rdpl.portway.sort.click", I18n.format("rdpl.portway.order." + PortwayMenu.after(order)))), x, y);
                return;
            }
            drawHoveringText(Collections.singletonList(I18n.format("rdpl.portway." + kind, nav.getInteger(ContainerPortway.PAGE), nav.getInteger(ContainerPortway.PAGES))), x, y);
            return;
        }
        NBTTagCompound tag = stack.getSubCompound(ContainerPortway.TAG);
        if (tag == null) {
            super.renderToolTip(stack, x, y);
            return;
        }
        List<String> lines = new ArrayList<>();
        if (!tag.getString(ContainerPortway.NAME).isEmpty()) { lines.add(tag.getString(ContainerPortway.NAME)); }
        lines.add(I18n.format("rdpl.portway.stop", Portways.dimensionName(tag.getInteger(ContainerPortway.DIMENSION)), tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
        if (tag.getBoolean(ContainerPortway.HERE)) { lines.add(I18n.format("rdpl.portway.here")); }
        else if (tag.hasKey(ContainerPortway.AWAY)) { lines.add(I18n.format(Portways.counted("rdpl.portway.distance", tag.getInteger(ContainerPortway.AWAY)), tag.getInteger(ContainerPortway.AWAY))); }
        if (tag.getBoolean(ContainerPortway.LOCKED)) { lines.add(I18n.format("rdpl.portway.locked")); }
        prices(tag, lines);
        int wait = tag.getInteger(ContainerPortway.WAIT);
        if (wait > 0) { lines.add(I18n.format(Portways.counted("rdpl.portway.cooldown", wait), wait)); }
        if (tag.getBoolean(ContainerPortway.PERSONAL)) { lines.add(I18n.format("rdpl.portway.forgethint")); }
        drawHoveringText(lines, x, y);
    }

    private static void prices(NBTTagCompound tag, List<String> lines) {
        int levels = tag.getInteger(ContainerPortway.XP);
        if (levels > 0) { lines.add(I18n.format(tag.getBoolean(ContainerPortway.POINTS) ? (levels == 1 ? "rdpl.portway.pays.point" : "rdpl.portway.pays.points") : levels == 1 ? "rdpl.portway.pays.level" : "rdpl.portway.pays.levels", levels)); }
        int count = tag.getInteger(ContainerPortway.ITEM);
        Item item = count > 0 ? Item.REGISTRY.getObject(new ResourceLocation(tag.getString(ContainerPortway.ITEM_NAME))) : null;
        if (item != null && item != Items.AIR) { lines.add(I18n.format("rdpl.portway.pays.item", count, new ItemStack(item).getDisplayName())); }
        int health = tag.getInteger(ContainerPortway.HEALTH);
        if (health > 0) { lines.add(I18n.format("rdpl.portway.pays.health", health)); }
        int hunger = tag.getInteger(ContainerPortway.HUNGER);
        if (hunger > 0) { lines.add(I18n.format("rdpl.portway.pays.hunger", hunger)); }
    }
}
