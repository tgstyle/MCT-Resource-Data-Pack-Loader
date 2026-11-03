package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class ItemSelection {
    private final String kind;
    private final String verb;
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final Set<Identifier> tags = new LinkedHashSet<>();

    public ItemSelection(String kind, String verb) {
        this.kind = kind;
        this.verb = verb;
    }

    public void clear() {
        names.clear();
        prefixes.clear();
        tags.clear();
    }

    public void read(JsonObject json, Identifier key) {
        for (String name : Json.strings(json, "names")) {
            String trimmed = Settings.lower(name);
            if (trimmed.indexOf(':') < 0) { ContentLog.LOGGER.error("Name '{}' in {} needs a namespace, such as minecraft:iron_ingot, skipping it", name, key); }
            else if (trimmed.endsWith("*")) { prefixes.add(trimmed.substring(0, trimmed.length() - 1)); }
            else { names.add(trimmed); }
        }
        for (String namespace : Json.strings(json, "namespaces")) { prefixes.add(Settings.lower(namespace) + ":"); }
        for (String tag : Json.strings(json, "tags")) {
            Identifier location = Registered.tagId(tag);
            if (location == null) { ContentLog.LOGGER.error("Tag '{}' in {} is not a valid name, skipping it", tag, key); }
            else { tags.add(location); }
        }
    }

    public static boolean isPrefixName(String name) { return Settings.lower(name).endsWith("*") && Settings.lower(name).split(":").length == 2; }

    public boolean unnamed() { return names.isEmpty() && prefixes.isEmpty() && tags.isEmpty(); }

    public Set<Identifier> tags() { return Collections.unmodifiableSet(tags); }

    public Set<Identifier> items() {
        Set<Identifier> ids = new LinkedHashSet<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) { continue; }
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (named(id) || item instanceof BlockItem block && named(BuiltInRegistries.BLOCK.getKey(block.getBlock()))) { ids.add(id); }
        }
        return ids;
    }

    public Set<Identifier> blocks() {
        Set<Identifier> ids = new LinkedHashSet<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == Blocks.AIR) { continue; }
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            Item item = block.asItem();
            if (named(id) || item != Items.AIR && named(BuiltInRegistries.ITEM.getKey(item))) { ids.add(id); }
        }
        return ids;
    }

    public <T> void tagged(Map<Identifier, List<T>> built, Set<Identifier> ids, Consumer<Identifier> missing) {
        for (Identifier tag : tags) {
            List<T> held = built.get(tag);
            if (held == null) {
                missing.accept(tag);
                continue;
            }
            for (T value : held) {
                Identifier id = id(value);
                if (id != null) { ids.add(id); }
            }
        }
    }

    public void unknown() {
        for (String name : names) {
            Identifier id = Identifier.tryParse(name);
            if (id == null || !BuiltInRegistries.ITEM.containsKey(id) && !BuiltInRegistries.BLOCK.containsKey(id)) { ContentLog.LOGGER.error("{} name {} is no registered block or item, so it {} nothing", kind, name, verb); }
        }
    }

    @Nullable public static Identifier id(Object value) { return value instanceof Holder<?> holder ? holder.unwrapKey().map(ResourceKey::identifier).orElse(null) : null; }

    private boolean named(Identifier id) {
        String name = id.toString();
        if (names.contains(name)) { return true; }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }
}
