package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientCompat;
import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class WeightHud {
    private static final int BAR_WIDTH = 60;
    private static final int BAR_HEIGHT = 5;
    private static final int BAR_OFFSET = 126;
    private static final int BAR_BOTTOM = 12;
    private static final int BACK = 0xB0000000;
    private static final int OPAQUE = 0xFF000000;
    private static final int LABEL_GAP = 4;

    private WeightHud() {}

    public static void onLayer(GuiGraphicsExtractor graphics, DeltaTracker ignoredDelta) {
        if (ClientCompat.hudHidden(Minecraft.getInstance()) || !ClientWeights.barShown()) { return; }
        WeightDef settings = ContentWeights.settings();
        String anchor = settings.hudAnchor;
        int left = leftEdge(anchor, graphics.guiWidth()) + settings.hudX;
        int top = topEdge(anchor, graphics.guiHeight()) + settings.hudY;
        int bottom = top + BAR_HEIGHT;
        int filled = Math.round(ClientWeights.fill() * (BAR_WIDTH - 2));
        graphics.fill(left, top, left + BAR_WIDTH, bottom, BACK);
        graphics.fill(left + 1, top + 1, left + 1 + filled, bottom - 1, OPAQUE | ClientWeights.color());
    }

    private static int leftEdge(String anchor, int width) {
        return switch (anchor) {
            case WeightDef.HUD_TOP_LEFT, WeightDef.HUD_LEFT, WeightDef.HUD_BOTTOM_LEFT -> 0;
            case WeightDef.HUD_TOP, WeightDef.HUD_CENTER, WeightDef.HUD_BOTTOM -> (width - BAR_WIDTH) / 2;
            case WeightDef.HUD_TOP_RIGHT, WeightDef.HUD_RIGHT, WeightDef.HUD_BOTTOM_RIGHT -> width - BAR_WIDTH;
            default -> width / 2 + BAR_OFFSET;
        };
    }

    private static int topEdge(String anchor, int height) {
        return switch (anchor) {
            case WeightDef.HUD_TOP_LEFT, WeightDef.HUD_TOP, WeightDef.HUD_TOP_RIGHT -> 0;
            case WeightDef.HUD_LEFT, WeightDef.HUD_CENTER, WeightDef.HUD_RIGHT -> (height - BAR_HEIGHT) / 2;
            case WeightDef.HUD_BOTTOM_LEFT, WeightDef.HUD_BOTTOM, WeightDef.HUD_BOTTOM_RIGHT -> height - BAR_HEIGHT;
            default -> height - BAR_BOTTOM - BAR_HEIGHT;
        };
    }

    public static void onScreen(ScreenEvent.Render.Foreground event) {
        Screen screen = event.getScreen();
        if (!inventory(screen) || !ClientWeights.active() || !ContentWeights.settings().hudInventory) { return; }
        AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) screen;
        Component line = Component.translatable("rdpl.weight.inventory", ClientWeights.format(ClientWeights.total()), ClientWeights.format(ClientWeights.limit()));
        event.getGuiGraphics().text(Minecraft.getInstance().font, line, container.getLeftPos(), container.getTopPos() + container.getImageHeight() + LABEL_GAP, OPAQUE | ClientWeights.color(), true);
    }

    private static boolean inventory(Screen screen) {
        if (screen instanceof InventoryScreen) { return true; }
        return screen instanceof CreativeModeInventoryScreen && CreativeModeInventoryScreen.selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }
}
