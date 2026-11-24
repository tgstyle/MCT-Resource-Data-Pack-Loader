package mctmods.resourcedatapackloader.client.renderer;

import net.minecraft.client.renderer.GlStateManager;

public final class Blends {
    private Blends() {}

    public static void on() {
        GlStateManager.enableBlend();
        translucent();
    }

    public static void translucent() { GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO); }
}
