package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackOptions;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PackOptionsScreen extends Screen {
    private static final int TOP = 28;
    private static final int BOTTOM = 48;
    private static final int ROW = 32;
    private static final int LIST_WIDTH = 260;
    private static final int TOGGLE_WIDTH = 60;
    private static final int TOGGLE_HEIGHT = 20;
    private static final int HEADING = 0xFFD080;
    private static final int PLAIN = 0xFFFFFF;
    private static final int DIM = 0x808080;
    private static final int WARNING = 0xFF5555;
    private static final int RULE = 0x30FFFFFF;
    private final Screen parent;
    private final Map<String, Map<String, Boolean>> staged = new LinkedHashMap<>();
    private final Map<String, Map<String, Boolean>> loaded = new LinkedHashMap<>();
    private final Set<String> folded = new HashSet<>();
    private OptionList list;

    public PackOptionsScreen(Screen parent) {
        super(Component.translatable("rdpl.gui.packOptions.title"));
        this.parent = parent;
        for (String file : PackOptions.files()) {
            staged.put(file, PackOptions.optionsOf(file));
            loaded.put(file, PackOptions.loadedOptionsOf(file));
        }
    }

    private boolean changed() { return !staged.equals(loaded); }

    @Override protected void init() {
        list = new OptionList();
        addRenderableWidget(list);
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).bounds(width / 2 - 100, height - 27, 200, 20).build());
    }

    @Override public void onClose() {
        for (Map.Entry<String, Map<String, Boolean>> entry : staged.entrySet()) { PackOptions.save(entry.getKey(), entry.getValue()); }
        if (minecraft != null) { minecraft.setScreen(parent); }
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, PLAIN);
        if (changed()) { graphics.drawCenteredString(font, Component.translatable("rdpl.gui.packOptions.restart"), width / 2, height - 42, WARNING); }
    }

    private final class OptionList extends ObjectSelectionList<Row> {
        OptionList() {
            super(Objects.requireNonNull(PackOptionsScreen.this.minecraft), PackOptionsScreen.this.width, PackOptionsScreen.this.height - TOP - BOTTOM, TOP, ROW);
            rebuild();
        }

        void rebuild() {
            clearEntries();
            boolean first = true;
            for (Map.Entry<String, Map<String, Boolean>> file : staged.entrySet()) {
                addEntry(new Row(file.getKey(), null, first));
                first = false;
                if (folded.contains(file.getKey())) { continue; }
                for (String name : file.getValue().keySet()) { addEntry(new Row(file.getKey(), name, false)); }
            }
        }

        @Override public int getRowWidth() { return LIST_WIDTH; }

        @Override protected int getScrollbarPosition() { return PackOptionsScreen.this.width / 2 + LIST_WIDTH / 2 + 4; }
    }

    private final class Row extends ObjectSelectionList.Entry<Row> {
        private final String file;
        @Nullable private final String name;
        private final boolean first;
        @Nullable private final Button toggle;

        Row(String file, @Nullable String name, boolean first) {
            this.file = file;
            this.name = name;
            this.first = first;
            this.toggle = name == null ? null : Button.builder(Component.empty(), button -> flip()).bounds(0, 0, TOGGLE_WIDTH, TOGGLE_HEIGHT).build();
        }

        private void flip() {
            Map<String, Boolean> options = staged.get(file);
            options.put(name, !Boolean.TRUE.equals(options.get(name)));
        }

        @Override public void render(@Nonnull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            if (name == null || toggle == null) {
                if (!first) { graphics.fill(left + 2, top + 1, left + width - 2, top + 2, RULE); }
                String label = (folded.contains(file) ? "+ " : "- ") + file;
                graphics.drawString(font, label, left + width / 2 - font.width(label) / 2, top + height / 2 - 4, HEADING, false);
                return;
            }
            String about = PackOptions.about(file, name);
            if (about == null) { graphics.drawString(font, name, left + 2, top + height / 2 - 4, PLAIN, false); }
            else {
                graphics.drawString(font, name, left + 2, top + 5, PLAIN, false);
                graphics.drawString(font, font.plainSubstrByWidth(about, width - TOGGLE_WIDTH - 10), left + 2, top + 17, DIM, false);
            }
            toggle.setX(left + width - TOGGLE_WIDTH - 4);
            toggle.setY(top + 1);
            boolean on = Boolean.TRUE.equals(staged.get(file).get(name));
            toggle.setMessage(Component.translatable(on ? "options.on" : "options.off").withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
            toggle.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (toggle == null) {
                if (!folded.remove(file)) { folded.add(file); }
                if (minecraft != null) { minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)); }
                PackOptionsScreen.this.list.rebuild();
                return true;
            }
            return toggle.mouseClicked(mouseX, mouseY, button);
        }

        @Override @Nonnull public Component getNarration() { return Component.literal(name == null ? file : file + " " + name); }
    }
}
