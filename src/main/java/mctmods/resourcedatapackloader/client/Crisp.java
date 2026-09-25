package mctmods.resourcedatapackloader.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public final class Crisp {
    private static final int LIFTED = 2;
    private static final double CRISP = 2.0D;

    private Crisp() {}

    private static int real() { return new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor(); }

    private static int lift() { return real() == 1 ? LIFTED : 1; }

    private static double gui() { return real() * lift(); }

    public static double factor() { return gui(); }

    public static int fit(int scaled) { return scaled / lift(); }

    public static void raise() {
        GlStateManager.pushMatrix();
        float lift = lift();
        GlStateManager.scale(lift, lift, 1.0F);
    }

    public static void lower() { GlStateManager.popMatrix(); }

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

    public static float fine(float at, float size) { return size * gui() < CRISP ? snap(at) : at; }
}
