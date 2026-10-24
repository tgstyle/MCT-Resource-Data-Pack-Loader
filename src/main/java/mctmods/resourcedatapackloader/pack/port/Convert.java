package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Snbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.GsonHelper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

final class Convert {
    private static final Set<String> BLOCK_KEYS = Set.of("block", "replace", "adjacent", "soil", "log", "leaves", "topBlock", "fillerBlock", "stoneBlock", "edge", "ground", "crops", "platformBlock", "portalBlocks", "outline", "fill", "surface", "coverReplace", "floorCover", "ceilingCover", "jobSite", "modelBlock", "leafSapling", "crop", "rich", "poor", "middle", "budding", "crystal", "bell", "deepStone", "skyStone", "voidPlatformBlock", "except", "blocks", "flatBedrockFiller", "hoeTillsInto", "shovelPathBecomes", "shovelPathReverts", "sewerBlock");
    private static final Set<String> ITEM_KEYS = Set.of("item", "items", "tools", "container", "seed", "produce", "icon", "repairItem", "hold", "consume", "craft", "killedDrops", "drop", "output", "input", "result", "ingredient", "opensWith", "containerItem", "mainhand", "offhand", "head", "chest", "legs", "feet", "immunityItem", "immuneItems", "ignitedBy", "saysIcon", "outputs", "with");
    private static final Set<String> ENTITY_KEYS = Set.of("entity", "entities", "targets", "killed", "variant", "customEntitiesToHeal", "villagerEntity", "becomes", "picksFrom", "mob", "mobs");
    private static final Set<String> BIOME_KEYS = Set.of("biomes", "biome", "replaces", "baseBiome", "biomeNames", "biomeList", "roles", "biomeWhitelist");
    private static final Set<String> DIMENSION_KEYS = Set.of("dimensions", "dimension", "returnDimension", "respawnDimension", "blockOreDimensions", "blockGeneratorDimensions", "blockBiomeDimensions", "flatBedrockDimensions", "voidWorldDimensions", "blockReplacementDimensions", "pregenDimensions", "pregenDimensionsWhenEntered", "rubicWorldDimensions");
    private static final Set<String> DIMENSION_PREFIXED = Set.of("welcomeSays", "worldDifficulty", "cloudHeight", "weatherCeiling", "worldGravity", "worldFallDamage", "worldJumpStrength", "worldTerminalVelocity", "worldBelow", "worldAbove", "flatBedrockFillers", "structureBiomes", "structureBiomesAreBlacklist");
    private static final Set<String> STATE_KEYS = Set.of("replace", "adjacent", "except", "blocks");
    private static final Set<String> COST_PARTS = Set.of("xp", "item", "health", "hunger");
    private static final Set<String> RAW_CONTAINERS = Set.of("palette", "legend", "rows", "map", "pattern", "key", "notes", "attributes", "pathPriorities", "gameRules", "decoration", "spawnRates", "settings");
    private static final Pattern DIM_PREFIX = Pattern.compile("^(-?\\d+)=(.*)$");
    private static final Pattern REPLACEMENT = Pattern.compile("^([^=]+)=([^=]+)$");
    private static final Pattern WEIGHT = Pattern.compile("\\d+");
    private static final List<String> FLUID_TEXTURES = List.of("still", "flow");
    private static final List<String> CONTAINER_TEXTURES = List.of("chestModel", "guiTexture");
    static final String WILDCARD = ":*";
    private static final String AT_BLOCK = "at=";
    private static final String UNDER_BLOCK = "under=";
    static final String GAME_LOOP = "gameLoopFunction";
    static final String FLAT = "flat";
    static final int FLAT_FLOOR = ContentWorldShape.VANILLA_MIN;
    private static final List<String> FLAT_TYPES = List.of(FLAT, "superflat");

    private Convert() {}

    enum Context { NONE, BLOCK, ITEM, ENTITY, BIOME, DIMENSION }

