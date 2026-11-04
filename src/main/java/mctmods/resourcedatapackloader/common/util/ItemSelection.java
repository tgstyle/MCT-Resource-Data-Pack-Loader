package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public final class ItemSelection {
    private final String kind;
    private final String verb;
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final Set<ResourceLocation> tags = new LinkedHashSet<>();

    public ItemSelection(String kind, String verb) {
        this.kind = kind;
        this.verb = verb;
    }

    public void clear() {
        names.clear();
        prefixes.clear();
        tags.clear();
    }

    public void read(JsonObject json, ResourceLocation key) {
        readNames(Json.strings(json, "names"), key);
        for (String namespace : Json.strings(json, "namespaces")) { prefixes.add(Settings.lower(namespace) + ":"); }
        readTags(Json.strings(json, "tags"), key);
    }

    public void readNames(Iterable<String> given, ResourceLocation key) {
        for (String name : given) {
            String trimmed = Settings.lower(name);
            if (trimmed.indexOf(':') < 0) { ContentLog.LOGGER.error("Name '{}' in {} needs a namespace, such as minecraft:iron_ingot, skipping it", name, key); }
            else if (trimmed.endsWith("*")) { prefixes.add(trimmed.substring(0, trimmed.length() - 1)); }
            else { names.add(trimmed); }
        }
    }

    public void readTags(Iterable<String> given, ResourceLocation key) {
        for (String tag : given) {
            ResourceLocation location = Registered.tagId(tag);
            if (location == null) { ContentLog.LOGGER.error("Tag '{}' in {} is not a valid name, skipping it", tag, key); }
            else { tags.add(location); }
        }
    }

    public boolean unnamed() { return names.isEmpty() && prefixes.isEmpty() && tags.isEmpty(); }

    public Set<ResourceLocation> tags() { return Collections.unmodifiableSet(tags); }

    public Set<ResourceLocation> items() {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (item == Items.AIR || id == null) { continue; }
            if (named(id) || item instanceof BlockItem block && named(ForgeRegistries.BLOCKS.getKey(block.getBlock()))) { ids.add(id); }
        }
        return ids;
    }

    public Set<ResourceLocation> blocks() {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (block == Blocks.AIR || id == null) { continue; }
            Item item = block.asItem();
            if (named(id) || item != Items.AIR && named(ForgeRegistries.ITEMS.getKey(item))) { ids.add(id); }
        }
        return ids;
    }

    public <T> void tagged(Map<ResourceLocation, Collection<T>> built, Set<ResourceLocation> ids, Consumer<ResourceLocation> missing) {
        for (ResourceLocation tag : tags) {
            Collection<T> held = built.get(tag);
            if (held == null) {
                missing.accept(tag);
                continue;
            }
            for (T value : held) {
                ResourceLocation id = id(value);
                if (id != null) { ids.add(id); }
            }
        }
    }

    public void unknown() {
        for (String name : names) {
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (id == null || !ForgeRegistries.ITEMS.containsKey(id) && !ForgeRegistries.BLOCKS.containsKey(id)) { ContentLog.LOGGER.error("{} name {} is no registered block or item, so it {} nothing", kind, name, verb); }
        }
    }

    @Nullable public static ResourceLocation id(Object value) { return value instanceof Holder<?> holder ? holder.unwrapKey().map(ResourceKey::location).orElse(null) : null; }

    private boolean named(@Nullable ResourceLocation id) {
        if (id == null) { return false; }
        String name = id.toString();
        if (names.contains(name)) { return true; }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }
}
