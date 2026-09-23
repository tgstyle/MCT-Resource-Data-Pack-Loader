package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.HardnessDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.gate.GateStorage;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkTokens;
import mctmods.resourcedatapackloader.content.worldgen.ContentField;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeManager;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkMap;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentHardness {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, HardnessDef> DEFS = new LinkedHashMap<>();
    private static final Map<Block, List<HardnessDef>> WHOLE = new IdentityHashMap<>();
    private static final Map<BlockState, List<HardnessDef>> EXACT = new IdentityHashMap<>();
    private static final Map<Block, HardnessDef> DENIED = new IdentityHashMap<>();
    private static final Map<BlockState, HardnessDef> DENIED_EXACT = new IdentityHashMap<>();
    private static final Map<HardnessDef, List<ItemStack>> TOOLS = new IdentityHashMap<>();
    private static final Map<HardnessDef, BlockState> BECOMES = new IdentityHashMap<>();
    private static final Map<ResourceKey<Level>, Deque<ChunkPos>> SWAPS = new HashMap<>();
    private static final int SWAP_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
    private static final int BREAK_EFFECT = 2001;
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
        TOOLS.clear();
        BECOMES.clear();
        boolean rolls = false;
        for (HardnessDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            int found = 0;
            for (BlockMatchDef name : def.except()) { found += deny(def, name); }
            for (BlockMatchDef name : def.blocks()) { found += bind(def, name); }
            if (found == 0) {
                ContentLog.LOGGER.error("Hardness group {} names no registered block, so it does nothing", def.key());
                continue;
            }
            if (def.rolls()) { rolls = true; }
            List<ItemStack> tools = new ArrayList<>();
            for (String name : def.tools()) {
                ItemStack stack = ContentStacks.parse(def.key(), name, 1);
                if (!stack.isEmpty()) { tools.add(stack); }
            }
            TOOLS.put(def, tools);
            if (!def.swaps()) { continue; }
            BlockState target = ContentStates.known(def.becomes(), def.key() + " becomes");
            if (target != null) { BECOMES.put(def, target); }
        }
        anyRolls = rolls;
    }

    @Nullable private static Block named(HardnessDef def, BlockMatchDef name, String where) {
        Block block = Registered.find(ForgeRegistries.BLOCKS, name.block());
        if (block == null) { ContentLog.LOGGER.error("Hardness group {} names block {} under {}, which is not registered, leaving it out", def.key(), name.block(), where); }
        return block;
    }

    private static int deny(HardnessDef def, BlockMatchDef name) {
        Block block = named(def, name, "except");
        if (block == null) { return 0; }
        if (name.properties().isEmpty()) {
            DENIED.put(block, def);
            return 1;
        }
        List<BlockState> states = ContentStates.matching(block, name.properties(), def.key() + " except");
        for (BlockState state : states) { DENIED_EXACT.put(state, def); }
        return states.isEmpty() ? 0 : 1;
    }

    private static int bind(HardnessDef def, BlockMatchDef name) {
        Block block = named(def, name, "blocks");
        if (block == null) { return 0; }
        if (name.properties().isEmpty()) {
            WHOLE.computeIfAbsent(block, held -> new ArrayList<>()).add(def);
            return 1;
        }
        List<BlockState> states = ContentStates.matching(block, name.properties(), def.key() + " blocks");
        for (BlockState state : states) { EXACT.computeIfAbsent(state, held -> new ArrayList<>()).add(def); }
        return states.isEmpty() ? 0 : 1;
    }

    public static boolean anyRolls() { return anyRolls; }

    static Map<Block, List<HardnessDef>> whole() { return WHOLE; }

    static Map<BlockState, List<HardnessDef>> exact() { return EXACT; }

    public static boolean idle() { return WHOLE.isEmpty() && EXACT.isEmpty(); }

    @Nullable public static HardnessDef groupFor(@Nullable BlockState state) { return groupFor(state, null); }

    @Nullable public static HardnessDef groupFor(@Nullable BlockState state, @Nullable LivingEntity who) {
        if (state == null || idle()) { return null; }
        if (!DENIED.isEmpty() && DENIED.containsKey(state.getBlock())) { return null; }
        if (!DENIED_EXACT.isEmpty() && DENIED_EXACT.containsKey(state)) { return null; }
        HardnessDef open = null;
        for (int side = 0; side < 2; side++) {
            List<HardnessDef> defs = side == 0 ? WHOLE.get(state.getBlock()) : EXACT.isEmpty() ? null : EXACT.get(state);
            if (defs == null) { continue; }
            for (HardnessDef def : defs) {
                if (def.advancement().isEmpty()) {
                    if (open == null) { open = def; }
                    continue;
                }
                if (who instanceof Player player && Advancements.has(player, def.advancement())) { return def; }
            }
        }
        return open;
    }

    public static int bucket(HardnessDef def, int x, int y, int z) {
        if (def.buckets() <= 1) { return 0; }
        if (y < def.minHeight() || y > def.maxHeight()) { return 0; }
        float strength = def.field().strength(salt, x, y, z);
        int bucket = Math.round(strength * (def.buckets() - 1));
        return Mth.clamp(bucket, 0, def.buckets() - 1);
    }

    public static boolean breaksAway(@Nullable BlockState state, @Nullable LivingEntity who) {
        HardnessDef def = groupFor(state, who);
        return def == null || !def.keeps();
    }

    public static boolean mayBreak(@Nullable LivingEntity who, @Nullable BlockState state, ItemStack held) {
        HardnessDef def = groupFor(state, who);
        if (def == null || !def.adventure() || who == null || held.isEmpty()) { return false; }
        List<ItemStack> tools = TOOLS.get(def);
        if (tools != null && !tools.isEmpty()) {
            boolean carried = false;
            for (ItemStack tool : tools) {
                if (ItemStack.isSameItem(held, tool)) {
                    carried = true;
                    break;
                }
            }
            if (!carried) { return false; }
        }
        if (def.teams().isEmpty() && def.players().isEmpty() && def.entities().isEmpty()) { return true; }
        Team side = who.getTeam();
        if (side != null && def.teams().contains(side.getName())) { return true; }
        if (who instanceof Player player) { return def.players().contains(player.getGameProfile().getName()); }
        return def.entities().contains(EntityType.getKey(who.getType()).toString());
    }

    public static boolean digBarred(LivingEntity mob, @Nullable BlockState state, ItemStack held) {
        HardnessDef def = groupFor(state, mob);
        if (def == null || !def.adventure() || mob.getServer() == null || mob.getServer().getDefaultGameType() != GameType.ADVENTURE) { return false; }
        return !mayBreak(mob, state, held);
    }

    public static void dig(LivingEntity digger, BlockPos pos) {
        if (!(digger.level() instanceof ServerLevel level)) { return; }
        BlockState state = level.getBlockState(pos);
        int earned = ContentEntities.collectsExperience(digger) ? state.getExpDrop(level, level.getRandom(), pos, 0, 0) : 0;
        if (breaksAway(state, null)) {
            if (level.destroyBlock(pos, true, digger) && earned > 0) { state.getBlock().popExperience(level, pos, earned); }
            return;
        }
        Block.dropResources(state, level, pos, level.getBlockEntity(pos), digger, ItemStack.EMPTY);
        if (earned > 0) { state.getBlock().popExperience(level, pos, earned); }
        level.levelEvent(BREAK_EFFECT, pos, Block.getId(state));
    }

    public static float miningAt(@Nullable BlockState state, @Nullable LivingEntity who, int x, int y, int z) {
        HardnessDef def = groupFor(state, who);
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

    private static String swapToken(HardnessDef def) { return "swap:" + def.key(); }

    private static String swapKey(HardnessDef def) { return "hardness:" + def.key(); }

    private static boolean unlocked(MinecraftServer server, HardnessDef def) { return BECOMES.containsKey(def) && GateStorage.unlockedGlobally(server, swapKey(def)); }

    private static boolean outsideGroup(HardnessDef def, BlockState state) {
        if (!DENIED.isEmpty() && DENIED.containsKey(state.getBlock())) { return true; }
        if (!DENIED_EXACT.isEmpty() && DENIED_EXACT.containsKey(state)) { return true; }
        List<HardnessDef> whole = WHOLE.get(state.getBlock());
        if (whole != null && whole.contains(def)) { return false; }
        List<HardnessDef> exact = EXACT.isEmpty() ? null : EXACT.get(state);
        return exact == null || !exact.contains(def);
    }

    private static int swap(ServerLevel level, LevelChunk chunk, HardnessDef def) {
        BlockState target = BECOMES.get(def);
        if (target == null) { return 0; }
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();
        int swapped = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        LevelChunkSection[] sections = chunk.getSections();
        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            if (section == null || section.hasOnlyAir() || !section.maybeHas(state -> state != target && !outsideGroup(def, state))) { continue; }
            int bottom = chunk.getSectionYFromSectionIndex(index) << 4;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState found = section.getBlockState(x, y, z);
                        if (found == target || outsideGroup(def, found)) { continue; }
                        pos.set(baseX + x, bottom + y, baseZ + z);
                        level.setBlock(pos, target, SWAP_FLAGS);
                        swapped++;
                    }
                }
            }
        }
        return swapped;
    }

    private static void swapped(ServerLevel level, LevelChunk chunk, List<HardnessDef> defs) {
        Set<String> tokens = new HashSet<>(ContentChunkTokens.get(chunk));
        for (HardnessDef def : defs) {
            swap(level, chunk, def);
            tokens.add(swapToken(def));
        }
        ContentChunkTokens.put(chunk, tokens);
    }

    private static List<HardnessDef> missing(MinecraftServer server, LevelChunk chunk) {
        List<HardnessDef> found = new ArrayList<>();
        Set<String> already = ContentChunkTokens.get(chunk);
        for (HardnessDef def : BECOMES.keySet()) {
            if (!already.contains(swapToken(def)) && unlocked(server, def)) { found.add(def); }
        }
        return found;
    }

    private static void swapLoaded(MinecraftServer server, HardnessDef def) {
        int swapped = 0;
        int chunks = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (ChunkHolder holder : ((IChunkMap) level.getChunkSource().chunkMap).rdpl$getChunks()) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(holder.getPos().x, holder.getPos().z);
                if (chunk == null) { continue; }
                swapped += swap(level, chunk, def);
                Set<String> tokens = new HashSet<>(ContentChunkTokens.get(chunk));
                tokens.add(swapToken(def));
                ContentChunkTokens.put(chunk, tokens);
                chunks++;
            }
        }
        ContentLog.LOGGER.info("Hardness group {} became {} in {} loaded chunk(s), {} block(s) swapped; chunks loaded or made from now on follow", def.key(), def.becomes(), chunks, swapped);
    }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (BECOMES.isEmpty() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        String earned = event.getAdvancement().getId().toString();
        for (HardnessDef def : BECOMES.keySet()) {
            if (!def.becomesOn().equals(earned) || unlocked(player.server, def)) { continue; }
            GateStorage.unlockGlobally(player.server, swapKey(def));
            swapLoaded(player.server, def);
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (BECOMES.isEmpty() || !(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        if (!missing(level.getServer(), chunk).isEmpty()) { SWAPS.computeIfAbsent(level.dimension(), key -> new ArrayDeque<>()).add(chunk.getPos()); }
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) { SWAPS.remove(level.dimension()); }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || SWAPS.isEmpty() || !(event.level instanceof ServerLevel level)) { return; }
        Deque<ChunkPos> queue = SWAPS.get(level.dimension());
        while (queue != null && !queue.isEmpty() && ContentRetrogen.canCatchUp(level, queue.peekFirst())) {
            ChunkPos pos = queue.removeFirst();
            LevelChunk chunk = level.getChunk(pos.x, pos.z);
            List<HardnessDef> defs = missing(level.getServer(), chunk);
            if (defs.isEmpty()) { continue; }
            swapped(level, chunk, defs);
            ContentRetrogen.caughtUp(level, pos);
        }
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (idle() || !(event.getLevel() instanceof ServerLevel level) || !(event.getPlayer() instanceof ServerPlayer player)) { return; }
        BlockState state = event.getState();
        ItemStack held = player.getMainHandItem();
        if (event.isCanceled() && player.gameMode.getGameModeForPlayer() == GameType.ADVENTURE && mayBreak(player, state, held)) { event.setCanceled(false); }
        if (event.isCanceled() || player.isCreative() || breaksAway(state, player)) { return; }
        int earned = event.getExpToDrop();
        event.setCanceled(true);
        BlockPos pos = event.getPos();
        ItemStack before = held.copy();
        boolean harvests = state.canHarvestBlock(level, pos, player);
        if (!held.isEmpty()) {
            held.mineBlock(level, state, pos, player);
            if (held.isEmpty()) { ForgeEventFactory.onPlayerDestroyItem(player, before, InteractionHand.MAIN_HAND); }
        }
        if (harvests) {
            state.getBlock().playerDestroy(level, player, pos, state, level.getBlockEntity(pos), before);
            if (earned > 0) { state.getBlock().popExperience(level, pos, earned); }
        }
        level.levelEvent(player, BREAK_EFFECT, pos, Block.getId(state));
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (idle() || event.getPosition().isEmpty()) { return; }
        BlockPos pos = event.getPosition().get();
        float multiplier = miningAt(event.getState(), event.getEntity(), pos.getX(), pos.getY(), pos.getZ());
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
        int minHeight = GsonHelper.getAsInt(json, "minHeight", Integer.MIN_VALUE);
        int maxHeight = GsonHelper.getAsInt(json, "maxHeight", Integer.MAX_VALUE);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Hardness group {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        JsonObject adventure = GsonHelper.getAsJsonObject(json, "adventure", new JsonObject());
        JsonObject becomes = GsonHelper.getAsJsonObject(json, "becomes", new JsonObject());
        return new HardnessDef(key, blocks, matches(key, json, "except"),
                mining[0], mining[1], blast[0], blast[1], buckets,
                minHeight, maxHeight, Json.strings(json, "requires"), field(json),
                GsonHelper.getAsBoolean(json, "keeps", false), json.has("adventure"),
                Json.strings(adventure, "tools"), Json.strings(adventure, "teams"), Json.strings(adventure, "players"), Json.strings(adventure, "entities"),
                GsonHelper.getAsString(json, "advancement", "").trim(),
                GsonHelper.getAsString(becomes, "advancement", "").trim(), GsonHelper.getAsString(becomes, "block", "").trim());
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
