package mctmods.resourcedatapackloader.client;

import net.minecraft.client.Minecraft;

public final class Crisp {
    private Crisp() {}

    private static double gui() {
        Minecraft mc = Minecraft.getInstance();
        return mc.getWindow().getGuiScale();
    }

    public static float scale(float wanted) {
        double gui = gui();
        if (gui <= 0.0D) { return wanted; }
        return (float) (Math.max(1L, Math.round(gui * wanted)) / gui);
    }

    public static float snap(float at) {
        double gui = gui();
        if (gui <= 0.0D) { return at; }
        return (float) (Math.round(at * gui) / gui);
    }
}
