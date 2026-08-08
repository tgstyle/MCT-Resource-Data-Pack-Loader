package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.HardnessDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentField;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentHardness {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, HardnessDef> DEFS = new LinkedHashMap<>();
    private static final Map<Block, HardnessDef> WHOLE = new IdentityHashMap<>();
    private static final Map<IBlockState, HardnessDef> EXACT = new IdentityHashMap<>();
    private static final Map<Block, HardnessDef> DENIED = new IdentityHashMap<>();
    private static final Map<IBlockState, HardnessDef> DENIED_EXACT = new IdentityHashMap<>();
    private static long salt;
    private static boolean loaded;
    private static boolean anyRolls;

    private ContentHardness() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.hardness) { return false; }

        PackManager.get().forEach(PackManager.HARDNESS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                HardnessDef def = read(key, contents);
                if (def != null) { DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in hardness file {}, ignoring it: {}", key, ex.getMessage()); }
        });

        if (!DEFS.isEmpty()) { Summary.info("hardness", "Loaded " + DEFS.size() + " hardness group(s)"); }
        return !DEFS.isEmpty();
    }

    public static void resolve() {
        WHOLE.clear();
        EXACT.clear();
        DENIED.clear();
        DENIED_EXACT.clear();
        anyRolls = false;

        for (HardnessDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }

            int found = 0;
            for (BlockMatchDef name : def.except) { found += bind(def, name, DENIED, DENIED_EXACT, "except"); }
            for (BlockMatchDef name : def.blocks) { found += bind(def, name, WHOLE, EXACT, "blocks"); }
            if (found == 0) {
                ContentLog.LOGGER.error("Hardness group {} names no registered block, so it does nothing", def.registryName);
                continue;
            }
            if (def.rolls()) { anyRolls = true; }
        }
    }

    private static int bind(HardnessDef def, BlockMatchDef name, Map<Block, HardnessDef> whole, Map<IBlockState, HardnessDef> exact, String where) {
        Block block = ContentStates.block(name.block.toString(), def.registryName + " " + where);
        if (block == null) { return 0; }

        if (!name.properties.isEmpty()) { exact.put(ContentStates.of(block, 0, name.properties, def.registryName), def); }
        else if (name.meta >= 0) { exact.put(ContentStates.of(block, name.meta), def); }
        else { whole.put(block, def); }
        return 1;
    }

    public static boolean anyRolls() { return anyRolls; }

    static Map<Block, HardnessDef> whole() { return WHOLE; }

    static Map<IBlockState, HardnessDef> exact() { return EXACT; }

    public static boolean wanted() { return !WHOLE.isEmpty() || !EXACT.isEmpty(); }

    @Nullable public static HardnessDef groupFor(@Nullable IBlockState state) {
        if (state == null || WHOLE.isEmpty() && EXACT.isEmpty()) { return null; }
        if (!DENIED.isEmpty() && DENIED.containsKey(state.getBlock())) { return null; }
        if (!DENIED_EXACT.isEmpty() && DENIED_EXACT.containsKey(state)) { return null; }

        HardnessDef def = WHOLE.get(state.getBlock());
        if (def == null && !EXACT.isEmpty()) { def = EXACT.get(state); }
        return def;
    }

    public static int bucket(HardnessDef def, int x, int y, int z) {
        if (def.buckets <= 1) { return 0; }
        if (y < def.minHeight || y > def.maxHeight) { return 0; }

        float strength = def.field.strength(salt, x, y, z);
        int bucket = Math.round(strength * (def.buckets - 1));
        return Math.max(0, Math.min(def.buckets - 1, bucket));
    }

    public static float miningAt(@Nullable IBlockState state, int x, int y, int z) {
        HardnessDef def = groupFor(state);
        if (def == null) { return 1.0F; }

        return Math.max(0.0001F, def.mining(bucket(def, x, y, z)));
    }

    public static float blastAt(@Nullable IBlockState state, int x, int y, int z) {
        HardnessDef def = groupFor(state);
        if (def == null) { return 1.0F; }

        return Math.max(0.0F, def.blast(bucket(def, x, y, z)));
    }

    public static long modelSeed(@Nullable IBlockState state, Vec3i at, long fallback) {
        HardnessDef def = groupFor(state);
        if (def == null || def.buckets <= 1) { return fallback; }

        return ((long) bucket(def, at.getX(), at.getY(), at.getZ())) << 16;
    }

    public static void salt(long value) { salt = value; }

    @SubscribeEvent public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (WHOLE.isEmpty() && EXACT.isEmpty()) { return; }

        BlockPos pos = event.getPos();
        if (pos == null) { return; }

        float multiplier = miningAt(event.getState(), pos.getX(), pos.getY(), pos.getZ());
        if (multiplier == 1.0F) { return; }

        event.setNewSpeed(event.getOriginalSpeed() / multiplier);
    }

    @SubscribeEvent public static void onLogin(PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }

        RDPLNetwork.sendHardnessSalt((EntityPlayerMP) event.player, salt);
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || world.provider.getDimension() != 0) { return; }

        salt(derive(world.getSeed()));
    }

    public static long derive(long seed) {
        long value = seed * -7046029254386353131L;
        value ^= value >>> 32;
        value *= -4658895280553007687L;
        value ^= value >>> 29;
        return value;
    }

    @Nullable private static HardnessDef read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Hardness group {} is empty, ignoring it", key);
            return null;
        }

        List<BlockMatchDef> blocks = matches(key, json, "blocks");
        if (blocks.isEmpty()) {
            ContentLog.LOGGER.error("Hardness group {} names no blocks, ignoring it", key);
            return null;
        }

        float[] mining = range(json, "miningTime");
        float[] blast = range(json, "blastResistance");
        int buckets = Math.max(1, Math.min(256, JsonUtils.getInt(json, "buckets", 10)));

        int minHeight = JsonUtils.getInt(json, "minHeight", 0);
        int maxHeight = JsonUtils.getInt(json, "maxHeight", 255);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Hardness group {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }

        return new HardnessDef(key, blocks, matches(key, json, "except"),
                mining[0], mining[1], blast[0], blast[1], buckets,
                minHeight, maxHeight, strings(json), field(json));
    }

    private static ContentField field(JsonObject json) {
        if (!json.has("field")) { return new ContentField(ContentField.CHANCES, ContentField.SPREAD); }

        return fieldFrom(JsonUtils.getJsonObject(json, "field"));
    }

    public static ContentField fieldFrom(JsonObject entry) {
        if (!"seeded".equals(JsonUtils.getString(entry, "type", "speckle"))) { return new ContentField(chances(entry), JsonUtils.getFloat(entry, "spread", ContentField.SPREAD)); }

        return new ContentField(
                JsonUtils.getInt(entry, "cell", ContentField.CELL),
                JsonUtils.getInt(entry, "seeds", ContentField.SEEDS),
                JsonUtils.getFloat(entry, "reach", ContentField.REACH),
                JsonUtils.getInt(entry, "arms", ContentField.ARMS),
                JsonUtils.getFloat(entry, "armReach", ContentField.ARM_REACH));
    }

    private static int[] chances(JsonObject json) {
        if (!json.has("chances")) { return ContentField.CHANCES; }

        JsonArray held = JsonUtils.getJsonArray(json, "chances");
        int[] values = new int[held.size()];
        for (int index = 0; index < values.length; index++) { values[index] = Math.max(0, held.get(index).getAsInt()); }
        return values;
    }

    private static float[] range(JsonObject json, String name) {
        if (!json.has(name)) { return new float[] { 1.0F, 1.0F }; }

        JsonElement element = json.get(name);
        if (element.isJsonObject()) {
            JsonObject entry = element.getAsJsonObject();
            float least = JsonUtils.getFloat(entry, "min", 1.0F);
            float most = JsonUtils.getFloat(entry, "max", 1.0F);
            return new float[] { Math.min(least, most), Math.max(least, most) };
        }
        float value = element.getAsFloat();
        return new float[] { value, value };
    }

    private static List<BlockMatchDef> matches(ResourceLocation key, JsonObject json, String name) {
        List<BlockMatchDef> values = new ArrayList<>();
        if (!json.has(name)) { return values; }

        JsonElement element = json.get(name);
        if (!element.isJsonArray()) {
            values.add(match(key, element));
            return values;
        }
        for (JsonElement held : element.getAsJsonArray()) { values.add(match(key, held)); }
        return values;
    }

    private static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject entry = element.getAsJsonObject();
            Map<String, String> properties = new LinkedHashMap<>();
            if (entry.has("properties")) {
                JsonObject held = JsonUtils.getJsonObject(entry, "properties");
                for (Map.Entry<String, JsonElement> pair : held.entrySet()) { properties.put(pair.getKey(), pair.getValue().getAsString()); }
            }
            return new BlockMatchDef(new ResourceLocation(JsonUtils.getString(entry, "block", "minecraft:stone")), JsonUtils.getInt(entry, "meta", -1), properties);
        }

        String name = element.getAsString();
        String[] parts = name.split(":");
        if (parts.length < 3) { return new BlockMatchDef(new ResourceLocation(name), -1, new LinkedHashMap<>()); }

        ResourceLocation block = new ResourceLocation(parts[0] + ":" + parts[1]);
        try { return new BlockMatchDef(block, Integer.parseInt(parts[2]), new LinkedHashMap<>()); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Block metadata '{}' in {} is not a number, using 0", parts[2], key);
            return new BlockMatchDef(block, 0, new LinkedHashMap<>());
        }
    }

    private static List<String> strings(JsonObject json) {
        List<String> values = new ArrayList<>();
        if (!json.has("requires")) { return values; }

        for (JsonElement element : JsonUtils.getJsonArray(json, "requires")) { values.add(element.getAsString()); }
        return values;
    }
}
