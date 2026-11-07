package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.client.util.TrueDarkness;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class GammaLock {
    private GammaLock() {}

    @SubscribeEvent public static void lockSlider(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiVideoSettings)) { return; }
        float locked = TrueDarkness.lockedGamma(Minecraft.getMinecraft().world);
        if (Float.isNaN(locked)) { return; }
        GuiVideoSettings screen = (GuiVideoSettings) event.getGui();
        if (screen.optionsRowList instanceof GuiOptionsRowList) {
            for (GuiOptionsRowList.Row row : ((GuiOptionsRowList) screen.optionsRowList).options) {
                lock(row.buttonA, locked);
                lock(row.buttonB, locked);
            }
        }
        for (GuiButton button : event.getButtonList()) { lock(button, locked); }
    }

    private static void lock(@Nullable GuiButton button, float locked) {
        if (!(button instanceof GuiOptionSlider) || button.id != GameSettings.Options.GAMMA.getOrdinal()) { return; }
        ((GuiOptionSlider) button).sliderValue = locked;
        button.enabled = false;
        button.displayString = I18n.format(GameSettings.Options.GAMMA.getTranslation()) + ": " + label(locked);
    }

    private static String label(float gamma) {
        if (gamma == 0.0F) { return I18n.format("options.gamma.min"); }
        if (gamma == 1.0F) { return I18n.format("options.gamma.max"); }
        return "+" + (int) (gamma * 100.0F) + "%";
    }
}
