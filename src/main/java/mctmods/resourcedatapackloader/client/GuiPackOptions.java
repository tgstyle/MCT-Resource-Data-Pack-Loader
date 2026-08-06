package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GuiPackOptions extends GuiScreen {
    private final GuiScreen parent;
    private final Map<String, Map<String, Boolean>> staged = new LinkedHashMap<>();
    private final Map<String, Map<String, Boolean>> loaded = new LinkedHashMap<>();
    private OptionList list;

    public GuiPackOptions(GuiScreen parent) {
        this.parent = parent;
        for (String file : PackOptions.files()) {
            staged.put(file, PackOptions.optionsOf(file));
            loaded.put(file, PackOptions.optionsOf(file));
        }
    }

    private boolean changed() {
        for (Map.Entry<String, Map<String, Boolean>> entry : staged.entrySet()) {
            Map<String, Boolean> was = loaded.get(entry.getKey());
            if (was == null) { continue; }

            for (Map.Entry<String, Boolean> option : entry.getValue().entrySet()) {
                if (!PackOptions.gates(entry.getKey(), option.getKey())) { continue; }
                if (!option.getValue().equals(was.get(option.getKey()))) { return true; }
            }
        }
        return false;
    }

    @Override public void initGui() {
        buttonList.clear();
        list = new OptionList();
        buttonList.add(new GuiButton(0, width / 2 - 100, height - 27, I18n.format("gui.done")));
    }

    @Override protected void actionPerformed(GuiButton button) {
        if (button.id != 0) { return; }
        for (Map.Entry<String, Map<String, Boolean>> entry : staged.entrySet()) { PackOptions.save(entry.getKey(), entry.getValue()); }
        PackOptions.report();
        mc.displayGuiScreen(parent);
    }

    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        list.handleMouseInput();
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (list.mouseClicked(mouseX, mouseY, mouseButton)) { return; }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (list.mouseReleased(mouseX, mouseY, state)) { return; }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        list.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, I18n.format("rdpl.gui.packOptions.title"), width / 2, 12, 0xFFFFFF);
        if (changed()) { drawCenteredString(fontRenderer, I18n.format("rdpl.gui.packOptions.restart"), width / 2, height - 42, 0xFF5555); }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class OptionList extends GuiListExtended {
        private final List<Row> rows = new ArrayList<>();

        OptionList() {
            super(GuiPackOptions.this.mc, GuiPackOptions.this.width, GuiPackOptions.this.height, 28, GuiPackOptions.this.height - 48, 32);
            for (Map.Entry<String, Map<String, Boolean>> file : staged.entrySet()) {
                rows.add(new Row(file.getKey(), null));
                for (String name : file.getValue().keySet()) { rows.add(new Row(file.getKey(), name)); }
            }
        }

        @Override protected int getSize() { return rows.size(); }

        @Override @Nonnull public IGuiListEntry getListEntry(int index) { return rows.get(index); }

        @Override public int getListWidth() { return 260; }

        @Override protected int getScrollBarX() { return GuiPackOptions.this.width / 2 + 134; }

        class Row implements IGuiListEntry {
            private final String file;
            private final String name;
            private final GuiButton toggle;

            Row(String file, String name) {
                this.file = file;
                this.name = name;
                this.toggle = name == null ? null : new GuiButton(0, 0, 0, 60, 20, "");
            }

            @Override public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}

            @Override public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                Minecraft held = GuiPackOptions.this.mc;
                if (name == null) {
                    held.fontRenderer.drawString(file, x + 2, y + slotHeight / 2 - 4, 0xA0A0A0);
                    return;
                }
                String about = PackOptions.about(file, name);
                if (about == null) { held.fontRenderer.drawString(name, x + 2, y + slotHeight / 2 - 4, 0xFFFFFF); }
                else {
                    held.fontRenderer.drawString(name, x + 2, y + 5, 0xFFFFFF);
                    held.fontRenderer.drawString(held.fontRenderer.trimStringToWidth(about, listWidth - 70), x + 2, y + 17, 0x808080);
                }
                toggle.x = x + listWidth - 64;
                toggle.y = y + 1;
                boolean on = staged.get(file).get(name);
                toggle.displayString = I18n.format(on ? "options.on" : "options.off");
                toggle.packedFGColour = on ? 0x55FF55 : 0xFF5555;
                toggle.drawButton(held, mouseX, mouseY, partialTicks);
            }

            @Override public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
                if (toggle == null || !toggle.mousePressed(GuiPackOptions.this.mc, mouseX, mouseY)) { return false; }

                Map<String, Boolean> options = staged.get(file);
                options.put(name, !options.get(name));
                toggle.playPressSound(GuiPackOptions.this.mc.getSoundHandler());
                return true;
            }

            @Override public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}
        }
    }
}