    static String definition(JsonObject json, String folder, String namespace, String file, Ported pack) {
        Map<String, String> renamed = pack.renamedIn(namespace, folder, file);
        if (!renamed.isEmpty() && GsonHelper.isObjectNode(json, "variants")) {
            JsonObject variants = json.getAsJsonObject("variants");
            JsonObject out = new JsonObject();
            for (Map.Entry<String, JsonElement> variant : variants.entrySet()) { out.add(renamed.getOrDefault(variant.getKey(), variant.getKey()), variant.getValue()); }
            json.add("variants", out);
            pack.rewrote();
        }
        if (PackManager.BLOCKS.equals(folder) && json.has("harvestLevel") && !json.has("harvestToolLevel")) { json.add("harvestToolLevel", json.remove("harvestLevel")); }
        if ((PackManager.BLOCKS.equals(folder) || PackManager.ITEMS.equals(folder) || PackManager.FLUIDS.equals(folder)) && GsonHelper.isValidPrimitive(json, "creativeTab")) {
            String tab = json.get("creativeTab").getAsString().trim();
            if (!tab.isEmpty() && tab.indexOf(':') < 0) {
                String vanilla = ContentFormats.vanillaTab(tab);
                json.addProperty("creativeTab", vanilla != null ? vanilla : pack.mainNamespace() + ":" + tab.toLowerCase(Locale.ROOT));
            }
        }
        if (PackManager.FLUIDS.equals(folder)) {
            for (String named : FLUID_TEXTURES) { renamed(json, named, Convert::texture, pack); }
        }
        if (PackManager.GAMERULES.equals(folder)) {
            for (Map.Entry<String, JsonElement> dimension : json.entrySet()) {
                if (dimension.getValue().isJsonObject() && dimension.getValue().getAsJsonObject().remove(GAME_LOOP) != null) { pack.rewrote(); }
            }
        }
        if (PackManager.GAMERULES.equals(folder) || PackManager.BLASTPLASTER.equals(folder)) { renameDimensionKeys(PackManager.GAMERULES.equals(folder) ? json : GsonHelper.isObjectNode(json, "dimensions") ? json.getAsJsonObject("dimensions") : new JsonObject(), pack); }
        if (PackManager.WORLDTEMPLATES.equals(folder)) {
            if (GsonHelper.isObjectNode(json, "structures")) { ConvertDefinitions.modernStructuresOff(json, pack); }
            if (GsonHelper.isObjectNode(json, "settings")) {
                ConvertDefinitions.templatePositions(json.getAsJsonObject("settings"), namespace + ":" + file, pack);
                ConvertDefinitions.flatLayers(json.getAsJsonObject("settings"), pack);
                ConvertDefinitions.villagePieces(json.getAsJsonObject("settings"), pack);
            }
        }
        if (PackManager.TEAMS.equals(folder)) { ConvertDefinitions.teamPositions(json, namespace + ":" + file, pack); }
        if (PackManager.SCORING.equals(folder) && GsonHelper.isObjectNode(json, "opens")) { ConvertDefinitions.movedPoint(json.getAsJsonObject("opens"), "lobby", pack.overworldShift(), pack, "Score file " + namespace + ":" + file); }
        if (PackManager.DIMENSIONS.equals(folder)) {
            ConvertDefinitions.groundLevel(json, namespace + ":" + file, pack);
            json.remove("id");
            json.remove("suffix");
        }
        if (PackManager.CAVEREGIONS.equals(folder)) {
            renamed(json, "ambientSound", Commands::sound, pack);
            renamed(json, "particle", Commands::particleId, pack);
        }
        if (PackManager.RAIDS.equals(folder)) { renamed(json, "sound", Commands::sound, pack); }
        if (PackManager.ANVILS.equals(folder)) { ConvertDefinitions.anvil(json, pack); }
        if (PackManager.PLAYER_LOOT.equals(folder) && GsonHelper.isValidPrimitive(json, "table")) { ConvertLoot.lootTable(json, "table", pack); }
        JsonArray furnaceRemovals = PackManager.FURNACE.equals(folder) && GsonHelper.isArrayNode(json, "remove") ? ConvertDefinitions.furnaceRemovals(json.remove("remove").getAsJsonArray(), pack) : null;
        JsonArray exposureBlocks = PackManager.EXPOSURES.equals(folder) && GsonHelper.isArrayNode(json, "blocks") ? ConvertDefinitions.exposureNames(json.remove("blocks").getAsJsonArray(), true, pack) : null;
        JsonArray exposureItems = PackManager.EXPOSURES.equals(folder) && GsonHelper.isArrayNode(json, "items") ? ConvertDefinitions.exposureNames(json.remove("items").getAsJsonArray(), false, pack) : null;
        JsonArray exposureDimensions = PackManager.EXPOSURES.equals(folder) && GsonHelper.isArrayNode(json, "dimensions") ? ConvertDefinitions.exposureDimensions(json.remove("dimensions").getAsJsonArray(), pack) : null;
        if (PackManager.FUELS.equals(folder)) { ConvertDefinitions.oreDictTags(json, "fuels", pack); }
        if (PackManager.ORDERS.equals(folder)) { ConvertDefinitions.orderTags(json, pack); }
        if (PackManager.ENTITIES.equals(folder) && GsonHelper.isObjectNode(json, "storage") && GsonHelper.isObjectNode(json.getAsJsonObject("storage"), "items")) { ConvertDefinitions.oreDictTags(json.getAsJsonObject("storage").getAsJsonObject("items"), "filter", pack); }
        if (PackManager.WORLDGEN.equals(folder) && json.has("block") && json.has("meta")) {
            json.addProperty("block", json.get("block").getAsString() + ":" + json.get("meta").getAsInt());
            json.remove("meta");
        }
        walk(json, "", Context.NONE, pack, folder);
        if (furnaceRemovals != null) { json.add("remove", furnaceRemovals); }
        if (exposureBlocks != null) { json.add("blocks", exposureBlocks); }
        if (exposureItems != null) { json.add("items", exposureItems); }
        if (exposureDimensions != null) { json.add("dimensions", exposureDimensions); }
        return Ported.GSON.toJson(json);
    }

