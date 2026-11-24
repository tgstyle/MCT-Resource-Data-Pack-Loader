package mctmods.resourcedatapackloader.client.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class CenteredText {
    private CenteredText() {}

    public static void shadow(FontRenderer font, String text, float centerX, float y, int color) {
        font.drawStringWithShadow(text, centerX - font.getStringWidth(text) / 2.0F, y, color);
    }
}
