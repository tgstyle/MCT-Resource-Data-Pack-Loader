package mctmods.resourcedatapackloader.common.pack.loading;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;

public final class PackListPacks {
    private static boolean checked;
    private static boolean dropped;
    private PackListPacks() {}

    public static Map<String, Pack> apply(Map<String, Pack> found) {
        if (!checked) {
            checked = true;
            for (String id : PackListRules.defaults()) {
                if (!found.containsKey("file/" + id) && !found.containsKey(id)) { ContentLog.LOGGER.error("Pack list: default pack {} is not installed, so it stays off", id); }
            }
        }
        if (PackListRules.rules().isEmpty()) { return found; }
        ImmutableMap.Builder<String, Pack> out = ImmutableMap.builder();
        for (Map.Entry<String, Pack> entry : found.entrySet()) {
            PackListRules.Rule rule = PackListRules.rule(entry.getKey());
            out.put(entry.getKey(), rule == null ? entry.getValue() : rewritten(entry.getValue(), rule));
        }
        return out.build();
    }

    public static void dropAlwaysOn() { dropped = true; }

    public static boolean dropped() { return dropped; }

    public static boolean holdsAlwaysOn(Collection<Pack> selected) {
        if (dropped) { return false; }
        for (Pack pack : selected) {
            PackListRules.Rule rule = PackListRules.rule(pack.getId());
            if (rule != null && rule.alwaysOn() && !PackListRules.own(pack.getId()) && !PackListRules.VANILLA.equals(pack.getId())) { return true; }
        }
        return false;
    }

    private static Pack rewritten(Pack pack, PackListRules.Rule rule) {
        Component title = rule.title == null ? pack.getTitle() : Component.translatable(rule.title);
        Component description = rule.description == null ? pack.getDescription() : Component.translatable(rule.description);
        int format = format(rule.compatible ? PackCompatibility.COMPATIBLE : pack.getCompatibility());
        Pack.Info info = new Pack.Info(description, format, format, pack.getRequestedFeatures(), rule.hidden || pack.isHidden());
        Pack.Position position = rule.top == null ? pack.getDefaultPosition() : rule.top ? Pack.Position.TOP : Pack.Position.BOTTOM;
        return Pack.create(pack.getId(), title, !dropped && rule.alwaysOn() || pack.isRequired(), id -> pack.open(), info, PackType.CLIENT_RESOURCES, position, !dropped && rule.fixed || pack.isFixedPosition(), pack.getPackSource());
    }

    private static int format(PackCompatibility compatibility) {
        int current = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
        return compatibility == PackCompatibility.TOO_OLD ? current - 1 : compatibility == PackCompatibility.TOO_NEW ? current + 1 : current;
    }
}
