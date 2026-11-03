package mctmods.resourcedatapackloader.common.itemlocks;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.network.AbstractStringListMessage;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public final class ContentItemLocks {
    private static volatile List<Group> groups = Collections.emptyList();

    private ContentItemLocks() {}

    public static void load() {
        List<Group> read = new ArrayList<>();
        if (Config.content.itemLocks) { Json.eachObject(PackManager.ITEM_LOCKS, "item lock file", (key, json) -> read(read, key, json)); }
        groups = read;
        if (read.size() > AbstractStringListMessage.MOST) { ContentLog.LOGGER.error("{} item lock files load, more than the {} a player can be told are open, so the rest may stay locked", read.size(), AbstractStringListMessage.MOST); }
        if (!read.isEmpty()) { Summary.info("items.locks", "Loaded " + read.size() + " item lock(s)"); }
    }

    public static void report(MinecraftServer server) {
        for (Group group : groups) {
            if (group.advancement.isEmpty()) { ContentLog.LOGGER.error("Item lock file {} names no advancement, so it stays locked for everyone", group.id); }
            else if (server.getAdvancementManager().getAdvancement(new ResourceLocation(group.advancement)) == null) { ContentLog.LOGGER.error("Item lock file {} names the advancement {}, which no pack defines, so it stays locked for everyone", group.id, group.advancement); }
        }
    }

    public static boolean any() { return !groups.isEmpty(); }

    public static int count() { return groups.size(); }

    public static Set<String> open(EntityPlayer player) {
        Set<String> open = new HashSet<>();
        for (Group group : groups) {
            if (Advancements.has(player, group.advancement)) { open.add(group.id); }
        }
        return open;
    }

    public static boolean closed(ItemStack stack, Set<String> open) {
        for (Group group : groups) {
            if (!open.contains(group.id) && group.selection.matches(stack)) { return true; }
        }
        return false;
    }

    private static void read(List<Group> into, ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        ItemSelection selection = new ItemSelection("Item lock", "locks");
        selection.read(json, key);
        if (selection.unnamed()) {
            ContentLog.LOGGER.error("Item lock file {} selects no items, ignoring it", key);
            return;
        }
        selection.resolve();
        into.add(new Group(key.toString(), Json.getString(json, "advancement", "").trim(), selection));
    }

    private static final class Group {
        final String id;
        final String advancement;
        final ItemSelection selection;

        Group(String id, String advancement, ItemSelection selection) {
            this.id = id;
            this.advancement = advancement;
            this.selection = selection;
        }
    }
}
