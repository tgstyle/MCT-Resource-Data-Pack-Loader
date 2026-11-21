package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDamageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneListDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.BlockSelection;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public final class ZoneRules {
    public static final String DEFAULT_MESSAGE = "rdpl.zone.denied";
    private static final List<String> ZONE_KEYS = List.of("id", "where", "except", "break", "place", "use", "open", "fly", "message", "denyToolActions", "damage");
    private static final List<String> WHERE_KEYS = List.of("dimension", "box", "claim");
    private static final List<String> EXCEPT_KEYS = List.of("creative", "tag");
    private static final List<String> DAMAGE_KEYS = List.of("entities", "unlessAttackerInside", "allowNoAttacker");
    private static final List<String> RULE_KEYS = List.of("allow", "deny");
    private static final List<String> PLACE_KEYS = List.of("allow", "deny", "on");
    private static final List<String> OPEN_KEYS = List.of("allow", "deny", "close");
    private static final String ALL = "all";
    private static final int BOX_VALUES = 6;
    private static final String TOOLS = "denyToolActions";
    private static final List<ZoneDef> ZONES = new ArrayList<>();
    private static final Map<ResourceKey<Level>, List<ZoneDef>> BY_DIMENSION = new HashMap<>();

    private static boolean anyDamage;
    private static boolean anyCloses;

    private ZoneRules() {}

    public static void load() {
        ZONES.clear();
        BY_DIMENSION.clear();
        Json.eachObject(PackManager.ZONES, "zone file", ZoneRules::read);
        anyDamage = ZONES.stream().anyMatch(zone -> zone.damage() != null);
        anyCloses = ZONES.stream().anyMatch(zone -> zone.opens() != null && zone.opens().close);
        if (!ZONES.isEmpty()) { Summary.info("zones", "Loaded " + ZONES.size() + " zone(s)"); }
    }

    public static boolean none(LevelAccessor level) { return ZONES.isEmpty() || level.isClientSide(); }

    public static boolean noDamage() { return !anyDamage; }

    public static boolean noCloses() { return !anyCloses; }

    @Nullable public static ZoneDef covering(Player player, BlockPos pos, Predicate<ZoneDef> wanted) {
        for (ZoneDef zone : zonesIn(player.level())) {
            if (wanted.test(zone) && inside(zone, player.level(), pos) && !excepts(zone, player)) { return zone; }
        }
        return null;
    }

    @Nullable public static ZoneDef refusingDamage(LivingEntity victim, @Nullable Entity attacker, @Nullable Player by) {
        Level level = victim.level();
        for (ZoneDef zone : zonesIn(level)) {
            ZoneDamageDef damage = zone.damage();
            if (damage == null || !damage.covers(victim) || !inside(zone, level, victim.blockPosition()) || excepts(zone, by)) { continue; }
            if (attacker == null ? !damage.allowNoAttacker : !(damage.unlessAttackerInside && inside(zone, level, attacker.blockPosition()))) { return zone; }
        }
        return null;
    }

    private static List<ZoneDef> zonesIn(Level level) {
        List<ZoneDef> here = BY_DIMENSION.get(level.dimension());
        if (here == null) {
            here = inDimension(level);
            BY_DIMENSION.put(level.dimension(), here);
        }
        return here;
    }

    private static List<ZoneDef> inDimension(Level level) {
        List<ZoneDef> found = new ArrayList<>();
        for (ZoneDef zone : ZONES) {
            if (zone.dimension().isEmpty() || CardPlace.inAny(List.of(zone.dimension()), level)) { found.add(zone); }
        }
        return found;
    }

    private static boolean inside(ZoneDef zone, Level level, BlockPos pos) {
        int[] box = zone.box();
        if (box != null && (pos.getX() < box[0] || pos.getX() > box[3] || pos.getY() < box[1] || pos.getY() > box[4] || pos.getZ() < box[2] || pos.getZ() > box[5])) { return false; }
        return !zone.claim() || ClaimGuard.covers(level, pos) != 0;
    }

    private static boolean excepts(ZoneDef zone, @Nullable Player player) { return player != null && ((zone.exceptCreative() && player.isCreative()) || (!zone.exceptTag().isEmpty() && player.getTags().contains(zone.exceptTag()))); }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", ZONE_KEYS);
        String id = Json.text(json, "id", key.toString());
        JsonObject where = Json.object(json, "where");
        Json.unknownKeys(where, "where", WHERE_KEYS);
        int[] box = box(id, where);
        if (box != null && box.length == 0) { return; }
        JsonObject except = Json.object(json, "except");
        Json.unknownKeys(except, "except", EXCEPT_KEYS);
        ZoneListDef<BlockState> breaks = blocks(key, id, json, "break");
        ZoneListDef<BlockState> places = blocks(key, id, json, "place");
        ZoneListDef<ItemStack> uses = items(key, id, json);
        ZoneListDef<BlockState> opens = blocks(key, id, json, "open");
        boolean noFly = json.has("fly") && !Json.getBoolean(json, "fly", true);
        EnumSet<ZoneTool> denyTools = readTools(key, json);
        ZoneDamageDef damage = readDamage(key, json);
        if (breaks == null && places == null && uses == null && opens == null && damage == null && denyTools.isEmpty() && !noFly) {
            ContentLog.LOGGER.error("Zone file {} has no break, place, use, open, damage or denyToolActions rule and does not stop flight, ignoring it", key);
            return;
        }
        ZONES.add(new ZoneDef(id, CardPlace.dimensionId(Json.text(where, "dimension", "")), box, Json.flag(where, "claim"), Json.flag(except, "creative"), Json.text(except, "tag", ""), breaks, places, uses, opens, noFly, Json.text(json, "message", DEFAULT_MESSAGE), denyTools, damage));
    }

    private static EnumSet<ZoneTool> readTools(ResourceLocation key, JsonObject json) {
        EnumSet<ZoneTool> tools = EnumSet.noneOf(ZoneTool.class);
        if (!json.has(TOOLS)) { return tools; }
        JsonElement value = json.get(TOOLS);
        boolean text = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString();
        if (!text && !value.isJsonArray()) {
            ContentLog.LOGGER.error("Zone file {} gives denyToolActions that is not a list of hoe_till, axe_strip or shovel_flatten, ignoring it", key);
            return tools;
        }
        JsonArray entries = new JsonArray();
        if (text) { entries.add(value); }
        else { entries = value.getAsJsonArray(); }
        for (JsonElement entry : entries) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("Zone file {} gives denyToolActions an entry {} that is not text, ignoring it", key, entry);
                continue;
            }
            ZoneTool tool = ZoneTool.of(entry.getAsString().trim());
            if (tool == null) { ContentLog.LOGGER.error("Zone file {} gives denyToolActions the entry '{}', which is not hoe_till, axe_strip or shovel_flatten, ignoring it", key, entry.getAsString()); }
            else { tools.add(tool); }
        }
        return tools;
    }

    @Nullable private static ZoneDamageDef readDamage(ResourceLocation key, JsonObject json) {
        if (!json.has("damage")) { return null; }
        if (!json.get("damage").isJsonObject()) {
            ContentLog.LOGGER.error("Zone file {} gives damage that is not an object, ignoring it", key);
            return null;
        }
        JsonObject damage = json.getAsJsonObject("damage");
        Json.unknownKeys(damage, "damage", DAMAGE_KEYS);
        List<String> entities = damage.has("entities") ? MobStats.entities("Zone file " + key, "damage entities", damage.get("entities")) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("Zone file {} has a damage rule with no entities, ignoring it", key);
            return null;
        }
        return new ZoneDamageDef(entities, bool(key, damage, "damage", "unlessAttackerInside"), bool(key, damage, "damage", "allowNoAttacker"));
    }

    private static boolean bool(ResourceLocation key, JsonObject rule, String part, String member) {
        if (!rule.has(member)) { return false; }
        JsonElement value = rule.get(member);
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("Zone file {} gives {} {} the value {}, which is not true or false, using false", key, part, member, value);
        return false;
    }

    @Nullable private static int[] box(String id, JsonObject where) {
        if (!where.has("box")) { return null; }
        List<Integer> values = Json.ints(Json.getJsonArray(where, "box", new JsonArray()), "box");
        if (values.size() != BOX_VALUES) {
            ContentLog.LOGGER.error("Zone {} gives a box of {} numbers, it needs {} (x1, y1, z1, x2, y2, z2), ignoring the zone", id, values.size(), BOX_VALUES);
            return new int[0];
        }
        int[] box = new int[BOX_VALUES];
        for (int axis = 0; axis < 3; axis++) {
            box[axis] = Math.min(values.get(axis), values.get(axis + 3));
            box[axis + 3] = Math.max(values.get(axis), values.get(axis + 3));
        }
        return box;
    }

    @Nullable private static ZoneListDef<BlockState> blocks(ResourceLocation key, String id, JsonObject json, String part) {
        return list(key, id, json, part, (label, entries) -> onBlocks(key, label, entries));
    }

    @Nullable private static ZoneListDef<ItemStack> items(ResourceLocation key, String id, JsonObject json) {
        return list(key, id, json, "use", (label, entries) -> {
            ItemSelection selection = new ItemSelection(label, "matches");
            selection.readNames(entries, key);
            selection.unknown();
            return selection::matches;
        });
    }

    @Nullable private static <T> ZoneListDef<T> list(ResourceLocation key, String id, JsonObject json, String part, Listing<T> listing) {
        if (!json.has(part) || !json.get(part).isJsonObject()) { return null; }
        JsonObject rule = json.getAsJsonObject(part);
        Json.unknownKeys(rule, part, "place".equals(part) ? PLACE_KEYS : "open".equals(part) ? OPEN_KEYS : RULE_KEYS);
        String label = "Zone " + id + " " + part;
        boolean denyAll = rule.has("deny") && rule.get("deny").isJsonPrimitive() && ALL.equalsIgnoreCase(rule.get("deny").getAsString().trim());
        Predicate<T> allow = rule.has("allow") ? listing.of(label + " allow", entries(rule, "allow")) : null;
        Predicate<T> deny = rule.has("deny") && !denyAll ? listing.of(label + " deny", entries(rule, "deny")) : null;
        Predicate<BlockState> on = "place".equals(part) && rule.has("on") ? onBlocks(key, label + " on", entries(rule, "on")) : null;
        ZoneListDef<T> list = new ZoneListDef<>(allow, deny, denyAll, on, "open".equals(part) && bool(key, rule, part, "close"));
        if (list.empty()) {
            ContentLog.LOGGER.error("Zone {} has a {} rule with no allow, deny or on, ignoring it", id, part);
            return null;
        }
        return list;
    }

    private static Predicate<BlockState> onBlocks(ResourceLocation key, String label, List<String> entries) {
        BlockSelection selection = new BlockSelection(label, "matches", entries, key);
        return state -> selection.matches(state.getBlock());
    }

    private static List<String> entries(JsonObject rule, String member) {
        JsonElement value = rule.get(member);
        return value != null && value.isJsonPrimitive() ? List.of(value.getAsString().trim()) : Json.names(rule, member);
    }

    private interface Listing<T> {
        Predicate<T> of(String label, List<String> entries);
    }
}
