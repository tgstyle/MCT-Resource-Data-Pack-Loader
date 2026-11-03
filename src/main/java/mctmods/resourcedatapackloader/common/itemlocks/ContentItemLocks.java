package mctmods.resourcedatapackloader.common.itemlocks;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.network.StringListCodec;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public final class ContentItemLocks {
    private static final String ITEM_TAGS = Registries.tagsDirPath(Registries.ITEM);
    private static final String LOCKED = "locked/";
    private static final PackGeneration GENERATION = new PackGeneration();
    private static volatile List<Group> groups = Collections.emptyList();

    private ContentItemLocks() {}

    public static <T> void strip(String directory, Map<Identifier, List<T>> built, Function<Identifier, Optional<? extends T>> lookup) {
        if (!ITEM_TAGS.equals(directory)) { return; }
        reload();
        for (Group group : groups) {
            Set<Identifier> ids = group.selection.items();
            group.selection.tagged(built, ids, tag -> ContentLog.LOGGER.error("Item lock tag {} in {} is no item tag, so it locks nothing", tag, group.id));
            group.selection.unknown();
            List<T> values = new ArrayList<>();
            for (Identifier id : ids) { lookup.apply(id).ifPresent(values::add); }
            built.put(group.tag, ImmutableSet.copyOf(values).asList());
        }
    }

    @Nullable public static String group(Identifier tag) {
        String path = tag.getPath();
        if (!ResourceDataPackLoader.MOD_ID.equals(tag.getNamespace()) || !path.startsWith(LOCKED)) { return null; }
        int slash = path.indexOf('/', LOCKED.length());
        return slash < 0 ? null : path.substring(LOCKED.length(), slash) + ":" + path.substring(slash + 1);
    }

    public static void onServerStarted(ServerStartedEvent event) { report(event.getServer()); }

    public static void report(MinecraftServer server) {
        for (Group group : groups) {
            if (group.advancement.isEmpty()) { ContentLog.LOGGER.error("Item lock file {} names no advancement, so it stays locked for everyone", group.id); }
            else if (!Advancements.defined(server, group.advancement)) { ContentLog.LOGGER.error("Item lock file {} names the advancement {}, which no pack defines, so it stays locked for everyone", group.id, group.advancement); }
        }
    }

    public static boolean any() { return !groups.isEmpty(); }

    public static int count() { return groups.size(); }

    public static Set<String> open(Player player) {
        Set<String> open = new HashSet<>();
        for (Group group : groups) {
            if (Advancements.has(player, group.advancement)) { open.add(group.id); }
        }
        return open;
    }

    private static void reload() {
        if (!GENERATION.stale()) { return; }
        List<Group> read = new ArrayList<>();
        if (Config.content.itemLocks()) { Json.eachObject(PackManager.ITEM_LOCKS, "item lock file", (key, json) -> read(read, key, json)); }
        groups = read;
        if (read.size() > StringListCodec.MOST) { ContentLog.LOGGER.error("{} item lock files load, more than the {} a player can be told are open, so the rest may stay locked", read.size(), StringListCodec.MOST); }
        if (!read.isEmpty()) { Summary.info("items.locks", "Loaded " + read.size() + " item lock(s)"); }
    }

    private static void read(List<Group> into, Identifier key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        ItemSelection selection = new ItemSelection("Item lock", "locks");
        selection.read(json, key);
        if (selection.unnamed()) {
            ContentLog.LOGGER.error("Item lock file {} selects no items, ignoring it", key);
            return;
        }
        into.add(new Group(key.toString(), ResourceDataPackLoader.id(LOCKED + key.getNamespace() + "/" + key.getPath()), Json.getString(json, "advancement", "").trim(), selection));
    }

    private record Group(String id, Identifier tag, String advancement, ItemSelection selection) {}
}
