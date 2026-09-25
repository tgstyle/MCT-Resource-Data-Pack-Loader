package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

final class Convert {
    static final String FLAT = "flat";
    private static final Set<String> BLOCK_KEYS = set("block", "replace", "adjacent", "soil", "log", "leaves", "topBlock", "fillerBlock", "stoneBlock", "edge", "ground", "crops", "platformBlock", "portalBlocks", "outline", "fill", "surface", "coverReplace", "floorCover", "ceilingCover", "jobSite", "modelBlock", "leafSapling", "crop", "rich", "poor", "middle", "budding", "crystal", "bell", "deepStone", "skyStone", "voidPlatformBlock", "except", "blocks", "flatBedrockFiller", "hoeTillsInto", "shovelPathBecomes", "shovelPathReverts", "sewerBlock");
    private static final Set<String> ITEM_KEYS = set("item", "items", "tools", "container", "seed", "produce", "icon", "repairItem", "hold", "consume", "craft", "killedDrops", "drop", "output", "input", "result", "ingredient", "opensWith", "containerItem", "mainhand", "offhand", "head", "chest", "legs", "feet", "immunityItem", "ignitedBy", "saysIcon", "outputs", "with");
    private static final Set<String> ENTITY_KEYS = set("entity", "entities", "targets", "killed", "variant", "customEntitiesToHeal", "villagerEntity", "becomes", "picksFrom");
    private static final Set<String> BIOME_KEYS = set("biomes", "biome", "replaces", "baseBiome", "biomeNames", "roles", "biomeWhitelist");
    private static final Set<String> DIMENSION_KEYS = set("dimensions", "dimension", "returnDimension", "respawnDimension", "blockOreDimensions", "blockBiomeDimensions", "flatBedrockDimensions", "voidWorldDimensions", "blockReplacementDimensions", "pregenDimensions", "pregenDimensionsWhenEntered", "rubicWorldDimensions");
    private static final Set<String> DIMENSION_PREFIXED = set("welcomeSays", "worldDifficulty", "cloudHeight", "weatherCeiling", "worldGravity", "worldFallDamage", "worldJumpStrength", "worldTerminalVelocity", "worldBelow", "worldAbove", "flatBedrockFillers", "structureBiomes", "structureBiomesAreBlacklist");
    private static final Set<String> RAW_CONTAINERS = set("palette", "legend", "rows", "map", "pattern", "key", "notes", "attributes", "pathPriorities", "gameRules", "decoration", "spawnRates", "settings");
    private static final Set<String> BEHAVIORS = set("animals", "bush", "path", "till");
    private static final Set<String> FLAT_TYPES = set(FLAT, "superflat");
    private static final Pattern DIM_PREFIX = Pattern.compile("^([a-z0-9_.-]+:[a-z0-9_./-]+)=(.*)$");
    private static final Pattern REPLACEMENT = Pattern.compile("^([^=]+)=([^=]+)$");
    private static final Pattern WEIGHT = Pattern.compile("\\d+");
    private static final Map<String, String> TABS = new HashMap<>();

    static {
        String[][] tabs = {{"building_blocks", "buildingBlocks"}, {"functional_blocks", "decorations"}, {"colored_blocks", "decorations"}, {"natural_blocks", "decorations"},
                {"redstone_blocks", "redstone"}, {"tools_and_utilities", "tools"}, {"ingredients", "materials"}, {"food_and_drinks", "food"}, {"combat", "combat"},
                {"spawn_eggs", "misc"}, {"op_blocks", "misc"}};
        for (String[] pair : tabs) { TABS.put(pair[0], pair[1]); }
    }

    private Convert() {}

