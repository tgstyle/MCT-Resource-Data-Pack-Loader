package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentInherits {
    private static final String INHERITS = "inherits";

    private ContentInherits() {}

    public static Map<ResourceLocation, String> collect(String folder) {
        Map<ResourceLocation, String> raw = new LinkedHashMap<>();
        PackManager.get().forEach(folder, PackManager.JSON, (namespace, path, contents) -> raw.put(ResourceLocation.fromNamespaceAndPath(namespace, path), contents));
        Map<ResourceLocation, JsonObject> parsed = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> owners = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, String> entry : raw.entrySet()) {
            try {
                JsonObject held = JsonParser.parseString(entry.getValue()).getAsJsonObject();
                parsed.put(entry.getKey(), held);
                if (held.has(ContentParser.VARIANTS) && held.get(ContentParser.VARIANTS).isJsonObject()) {
                    for (Map.Entry<String, JsonElement> variant : held.getAsJsonObject(ContentParser.VARIANTS).entrySet()) {
                        ResourceLocation id = ResourceLocation.tryBuild(entry.getKey().getNamespace(), variant.getKey());
                        if (id != null) { owners.put(id, entry.getKey()); }
                    }
                }
            }
            catch (RuntimeException unreadable) { ContentLog.LOGGER.debug("Definition {} is not readable JSON, inherits cannot see it", entry.getKey()); }
        }
        Map<ResourceLocation, JsonObject> resolved = new LinkedHashMap<>();
        for (ResourceLocation key : parsed.keySet()) { resolve(key, parsed, owners, resolved, new HashSet<>()); }
        Map<ResourceLocation, String> out = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, String> entry : raw.entrySet()) {
            JsonObject held = resolved.get(entry.getKey());
            out.put(entry.getKey(), held != null ? held.toString() : entry.getValue());
        }
        return out;
    }

    @Nullable private static JsonObject resolve(ResourceLocation key, Map<ResourceLocation, JsonObject> parsed, Map<ResourceLocation, ResourceLocation> owners, Map<ResourceLocation, JsonObject> resolved, Set<ResourceLocation> walking) {
        JsonObject known = resolved.get(key);
        if (known != null) { return known; }
        JsonObject held = parsed.get(key);
        if (held == null) { return null; }
        if (!held.has(INHERITS)) {
            resolved.put(key, held);
            return held;
        }
        if (!walking.add(key)) {
            ContentLog.LOGGER.error("Definition {} inherits in a circle, ignoring its inherits", key);
            resolved.put(key, held);
            return held;
        }
        String asked = held.get(INHERITS).getAsString();
        ResourceLocation parentName = asked.contains(":") ? ResourceLocation.tryParse(asked) : ResourceLocation.tryBuild(key.getNamespace(), asked);
        if (parentName == null) {
            ContentLog.LOGGER.error("Definition {} inherits '{}', which is not a valid name, ignoring its inherits", key, asked);
            resolved.put(key, held);
            return held;
        }
        ResourceLocation parentFile = owners.get(parentName);
        String parentVariantName = parentName.getPath();
        if (parentFile == null && parsed.containsKey(parentName)) {
            parentFile = parentName;
            parentVariantName = baseVariant(parsed.get(parentName), parentName);
            if (parentVariantName == null) { ContentLog.LOGGER.warn("Definition {} inherits file '{}', which holds several variants and none named '{}', so only its shared stats are inherited. Name a variant instead", key, asked, tail(parentName)); }
        }
        JsonObject parent = parentFile == null ? null : resolve(parentFile, parsed, owners, resolved, walking);
        if (parent == null) {
            ContentLog.LOGGER.error("Definition {} inherits '{}', which is not a definition of the same kind, ignoring its inherits", key, asked);
            resolved.put(key, held);
            return held;
        }
        JsonObject made = copy(parent);
        made.remove(ContentParser.VARIANTS);
        made.remove(INHERITS);
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            if (!ContentParser.VARIANTS.equals(entry.getKey()) && !INHERITS.equals(entry.getKey())) { made.add(entry.getKey(), entry.getValue()); }
        }
        JsonObject parentVariant = parentVariantName != null && parent.has(ContentParser.VARIANTS) && parent.get(ContentParser.VARIANTS).isJsonObject() && parent.getAsJsonObject(ContentParser.VARIANTS).has(parentVariantName)
                ? parent.getAsJsonObject(ContentParser.VARIANTS).getAsJsonObject(parentVariantName) : new JsonObject();
        JsonObject variants = new JsonObject();
        if (held.has(ContentParser.VARIANTS) && held.get(ContentParser.VARIANTS).isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : held.getAsJsonObject(ContentParser.VARIANTS).entrySet()) {
                JsonObject base = copy(parentVariant);
                if (entry.getValue().isJsonObject()) {
                    for (Map.Entry<String, JsonElement> field : entry.getValue().getAsJsonObject().entrySet()) { base.add(field.getKey(), field.getValue()); }
                }
                variants.add(entry.getKey(), base);
            }
        }
        made.add(ContentParser.VARIANTS, variants);
        resolved.put(key, made);
        return made;
    }

    @Nullable private static String baseVariant(JsonObject file, ResourceLocation name) {
        if (!file.has(ContentParser.VARIANTS) || !file.get(ContentParser.VARIANTS).isJsonObject()) { return null; }
        JsonObject variants = file.getAsJsonObject(ContentParser.VARIANTS);
        String tail = tail(name);
        if (variants.has(tail)) { return tail; }
        if (variants.size() == 1) { return variants.entrySet().iterator().next().getKey(); }
        return null;
    }

    private static String tail(ResourceLocation name) {
        String path = name.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private static JsonObject copy(JsonObject held) { return JsonParser.parseString(held.toString()).getAsJsonObject(); }
}
