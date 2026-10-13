package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentInherits {
    private static final String INHERITS = "inherits";

    private ContentInherits() {}

    public static Map<Identifier, String> collect(String folder) {
        Map<Identifier, String> raw = new LinkedHashMap<>();
        PackManager.get().forEach(folder, PackManager.JSON, (namespace, path, contents) -> raw.put(Identifier.fromNamespaceAndPath(namespace, path), contents));
        Map<Identifier, JsonObject> parsed = new LinkedHashMap<>();
        Map<Identifier, Identifier> owners = new LinkedHashMap<>();
        for (Map.Entry<Identifier, String> entry : raw.entrySet()) {
            try {
                JsonObject held = Json.within(folder, entry.getKey(), () -> JsonParser.parseString(entry.getValue()).getAsJsonObject());
                parsed.put(entry.getKey(), held);
                JsonObject heldVariants = variantsOf(held);
                if (heldVariants != null) {
                    for (Map.Entry<String, JsonElement> variant : heldVariants.entrySet()) {
                        Identifier id = Identifier.tryBuild(entry.getKey().getNamespace(), variant.getKey().toLowerCase(Locale.ROOT));
                        if (id != null) { owners.put(id, entry.getKey()); }
                    }
                }
            }
            catch (RuntimeException unreadable) { ContentLog.LOGGER.error("Parsing error in {} {}, it inherits and lends nothing", folder, entry.getKey(), unreadable); }
        }
        Map<Identifier, JsonObject> resolved = new LinkedHashMap<>();
        for (Identifier key : parsed.keySet()) { resolve(key, parsed, owners, resolved, new HashSet<>()); }
        Map<Identifier, String> out = new LinkedHashMap<>();
        for (Map.Entry<Identifier, String> entry : raw.entrySet()) {
            JsonObject held = resolved.get(entry.getKey());
            out.put(entry.getKey(), held != null ? held.toString() : entry.getValue());
        }
        return out;
    }

    @Nullable private static JsonObject resolve(Identifier key, Map<Identifier, JsonObject> parsed, Map<Identifier, Identifier> owners, Map<Identifier, JsonObject> resolved, Set<Identifier> walking) {
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
        JsonElement named = held.get(INHERITS);
        if (!named.isJsonPrimitive()) {
            ContentLog.LOGGER.error("Definition {} has an inherits that is not a name, ignoring its inherits", key);
            resolved.put(key, held);
            return held;
        }
        String asked = named.getAsString();
        String lowered = asked.toLowerCase(Locale.ROOT);
        Identifier parentName = lowered.contains(":") ? Identifier.tryParse(lowered) : Identifier.tryBuild(key.getNamespace(), lowered);
        if (parentName == null) {
            ContentLog.LOGGER.error("Definition {} inherits '{}', which is not a valid name, ignoring its inherits", key, asked);
            resolved.put(key, held);
            return held;
        }
        Identifier parentFile = owners.get(parentName);
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
        JsonObject made = parent.deepCopy();
        made.remove(ContentParser.VARIANTS);
        made.remove(INHERITS);
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            if (!ContentParser.VARIANTS.equals(entry.getKey()) && !INHERITS.equals(entry.getKey())) { made.add(entry.getKey(), entry.getValue()); }
        }
        JsonObject parentVariants = variantsOf(parent);
        JsonObject parentVariant = parentVariantName != null && parentVariants != null && parentVariants.has(parentVariantName) ? parentVariants.getAsJsonObject(parentVariantName) : new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject ownVariants = variantsOf(held);
        if (ownVariants != null) {
            for (Map.Entry<String, JsonElement> entry : ownVariants.entrySet()) {
                JsonObject base = parentVariant.deepCopy();
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

    @Nullable private static JsonObject variantsOf(JsonObject file) { return GsonHelper.isObjectNode(file, ContentParser.VARIANTS) ? file.getAsJsonObject(ContentParser.VARIANTS) : null; }

    @Nullable private static String baseVariant(JsonObject file, Identifier name) {
        JsonObject variants = variantsOf(file);
        if (variants == null) { return null; }
        String tail = tail(name);
        if (variants.has(tail)) { return tail; }
        if (variants.size() == 1) { return variants.entrySet().iterator().next().getKey(); }
        return null;
    }

    private static String tail(Identifier name) {
        String path = name.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
