package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.ResourceLocation;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ContentInherits {
    private ContentInherits() {}

    public static Map<ResourceLocation, String> collect(String type) {
        Map<ResourceLocation, String> raw = new LinkedHashMap<>();
        PackManager.get().forEach(type, PackManager.JSON, (namespace, path, contents) -> raw.put(new ResourceLocation(namespace, path), contents));

        Map<ResourceLocation, JsonObject> parsed = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> owners = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, String> entry : raw.entrySet()) {
            try {
                JsonObject held = new JsonParser().parse(entry.getValue()).getAsJsonObject();
                parsed.put(entry.getKey(), held);
                if (held.has("variants") && held.get("variants").isJsonObject()) {
                    for (Map.Entry<String, JsonElement> variant : held.getAsJsonObject("variants").entrySet()) {
                        owners.put(new ResourceLocation(entry.getKey().getNamespace(), variant.getKey()), entry.getKey());
                    }
                }
            }
            catch (Exception ignored) {}
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

    private static JsonObject resolve(ResourceLocation key, Map<ResourceLocation, JsonObject> parsed, Map<ResourceLocation, ResourceLocation> owners, Map<ResourceLocation, JsonObject> resolved, Set<ResourceLocation> walking) {
        JsonObject known = resolved.get(key);
        if (known != null) { return known; }

        JsonObject held = parsed.get(key);
        if (held == null) { return null; }
        if (!held.has("inherits")) {
            resolved.put(key, held);
            return held;
        }
        if (!walking.add(key)) {
            ContentLog.LOGGER.error("Definition {} inherits in a circle, ignoring its inherits", key);
            resolved.put(key, held);
            return held;
        }

        String asked = held.get("inherits").getAsString();
        ResourceLocation parentName = asked.contains(":") ? new ResourceLocation(asked) : new ResourceLocation(key.getNamespace(), asked);
        ResourceLocation parentFile = owners.get(parentName);
        JsonObject parent = parentFile == null ? null : resolve(parentFile, parsed, owners, resolved, walking);
        if (parent == null) {
            ContentLog.LOGGER.error("Definition {} inherits '{}', which is not a definition of the same kind, ignoring its inherits", key, asked);
            resolved.put(key, held);
            return held;
        }

        JsonObject made = copy(parent);
        made.remove("variants");
        made.remove("inherits");
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            if (!"variants".equals(entry.getKey()) && !"inherits".equals(entry.getKey())) { made.add(entry.getKey(), entry.getValue()); }
        }

        JsonObject parentVariant = parent.has("variants") && parent.getAsJsonObject("variants").has(parentName.getPath())
                ? parent.getAsJsonObject("variants").getAsJsonObject(parentName.getPath()) : new JsonObject();
        JsonObject variants = new JsonObject();
        if (held.has("variants") && held.get("variants").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : held.getAsJsonObject("variants").entrySet()) {
                JsonObject base = copy(parentVariant);
                if (entry.getValue().isJsonObject()) {
                    for (Map.Entry<String, JsonElement> field : entry.getValue().getAsJsonObject().entrySet()) { base.add(field.getKey(), field.getValue()); }
                }
                variants.add(entry.getKey(), base);
            }
        }
        made.add("variants", variants);
        resolved.put(key, made);
        return made;
    }

    private static JsonObject copy(JsonObject held) { return new JsonParser().parse(held.toString()).getAsJsonObject(); }
}
