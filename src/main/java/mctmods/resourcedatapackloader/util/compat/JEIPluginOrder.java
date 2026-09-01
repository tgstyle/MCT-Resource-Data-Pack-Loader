package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.util.ContentLog;

import mezz.jei.api.IModPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class JEIPluginOrder {
    private static final Set<IModPlugin> PROVIDERS = Collections.newSetFromMap(new IdentityHashMap<>());
    @Nullable private static IModPlugin current;

    private JEIPluginOrder() {}

    public static void reset() {
        PROVIDERS.clear();
        current = null;
    }

    public static void begin(IModPlugin plugin) { current = plugin; }

    public static void end() { current = null; }

    public static void markProvider() {
        if (current != null) { PROVIDERS.add(current); }
    }

    public static List<IModPlugin> reorder(List<IModPlugin> plugins) {
        if (PROVIDERS.isEmpty()) { return plugins; }
        List<IModPlugin> providers = new ArrayList<>();
        List<IModPlugin> consumers = new ArrayList<>();
        for (IModPlugin plugin : plugins) {
            if (PROVIDERS.contains(plugin)) { providers.add(plugin); }
            else { consumers.add(plugin); }
        }
        if (providers.isEmpty() || consumers.isEmpty()) { return plugins; }
        List<IModPlugin> ordered = new ArrayList<>(providers);
        ordered.addAll(consumers);
        if (!ordered.equals(plugins)) {
            ContentLog.LOGGER.info("Reordered JEI runtime notification so recipe registry providers initialize before plugins that query them: {}", names(providers));
        }
        return ordered;
    }

    private static List<String> names(List<IModPlugin> plugins) {
        List<String> names = new ArrayList<>(plugins.size());
        for (IModPlugin plugin : plugins) { names.add(plugin.getClass().getName()); }
        return names;
    }
}
