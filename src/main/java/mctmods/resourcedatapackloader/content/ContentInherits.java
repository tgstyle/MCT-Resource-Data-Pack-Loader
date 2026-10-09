package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.util.ResourceLocation;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

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
                JsonObject heldVariants = variantsOf(held);
                if (heldVariants != null) {
                    for (Map.Entry<String, JsonElement> variant : heldVariants.entrySet()) {
                        owners.put(new ResourceLocation(entry.getKey().getNamespace(), variant.getKey()), entry.getKey());
                    }
                }
            }
            catch (JsonParseException | IllegalStateException ex) { ContentLog.LOGGER.error("Parsing error in {} {}, it inherits and lends nothing", type, entry.getKey(), ex); }
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
        JsonElement named = held.get("inherits");
        if (!named.isJsonPrimitive()) {
            ContentLog.LOGGER.error("Definition {} has an inherits that is not a name, ignoring its inherits", key);
            resolved.put(key, held);
            return held;
        }
        String asked = named.getAsString();
        ResourceLocation parentName = asked.contains(":") ? new ResourceLocation(asked) : new ResourceLocation(key.getNamespace(), asked);
        ResourceLocation parentFile = owners.get(parentName);
        String parentVariantName = parentName.getPath();
        if (parentFile == null && parsed.containsKey(parentName)) {
            parentFile = parentName;
            parentVariantName = baseVariant(parsed.get(parentName), parentName);
            if (parentVariantName == null) { ContentLog.LOGGER.warn("Definition {} inherits file '{}', which holds several variants and none named '{}', so only its shared stats are inherited. Name a variant instead", key, asked, tail(parentName)); }
        }
        JsonObject parent = parentFile == null ? null : resolve(parentFile, parsed, owners, resolved, walking);
        JsonObject settled = resolved.get(key);
        if (settled != null) { return settled; }
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
        JsonObject parentVariants = variantsOf(parent);
        JsonObject parentVariant = parentVariantName != null && parentVariants != null && parentVariants.has(parentVariantName) ? parentVariants.getAsJsonObject(parentVariantName) : new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject ownVariants = variantsOf(held);
        if (ownVariants != null) {
            for (Map.Entry<String, JsonElement> entry : ownVariants.entrySet()) {
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

    @Nullable private static JsonObject variantsOf(JsonObject file) { return file.has("variants") && file.get("variants").isJsonObject() ? file.getAsJsonObject("variants") : null; }

    @Nullable private static String baseVariant(JsonObject file, ResourceLocation name) {
        JsonObject variants = variantsOf(file);
        if (variants == null) { return null; }
        String tail = tail(name);
        if (variants.has(tail)) { return tail; }
        if (variants.entrySet().size() == 1) { return variants.entrySet().iterator().next().getKey(); }
        return null;
    }

    private static String tail(ResourceLocation name) {
        String path = name.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private static JsonObject copy(JsonObject held) { return new JsonParser().parse(held.toString()).getAsJsonObject(); }
}
