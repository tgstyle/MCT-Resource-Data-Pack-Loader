package mctmods.resourcedatapackloader.common.pack.loading;

import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public final class ContentInherits {
    private static final String INHERITS = "inherits";
    public static final Set<String> DIMENSION_IDENTITY = Set.of("id", "suffix");
    public static final Set<String> BIOME_IDENTITY = Set.of("id");

    private ContentInherits() {}

    public static Map<Identifier, String> collect(String folder) { return collect(folder, Set.of(), false); }

    public static Map<Identifier, String> collectFiles(String folder, Set<String> identity) { return collect(folder, identity, true); }

    private static Map<Identifier, String> collect(String folder, Set<String> identity, boolean merging) {
        Map<Identifier, String> raw = new LinkedHashMap<>();
        PackManager.get().forEach(folder, PackManager.JSON, (namespace, path, contents) -> raw.put(Identifier.fromNamespaceAndPath(namespace, path), contents));
        Map<Identifier, JsonObject> parsed = new LinkedHashMap<>();
        Map<Identifier, Identifier> owners = new LinkedHashMap<>();
        for (Map.Entry<Identifier, String> entry : raw.entrySet()) {
            try {
                JsonObject held = Json.within(folder, entry.getKey(), () -> JsonParser.parseString(entry.getValue()).getAsJsonObject());
                parsed.put(entry.getKey(), held);
                for (Identifier id : variantIds(entry.getKey(), held)) { owners.put(id, entry.getKey()); }
            }
            catch (RuntimeException unreadable) { ContentLog.LOGGER.error("Parsing error in {} {}, it inherits and lends nothing", folder, entry.getKey(), unreadable); }
        }
        Map<Identifier, JsonObject> resolved = new LinkedHashMap<>();
        for (Identifier key : parsed.keySet()) { resolve(key, parsed, owners, resolved, new HashSet<>(), identity, merging); }
        Map<Identifier, String> out = new LinkedHashMap<>();
        for (Map.Entry<Identifier, String> entry : raw.entrySet()) {
            JsonObject held = resolved.get(entry.getKey());
            out.put(entry.getKey(), held != null ? held.toString() : entry.getValue());
        }
        return out;
    }

    @Nullable private static JsonObject resolve(Identifier key, Map<Identifier, JsonObject> parsed, Map<Identifier, Identifier> owners, Map<Identifier, JsonObject> resolved, Set<Identifier> walking, Set<String> identity, boolean merging) {
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
            boolean variant = variantsOf(parsed.get(parentName)) != null || variantsOf(held) != null;
            parentVariantName = variant ? baseVariant(parsed.get(parentName), parentName) : null;
            if (variant && parentVariantName == null) { ContentLog.LOGGER.warn("Definition {} inherits file '{}', which holds several variants and none named '{}', so only its shared stats are inherited. Name a variant instead", key, asked, tail(parentName)); }
        }
        JsonObject parent = parentFile == null ? null : resolve(parentFile, parsed, owners, resolved, walking, identity, merging);
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
        if (merging) { identity.forEach(made::remove); }
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            String name = entry.getKey();
            if (ContentParser.VARIANTS.equals(name) || INHERITS.equals(name)) { continue; }
            if (merging && made.has(name) && made.get(name).isJsonObject() && entry.getValue().isJsonObject()) { overlay(made.getAsJsonObject(name), entry.getValue().getAsJsonObject()); }
            else { made.add(name, entry.getValue()); }
        }
        JsonObject ownVariants = variantsOf(held);
        JsonObject parentVariants = variantsOf(parent);
        if (ownVariants != null || parentVariants != null) {
            JsonObject parentVariant = parentVariantName != null && parentVariants != null && parentVariants.has(parentVariantName) ? parentVariants.getAsJsonObject(parentVariantName) : new JsonObject();
            JsonObject variants = new JsonObject();
            if (ownVariants != null) {
                for (Map.Entry<String, JsonElement> entry : ownVariants.entrySet()) {
                    JsonObject base = parentVariant.deepCopy();
                    if (entry.getValue().isJsonObject()) { overlay(base, entry.getValue().getAsJsonObject()); }
                    variants.add(entry.getKey(), base);
                }
            }
            made.add(ContentParser.VARIANTS, variants);
        }
        resolved.put(key, made);
        return made;
    }

    private static void overlay(JsonObject base, JsonObject over) {
        for (Map.Entry<String, JsonElement> field : over.entrySet()) { base.add(field.getKey(), field.getValue()); }
    }

    @Nullable public static Identifier variantId(String namespace, String name) { return Identifier.tryBuild(namespace, name.toLowerCase(Locale.ROOT)); }

    public static List<Identifier> variantIds(Identifier file, JsonObject held) {
        List<Identifier> ids = new ArrayList<>();
        JsonObject variants = variantsOf(held);
        if (variants == null) { return ids; }
        for (String name : variants.keySet()) {
            Identifier id = variantId(file.getNamespace(), name);
            if (id != null) { ids.add(id); }
        }
        return ids;
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
