package mctmods.resourcedatapackloader.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class Crisp {
    private static final int LIFTED = 2;

    private Crisp() {}

    private static double setting() { return Minecraft.getInstance().getWindow().getGuiScale(); }

    private static int lift() { return setting() == 1.0D ? LIFTED : 1; }

    private static double gui() { return setting() * lift(); }

    public static double factor() { return gui(); }

    public static int fit(int scaled) { return scaled / lift(); }

    public static double fit(double scaled) { return scaled / lift(); }

    public static int real(int units) { return units * lift(); }

    public static void raise(GuiGraphics graphics) {
        graphics.pose().pushPose();
        float lift = lift();
        graphics.pose().scale(lift, lift, 1.0F);
    }

    public static void lower(GuiGraphics graphics) { graphics.pose().popPose(); }

    public static float scale(float wanted) {
        double gui = gui();
        if (gui <= 0.0D) { return wanted; }
        return (float) (Math.max(1L, Math.round(gui * wanted)) / gui);
    }

    public static float below(float wanted, float least) {
        double gui = gui();
        if (gui <= 0.0D) { return Math.max(wanted, least); }
        float pixel = (float) (Math.floor(gui * wanted) / gui);
        return pixel > least ? pixel : Math.max(wanted, least);
    }

    public static float snap(float at) {
        double gui = gui();
        if (gui <= 0.0D) { return at; }
        return (float) (Math.round(at * gui) / gui);
    }
}
