package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.util.messages.Toasts;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraftforge.client.event.ToastAddEvent;

public final class ToastFilter {
    private static final Map<SystemToast.SystemToastIds, String> SYSTEM = systemNames();

    private ToastFilter() {}

    public static void onToastAdd(ToastAddEvent event) { if (Toasts.hides(kind(event.getToast()), name(event.getToast()))) { event.setCanceled(true); } }

    public static void receive(int kinds, List<String> names) {
        List<String> known = new ArrayList<>(names.size());
        for (String name : names) {
            if (known(name)) { known.add(name); }
            else { ContentLog.LOGGER.error("The active world template lists '{}' under hiddenToasts, which is not a system toast id or a toast class name, so it is skipped", name); }
        }
        Toasts.show(kinds, known);
    }

    private static boolean known(String name) { return name.indexOf('.') > 0 || SYSTEM.containsValue(name); }

    private static String name(Toast toast) { return toast instanceof SystemToast system ? SYSTEM.getOrDefault(system.getToken(), toast.getClass().getName()) : toast.getClass().getName(); }

    private static Map<SystemToast.SystemToastIds, String> systemNames() {
        Map<SystemToast.SystemToastIds, String> names = new EnumMap<>(SystemToast.SystemToastIds.class);
        for (SystemToast.SystemToastIds id : SystemToast.SystemToastIds.values()) { names.put(id, Settings.lower(id.name())); }
        return names;
    }

    private static int kind(Toast toast) {
        if (toast instanceof AdvancementToast) { return Toasts.ADVANCEMENTS; }
        if (toast instanceof RecipeToast) { return Toasts.RECIPES; }
        if (toast instanceof TutorialToast) { return Toasts.TUTORIAL; }
        if (toast instanceof SystemToast) { return Toasts.SYSTEM; }
        return Toasts.OTHER;
    }
}
