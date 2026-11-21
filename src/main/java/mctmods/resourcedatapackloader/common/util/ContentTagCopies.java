package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagManager;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentTagCopies {
    private static final String ITEM_TAGS = TagManager.getTagDir(Registries.ITEM);
    private static final String BLOCK_TAGS = TagManager.getTagDir(Registries.BLOCK);
    private static final String BLOCK_FOLDER = "block/";
    private static final String ITEM_FOLDER = "item/";
    private static final List<String> COPY_KEYS = List.of("tag", "matches", "from");
    private static volatile List<Copy> copies = List.of();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentTagCopies() {}

    public static <T> void apply(String directory, Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup) {
        boolean items = ITEM_TAGS.equals(directory);
        if (!items && !BLOCK_TAGS.equals(directory)) { return; }
        reload();
        int added = 0;
        Set<ResourceLocation> tags = new HashSet<>();
        for (Copy copy : copies) {
            if (copy.block == items) { continue; }
            try {
                int gained = copy(copy, built, lookup, items);
                if (gained > 0) {
                    added += gained;
                    tags.add(copy.tag);
                }
            }
            catch (RuntimeException | StackOverflowError ex) {
                ContentLog.LOGGER.error("Tag copy in {} failed on its pattern '{}' ({}), ignoring it", copy.key, copy.pattern.pattern(), ex.toString());
            }
        }
        if (added > 0) { Summary.info(items ? "tag_copied.items" : "tag_copied.blocks", "Copied " + added + (items ? " item(s)" : " block(s)") + " into " + tags.size() + " tag(s) by pattern"); }
    }

    private static <T> int copy(Copy copy, Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup, boolean items) {
        Set<ResourceLocation> candidates = new LinkedHashSet<>();
        if (copy.from == null) { candidates.addAll(items ? ForgeRegistries.ITEMS.getKeys() : ForgeRegistries.BLOCKS.getKeys()); }
        else {
            Collection<T> source = built.get(copy.from);
            if (source == null || source.isEmpty()) {
                ContentLog.LOGGER.error("Tag copy in {} takes from '{}', which has no members, so nothing is copied", copy.key, copy.from);
                return 0;
            }
            for (T value : source) {
                ResourceLocation id = ItemSelection.id(value);
                if (id != null) { candidates.add(id); }
            }
        }
        Collection<T> existing = built.get(copy.tag);
        Set<T> members = new LinkedHashSet<>(existing == null ? List.of() : existing);
        boolean found = false;
        int before = members.size();
        ResourceLocation air = items ? ForgeRegistries.ITEMS.getDefaultKey() : ForgeRegistries.BLOCKS.getDefaultKey();
        for (ResourceLocation id : candidates) {
            if (id.equals(air)) { continue; }
            if (!copy.pattern.matcher(id.toString()).find()) { continue; }
            found = true;
            lookup.apply(id).ifPresent(members::add);
        }
        if (!found) {
            ContentLog.LOGGER.warn("Tag copy in {} matched nothing for '{}'", copy.key, copy.pattern.pattern());
            return 0;
        }
        if (members.size() == before) { return 0; }
        built.put(copy.tag, ImmutableSet.copyOf(members));
        return members.size() - before;
    }

    private static synchronized void reload() {
        if (!GENERATION.stale()) { return; }
        List<Copy> found = new ArrayList<>();
        Json.eachFile(PackManager.TAG_COPIES, "tag copy file", (key, contents) -> read(key, contents, found));
        copies = List.copyOf(found);
    }

    private static ResourceLocation tagId(String text) {
        String trimmed = text.trim();
        return ResourceLocation.tryParse(trimmed.startsWith("#") ? trimmed.substring(1) : trimmed);
    }

    private static void read(ResourceLocation key, String contents, List<Copy> into) {
        JsonObject json = Json.object(key, contents, "tag copy file");
        if (json == null) { return; }
        Json.unknownKeys(json, "the top level", COPY_KEYS);
        boolean block = key.getPath().startsWith(BLOCK_FOLDER);
        if (!block && !key.getPath().startsWith(ITEM_FOLDER)) {
            ContentLog.LOGGER.error("Tag copy in {} sits outside {}/{} and {}/{}, ignoring it", key, PackManager.TAG_COPIES, "item", PackManager.TAG_COPIES, "block");
            return;
        }
        ResourceLocation tag = tagId(Json.getString(json, "tag", ""));
        if (tag == null) {
            ContentLog.LOGGER.error("Tag copy in {} names no valid tag, ignoring it", key);
            return;
        }
        String matches = Json.getString(json, "matches", "");
        if (matches.isEmpty()) {
            ContentLog.LOGGER.error("Tag copy in {} has no matches pattern, ignoring it", key);
            return;
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile(matches);
        }
        catch (PatternSyntaxException ex) {
            ContentLog.LOGGER.error("Tag copy in {} has the pattern '{}', which is not a regular expression ({}), ignoring it", key, matches, ex.getDescription());
            return;
        }
        ResourceLocation from = json.has("from") ? tagId(Json.getString(json, "from", "")) : null;
        if (json.has("from") && from == null) {
            ContentLog.LOGGER.error("Tag copy in {} takes from '{}', which is not a valid tag id, ignoring it", key, Json.getString(json, "from", ""));
            return;
        }
        into.add(new Copy(key, tag, pattern, from, block));
    }

    private record Copy(ResourceLocation key, ResourceLocation tag, Pattern pattern, @Nullable ResourceLocation from, boolean block) {}
}
