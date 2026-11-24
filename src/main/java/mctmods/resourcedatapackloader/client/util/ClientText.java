package mctmods.resourcedatapackloader.client.util;

import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;

public final class ClientText {
    private ClientText() {}

    public static String shown(String text) { return I18n.hasKey(text) ? I18n.format(text) : text; }

    public static String or(@Nullable String text, String fallback) { return text == null ? fallback : shown(text); }
}
