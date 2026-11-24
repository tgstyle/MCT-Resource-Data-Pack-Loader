package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDamageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneListDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class ZoneRules {
    public static final String DEFAULT_MESSAGE = "rdpl.zone.denied";
    private static final List<String> ZONE_KEYS = Collections.unmodifiableList(Arrays.asList("id", "where", "except", "break", "place", "use", "open", "fly", "message", "denyToolActions", "damage"));
    private static final List<String> WHERE_KEYS = Collections.unmodifiableList(Arrays.asList("dimension", "box", "claim"));
    private static final List<String> EXCEPT_KEYS = Collections.unmodifiableList(Arrays.asList("creative", "tag"));
    private static final List<String> DAMAGE_KEYS = Collections.unmodifiableList(Arrays.asList("entities", "unlessAttackerInside", "allowNoAttacker"));
    private static final List<String> RULE_KEYS = Collections.unmodifiableList(Arrays.asList("allow", "deny"));
    private static final List<String> PLACE_KEYS = Collections.unmodifiableList(Arrays.asList("allow", "deny", "on"));
    private static final List<String> OPEN_KEYS = Collections.unmodifiableList(Arrays.asList("allow", "deny", "close"));
    private static final String ALL = "all";
    private static final String DAMAGE = "damage";
    private static final int BOX_VALUES = 6;
    private static final List<ZoneDef> ZONES = new ArrayList<>();
    private static final Map<Integer, List<ZoneDef>> BY_DIMENSION = new HashMap<>();
    private static final String TOOLS = "denyToolActions";
    private static boolean anyDamage;
    private static boolean anyCloses;

    private ZoneRules() {}

    public static void load() {
        ZONES.clear();
        BY_DIMENSION.clear();
        Json.eachObject(PackManager.ZONES, "zone file", ZoneRules::read);
        anyDamage = ZONES.stream().anyMatch(zone -> zone.damage != null);
        anyCloses = ZONES.stream().anyMatch(zone -> zone.opens != null && zone.opens.close);
        if (ZONES.isEmpty()) { return; }
        Summary.info("zones", "Loaded " + ZONES.size() + " zone(s)");
        if (ZONES.stream().anyMatch(zone -> zone.opens != null)) { ContentLog.LOGGER.info("Zone open rules on 1.12.2 name a container by the block of the container clicked, since the game has no container names"); }
    }

    public static boolean none(World world) { return ZONES.isEmpty() || world.isRemote; }

    public static boolean noDamage() { return !anyDamage; }

    public static boolean noCloses() { return !anyCloses; }

    @Nullable public static ZoneDef covering(EntityPlayer player, BlockPos pos, Predicate<ZoneDef> wanted) { return covering(player.world, player, pos, wanted); }

    @Nullable public static ZoneDef covering(World world, @Nullable EntityPlayer player, BlockPos pos, Predicate<ZoneDef> wanted) {
        List<ZoneDef> here = here(world);
        if (here.isEmpty()) { return null; }
        for (ZoneDef zone : here) {
            if (wanted.test(zone) && inside(zone, world, pos) && !excepts(zone, player)) { return zone; }
        }
        return null;
    }

    @Nullable public static ZoneDef refusingDamage(EntityLivingBase victim, @Nullable Entity attacker, @Nullable EntityPlayer by) {
        for (ZoneDef zone : here(victim.world)) {
            ZoneDamageDef damage = zone.damage;
            if (damage == null || !damage.covers(victim) || !inside(zone, victim.world, victim.posX, victim.posY, victim.posZ) || excepts(zone, by)) { continue; }
            if (attacker == null ? !damage.allowNoAttacker : !(damage.unlessAttackerInside && inside(zone, victim.world, attacker.posX, attacker.posY, attacker.posZ))) { return zone; }
        }
        return null;
    }

    private static List<ZoneDef> inDimension(int dimension) {
        List<ZoneDef> found = new ArrayList<>();
        for (ZoneDef zone : ZONES) {
            if (zone.dimension.isEmpty() || CardPlace.inAny(Collections.singletonList(zone.dimension), dimension)) { found.add(zone); }
        }
        return found;
    }

    private static boolean inside(ZoneDef zone, World world, BlockPos pos) { return inBox(zone, pos.getX(), pos.getY(), pos.getZ()) && (!zone.claim || ClaimGuard.covers(world, pos) != 0); }

    private static boolean inside(ZoneDef zone, World world, double x, double y, double z) { return inside(zone, world, new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))); }

    private static List<ZoneDef> here(World world) { return BY_DIMENSION.computeIfAbsent(world.provider.getDimension(), ZoneRules::inDimension); }

    private static boolean inBox(ZoneDef zone, int x, int y, int z) {
        int[] box = zone.box;
        return box == null || (x >= box[0] && x <= box[3] && y >= box[1] && y <= box[4] && z >= box[2] && z <= box[5]);
    }

    private static boolean excepts(ZoneDef zone, @Nullable EntityPlayer player) { return player != null && ((zone.exceptCreative && player.isCreative()) || (!zone.exceptTag.isEmpty() && player.getTags().contains(zone.exceptTag))); }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", ZONE_KEYS);
        String id = Json.text(json, "id", key.toString());
        JsonObject where = Json.object(json, "where");
        Json.unknownKeys(where, "where", WHERE_KEYS);
        int[] box = box(id, where);
        if (box != null && box.length == 0) { return; }
        JsonObject except = Json.object(json, "except");
        Json.unknownKeys(except, "except", EXCEPT_KEYS);
        ZoneListDef breaks = list(key, id, json, "break");
        ZoneListDef places = list(key, id, json, "place");
        ZoneListDef uses = list(key, id, json, "use");
        ZoneListDef opens = list(key, id, json, "open");
        boolean noFly = json.has("fly") && !Json.getBoolean(json, "fly", true);
        EnumSet<ZoneTool> denyTools = readTools(key, json);
        ZoneDamageDef damage = readDamage(key, json);
        if (breaks == null && places == null && uses == null && opens == null && damage == null && denyTools.isEmpty() && !noFly) {
            ContentLog.LOGGER.error("Zone file {} has no break, place, use, open, damage or denyToolActions rule and does not stop flight, ignoring it", key);
            return;
        }
        ZONES.add(new ZoneDef(id, Json.text(where, "dimension", ""), box, Json.flag(where, "claim"), Json.flag(except, "creative"), Json.text(except, "tag", ""), breaks, places, uses, opens, noFly, Json.text(json, "message", DEFAULT_MESSAGE), denyTools, damage));
    }

    private static EnumSet<ZoneTool> readTools(ResourceLocation key, JsonObject json) {
        EnumSet<ZoneTool> tools = EnumSet.noneOf(ZoneTool.class);
        if (!json.has(TOOLS)) { return tools; }
        JsonElement value = json.get(TOOLS);
        boolean text = Json.isString(value);
        if (!text && !value.isJsonArray()) {
            ContentLog.LOGGER.error("Zone file {} gives denyToolActions that is not a list of hoe_till, axe_strip or shovel_flatten, ignoring it", key);
            return tools;
        }
        JsonArray entries = new JsonArray();
        if (text) { entries.add(value); }
        else { entries = value.getAsJsonArray(); }
        for (JsonElement entry : entries) {
            if (!Json.isString(entry)) {
                ContentLog.LOGGER.error("Zone file {} gives denyToolActions an entry {} that is not text, ignoring it", key, entry);
                continue;
            }
            ZoneTool tool = ZoneTool.of(entry.getAsString().trim());
            if (tool == null) { ContentLog.LOGGER.error("Zone file {} gives denyToolActions the entry '{}', which is not hoe_till, axe_strip or shovel_flatten, ignoring it", key, entry.getAsString()); }
            else if (tool == ZoneTool.AXE_STRIP) { ContentLog.LOGGER.error("Zone file {} denies axe_strip, which 1.12.2 cannot do since logs cannot be stripped; ignoring that entry", key); }
            else { tools.add(tool); }
        }
        return tools;
    }

    @Nullable private static ZoneDamageDef readDamage(ResourceLocation key, JsonObject json) {
        JsonElement held = json.get(DAMAGE);
        if (held == null) { return null; }
        if (!held.isJsonObject()) {
            ContentLog.LOGGER.error("Zone file {} gives damage that is not an object, ignoring it", key);
            return null;
        }
        JsonObject damage = held.getAsJsonObject();
        Json.unknownKeys(damage, "damage", DAMAGE_KEYS);
        List<String> entities = MobStats.required("Zone file " + key, damage, "entities", "damage entities");
        if (entities == null) {
            ContentLog.LOGGER.error("Zone file {} has a damage rule with no entities, ignoring it", key);
            return null;
        }
        return new ZoneDamageDef(entities, bool(key, damage, DAMAGE, "unlessAttackerInside"), bool(key, damage, DAMAGE, "allowNoAttacker"));
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

    @Nullable private static ZoneListDef list(ResourceLocation key, String id, JsonObject json, String part) {
        if (!json.has(part) || !json.get(part).isJsonObject()) { return null; }
        JsonObject rule = json.getAsJsonObject(part);
        Json.unknownKeys(rule, part, "place".equals(part) ? PLACE_KEYS : "open".equals(part) ? OPEN_KEYS : RULE_KEYS);
        String label = "Zone " + id + " " + part;
        boolean denyAll = rule.has("deny") && rule.get("deny").isJsonPrimitive() && ALL.equalsIgnoreCase(rule.get("deny").getAsString().trim());
        ItemSelection allow = rule.has("allow") ? selection(key, label + " allow", entries(rule, "allow")) : null;
        ItemSelection deny = rule.has("deny") && !denyAll ? selection(key, label + " deny", entries(rule, "deny")) : null;
        ItemSelection on = "place".equals(part) && rule.has("on") ? selection(key, label + " on", entries(rule, "on")) : null;
        ZoneListDef list = new ZoneListDef(allow, deny, denyAll, on, "open".equals(part) && bool(key, rule, part, "close"));
        if (list.empty()) {
            ContentLog.LOGGER.error("Zone {} has a {} rule with no allow, deny or on, ignoring it", id, part);
            return null;
        }
        return list;
    }

    private static List<String> entries(JsonObject rule, String member) {
        JsonElement value = rule.get(member);
        return value != null && value.isJsonPrimitive() ? Collections.singletonList(value.getAsString().trim()) : Json.names(rule, member);
    }

    private static ItemSelection selection(ResourceLocation key, String label, List<String> entries) {
        JsonArray names = new JsonArray();
        JsonArray ores = new JsonArray();
        for (String entry : entries) {
            if (entry.startsWith("#")) { ores.add(new JsonPrimitive(entry.substring(1))); }
            else { names.add(new JsonPrimitive(entry)); }
        }
        JsonObject built = new JsonObject();
        built.add("names", names);
        built.add("oreDict", ores);
        ItemSelection selection = new ItemSelection(label, "matches");
        selection.read(built, key);
        selection.resolve();
        return selection;
    }
}
