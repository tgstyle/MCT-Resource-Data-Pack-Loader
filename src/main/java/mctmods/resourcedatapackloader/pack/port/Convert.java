package mctmods.resourcedatapackloader.pack.port;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class Convert {
    private static final Set<String> BLOCK_KEYS = Set.of("block", "replace", "adjacent", "soil", "log", "leaves", "topBlock", "fillerBlock", "stoneBlock", "edge", "ground", "crops", "platformBlock", "portalBlocks", "outline", "fill", "surface", "coverReplace", "floorCover", "ceilingCover", "jobSite", "modelBlock", "leafSapling", "crop", "rich", "poor", "deepStone", "skyStone", "voidPlatformBlock", "except", "blocks", "flatBedrockFiller", "hoeTillsInto", "shovelPathBecomes", "shovelPathReverts", "sewerBlock");
    private static final Set<String> ITEM_KEYS = Set.of("item", "items", "container", "seed", "produce", "icon", "repairItem", "hold", "consume", "craft", "killedDrops", "drop", "output", "input", "result", "ingredient", "opensWith", "containerItem", "mainhand", "offhand", "head", "chest", "legs", "feet", "immunityItem", "ignitedBy", "saysIcon");
    private static final Set<String> ENTITY_KEYS = Set.of("entity", "entities", "targets", "killed", "variant", "customEntitiesToHeal", "villagerEntity", "becomes");
    private static final Set<String> BIOME_KEYS = Set.of("biomes", "biome", "replaces", "baseBiome", "biomeNames", "roles", "biomeWhitelist");
    private static final Set<String> DIMENSION_KEYS = Set.of("dimensions", "dimension", "returnDimension", "respawnDimension", "blockOreDimensions", "blockBiomeDimensions", "flatBedrockDimensions", "voidWorldDimensions", "blockReplacementDimensions", "pregenDimensions", "pregenDimensionsWhenEntered", "blockGeneratorDimensions", "rubicWorldDimensions");
    private static final Set<String> DIMENSION_PREFIXED = Set.of("welcomeSays", "worldDifficulty", "cloudHeight", "weatherCeiling", "worldGravity", "worldFallDamage", "worldJumpStrength", "worldTerminalVelocity", "worldBelow", "worldAbove", "flatBedrockFillers", "structureBiomes", "structureBiomesAreBlacklist");
    private static final Set<String> STATE_KEYS = Set.of("replace", "adjacent", "except", "blocks");
    private static final Set<String> RAW_CONTAINERS = Set.of("palette", "legend", "rows", "map", "pattern", "key", "notes", "attributes", "pathPriorities", "gameRules", "decoration", "spawnRates", "settings");
    private static final Pattern DIM_PREFIX = Pattern.compile("^(-?\\d+)=(.*)$");
    private static final Pattern REPLACEMENT = Pattern.compile("^([^=]+)=([^=]+)$");
    private static final Pattern LANG_TILE = Pattern.compile("^(tile|item)\\.([a-z0-9_]+)[:.]([a-z0-9_./-]+?)(?:\\.([a-z0-9_]+))?\\.(name|locked)$");
    private static final Pattern LANG_ENTITY = Pattern.compile("^entity\\.([a-z0-9_]+)\\.([a-z0-9_]+)\\.name$");
    private static final Pattern LANG_FLUID = Pattern.compile("^fluid\\.(?:([a-z0-9_]+)\\.)?([a-z0-9_]+)$");
    private static final Pattern LANG_TAB = Pattern.compile("^itemGroup\\.([a-z0-9_]+)$");

    private Convert() {}

    public enum Context { NONE, BLOCK, ITEM, ENTITY, BIOME, DIMENSION }

    public static String definition(JsonObject json, String folder, String namespace, String file, Ported pack) {
        Map<String, String> renamed = pack.renamedIn(namespace, folder, file);
        if (!renamed.isEmpty() && json.has("variants") && json.get("variants").isJsonObject()) {
            JsonObject variants = json.getAsJsonObject("variants");
            JsonObject out = new JsonObject();
            for (Map.Entry<String, JsonElement> variant : variants.entrySet()) { out.add(renamed.getOrDefault(variant.getKey(), variant.getKey()), variant.getValue()); }
            json.add("variants", out);
            pack.rewrote();
        }
        if ("blocks".equals(folder) && json.has("harvestLevel") && !json.has("harvestToolLevel")) { json.add("harvestToolLevel", json.remove("harvestLevel")); }
        if (("blocks".equals(folder) || "items".equals(folder) || "fluids".equals(folder)) && json.has("creativeTab") && json.get("creativeTab").isJsonPrimitive()) {
            String tab = json.get("creativeTab").getAsString().trim();
            if (!tab.isEmpty() && tab.indexOf(':') < 0) { json.addProperty("creativeTab", pack.mainNamespace() + ":" + tab); }
        }
        if ("gamerules".equals(folder) || "blastplaster".equals(folder)) { renameDimensionKeys("gamerules".equals(folder) ? json : json.has("dimensions") && json.get("dimensions").isJsonObject() ? json.getAsJsonObject("dimensions") : new JsonObject(), pack); }
        if ("dimensions".equals(folder)) {
            json.remove("id");
            json.remove("suffix");
        }
        if ("worldgen".equals(folder) && json.has("block") && json.has("meta")) {
            json.addProperty("block", json.get("block").getAsString() + ":" + json.get("meta").getAsInt());
            json.remove("meta");
        }
        walk(json, "", Context.NONE, pack, folder);
        return Ported.GSON.toJson(json);
    }

    private static void renameDimensionKeys(JsonObject json, Ported pack) {
        List<String> keys = new ArrayList<>(json.keySet());
        for (String key : keys) {
            String mapped = Ids.dimension(key);
            if (!mapped.equals(key)) {
                JsonElement held = json.remove(key);
                json.add(mapped, held);
                pack.rewrote();
            }
        }
    }

    private static void walk(JsonObject json, String parentKey, Context inherited, Ported pack, String folder) {
        if (json.has("meta") && (json.has("block") || json.has("item"))) {
            String key = json.has("block") ? "block" : "item";
            String named = json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : "";
            if (!named.isEmpty() && Ids.splitMeta(named) == null) { json.addProperty(key, named + ":" + json.get("meta").getAsInt()); }
            json.remove("meta");
        }
        if ("variants".equals(parentKey)) {
            for (Map.Entry<String, JsonElement> variant : json.entrySet()) {
                if (!variant.getValue().isJsonObject()) { continue; }
                JsonObject held = variant.getValue().getAsJsonObject();
                held.remove("meta");
                if (held.has("oreDict")) {
                    JsonArray tags = held.has("tags") && held.get("tags").isJsonArray() ? held.getAsJsonArray("tags") : new JsonArray();
                    for (JsonElement ore : held.getAsJsonArray("oreDict")) { tags.add(Ids.oreDictTag(ore.getAsString())); }
                    held.remove("oreDict");
                    held.add("tags", tags);
                    pack.rewrote();
                }
                walk(held, variant.getKey(), Context.NONE, pack, folder);
            }
            return;
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(json.entrySet())) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Context context = contextOf(key, inherited);
            if (value.isJsonObject()) {
                if ("blocks".equals(key) && "worldgen".equals(folder)) { continue; }
                walk(value.getAsJsonObject(), key, RAW_CONTAINERS.contains(key) ? Context.NONE : context, pack, folder);
            }
            else if (value.isJsonArray()) {
                JsonArray array = value.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    JsonElement element = array.get(i);
                    if (element.isJsonObject()) { walk(element.getAsJsonObject(), key, context, pack, folder); }
                    else if (element.isJsonPrimitive()) {
                        JsonElement replaced = primitive(key, key, element.getAsJsonPrimitive(), context, pack);
                        if (replaced != null) { array.set(i, replaced); }
                    }
                }
            }
            else if (value.isJsonPrimitive()) {
                JsonElement replaced = primitive(key, parentKey, value.getAsJsonPrimitive(), context, pack);
                if (replaced != null) {
                    if (replaced.isJsonObject() && "block".equals(key)) {
                        JsonObject state = replaced.getAsJsonObject();
                        json.addProperty("block", state.get("block").getAsString());
                        if (state.has("properties") && !json.has("properties")) { json.add("properties", state.get("properties")); }
                    }
                    else { json.add(key, replaced); }
                }
            }
        }
    }

    private static Context contextOf(String key, Context inherited) {
        if (BLOCK_KEYS.contains(key)) { return Context.BLOCK; }
        if (ITEM_KEYS.contains(key)) { return Context.ITEM; }
        if (ENTITY_KEYS.contains(key)) { return Context.ENTITY; }
        if (BIOME_KEYS.contains(key)) { return Context.BIOME; }
        if (DIMENSION_KEYS.contains(key)) { return Context.DIMENSION; }
        if (key.startsWith("village") && (key.endsWith("Block") || key.endsWith("Base") || key.endsWith("Blocks"))) { return Context.BLOCK; }
        return inherited;
    }

    @Nullable private static JsonElement primitive(String key, String parentKey, JsonPrimitive value, Context context, Ported pack) {
        if (context == Context.DIMENSION) {
            String named = value.isNumber() ? String.valueOf(value.getAsInt()) : value.getAsString().trim();
            String mapped = Ids.dimension(named);
            if (!mapped.equals(named) || value.isNumber()) {
                pack.rewrote();
                return new JsonPrimitive(mapped);
            }
            return null;
        }
        if (!value.isString()) { return null; }
        String text = value.getAsString();
        if (DIMENSION_PREFIXED.contains(key)) {
            Matcher m = DIM_PREFIX.matcher(text.trim());
            if (m.matches()) {
                String rest = "worldBelow".equals(key) || "worldAbove".equals(key) ? Ids.dimension(m.group(2).trim()) : m.group(2);
                pack.rewrote();
                return new JsonPrimitive(Ids.dimension(m.group(1)) + "=" + rest);
            }
            return null;
        }
        if ("blockReplacements".equals(key)) {
            Matcher m = REPLACEMENT.matcher(text.trim());
            if (m.matches()) {
                String from = blockName(m.group(1).trim(), pack);
                String to = blockName(m.group(2).trim(), pack);
                if (!from.equals(m.group(1).trim()) || !to.equals(m.group(2).trim())) {
                    pack.rewrote();
                    return new JsonPrimitive(from + "=" + to);
                }
            }
            return null;
        }
        if ("biomeTypes".equals(key) || "types".equals(key)) {
            String lowered = text.trim().toLowerCase(Locale.ROOT);
            return lowered.equals(text) ? null : new JsonPrimitive(lowered);
        }
        if ("potion".equals(key) && text.indexOf(',') > 0) {
            String tight = text.replace(" ", "");
            return tight.equals(text) ? null : new JsonPrimitive(tight);
        }
        return switch (context) {
            case BLOCK -> block(text, pack, STATE_KEYS.contains(parentKey));
            case ITEM -> {
                String mapped = itemName(text, pack);
                yield mapped.equals(text) ? null : new JsonPrimitive(mapped);
            }
            case ENTITY -> {
                if (text.indexOf(':') < 0 || text.contains("=")) { yield null; }
                String mapped = pack.owns(text) ? text : Ids.entity(text);
                yield mapped.equals(text) ? null : rewrite(pack, mapped);
            }
            case BIOME -> {
                if (text.indexOf(':') < 0 || text.contains("=")) { yield null; }
                String mapped = pack.owns(text) ? text : Ids.biome(text);
                yield mapped.equals(text) ? null : rewrite(pack, mapped);
            }
            default -> null;
        };
    }

    private static JsonElement rewrite(Ported pack, String mapped) {
        pack.rewrote();
        return new JsonPrimitive(mapped);
    }

    @Nullable private static JsonElement block(String text, Ported pack, boolean states) {
        String trimmed = text.trim();
        if (trimmed.isEmpty() || trimmed.contains("=") || trimmed.indexOf(':') < 0) { return null; }
        String[] split = Ids.splitMeta(trimmed);
        String name = split == null ? trimmed : split[0];
        int meta = split == null ? 0 : Integer.parseInt(split[1]);
        if (pack.owns(name)) {
            String own = pack.ownBlock(name, meta);
            if (own == null) { own = pack.ownItem(name, meta); }
            if (own == null || own.equals(trimmed)) { return null; }
            pack.rewrote();
            return new JsonPrimitive(own);
        }
        if (!Ids.isVanilla(name)) { return null; }
        Ids.Block state = Ids.block(name, meta);
        if (state.name().equals(trimmed) && state.properties().isEmpty()) { return null; }
        pack.rewrote();
        if (state.properties().isEmpty() || !states || meta == 0) { return new JsonPrimitive(state.name()); }
        JsonObject out = new JsonObject();
        out.addProperty("block", state.name());
        JsonObject properties = new JsonObject();
        for (Map.Entry<String, String> property : state.properties().entrySet()) { properties.addProperty(property.getKey(), property.getValue()); }
        out.add("properties", properties);
        return out;
    }

    public static String blockName(String text, Ported pack) {
        JsonElement mapped = block(text, pack, false);
        if (mapped == null) { return text; }
        return mapped.isJsonObject() ? mapped.getAsJsonObject().get("block").getAsString() : mapped.getAsString();
    }

    public static String itemName(String text, Ported pack) {
        String trimmed = text.trim();
        if (trimmed.isEmpty() || trimmed.contains("=") || trimmed.indexOf(':') < 0) { return text; }
        String[] split = Ids.splitMeta(trimmed);
        String name = split == null ? trimmed : split[0];
        int meta = split == null ? 0 : Integer.parseInt(split[1]);
        if (pack.owns(name)) {
            String own = pack.ownItem(name, meta);
            if (own == null) { own = pack.ownBlock(name, meta); }
            if (own == null) { return text; }
            if (!own.equals(trimmed)) { pack.rewrote(); }
            return own;
        }
        if (!Ids.isVanilla(name)) { return text; }
        String mapped = Ids.item(name, meta);
        if (!mapped.equals(trimmed)) { pack.rewrote(); }
        return mapped;
    }

    public static String lang(String contents, Ported pack) {
        Map<String, String> out = new LinkedHashMap<>();
        for (String line : contents.split("\r?\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) { continue; }
            int eq = trimmed.indexOf('=');
            if (eq <= 0) { continue; }
            String key = trimmed.substring(0, eq).trim();
            String value = trimmed.substring(eq + 1);
            for (String mapped : langKeys(key, pack)) { out.put(mapped, value); }
        }
        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> entry : out.entrySet()) { json.addProperty(entry.getKey(), entry.getValue()); }
        return Ported.GSON.toJson(json);
    }

    private static List<String> langKeys(String key, Ported pack) {
        Matcher m = LANG_TILE.matcher(key);
        if (m.matches()) {
            String namespace = m.group(2);
            String file = m.group(3);
            String variant = m.group(4);
            String suffix = "locked".equals(m.group(5)) ? ".locked" : "";
            boolean tile = "tile".equals(m.group(1));
            String name = variant != null ? variant : tile ? pack.mainVariantOfBlockFile(namespace, file) : pack.mainVariantOfItemFile(namespace, file);
            if (name == null) { name = file.substring(file.lastIndexOf('/') + 1); }
            List<String> keys = new ArrayList<>();
            keys.add((tile ? "block." : "item.") + namespace + "." + name + suffix);
            if (!tile && pack.ownBlock(namespace + ":" + name, 0) != null) { keys.add("block." + namespace + "." + name + suffix); }
            pack.rewrote();
            return keys;
        }
        m = LANG_ENTITY.matcher(key);
        if (m.matches() && pack.ownsNamespace(m.group(1))) {
            pack.rewrote();
            return List.of("entity." + m.group(1) + "." + m.group(2));
        }
        m = LANG_FLUID.matcher(key);
        if (m.matches()) {
            String namespace = m.group(1) != null ? m.group(1) : pack.mainNamespace();
            pack.rewrote();
            return List.of("fluid." + namespace + "." + m.group(2), "fluid_type." + namespace + "." + m.group(2));
        }
        m = LANG_TAB.matcher(key);
        if (m.matches()) {
            pack.rewrote();
            return List.of("itemGroup." + pack.mainNamespace() + "." + m.group(1));
        }
        return List.of(key);
    }

    public static String texturePath(String path) {
        String namespace = path.indexOf(':') < 0 ? "minecraft" : path.substring(0, path.indexOf(':'));
        String rest = path.indexOf(':') < 0 ? path : path.substring(path.indexOf(':') + 1);
        if (rest.startsWith("blocks/")) { rest = "block/" + rest.substring("blocks/".length()); }
        else if (rest.startsWith("items/")) { rest = "item/" + rest.substring("items/".length()); }
        else if (rest.startsWith("textures/blocks/")) { rest = "textures/block/" + rest.substring("textures/blocks/".length()); }
        else if (rest.startsWith("textures/items/")) { rest = "textures/item/" + rest.substring("textures/items/".length()); }
        else { return path; }
        return namespace + ":" + rest;
    }

    public static String model(JsonObject json, Ported pack) {
        if (json.has("textures") && json.get("textures").isJsonObject()) {
            JsonObject textures = json.getAsJsonObject("textures");
            for (Map.Entry<String, JsonElement> texture : new ArrayList<>(textures.entrySet())) {
                if (!texture.getValue().isJsonPrimitive()) { continue; }
                String held = texture.getValue().getAsString();
                if (held.startsWith("#")) { continue; }
                String mapped = texturePath(held);
                if (!mapped.equals(held)) {
                    textures.addProperty(texture.getKey(), mapped);
                    pack.rewrote();
                }
            }
        }
        if (json.has("parent") && json.get("parent").isJsonPrimitive()) {
            String parent = json.get("parent").getAsString();
            if (parent.indexOf(':') < 0 && !parent.startsWith("block/") && !parent.startsWith("item/") && !parent.startsWith("builtin/")) {
                json.addProperty("parent", "minecraft:block/" + parent);
                pack.rewrote();
            }
        }
        return Ported.GSON.toJson(json);
    }

    public static String pixelMap(JsonObject json, Ported pack) {
        if (json.has("extends") && json.get("extends").isJsonPrimitive()) {
            String held = json.get("extends").getAsString();
            String mapped = texturePath(held);
            if (!mapped.equals(held)) {
                json.addProperty("extends", mapped);
                pack.rewrote();
            }
        }
        return Ported.GSON.toJson(json);
    }

    public static String recipe(JsonObject json, Ported pack) {
        if (json.has("type") && json.get("type").isJsonPrimitive()) {
            String type = json.get("type").getAsString();
            String mapped = switch (type) {
                case "forge:ore_shaped", "crafting_shaped" -> "minecraft:crafting_shaped";
                case "forge:ore_shapeless", "crafting_shapeless" -> "minecraft:crafting_shapeless";
                default -> type;
            };
            if (!mapped.equals(type)) {
                json.addProperty("type", mapped);
                pack.rewrote();
            }
        }
        if (json.has("key") && json.get("key").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("key").entrySet()) { ingredient(entry.getValue(), pack); }
        }
        if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
            for (JsonElement entry : json.getAsJsonArray("ingredients")) { ingredient(entry, pack); }
        }
        if (json.has("result") && json.get("result").isJsonObject()) {
            JsonObject result = json.getAsJsonObject("result");
            stack(result, pack);
        }
        return Ported.GSON.toJson(json);
    }

    private static void ingredient(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { ingredient(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        if (held.has("ore")) {
            held.addProperty("tag", Ids.oreDictTag(held.get("ore").getAsString()));
            held.remove("ore");
            pack.rewrote();
        }
        stack(held, pack);
    }

    private static void stack(JsonObject held, Ported pack) {
        if (!held.has("item") || !held.get("item").isJsonPrimitive()) { return; }
        String item = held.get("item").getAsString();
        int data = held.has("data") && held.get("data").isJsonPrimitive() && held.get("data").getAsJsonPrimitive().isNumber() ? held.get("data").getAsInt() : 0;
        if (data == 32767) { data = 0; }
        held.remove("data");
        held.addProperty("item", itemName(item + (data > 0 ? ":" + data : ""), pack));
    }

    public static String loot(JsonObject json, Ported pack) {
        lootWalk(json, pack);
        return Ported.GSON.toJson(json);
    }

    private static void lootWalk(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { lootWalk(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        for (String key : new String[] {"type", "function", "condition"}) {
            if (held.has(key) && held.get(key).isJsonPrimitive() && held.get(key).getAsString().indexOf(':') < 0) {
                held.addProperty(key, "minecraft:" + held.get(key).getAsString());
                pack.rewrote();
            }
        }
        if (held.has("name") && held.has("type") && "minecraft:item".equals(held.get("type").getAsString())) {
            int data = 0;
            if (held.has("functions") && held.get("functions").isJsonArray()) {
                JsonArray functions = held.getAsJsonArray("functions");
                for (int i = functions.size() - 1; i >= 0; i--) {
                    JsonElement function = functions.get(i);
                    if (!function.isJsonObject()) { continue; }
                    JsonObject held2 = function.getAsJsonObject();
                    String name = held2.has("function") ? held2.get("function").getAsString() : "";
                    if ("minecraft:set_data".equals(name) || "set_data".equals(name)) {
                        if (held2.has("data") && held2.get("data").isJsonPrimitive() && held2.get("data").getAsJsonPrimitive().isNumber()) { data = held2.get("data").getAsInt(); }
                        functions.remove(i);
                        pack.rewrote();
                    }
                }
            }
            held.addProperty("name", itemName(held.get("name").getAsString() + (data > 0 ? ":" + data : ""), pack));
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) {
            if (!"name".equals(entry.getKey())) { lootWalk(entry.getValue(), pack); }
        }
    }

    public static String advancement(JsonObject json, Ported pack) {
        if (json.has("display") && json.get("display").isJsonObject()) {
            JsonObject display = json.getAsJsonObject("display");
            if (display.has("icon") && display.get("icon").isJsonObject()) { stack(display.getAsJsonObject("icon"), pack); }
        }
        if (json.has("criteria") && json.get("criteria").isJsonObject()) { predicates(json.get("criteria"), pack); }
        return Ported.GSON.toJson(json);
    }

    private static void predicates(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { predicates(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        if (held.has("item") && held.get("item").isJsonPrimitive() && !held.has("items")) {
            stack(held, pack);
            JsonArray items = new JsonArray();
            items.add(held.get("item").getAsString());
            held.remove("item");
            held.add("items", items);
            pack.rewrote();
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) { predicates(entry.getValue(), pack); }
    }

    public static Map<String, JsonObject> oreDict(JsonObject json, Ported pack) {
        Map<String, JsonObject> tags = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String ore = entry.getKey();
            if (ore.startsWith("_") || ore.startsWith("-") || !entry.getValue().isJsonArray()) {
                if (ore.startsWith("-")) { pack.note("The ore dictionary removal '" + ore + "' has no twin here: a tag is rewritten whole with a data pack tag file carrying \"replace\": true"); }
                continue;
            }
            String tag = Ids.oreDictTag(ore);
            JsonArray values = new JsonArray();
            for (JsonElement item : entry.getValue().getAsJsonArray()) {
                if (!item.isJsonPrimitive()) { continue; }
                String named = item.getAsString();
                if (named.endsWith(":*")) { named = named.substring(0, named.length() - 2); }
                values.add(itemName(named, pack));
            }
            JsonObject file = new JsonObject();
            file.addProperty("replace", false);
            file.add("values", values);
            tags.put(tag, file);
            pack.rewrote();
        }
        return tags;
    }
}
