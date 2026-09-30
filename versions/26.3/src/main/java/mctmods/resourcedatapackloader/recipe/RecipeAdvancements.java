package mctmods.resourcedatapackloader.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class RecipeAdvancements {
    private static final String STONE = "minecraft:gui/advancements/backgrounds/stone";
    private static final Map<Object, CompletableFuture<Set<Identifier>>> BY_LOAD = new WeakHashMap<>();

    private RecipeAdvancements() {}

    private static synchronized CompletableFuture<Set<Identifier>> removed(Object load) { return BY_LOAD.computeIfAbsent(load, RecipeAdvancements::pending); }

    private static CompletableFuture<Set<Identifier>> pending(Object load) { return new CompletableFuture<>(); }

    public static void publish(Object load, Set<Identifier> ids) { removed(load).complete(ids); }

    public static CompletableFuture<Map<Identifier, Resource>> pruned(Object load, CompletableFuture<Map<Identifier, Resource>> listing) { return listing.thenCombine(removed(load), RecipeAdvancements::without); }

    private static Map<Identifier, Resource> without(Map<Identifier, Resource> listed, Set<Identifier> removed) {
        Set<String> names = removed.stream().map(Identifier::toString).collect(Collectors.toSet());
        Map<Identifier, Resource> kept = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : listed.entrySet()) {
            JsonElement json = json(entry.getValue());
            if (json == null || !json.isJsonObject()) {
                kept.put(entry.getKey(), entry.getValue());
                continue;
            }
            JsonObject advancement = json.getAsJsonObject();
            if (unlocks(advancement, names)) { continue; }
            kept.put(entry.getKey(), backgrounded(advancement) ? rewritten(entry.getValue(), advancement) : entry.getValue());
        }
        return kept;
    }

    private static boolean unlocks(JsonObject advancement, Set<String> names) {
        JsonElement criteria = advancement.get("criteria");
        return !names.isEmpty() && criteria != null && scan(criteria, names, false);
    }

    private static boolean backgrounded(JsonObject advancement) {
        JsonElement shown = advancement.get("display");
        if (shown == null || !shown.isJsonObject()) { return false; }
        JsonObject display = shown.getAsJsonObject();
        if (advancement.has("parent")) { return display.remove("background") != null; }
        if (display.has("background")) { return false; }
        display.addProperty("background", STONE);
        return true;
    }

    private static Resource rewritten(Resource from, JsonElement json) {
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        return new Resource(from.source(), () -> new ByteArrayInputStream(bytes));
    }

    @Nullable private static JsonElement json(Resource resource) {
        try (Reader reader = resource.openAsReader()) { return StrictJsonParser.parse(reader); }
        catch (IOException | JsonParseException ex) { return null; }
    }

    private static boolean scan(JsonElement element, Set<String> names, boolean recipes) {
        if (element.isJsonPrimitive()) { return recipes && element.getAsJsonPrimitive().isString() && names.contains(element.getAsString()); }
        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                if (scan(item, names, recipes)) { return true; }
            }
            return false;
        }
        if (!element.isJsonObject()) { return false; }
        JsonObject object = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (scan(entry.getValue(), names, "recipes".equals(entry.getKey()))) { return true; }
        }
        return false;
    }
}
