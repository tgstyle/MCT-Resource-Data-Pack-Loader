package mctmods.resourcedatapackloader.client.util;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;

public final class ScreenEvents {
    private ScreenEvents() {}

    @Nullable public static <T extends GuiScreen> T screen(GuiScreenEvent event, Class<T> type) { return type.isInstance(event.getGui()) ? type.cast(event.getGui()) : null; }
}
