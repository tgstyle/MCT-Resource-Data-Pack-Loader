package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.client.util.TrueDarkness;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;

public final class GammaLock {
    private static final int MIN = 0;
    private static final int MOODY = 50;
    private static final int MAX = 100;

    private GammaLock() {}

    public static void onInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof VideoSettingsScreen)) { return; }
        float locked = TrueDarkness.lockedGamma(Minecraft.getInstance().level);
        if (Float.isNaN(locked)) { return; }
        Options options = Minecraft.getInstance().options;
        for (GuiEventListener child : event.getListenersList()) {
            if (child instanceof OptionsList list && list.findOption(options.gamma()) instanceof AbstractSliderButton slider) { lock(slider, locked); }
        }
    }

    private static void lock(AbstractSliderButton slider, float locked) {
        slider.value = locked;
        slider.active = false;
        slider.setMessage(label(locked));
    }

    private static Component label(float gamma) {
        Component title = Component.translatable("options.gamma");
        int percent = (int) (gamma * 100.0F);
        if (percent == MIN) { return Options.genericValueLabel(title, Component.translatable("options.gamma.min")); }
        if (percent == MOODY) { return Options.genericValueLabel(title, Component.translatable("options.gamma.default")); }
        if (percent == MAX) { return Options.genericValueLabel(title, Component.translatable("options.gamma.max")); }
        return Options.genericValueLabel(title, percent);
    }
}
