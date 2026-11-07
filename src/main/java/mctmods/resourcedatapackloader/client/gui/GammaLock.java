package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.client.util.TrueDarkness;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class GammaLock {
    private static final int DEFAULT = 50;
    private static final int MAX = 100;

    private GammaLock() {}

    public static void onInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof VideoSettingsScreen)) { return; }
        Minecraft mc = Minecraft.getInstance();
        float locked = TrueDarkness.lockedGamma(mc.level);
        if (Float.isNaN(locked)) { return; }
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof OptionsList list && list.findOption(mc.options.gamma()) instanceof AbstractSliderButton slider) {
                slider.value = locked;
                slider.active = false;
                slider.setMessage(label(locked));
            }
        }
    }

    private static Component label(float gamma) {
        Component caption = Component.translatable("options.gamma");
        int percent = (int) (gamma * 100.0F);
        if (percent == 0) { return Options.genericValueLabel(caption, Component.translatable("options.gamma.min")); }
        if (percent == DEFAULT) { return Options.genericValueLabel(caption, Component.translatable("options.gamma.default")); }
        if (percent == MAX) { return Options.genericValueLabel(caption, Component.translatable("options.gamma.max")); }
        return Options.genericValueLabel(caption, percent);
    }
}