    static void renamed(JsonObject json, String key, UnaryOperator<String> rename, Ported pack) {
        if (!GsonHelper.isValidPrimitive(json, key)) { return; }
        String held = json.get(key).getAsString();
        String mapped = rename.apply(held);
        if (mapped.equals(held)) { return; }
        json.addProperty(key, mapped);
        pack.rewrote();
    }

    private static String texture(String held) { return ConvertAssets.texturePath(held.trim()); }

    static boolean flat(JsonObject settings) { return GsonHelper.isValidPrimitive(settings, "worldType") && FLAT_TYPES.contains(Settings.lower(settings.get("worldType").getAsString())); }

    static int flatShift(JsonObject settings) {
        if (!flat(settings)) { return 0; }
        return GsonHelper.isNumberValue(settings, "worldMinHeight") ? settings.get("worldMinHeight").getAsInt() : FLAT_FLOOR;
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

    private static void renameDimensionKeys(JsonObject json, Ported pack) {
        List<String> keys = new ArrayList<>(json.keySet());
        for (String key : keys) {
            String mapped = pack.dimension(key);
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
                    JsonArray tags = GsonHelper.isArrayNode(held, "tags") ? held.getAsJsonArray("tags") : new JsonArray();
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
            Context context = "portway".equals(parentKey) && ("allow".equals(key) || "deny".equals(key)) ? Context.DIMENSION : "dimension".equals(key) && COST_PARTS.contains(parentKey) ? Context.NONE : contextOf(key, inherited);
            if (value.isJsonObject()) {
                if ("blocks".equals(key) && PackManager.WORLDGEN.equals(folder)) { continue; }
                if ("kill".equals(key) && "points".equals(parentKey)) {
                    entityKeys(value.getAsJsonObject(), pack);
                    continue;
                }
                if ("container".equals(key)) {
                    container(value.getAsJsonObject(), pack);
                    continue;
                }
                walk(value.getAsJsonObject(), key, RAW_CONTAINERS.contains(key) ? Context.NONE : context, pack, folder);
            }
            else if (value.isJsonArray()) { json.add(key, array(value.getAsJsonArray(), key, context, pack, folder)); }
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

    private static JsonArray array(JsonArray values, String key, Context context, Ported pack, String folder) {
        JsonArray out = new JsonArray();
        for (JsonElement element : values) {
            if (element.isJsonObject()) { walk(element.getAsJsonObject(), key, context, pack, folder); }
            if (element.isJsonArray()) {
                out.add(array(element.getAsJsonArray(), key, context, pack, folder));
                continue;
            }
            if (!element.isJsonPrimitive()) {
                out.add(element);
                continue;
            }
            if (context == Context.ITEM && element.getAsString().trim().endsWith(WILDCARD)) {
                for (String name : itemNames(element.getAsString(), pack)) { out.add(name); }
                continue;
            }
            JsonElement replaced = primitive(key, key, element.getAsJsonPrimitive(), context, pack);
            out.add(replaced == null ? element : replaced);
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

    @Nullable private static JsonElement primitive(String key, String parentKey, JsonPrimitive value, Context context, Ported pack) {
        if (context == Context.DIMENSION) {
            String named = value.isNumber() ? String.valueOf(value.getAsInt()) : value.getAsString().trim();
            String mapped = pack.dimension(named);
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
                String rest = "worldBelow".equals(key) || "worldAbove".equals(key) ? pack.dimension(m.group(2).trim()) : m.group(2);
                pack.rewrote();
                return new JsonPrimitive(pack.dimension(m.group(1)) + "=" + rest);
            }
            return null;
        }
        if ("villageBlocks".equals(key)) {
            String[] fields = text.split(",");
            int split = fields[0].indexOf('=');
            if (split <= 0) { return null; }
            StringBuilder out = new StringBuilder(stateName(fields[0].substring(0, split).trim(), pack)).append('=').append(stateName(fields[0].substring(split + 1).trim(), pack));
            for (int field = 1; field < fields.length; field++) {
                String said = fields[field].trim();
                if (said.startsWith(AT_BLOCK)) { out.append(',').append(AT_BLOCK).append(stateName(said.substring(AT_BLOCK.length()).trim(), pack)); }
                else if (said.startsWith(UNDER_BLOCK)) { out.append(',').append(UNDER_BLOCK).append(stateName(said.substring(UNDER_BLOCK.length()).trim(), pack)); }
                else { out.append(',').append(said); }
            }
            pack.rewrote();
            return new JsonPrimitive(out.toString());
        }
        if ("villagePathPierCargo".equals(key)) {
            int split = text.indexOf('=');
            String named = split <= 0 ? "" : text.substring(0, split).trim();
            if (named.isEmpty() || "empty".equals(named)) { return null; }
            String mapped = stateName(named, pack);
            if (mapped.equals(named)) { return null; }
            pack.rewrote();
            return new JsonPrimitive(mapped + text.substring(split));
        }
        if (context == Context.BLOCK && key.startsWith("village")) {
            String mapped = mix(text, pack, key.startsWith("villageRail") || key.startsWith("villageSubway") || key.startsWith("villageSewer"));
            if (mapped == null) { return null; }
            pack.rewrote();
            return new JsonPrimitive(mapped);
        }
        if ("threatItems".equals(key)) {
            int split = text.lastIndexOf('=');
            if (split <= 0) { return null; }
            String item = text.substring(0, split).trim();
            String mapped = itemName(item, pack);
            return mapped.equals(item) ? null : new JsonPrimitive(mapped + text.substring(split));
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
            String lowered = Settings.lower(text);
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
                String mapped = Commands.entityId(text, pack);
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

    private static void entityKeys(JsonObject json, Ported pack) {
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(json.entrySet())) {
            String named = entry.getKey().trim();
            if (named.indexOf(':') < 0) { continue; }
            String mapped = Commands.entityId(named, pack);
            if (mapped.equals(entry.getKey())) { continue; }
            json.remove(entry.getKey());
            json.add(mapped, entry.getValue());
            pack.rewrote();
        }
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
            String own = pack.block(name, meta);
            if (own == null || own.equals(trimmed)) { return null; }
            pack.rewrote();
            return new JsonPrimitive(own);
        }
        if (Ids.isModded(name)) { return null; }
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

    @Nullable private static String mix(String text, Ported pack, boolean states) {
        List<String> parts = new ArrayList<>();
        boolean changed = false;
        for (String part : Snbt.split(text, ',')) {
            String entry = part.trim();
            String weight = "";
            int gap = entry.lastIndexOf(' ');
            if (gap > 0 && WEIGHT.matcher(entry.substring(gap + 1).trim()).matches()) {
                weight = " " + entry.substring(gap + 1).trim();
                entry = entry.substring(0, gap).trim();
            }
            String mapped = states ? stateName(entry, pack) : blockName(entry, pack);
            changed |= !mapped.equals(entry);
            parts.add(mapped + weight);
        }
        return changed ? String.join(", ", parts) : null;
    }

    private static String stateName(String text, Ported pack) {
        JsonElement mapped = block(text, pack, false);
        if (mapped == null) { return text; }
        if (!mapped.isJsonObject()) { return mapped.getAsString(); }
        JsonObject state = mapped.getAsJsonObject();
        StringBuilder out = new StringBuilder(state.get("block").getAsString());
        if (GsonHelper.isObjectNode(state, "properties") && !state.getAsJsonObject("properties").isEmpty()) {
            List<String> pairs = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : state.getAsJsonObject("properties").entrySet()) { pairs.add(entry.getKey() + "=" + entry.getValue().getAsString()); }
            out.append('[').append(String.join(",", pairs)).append(']');
        }
        return out.toString();
    }

    static String blockName(String text, Ported pack) {
        JsonElement mapped = block(text, pack, false);
        if (mapped == null) { return text; }
        return mapped.isJsonObject() ? mapped.getAsJsonObject().get("block").getAsString() : mapped.getAsString();
    }

    static List<String> itemNames(String text, Ported pack) {
        String trimmed = text.trim();
        if (!trimmed.endsWith(WILDCARD)) { return List.of(itemName(text, pack)); }
        String name = trimmed.substring(0, trimmed.length() - WILDCARD.length());
        pack.rewrote();
        return pack.variants(name, false);
    }

    static String itemName(String text, Ported pack) {
        String trimmed = text.trim();
        if (trimmed.endsWith(WILDCARD)) {
            pack.rewrote();
            return itemName(trimmed.substring(0, trimmed.length() - WILDCARD.length()), pack);
        }
        if (trimmed.isEmpty() || trimmed.contains("=") || trimmed.indexOf(':') < 0) { return text; }
        String[] split = Ids.splitMeta(trimmed);
        String name = split == null ? trimmed : split[0];
        int meta = split == null ? 0 : Integer.parseInt(split[1]);
        if (pack.owns(name)) {
            String own = pack.item(name, meta);
            if (own == null) { return text; }
            if (!own.equals(trimmed)) { pack.rewrote(); }
            return own;
        }
        if (Ids.isModded(name)) { return text; }
        String mapped = Ids.item(name, meta);
        if (!mapped.equals(trimmed)) { pack.rewrote(); }
        return mapped;
    }

    private static void container(JsonObject held, Ported pack) {
        if (GsonHelper.isValidPrimitive(held, "lootTable")) { ConvertLoot.lootTable(held, "lootTable", pack); }
        for (String key : CONTAINER_TEXTURES) {
            if (!GsonHelper.isStringValue(held, key)) { continue; }
            renamed(held, key, Convert::texture, pack);
        }
    }
}
