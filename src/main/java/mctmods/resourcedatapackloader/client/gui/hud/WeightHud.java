package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class WeightHud {
    private static final int BAR_WIDTH = 60;
    private static final int BAR_HEIGHT = 5;
    private static final int BAR_OFFSET = 126;
    private static final int BAR_BOTTOM = 12;
    private static final int BACK = 0xB0000000;
    private static final int OPAQUE = 0xFF000000;
    private static final int LABEL_GAP = 4;

    private WeightHud() {}

    @SubscribeEvent public static void onHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || !ClientWeights.barShown()) { return; }
        ScaledResolution resolution = event.getResolution();
        WeightDef settings = ContentWeights.settings();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        String anchor = settings.hudAnchor;
        int left = barLeft(anchor, width) + settings.hudX;
        int top = barTop(anchor, height) + settings.hudY;
        int bottom = top + BAR_HEIGHT;
        int filled = Math.round(ClientWeights.fill() * (BAR_WIDTH - 2));
        Gui.drawRect(left, top, left + BAR_WIDTH, bottom, BACK);
        Gui.drawRect(left + 1, top + 1, left + 1 + filled, bottom - 1, OPAQUE | ClientWeights.color());
    }

    @SubscribeEvent public static void onScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        GuiScreen screen = event.getGui();
        if (!inventory(screen) || !ClientWeights.active() || !ContentWeights.settings().hudInventory) { return; }
        GuiContainer container = (GuiContainer) screen;
        String line = I18n.format("rdpl.weight.inventory", ClientWeights.format(ClientWeights.total()), ClientWeights.format(ClientWeights.limit()));
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(line, container.getGuiLeft(), container.getGuiTop() + container.getYSize() + LABEL_GAP, OPAQUE | ClientWeights.color());
    }

    private static int barLeft(String anchor, int width) {
        switch (anchor) {
            case WeightDef.HUD_TOP_LEFT:
            case WeightDef.HUD_LEFT:
            case WeightDef.HUD_BOTTOM_LEFT: { return 0; }
            case WeightDef.HUD_TOP:
            case WeightDef.HUD_CENTER:
            case WeightDef.HUD_BOTTOM: { return (width - BAR_WIDTH) / 2; }
            case WeightDef.HUD_TOP_RIGHT:
            case WeightDef.HUD_RIGHT:
            case WeightDef.HUD_BOTTOM_RIGHT: { return width - BAR_WIDTH; }
            default: { return width / 2 + BAR_OFFSET; }
        }
    }

    private static int barTop(String anchor, int height) {
        switch (anchor) {
            case WeightDef.HUD_TOP_LEFT:
            case WeightDef.HUD_TOP:
            case WeightDef.HUD_TOP_RIGHT: { return 0; }
            case WeightDef.HUD_LEFT:
            case WeightDef.HUD_CENTER:
            case WeightDef.HUD_RIGHT: { return (height - BAR_HEIGHT) / 2; }
            case WeightDef.HUD_BOTTOM_LEFT:
            case WeightDef.HUD_BOTTOM:
            case WeightDef.HUD_BOTTOM_RIGHT: { return height - BAR_HEIGHT; }
            default: { return height - BAR_BOTTOM - BAR_HEIGHT; }
        }
    }

    private static boolean inventory(GuiScreen screen) {
        if (screen instanceof GuiInventory) { return true; }
        return screen instanceof GuiContainerCreative && ((GuiContainerCreative) screen).getSelectedTabIndex() == CreativeTabs.INVENTORY.getIndex();
    }
}
