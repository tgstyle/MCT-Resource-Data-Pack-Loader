package mctmods.resourcedatapackloader.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import javax.annotation.Nullable;

public final class ClientCompat {
    private ClientCompat() {}

    @Nullable public static Screen screen(Minecraft mc) { return mc.screen; }

    public static void setScreen(Minecraft mc, @Nullable Screen screen) { mc.setScreen(screen); }

    public static ChatComponent chat(Minecraft mc) { return mc.gui.getChat(); }

    public static RenderType cutoutBlockSheet() { return Sheets.cutoutBlockSheet(); }
}
