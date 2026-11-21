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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public final class ContentTagCopies {
    private static final String ITEM = "item";
    private static final String BLOCK = "block";
    private static final String ITEM_TAGS = Registries.tagsDirPath(Registries.ITEM);
    private static final String BLOCK_TAGS = Registries.tagsDirPath(Registries.BLOCK);
    private static final List<String> COPY_KEYS = List.of("tag", "matches", "from");
    private static final List<Copy> COPIES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentTagCopies() {}

    public static <T> void apply(String directory, Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup) {
        boolean items = ITEM_TAGS.equals(directory);
        if (!items && !BLOCK_TAGS.equals(directory)) { return; }
        reload();
        DefaultedRegistry<?> registry = items ? BuiltInRegistries.ITEM : BuiltInRegistries.BLOCK;
        int added = 0;
        Set<ResourceLocation> tags = new LinkedHashSet<>();
        for (Copy copy : copies(items)) {
            try {
                int joined = copy(copy, registry, built, lookup);
                if (joined > 0) {
                    added += joined;
                    tags.add(copy.tag);
                }
            }
            catch (RuntimeException | StackOverflowError ex) { ContentLog.LOGGER.error("Tag copy in {} failed on its pattern '{}' ({}), ignoring it", copy.key, copy.pattern.pattern(), ex.toString()); }
        }
        if (added > 0) { Summary.info(items ? "tag_copied.items" : "tag_copied.blocks", "Copied " + added + (items ? " item(s)" : " block(s)") + " into " + tags.size() + " tag(s) by pattern"); }
    }

    private static synchronized List<Copy> copies(boolean items) { return COPIES.stream().filter(copy -> copy.items == items).toList(); }

    private static <T> int copy(Copy copy, DefaultedRegistry<?> registry, Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup) {
        Set<ResourceLocation> candidates;
        if (copy.from == null) { candidates = registry.keySet(); }
        else {
            candidates = new LinkedHashSet<>();
            for (T value : built.getOrDefault(copy.from, List.of())) {
                ResourceLocation id = ItemSelection.id(value);
                if (id != null) { candidates.add(id); }
            }
            if (candidates.isEmpty()) {
                ContentLog.LOGGER.error("Tag copy in {} takes from '{}', which has no members, so nothing is copied", copy.key, copy.from);
                return 0;
            }
        }
        ResourceLocation none = registry.getDefaultKey();
        Set<ResourceLocation> found = new LinkedHashSet<>();
        for (ResourceLocation id : candidates) {
            if (!id.equals(none) && copy.pattern.matcher(id.toString()).find()) { found.add(id); }
        }
        if (found.isEmpty()) {
            ContentLog.LOGGER.warn("Tag copy in {} matched nothing for '{}'", copy.key, copy.pattern.pattern());
            return 0;
        }
        Set<T> members = new LinkedHashSet<>(built.getOrDefault(copy.tag, List.of()));
        int before = members.size();
        for (ResourceLocation id : found) { lookup.apply(id).ifPresent(members::add); }
        if (members.size() == before) { return 0; }
        built.put(copy.tag, ImmutableSet.copyOf(members));
        return members.size() - before;
    }

    private static synchronized void reload() {
        if (!GENERATION.stale()) { return; }
        COPIES.clear();
        Json.eachFile(PackManager.TAG_COPIES, "tag copy file", ContentTagCopies::read);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "tag copy file");
        if (json == null) { return; }
        Json.unknownKeys(json, "the top level", COPY_KEYS);
        String path = key.getPath();
        boolean items = path.startsWith(ITEM + "/");
        if (!items && !path.startsWith(BLOCK + "/")) {
            ContentLog.LOGGER.error("Tag copy in {} sits outside {}/{} and {}/{}, ignoring it", key, PackManager.TAG_COPIES, ITEM, PackManager.TAG_COPIES, BLOCK);
            return;
        }
        ResourceLocation tag = id(Json.getString(json, "tag", ""));
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
        try { pattern = Pattern.compile(matches); }
        catch (PatternSyntaxException ex) {
            ContentLog.LOGGER.error("Tag copy in {} has the pattern '{}', which is not a regular expression ({}), ignoring it", key, matches, ex.getDescription());
            return;
        }
        ResourceLocation from = id(Json.getString(json, "from", ""));
        if (json.has("from") && from == null) {
            ContentLog.LOGGER.error("Tag copy in {} takes from '{}', which is not a valid tag id, ignoring it", key, Json.getString(json, "from", ""));
            return;
        }
        COPIES.add(new Copy(key, items, tag, pattern, from));
    }

    @Nullable private static ResourceLocation id(String text) { return text.isEmpty() ? null : ResourceLocation.tryParse(text.startsWith("#") ? text.substring(1) : text); }

    private record Copy(ResourceLocation key, boolean items, ResourceLocation tag, Pattern pattern, @Nullable ResourceLocation from) {}
}
