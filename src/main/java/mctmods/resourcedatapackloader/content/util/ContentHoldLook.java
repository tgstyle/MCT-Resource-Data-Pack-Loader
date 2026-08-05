package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.mixin.AccessorGuiIngame;

import net.minecraft.client.Minecraft;

public final class ContentHoldLook {
    private static final long GRACE = 1500L;
    private static long lastMarked;

    private ContentHoldLook() {}

    public static boolean small() {
        AccessorGuiIngame overlay = (AccessorGuiIngame) Minecraft.getMinecraft().ingameGUI;
        if (overlay.rdpl$getTitlesTimer() > 0) {
            String subtitle = overlay.rdpl$getDisplayedSubTitle();
            if (subtitle != null && (subtitle.contains(ContentPregen.HOLD_MARK) || subtitle.contains(ContentPregen.WELCOME_MARK))) { lastMarked = System.currentTimeMillis(); }
        }
        return System.currentTimeMillis() - lastMarked < GRACE;
    }
}