    static Set<String> set(String... values) { return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values))); }

    private enum Context { NONE, BLOCK, ITEM, ENTITY, BIOME, DIMENSION }

    static final class Ref {
        final String id;
        final int meta;
        final boolean specific;
        final boolean vanilla;

        Ref(String id, int meta, boolean specific, boolean vanilla) {
            this.id = id;
            this.meta = meta;
            this.specific = specific;
            this.vanilla = vanilla;
        }

        String text() { return specific && meta > 0 ? id + ":" + meta : id; }
    }

    private static String stateName(String text, Map<String, String> properties) {
        int open = text.indexOf('[');
        if (open < 0 || !text.endsWith("]")) { return text; }
        for (String pair : text.substring(open + 1, text.length() - 1).split(",")) {
            int equals = pair.indexOf('=');
            if (equals > 0) { properties.put(pair.substring(0, equals).trim(), pair.substring(equals + 1).trim()); }
        }
        return text.substring(0, open);
    }

    private static boolean notAnId(String text) {
        int open = text.indexOf('[');
        String head = open < 0 ? text : text.substring(0, open);
        return head.isEmpty() || head.indexOf(':') <= 0 || head.indexOf('=') >= 0 || head.indexOf(' ') >= 0 || head.split(":").length != 2;
    }

    @Nullable static Ref block(String text, Map<String, String> given, Ported pack, String where) {
        String trimmed = text.trim();
        if (notAnId(trimmed)) { return null; }
        Map<String, String> properties = new LinkedHashMap<>(given);
        String name = Ids.namespaced(stateName(trimmed, properties));
        Ported.Own own = pack.ownBlock(name);
        if (own != null) {
            String id = "slab".equals(own.type) && "double".equals(properties.get("type")) ? own.id() + "_double" : own.id();
            return new Ref(id, own.meta, own.variants > 1 || own.meta > 0, false);
        }
        if (!Ids.vanilla(name)) { return new Ref(name, 0, false, false); }
        Ids.Legacy found = Ids.block(name, properties);
        if (found == null) {
            if (Ids.unknownBlock(name)) { pack.noTwin(name, where); }
            return new Ref(name, 0, Ids.variedBlock(name), true);
        }
        if (found.lossy) { pack.note(where + " names " + name + " with " + properties + ", a state 1.12.2 cannot hold, so it becomes " + found.id + ":" + found.meta); }
        return new Ref(found.id, found.meta, Ids.variedBlock(found.id), true);
    }

    static String blockString(String text, Ported pack, String where) {
        Ref ref = block(text, Collections.emptyMap(), pack, where);
        return ref == null ? text : ref.text();
    }

    @Nullable static Ref item(String text, Ported pack, String where) {
        String trimmed = text.trim();
        int components = trimmed.indexOf('[');
        if (components > 0) {
            pack.note(where + " gives " + trimmed + ", whose item components have no twin on 1.12.2, so only the item is kept");
            trimmed = trimmed.substring(0, components);
        }
        if (trimmed.endsWith(":*")) {
            Ref inner = item(trimmed.substring(0, trimmed.length() - 2), pack, where);
            return inner == null ? null : new Ref(inner.id + ":*", 0, false, inner.vanilla);
        }
        if (notAnId(trimmed)) { return null; }
        String name = Ids.namespaced(trimmed);
        Ported.Own own = pack.ownItem(name);
        if (own != null) { return new Ref(own.id(), own.meta, own.variants > 1, false); }
        if (!Ids.vanilla(name)) { return new Ref(name, 0, false, false); }
        String egg = Ids.spawnEgg(name);
        if (egg != null) {
            pack.note(where + " names " + name + ", which 1.12.2 holds as minecraft:spawn_egg with the entity " + Ids.entity(egg) + " in its nbt, so the plain spawn egg is kept");
            return new Ref("minecraft:spawn_egg", 0, false, true);
        }
        Ids.Legacy found = Ids.item(name);
        if (found == null) {
            if (!Ids.legacyItem(name)) { pack.noTwin(name, where); }
            return new Ref(name, 0, Ids.variedItem(name), true);
        }
        return new Ref(found.id, found.meta, found.meta > 0 || Ids.variedItem(found.id), true);
    }

    static String itemString(String text, Ported pack) { return itemString(text, pack, pack.mainNamespace() + " pack data"); }

    static String itemString(String text, Ported pack, String where) {
        Ref ref = item(text, pack, where);
        return ref == null ? text : ref.text();
    }

    static int flatShift(JsonObject settings) {
        if (!settings.has("worldType") || !settings.get("worldType").isJsonPrimitive() || !FLAT_TYPES.contains(settings.get("worldType").getAsString().trim().toLowerCase(Locale.ROOT))) { return 0; }
        int floor = settings.has("worldMinHeight") && settings.get("worldMinHeight").isJsonPrimitive() && settings.getAsJsonPrimitive("worldMinHeight").isNumber() ? settings.get("worldMinHeight").getAsInt() : Ported.MODERN_FLAT_FLOOR;
        return -floor;
    }

    static String shiftY(String written, int shift) {
        String trimmed = written.trim();
        if (shift == 0 || trimmed.isEmpty()) { return written; }
        try { return String.valueOf(Integer.parseInt(trimmed) + shift); }
        catch (NumberFormatException notWhole) {
            try { return new BigDecimal(trimmed).add(BigDecimal.valueOf(shift)).toPlainString(); }
            catch (NumberFormatException notNumber) { return written; }
        }
    }

    static String definition(JsonObject json, String folder, String namespace, String file, Ported pack) {
        String where = namespace + ":" + folder + "/" + file;
        switch (folder) {
            case "blocks":
            case "items":
            case "fluids":
                content(json, pack, where);
                break;
            case "gamerules":
                dimensionKeys(json, pack, where);
                break;
            case "blastplaster":
                if (json.has("dimensions") && json.get("dimensions").isJsonObject()) { dimensionKeys(json.getAsJsonObject("dimensions"), pack, where); }
                break;
            case "worldtemplates":
                ConvertDefinitions.template(json, pack, where);
                break;
            case "teams":
                ConvertDefinitions.team(json, pack, where);
                break;
            case "scoring":
                if (json.has("opens") && json.get("opens").isJsonObject()) { ConvertDefinitions.movedPoint(json.getAsJsonObject("opens"), "lobby", pack.overworldShift(), pack, where); }
                break;
            case "dimensions":
                ConvertDefinitions.dimension(json, namespace + ":" + file, pack);
                break;
            case "caveregions":
                renamed(json, "ambientSound", Ids.sound(text(json, "ambientSound")));
                particle(json);
                break;
            case "raids":
                renamed(json, "sound", Ids.sound(text(json, "sound")));
                break;
            case "anvils":
                ConvertDefinitions.anvil(json, pack, where);
                break;
            case "player_loot":
                if (json.has("table") && json.get("table").isJsonPrimitive()) { json.addProperty("table", Ids.lootTable(json.get("table").getAsString())); }
                break;
            case "fuels":
                ConvertDefinitions.fuels(json, pack, where);
                break;
            case "registry_remap":
                ConvertDefinitions.remap(json, pack, where);
                break;
            case "exposures":
                ConvertDefinitions.exposures(json, pack, where);
                break;
            case "villagers":
                if (!json.has("careers")) {
                    JsonArray careers = new JsonArray();
                    careers.add(namespace + "." + file);
                    json.add("careers", careers);
                    pack.note(where + " names no careers, which a 1.12.2 profession needs, so it has the one career '" + namespace + "." + file + "'");
                }
                if (json.has("jobSite")) {
                    json.remove("jobSite");
                    pack.note(where + " names a job site block, which 1.12.2 villagers do not have, so it is left out");
                }
                break;
            case "trades":
                ConvertDefinitions.trades(json, pack, where);
                break;
            default:
                break;
        }
        walk(json, "", Context.NONE, pack, folder, where);
        return Ported.GSON.toJson(json);
    }

    static String text(JsonObject json, String key) { return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : ""; }

    private static void renamed(JsonObject json, String key, String mapped) {
        if (json.has(key) && json.get(key).isJsonPrimitive() && !mapped.isEmpty()) { json.addProperty(key, mapped); }
    }

    private static void particle(JsonObject json) {
        String held = text(json, "particle");
        String mapped = held.isEmpty() ? null : Ids.particle(held);
        if (mapped != null) { json.addProperty("particle", mapped); }
    }

    private static void content(JsonObject json, Ported pack, String where) {
        behaviors(json, pack);
        if (json.has("creativeTab") && json.get("creativeTab").isJsonPrimitive()) { json.addProperty("creativeTab", tab(json.get("creativeTab").getAsString().trim(), pack)); }
        for (String key : new String[] {"still", "flow"}) {
            if (json.has(key) && json.get(key).isJsonPrimitive()) { json.addProperty(key, ConvertAssets.textureRef(json.get(key).getAsString())); }
        }
        if (!json.has("variants") || !json.get("variants").isJsonObject()) { return; }
        int meta = 0;
        for (Map.Entry<String, JsonElement> variant : json.getAsJsonObject("variants").entrySet()) {
            if (!variant.getValue().isJsonObject()) { continue; }
            JsonObject held = variant.getValue().getAsJsonObject();
            if (!held.has("meta")) { held.addProperty("meta", meta); }
            meta++;
            if (!held.has("tags") || !held.get("tags").isJsonArray()) { continue; }
            JsonArray ores = held.has("oreDict") && held.get("oreDict").isJsonArray() ? held.getAsJsonArray("oreDict") : new JsonArray();
            for (JsonElement tag : held.remove("tags").getAsJsonArray()) {
                String ore = Ids.oreName(tag.getAsString());
                if (ore == null) { pack.note(where + " tags the variant '" + variant.getKey() + "' with " + tag.getAsString() + ", which has no ore dictionary name, so it is left out"); }
                else { ores.add(ore); }
            }
            if (ores.size() > 0) { held.add("oreDict", ores); }
        }
    }

    private static void behaviors(JsonObject json, Ported pack) {
        if (!json.has("behavesAs") || !json.get("behavesAs").isJsonArray()) { return; }
        JsonArray kept = new JsonArray();
        for (JsonElement held : json.getAsJsonArray("behavesAs")) {
            if (!held.isJsonPrimitive()) { continue; }
            String name = held.getAsString().trim().toLowerCase(Locale.ROOT);
            if (BEHAVIORS.contains(name)) { kept.add(name); }
            else { pack.note("A block behaves as '" + name + "', which 1.12.2 does not have, so it is left out"); }
        }
        if (kept.size() > 0) { json.add("behavesAs", kept); }
        else { json.remove("behavesAs"); }
    }

    static String tab(String given, Ported pack) {
        int colon = given.indexOf(':');
        if (colon < 0) { return given; }
        String namespace = given.substring(0, colon);
        String path = given.substring(colon + 1);
        if (Ids.MINECRAFT.equals(namespace)) { return TABS.getOrDefault(path, "misc"); }
        return pack.ownsNamespace(namespace) ? path : given;
    }

    private static void dimensionKeys(JsonObject json, Ported pack, String where) {
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(json.entrySet())) {
            if (entry.getKey().indexOf(':') < 0) { continue; }
            Integer number = pack.dimensionNumber(entry.getKey());
            if (number == null) {
                pack.noTwin(entry.getKey(), where);
                continue;
            }
            json.remove(entry.getKey());
            json.add(String.valueOf(number), entry.getValue());
        }
    }

    private static void walk(JsonObject json, String parentKey, Context inherited, Ported pack, String folder, String where) {
        if ("variants".equals(parentKey)) {
            for (Map.Entry<String, JsonElement> variant : json.entrySet()) {
                if (variant.getValue().isJsonObject()) { walk(variant.getValue().getAsJsonObject(), variant.getKey(), Context.NONE, pack, folder, where); }
            }
            return;
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(json.entrySet())) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Context context = contextOf(key, inherited);
            if ("exposures".equals(folder) && ("blocks".equals(key) || "items".equals(key)) && parentKey.isEmpty()) { continue; }
            if (value.isJsonObject()) {
                if ("blocks".equals(key) && "worldgen".equals(folder)) { continue; }
                if ("kill".equals(key) && "points".equals(parentKey)) {
                    entityKeys(value.getAsJsonObject());
                    continue;
                }
                if ("container".equals(key)) {
                    container(value.getAsJsonObject());
                    continue;
                }
                walk(value.getAsJsonObject(), key, RAW_CONTAINERS.contains(key) ? Context.NONE : context, pack, folder, where);
            }
            else if (value.isJsonArray()) { json.add(key, array(value.getAsJsonArray(), key, context, pack, folder, where)); }
            else if (value.isJsonPrimitive()) {
                if ("block".equals(key) && context == Context.BLOCK && value.getAsJsonPrimitive().isString()) {
                    blockMember(json, pack, where);
                    continue;
                }
                if ("modelBlock".equals(key) && value.getAsJsonPrimitive().isString()) {
                    Ref ref = block(value.getAsString(), Collections.emptyMap(), pack, where);
                    if (ref != null) {
                        json.addProperty(key, ref.id);
                        if (ref.specific && !json.has("modelMeta")) { json.addProperty("modelMeta", ref.meta); }
                    }
                    continue;
                }
                JsonElement replaced = primitive(key, value.getAsJsonPrimitive(), context, pack, where);
                if (replaced != null) { json.add(key, replaced); }
            }
        }
    }

    private static void blockMember(JsonObject json, Ported pack, String where) {
        Map<String, String> properties = json.has("properties") && json.get("properties").isJsonObject() ? stringMap(json.getAsJsonObject("properties")) : Collections.emptyMap();
        Ref ref = block(json.get("block").getAsString(), properties, pack, where);
        if (ref == null) { return; }
        json.addProperty("block", ref.id);
        if (ref.specific && !json.has("meta")) { json.addProperty("meta", ref.meta); }
        if (ref.vanilla && !properties.isEmpty()) { json.remove("properties"); }
    }

    private static Map<String, String> stringMap(JsonObject json) {
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) { out.put(entry.getKey(), entry.getValue().getAsString()); }
        }
        return out;
    }

    private static JsonArray array(JsonArray values, String key, Context context, Ported pack, String folder, String where) {
        JsonArray out = new JsonArray();
        for (JsonElement element : values) {
            if (element.isJsonObject()) {
                walk(element.getAsJsonObject(), key, context, pack, folder, where);
                out.add(element);
            }
            else if (element.isJsonArray()) { out.add(array(element.getAsJsonArray(), key, context, pack, folder, where)); }
            else if (element.isJsonPrimitive()) {
                JsonElement replaced = primitive(key, element.getAsJsonPrimitive(), context, pack, where);
                out.add(replaced == null ? element : replaced);
            }
            else { out.add(element); }
        }
        return out;
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

    @Nullable private static JsonElement primitive(String key, JsonPrimitive value, Context context, Ported pack, String where) {
        if (context == Context.DIMENSION) {
            if (value.isNumber()) { return null; }
            String named = value.getAsString().trim();
            if (named.indexOf(':') < 0) { return null; }
            Integer number = pack.dimensionNumber(named);
            if (number == null) {
                pack.noTwin(named, where);
                return null;
            }
            return new JsonPrimitive(number);
        }
        if (!value.isString()) { return null; }
        String text = value.getAsString();
        if (DIMENSION_PREFIXED.contains(key)) {
            Matcher m = DIM_PREFIX.matcher(text.trim());
            if (!m.matches()) { return null; }
            Integer number = pack.dimensionNumber(m.group(1));
            if (number == null) { return null; }
            String rest = m.group(2);
            if ("worldBelow".equals(key) || "worldAbove".equals(key)) {
                Integer other = pack.dimensionNumber(rest.trim());
                if (other != null) { rest = String.valueOf(other); }
            }
            return new JsonPrimitive(number + "=" + rest);
        }
        if ("villageBlocks".equals(key)) { return new JsonPrimitive(villageBlocks(text, pack, where)); }
        if ("villagePathPierCargo".equals(key)) {
            int split = text.indexOf('=');
            if (split <= 0) { return null; }
            return new JsonPrimitive(blockString(text.substring(0, split).trim(), pack, where) + text.substring(split));
        }
        if (context == Context.BLOCK && key.startsWith("village")) { return new JsonPrimitive(mix(text, pack, where)); }
        if ("threatItems".equals(key)) {
            int split = text.lastIndexOf('=');
            if (split <= 0) { return null; }
            return new JsonPrimitive(itemString(text.substring(0, split).trim(), pack, where) + text.substring(split));
        }
        if ("blockReplacements".equals(key)) {
            Matcher m = REPLACEMENT.matcher(text.trim());
            if (!m.matches()) { return null; }
            return new JsonPrimitive(blockString(m.group(1).trim(), pack, where) + "=" + blockString(m.group(2).trim(), pack, where));
        }
        if ("biomeTypes".equals(key) || "types".equals(key)) { return new JsonPrimitive(text.trim().toUpperCase(Locale.ROOT)); }
        switch (context) {
            case BLOCK: {
                Ref ref = block(text, Collections.emptyMap(), pack, where);
                if (ref != null && "soil".equals(key) && ref.meta > 0) { pack.note(where + " names the soil " + text + ", and a 1.12.2 soil is a whole block, so it becomes " + ref.id); }
                return ref == null ? null : new JsonPrimitive("soil".equals(key) ? ref.id : ref.text());
            }
            case ITEM: {
                Ref ref = item(text, pack, where);
                return ref == null ? null : new JsonPrimitive(ref.text());
            }
            case ENTITY: {
                if (notAnId(text.trim()) || !Ids.vanilla(text.trim())) { return null; }
                return new JsonPrimitive(Ids.entity(text));
            }
            case BIOME: {
                if (notAnId(text.trim()) || !Ids.vanilla(text.trim())) { return null; }
                return new JsonPrimitive(Ids.biome(text));
            }
            default: return null;
        }
    }

    private static String villageBlocks(String text, Ported pack, String where) {
        String[] fields = text.split(",");
        int split = fields[0].indexOf('=');
        if (split <= 0) { return text; }
        StringBuilder out = new StringBuilder(blockString(fields[0].substring(0, split).trim(), pack, where)).append('=').append(blockString(fields[0].substring(split + 1).trim(), pack, where));
        for (int field = 1; field < fields.length; field++) {
            String said = fields[field].trim();
            if (said.startsWith("at=")) { out.append(",at=").append(blockString(said.substring("at=".length()).trim(), pack, where)); }
            else if (said.startsWith("under=")) { out.append(",under=").append(blockString(said.substring("under=".length()).trim(), pack, where)); }
            else { out.append(',').append(said); }
        }
        return out.toString();
    }

    private static String mix(String text, Ported pack, String where) {
        List<String> parts = new ArrayList<>();
        for (String part : text.split(",(?![^\\[]*])")) {
            String entry = part.trim();
            String weight = "";
            int gap = entry.lastIndexOf(' ');
            if (gap > 0 && WEIGHT.matcher(entry.substring(gap + 1).trim()).matches()) {
                weight = " " + entry.substring(gap + 1).trim();
                entry = entry.substring(0, gap).trim();
            }
            parts.add(blockString(entry, pack, where) + weight);
        }
        return String.join(", ", parts);
    }

    private static void entityKeys(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(json.entrySet())) {
            String named = entry.getKey().trim();
            if (named.indexOf(':') < 0 || !Ids.vanilla(named)) { continue; }
            String mapped = Ids.entity(named);
            if (mapped.equals(entry.getKey())) { continue; }
            json.remove(entry.getKey());
            json.add(mapped, entry.getValue());
        }
    }

    private static void container(JsonObject held) {
        if (held.has("lootTable") && held.get("lootTable").isJsonPrimitive()) { held.addProperty("lootTable", Ids.lootTable(held.get("lootTable").getAsString())); }
        for (String key : new String[] {"chestModel", "guiTexture"}) {
            if (held.has(key) && held.get(key).isJsonPrimitive() && held.getAsJsonPrimitive(key).isString()) { held.addProperty(key, ConvertAssets.textureRef(held.get(key).getAsString())); }
        }
    }

}
