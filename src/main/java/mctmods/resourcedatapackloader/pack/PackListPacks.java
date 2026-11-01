package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import java.util.Collection;
import java.util.Map;

public final class PackListPacks {
    private static boolean checked;
    private static boolean dropped;
    private PackListPacks() {}

    public static Map<String, Pack> apply(Map<String, Pack> found) {
        if (!checked) {
            checked = true;
            for (String id : PackListRules.defaults()) {
                if (!found.containsKey("file/" + id) && !found.containsKey(id)) {
                    ContentLog.LOGGER.error("Pack list: default pack {} is not installed, so it stays off", id);
                }
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
        PackLocationInfo was = pack.location();
        PackLocationInfo location = rule.title == null ? was : new PackLocationInfo(was.id(), Component.translatable(rule.title), was.source(), was.knownPackInfo());
        Component description = rule.description == null ? pack.getDescription() : Component.translatable(rule.description);
        Pack.Metadata metadata = new Pack.Metadata(description, rule.compatible ? PackCompatibility.COMPATIBLE : pack.getCompatibility(), pack.getRequestedFeatures(), pack.metadata.overlays(), rule.hidden || pack.isHidden());
        Pack.Position position = rule.top == null ? pack.getDefaultPosition() : rule.top ? Pack.Position.TOP : Pack.Position.BOTTOM;
        PackSelectionConfig config = new PackSelectionConfig(!dropped && rule.alwaysOn() || pack.isRequired(), position, !dropped && rule.fixed || pack.isFixedPosition());
        return new Pack(location, pack.resources, metadata, config).withChildren(pack.getChildren());
    }
}
