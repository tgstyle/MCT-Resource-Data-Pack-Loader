package mctmods.resourcedatapackloader.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiPackShapesWorld extends GuiScreen {
    private final GuiScreen parent;
    private final String owner;

    public GuiPackShapesWorld(GuiScreen parent, String owner) {
        this.parent = parent;
        this.owner = owner;
    }

    @Override public void initGui() { buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, I18n.format("gui.done"))); }

    @Override protected void actionPerformed(GuiButton button) {
        if (button.id != 0) { return; }
        mc.displayGuiScreen(parent);
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Pack overrides are active", width / 2, height / 4, 0xFFFFFF);
        drawCenteredString(fontRenderer, owner + " shapes this world, so these settings are not available", width / 2, height / 4 + 30, 0xA0A0A0);
        drawCenteredString(fontRenderer, "Remove the pack, or the settings it carries, to choose them yourself", width / 2, height / 4 + 45, 0xA0A0A0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
