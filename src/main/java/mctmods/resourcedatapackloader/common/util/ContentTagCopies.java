package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public final class ContentTagCopies {
    private static final String ITEM_TAGS = Registries.tagsDirPath(Registries.ITEM);
    private static final String BLOCK_TAGS = Registries.tagsDirPath(Registries.BLOCK);
    private static final String ITEM_FOLDER = "item/";
    private static final String BLOCK_FOLDER = "block/";
    private static final Identifier AIR = Identifier.withDefaultNamespace("air");
    private static final PackGeneration GENERATION = new PackGeneration();
    private static volatile List<Copy> copies = List.of();

    private ContentTagCopies() {}

    public static <T> void apply(String directory, Map<Identifier, List<T>> built, Function<Identifier, Optional<? extends T>> lookup) {
        boolean items = ITEM_TAGS.equals(directory);
        if (!items && !BLOCK_TAGS.equals(directory)) { return; }
        reload();
        int added = 0;
        Set<Identifier> grown = new HashSet<>();
        for (Copy copy : copies) {
            try {
                if (copy.items != items) { continue; }
                Set<Identifier> candidates = copy.from == null ? registered(items) : members(built.get(copy.from));
                if (candidates.isEmpty()) {
                    ContentLog.LOGGER.error("Tag copy in {} takes from '{}', which has no members, so nothing is copied", copy.key, copy.from);
                    continue;
                }
                List<T> current = built.getOrDefault(copy.tag, List.of());
                Set<Identifier> have = members(current);
                List<T> merged = new ArrayList<>(current);
                int found = 0;
                int before = merged.size();
                for (Identifier id : candidates) {
                    if (!copy.pattern.matcher(id.toString()).find()) { continue; }
                    found++;
                    if (have.contains(id)) { continue; }
                    Optional<? extends T> value = lookup.apply(id);
                    if (value.isEmpty()) { continue; }
                    merged.add(value.get());
                    have.add(id);
                }
                if (found == 0) { ContentLog.LOGGER.warn("Tag copy in {} matched nothing for '{}'", copy.key, copy.pattern.pattern()); }
                if (merged.size() == before) { continue; }
                built.put(copy.tag, ImmutableSet.copyOf(merged).asList());
                grown.add(copy.tag);
                added += merged.size() - before;
            }
            catch (RuntimeException | StackOverflowError ex) {
                ContentLog.LOGGER.error("Tag copy in {} failed on its pattern '{}' ({}), ignoring it", copy.key, copy.pattern.pattern(), ex.toString());
            }
        }
        if (added > 0) { Summary.info("tag_copied." + (items ? "items" : "blocks"), "Copied " + added + (items ? " item(s)" : " block(s)") + " into " + grown.size() + " tag(s) by pattern"); }
    }

    private static Set<Identifier> registered(boolean items) {
        Set<Identifier> ids = new HashSet<>(items ? BuiltInRegistries.ITEM.keySet() : BuiltInRegistries.BLOCK.keySet());
        ids.remove(AIR);
        return ids;
    }

    private static <T> Set<Identifier> members(@Nullable List<T> values) {
        Set<Identifier> ids = new HashSet<>();
        if (values == null) { return ids; }
        for (T value : values) {
            Identifier id = ItemSelection.id(value);
            if (id != null) { ids.add(id); }
        }
        return ids;
    }

    private static synchronized void reload() {
        if (!GENERATION.stale()) { return; }
        List<Copy> read = new ArrayList<>();
        Json.eachObject(PackManager.TAG_COPIES, "tag copy file", (key, json) -> read(read, key, json));
        copies = List.copyOf(read);
    }

    private static void read(List<Copy> into, Identifier key, JsonObject json) {
        boolean items = key.getPath().startsWith(ITEM_FOLDER);
        if (!items && !key.getPath().startsWith(BLOCK_FOLDER)) {
            ContentLog.LOGGER.error("Tag copy in {} sits outside {}/{} and {}/{}, ignoring it", key, PackManager.TAG_COPIES, "item", PackManager.TAG_COPIES, "block");
            return;
        }
        Identifier tag = tagId(json.has("tag") && json.get("tag").isJsonPrimitive() ? json.get("tag").getAsString() : "");
        if (tag == null) {
            ContentLog.LOGGER.error("Tag copy in {} names no valid tag, ignoring it", key);
            return;
        }
        String matches = json.has("matches") && json.get("matches").isJsonPrimitive() ? json.get("matches").getAsString() : "";
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
        Identifier from = null;
        if (json.has("from") && json.get("from").isJsonPrimitive()) {
            String text = json.get("from").getAsString();
            from = tagId(text);
            if (from == null) {
                ContentLog.LOGGER.error("Tag copy in {} takes from '{}', which is not a valid tag id, ignoring it", key, text);
                return;
            }
        }
        into.add(new Copy(key, tag, pattern, from, items));
    }

    @Nullable
    private static Identifier tagId(String text) {
        String trimmed = text.trim();
        return Identifier.tryParse(trimmed.startsWith("#") ? trimmed.substring(1) : trimmed);
    }

    private record Copy(Identifier key, Identifier tag, Pattern pattern, @Nullable Identifier from, boolean items) {}
}
