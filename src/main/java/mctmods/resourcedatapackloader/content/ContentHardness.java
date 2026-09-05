package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.HardnessDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentField;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeManager;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentHardness {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, HardnessDef> DEFS = new LinkedHashMap<>();
    private static final Map<Block, HardnessDef> WHOLE = new IdentityHashMap<>();
    private static final Map<BlockState, HardnessDef> EXACT = new IdentityHashMap<>();
    private static final Map<Block, HardnessDef> DENIED = new IdentityHashMap<>();
    private static final Map<BlockState, HardnessDef> DENIED_EXACT = new IdentityHashMap<>();
    private static final Map<Integer, long[]> MODEL_SEEDS = new ConcurrentHashMap<>();
    private static volatile long salt;
    private static volatile boolean anyRolls;
    private static boolean loaded;

    private ContentHardness() {}

    public static void setup() {
        if (load()) { resolve(); }
    }

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.hardness()) { return false; }
        Json.eachFile(PackManager.HARDNESS, "hardness file", (key, contents) -> {
            HardnessDef def = read(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("hardness", "Loaded " + DEFS.size() + " hardness group(s)"); }
        return !DEFS.isEmpty();
    }

    public static void resolve() {
        WHOLE.clear();
        EXACT.clear();
        DENIED.clear();
        DENIED_EXACT.clear();
        boolean rolls = false;
        for (HardnessDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            int found = 0;
            for (BlockMatchDef name : def.except()) { found += bind(def, name, DENIED, DENIED_EXACT, "except"); }
            for (BlockMatchDef name : def.blocks()) { found += bind(def, name, WHOLE, EXACT, "blocks"); }
            if (found == 0) {
                ContentLog.LOGGER.error("Hardness group {} names no registered block, so it does nothing", def.key());
                continue;
            }
            if (def.rolls()) { rolls = true; }
        }
        anyRolls = rolls;
    }

    private static int bind(HardnessDef def, BlockMatchDef name, Map<Block, HardnessDef> whole, Map<BlockState, HardnessDef> exact, String where) {
        Block block = Registered.find(ForgeRegistries.BLOCKS, name.block());
        if (block == null) {
            ContentLog.LOGGER.error("Hardness group {} names block {} under {}, which is not registered, leaving it out", def.key(), name.block(), where);
            return 0;
        }
        if (name.properties().isEmpty()) {
            whole.put(block, def);
            return 1;
        }
        List<BlockState> states = ContentStates.matching(block, name.properties(), def.key() + " " + where);
        for (BlockState state : states) { exact.put(state, def); }
        return states.isEmpty() ? 0 : 1;
    }

    public static boolean anyRolls() { return anyRolls; }

    static Map<Block, HardnessDef> whole() { return WHOLE; }

    static Map<BlockState, HardnessDef> exact() { return EXACT; }

    public static boolean idle() { return WHOLE.isEmpty() && EXACT.isEmpty(); }

    @Nullable public static HardnessDef groupFor(@Nullable BlockState state) {
        if (state == null || idle()) { return null; }
        if (!DENIED.isEmpty() && DENIED.containsKey(state.getBlock())) { return null; }
        if (!DENIED_EXACT.isEmpty() && DENIED_EXACT.containsKey(state)) { return null; }
        HardnessDef def = WHOLE.get(state.getBlock());
        if (def == null && !EXACT.isEmpty()) { def = EXACT.get(state); }
        return def;
    }

    public static int bucket(HardnessDef def, int x, int y, int z) {
        if (def.buckets() <= 1) { return 0; }
        if (y < def.minHeight() || y > def.maxHeight()) { return 0; }
        float strength = def.field().strength(salt, x, y, z);
        int bucket = Math.round(strength * (def.buckets() - 1));
        return Mth.clamp(bucket, 0, def.buckets() - 1);
    }

    public static float miningAt(@Nullable BlockState state, int x, int y, int z) {
        HardnessDef def = groupFor(state);
        if (def == null) { return 1.0F; }
        return Math.max(0.0001F, def.mining(bucket(def, x, y, z)));
    }

    public static float blastAt(@Nullable BlockState state, int x, int y, int z) {
        HardnessDef def = groupFor(state);
        if (def == null) { return 1.0F; }
        return Math.max(0.0F, def.blast(bucket(def, x, y, z)));
    }

    @Nullable public static Long modelSeed(BlockState state, BlockPos pos) {
        HardnessDef def = groupFor(state);
        if (def == null || def.buckets() <= 1) { return null; }
        return MODEL_SEEDS.computeIfAbsent(def.buckets(), ContentHardness::modelSeeds)[bucket(def, pos.getX(), pos.getY(), pos.getZ())];
    }

    private static long[] modelSeeds(int buckets) {
        long[] seeds = new long[buckets];
        boolean[] found = new boolean[buckets];
        Random random = new Random();
        int left = buckets;
        for (long seed = 0; left > 0; seed++) {
            random.setSeed(seed);
            int low = (int) random.nextLong();
            if (low == Integer.MIN_VALUE) { continue; }
            int bucket = Math.abs(low) % buckets;
            if (found[bucket]) { continue; }
            found[bucket] = true;
            seeds[bucket] = seed;
            left--;
        }
        return seeds;
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (idle() || event.getPosition().isEmpty()) { return; }
        BlockPos pos = event.getPosition().get();
        float multiplier = miningAt(event.getState(), pos.getX(), pos.getY(), pos.getZ());
        if (multiplier == 1.0F) { return; }
        event.setNewSpeed(event.getOriginalSpeed() / multiplier);
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof Level level)) { return; }
        salt = derive(((IBiomeManager) level.getBiomeManager()).rdpl$getBiomeZoomSeed());
        ContentLog.LOGGER.debug("Hardness salt {} from {} {}", salt, level.isClientSide() ? "client" : "server", level.dimension().location());
    }

    public static long derive(long seed) {
        long value = seed * -7046029254386353131L;
        value ^= value >>> 32;
        value *= -4658895280553007687L;
        value ^= value >>> 29;
        return value;
    }

    @Nullable private static HardnessDef read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
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
        int buckets = Mth.clamp(GsonHelper.getAsInt(json, "buckets", 10), 1, 256);
        int minHeight = GsonHelper.getAsInt(json, "minHeight", 0);
        int maxHeight = GsonHelper.getAsInt(json, "maxHeight", 255);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Hardness group {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        return new HardnessDef(key, blocks, matches(key, json, "except"),
                mining[0], mining[1], blast[0], blast[1], buckets,
                minHeight, maxHeight, Json.strings(json, "requires"), field(json));
    }

    private static ContentField field(JsonObject json) {
        if (!json.has("field")) { return new ContentField(ContentField.CHANCES, ContentField.SPREAD); }
        return fieldFrom(GsonHelper.getAsJsonObject(json, "field"));
    }

    public static ContentField fieldFrom(JsonObject entry) {
        if (!"seeded".equals(GsonHelper.getAsString(entry, "type", "speckle"))) { return new ContentField(chances(entry), GsonHelper.getAsFloat(entry, "spread", ContentField.SPREAD)); }
        return new ContentField(
                GsonHelper.getAsInt(entry, "cell", ContentField.CELL),
                GsonHelper.getAsInt(entry, "seeds", ContentField.SEEDS),
                GsonHelper.getAsFloat(entry, "reach", ContentField.REACH),
                GsonHelper.getAsInt(entry, "arms", ContentField.ARMS),
                GsonHelper.getAsFloat(entry, "armReach", ContentField.ARM_REACH));
    }

    private static int[] chances(JsonObject json) {
        if (!json.has("chances")) { return ContentField.CHANCES; }
        JsonArray held = GsonHelper.getAsJsonArray(json, "chances");
        int[] values = new int[held.size()];
        for (int index = 0; index < values.length; index++) { values[index] = Math.max(0, held.get(index).getAsInt()); }
        return values;
    }

    private static float[] range(JsonObject json, String name) {
        if (!json.has(name)) { return new float[] { 1.0F, 1.0F }; }
        JsonElement element = json.get(name);
        if (element.isJsonObject()) {
            JsonObject entry = element.getAsJsonObject();
            float least = GsonHelper.getAsFloat(entry, "min", 1.0F);
            float most = GsonHelper.getAsFloat(entry, "max", 1.0F);
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
            BlockMatchDef match = ContentParser.match(key, element);
            if (match != null) { values.add(match); }
            return values;
        }
        for (JsonElement held : element.getAsJsonArray()) {
            BlockMatchDef match = ContentParser.match(key, held);
            if (match != null) { values.add(match); }
        }
        return values;
    }
}
