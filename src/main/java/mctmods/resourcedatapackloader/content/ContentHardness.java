package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.HardnessDef;
import mctmods.resourcedatapackloader.content.gate.GateStorage;
import mctmods.resourcedatapackloader.content.worldgen.ContentField;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import static mctmods.resourcedatapackloader.util.Json.strings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHand;
import net.minecraft.world.GameType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentHardness {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, HardnessDef> DEFS = new LinkedHashMap<>();
    private static final Map<Block, List<HardnessDef>> WHOLE = new IdentityHashMap<>();
    private static final Map<IBlockState, List<HardnessDef>> EXACT = new IdentityHashMap<>();
    private static final Map<Block, HardnessDef> DENIED = new IdentityHashMap<>();
    private static final Map<IBlockState, HardnessDef> DENIED_EXACT = new IdentityHashMap<>();
    private static final Map<HardnessDef, List<ItemStack>> TOOLS = new IdentityHashMap<>();
    private static final Map<HardnessDef, IBlockState> BECOMES = new IdentityHashMap<>();
    private static final int SWAP_FLAGS = 2 | 16;
    private static long salt;
    private static boolean loaded;
    private static boolean anyRolls;

    private ContentHardness() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.hardness) { return false; }
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
        anyRolls = false;
        for (HardnessDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            int found = 0;
            for (BlockMatchDef name : def.except) { found += deny(def, name); }
            for (BlockMatchDef name : def.blocks) { found += bind(def, name); }
            if (found == 0) {
                ContentLog.LOGGER.error("Hardness group {} names no registered block, so it does nothing", def.registryName);
                continue;
            }
            if (def.rolls()) { anyRolls = true; }
            List<ItemStack> tools = new ArrayList<>();
            for (String name : def.tools) {
                ItemStack stack = ContentStacks.parse(def.registryName, name, 1);
                if (!stack.isEmpty()) { tools.add(stack); }
            }
            TOOLS.put(def, tools);
            if (def.swaps()) {
                IBlockState target = ContentStates.parse(def.becomes, def.registryName + " becomes");
                if (target != null) { BECOMES.put(def, target); }
            }
        }
    }

    public static boolean anySwaps() { return !BECOMES.isEmpty(); }

    private static int deny(HardnessDef def, BlockMatchDef name) {
        Block block = ContentStates.block(name.block.toString(), def.registryName + " except");
        if (block == null) { return 0; }
        if (!name.properties.isEmpty()) { DENIED_EXACT.put(ContentStates.of(block, 0, name.properties, def.registryName), def); }
        else if (name.meta >= 0) { DENIED_EXACT.put(ContentStates.of(block, name.meta), def); }
        else { DENIED.put(block, def); }
        return 1;
    }

    private static int bind(HardnessDef def, BlockMatchDef name) {
        Block block = ContentStates.block(name.block.toString(), def.registryName + " blocks");
        if (block == null) { return 0; }
        if (!name.properties.isEmpty()) { EXACT.computeIfAbsent(ContentStates.of(block, 0, name.properties, def.registryName), state -> new ArrayList<>()).add(def); }
        else if (name.meta >= 0) { EXACT.computeIfAbsent(ContentStates.of(block, name.meta), state -> new ArrayList<>()).add(def); }
        else { WHOLE.computeIfAbsent(block, held -> new ArrayList<>()).add(def); }
        return 1;
    }

    public static boolean anyRolls() { return anyRolls; }

    static Map<Block, List<HardnessDef>> whole() { return WHOLE; }

    static Map<IBlockState, List<HardnessDef>> exact() { return EXACT; }

    public static boolean wanted() { return !WHOLE.isEmpty() || !EXACT.isEmpty(); }

    @Nullable public static HardnessDef groupFor(@Nullable IBlockState state) { return groupFor(state, null); }

    @Nullable public static HardnessDef groupFor(@Nullable IBlockState state, @Nullable EntityLivingBase who) {
        if (state == null || WHOLE.isEmpty() && EXACT.isEmpty()) { return null; }
        if (!DENIED.isEmpty() && DENIED.containsKey(state.getBlock())) { return null; }
        if (!DENIED_EXACT.isEmpty() && DENIED_EXACT.containsKey(state)) { return null; }
        HardnessDef open = null;
        List<HardnessDef> whole = WHOLE.get(state.getBlock());
        List<HardnessDef> exact = EXACT.isEmpty() ? null : EXACT.get(state);
        for (int side = 0; side < 2; side++) {
            List<HardnessDef> defs = side == 0 ? whole : exact;
            if (defs == null) { continue; }
            for (HardnessDef def : defs) {
                if (def.advancement.isEmpty()) {
                    if (open == null) { open = def; }
                    continue;
                }
                if (who instanceof EntityPlayer && Advancements.has((EntityPlayer) who, def.advancement)) { return def; }
            }
        }
        return open;
    }

    public static int bucket(HardnessDef def, int x, int y, int z) {
        if (def.buckets <= 1) { return 0; }
        if (y < def.minHeight || y > def.maxHeight) { return 0; }
        float strength = def.field.strength(salt, x, y, z);
        int bucket = Math.round(strength * (def.buckets - 1));
        return MathHelper.clamp(bucket, 0, def.buckets - 1);
    }

    public static float miningAt(@Nullable IBlockState state, @Nullable EntityLivingBase who, int x, int y, int z) {
        HardnessDef def = groupFor(state, who);
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

    public static boolean keeps(@Nullable IBlockState state, @Nullable EntityLivingBase who) {
        HardnessDef def = groupFor(state, who);
        return def != null && def.keeps;
    }

    public static boolean mayBreak(@Nullable EntityLivingBase who, @Nullable IBlockState state, ItemStack held) {
        HardnessDef def = groupFor(state, who);
        if (def == null || !def.adventure || who == null || held.isEmpty()) { return false; }
        List<ItemStack> tools = TOOLS.get(def);
        if (tools != null && !tools.isEmpty()) {
            boolean carried = false;
            for (ItemStack tool : tools) {
                if (ContentStacks.matches(held, tool.getItem(), tool.getMetadata())) { carried = true; break; }
            }
            if (!carried) { return false; }
        }
        if (def.teams.isEmpty() && def.players.isEmpty() && def.entities.isEmpty()) { return true; }
        Team side = who.getTeam();
        if (side != null && def.teams.contains(side.getName())) { return true; }
        if (who instanceof EntityPlayer) { return def.players.contains(who.getName()); }
        ResourceLocation id = EntityList.getKey(who);
        return id != null && def.entities.contains(id.toString());
    }

    public static boolean mayDig(EntityLivingBase mob, @Nullable IBlockState state, ItemStack held) {
        HardnessDef def = groupFor(state, mob);
        if (def == null || !def.adventure || mob.world.getWorldInfo().getGameType() != GameType.ADVENTURE) { return true; }
        return mayBreak(mob, state, held);
    }

    public static boolean dig(EntityLivingBase digger, BlockPos pos) {
        World world = digger.world;
        IBlockState state = world.getBlockState(pos);
        int earned = mctmods.resourcedatapackloader.content.entity.ContentEntities.collectsExperience(digger) ? state.getBlock().getExpDrop(state, world, pos, 0) : 0;
        if (!keeps(state, null)) {
            boolean broke = world.destroyBlock(pos, true);
            if (broke && earned > 0) { state.getBlock().dropXpOnBlockBreak(world, pos, earned); }
            return broke;
        }
        state.getBlock().dropBlockAsItem(world, pos, state, 0);
        if (earned > 0) { state.getBlock().dropXpOnBlockBreak(world, pos, earned); }
        world.playEvent(2001, pos, Block.getStateId(state));
        return true;
    }

    public static String swapToken(HardnessDef def) { return "swap:" + def.registryName; }

    private static String swapKey(HardnessDef def) { return "hardness:" + def.registryName; }

    public static boolean unlocked(World world, HardnessDef def) { return BECOMES.containsKey(def) && GateStorage.unlockedGlobally(world, swapKey(def)); }

    public static List<HardnessDef> swapsMissing(World world, Set<String> already) {
        List<HardnessDef> missing = new ArrayList<>();
        for (HardnessDef def : BECOMES.keySet()) {
            if (!already.contains(swapToken(def)) && unlocked(world, def)) { missing.add(def); }
        }
        return missing;
    }

    private static boolean inGroup(HardnessDef def, IBlockState state) {
        if (!DENIED.isEmpty() && DENIED.containsKey(state.getBlock())) { return false; }
        if (!DENIED_EXACT.isEmpty() && DENIED_EXACT.containsKey(state)) { return false; }
        List<HardnessDef> whole = WHOLE.get(state.getBlock());
        if (whole != null && whole.contains(def)) { return true; }
        List<HardnessDef> exact = EXACT.isEmpty() ? null : EXACT.get(state);
        return exact != null && exact.contains(def);
    }

    public static int swap(World world, int chunkX, int chunkZ, HardnessDef def) {
        IBlockState target = BECOMES.get(def);
        if (target == null || !world.isChunkGeneratedAt(chunkX, chunkZ)) { return 0; }
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int swapped = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (ExtendedBlockStorage section : chunk.getBlockStorageArray()) {
            if (section == Chunk.NULL_BLOCK_STORAGE) { continue; }
            int bottom = section.getYLocation();
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        IBlockState found = section.get(x, y, z);
                        if (found == target || !inGroup(def, found)) { continue; }
                        pos.setPos(baseX + x, bottom + y, baseZ + z);
                        world.setBlockState(pos, target, SWAP_FLAGS);
                        swapped++;
                    }
                }
            }
        }
        return swapped;
    }

    private static void swapLoaded(MinecraftServer server, HardnessDef def) {
        int swapped = 0;
        int chunks = 0;
        for (WorldServer world : server.worlds) {
            for (Chunk chunk : new ArrayList<>(world.getChunkProvider().getLoadedChunks())) {
                swapped += swap(world, chunk.x, chunk.z, def);
                mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen.mark(world, chunk.getPos(), swapToken(def));
                chunks++;
            }
        }
        ContentLog.LOGGER.info("Hardness group {} became {} in {} loaded chunk(s), {} block(s) swapped; chunks loaded or made from now on follow", def.registryName, def.becomes, chunks, swapped);
    }

    @SubscribeEvent public static void onAdvancement(AdvancementEvent event) {
        if (BECOMES.isEmpty() || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        MinecraftServer server = event.getEntityPlayer().getServer();
        if (server == null) { return; }
        String earned = event.getAdvancement().getId().toString();
        World world = event.getEntityPlayer().world;
        for (HardnessDef def : BECOMES.keySet()) {
            if (!def.becomesOn.equals(earned) || unlocked(world, def)) { continue; }
            GateStorage.unlockGlobally(world, swapKey(def));
            swapLoaded(server, def);
        }
    }

    public static final class Swaps implements IWorldGenerator {
        @Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
            if (BECOMES.isEmpty() || world.isRemote) { return; }
            for (HardnessDef def : BECOMES.keySet()) {
                if (!unlocked(world, def)) { continue; }
                for (int dx = 0; dx <= 1; dx++) {
                    for (int dz = 0; dz <= 1; dz++) {
                        if (!world.isChunkGeneratedAt(chunkX + dx, chunkZ + dz)) { continue; }
                        swap(world, chunkX + dx, chunkZ + dz, def);
                        mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen.mark(world, new ChunkPos(chunkX + dx, chunkZ + dz), swapToken(def));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true) public static void onBreak(BlockEvent.BreakEvent event) {
        if (WHOLE.isEmpty() && EXACT.isEmpty() || event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        IBlockState state = event.getState();
        ItemStack held = player.getHeldItemMainhand();
        if (event.isCanceled() && player.interactionManager.getGameType() == GameType.ADVENTURE && mayBreak(player, state, held)) { event.setCanceled(false); }
        if (event.isCanceled() || player.interactionManager.isCreative() || !keeps(state, player)) { return; }
        event.setCanceled(true);
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        ItemStack before = held.copy();
        boolean harvests = state.getBlock().canHarvestBlock(world, pos, player);
        if (!held.isEmpty()) {
            held.onBlockDestroyed(world, state, pos, player);
            if (held.isEmpty()) { ForgeEventFactory.onPlayerDestroyItem(player, before, EnumHand.MAIN_HAND); }
        }
        if (harvests) {
            state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), before);
            if (event.getExpToDrop() > 0) { state.getBlock().dropXpOnBlockBreak(world, pos, event.getExpToDrop()); }
        }
        world.playEvent(player, 2001, pos, Block.getStateId(state));
    }

    @SubscribeEvent public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (WHOLE.isEmpty() && EXACT.isEmpty()) { return; }
        BlockPos pos = event.getPos();
        if (pos == null) { return; }
        float multiplier = miningAt(event.getState(), event.getEntityPlayer(), pos.getX(), pos.getY(), pos.getZ());
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
        int buckets = MathHelper.clamp(JsonUtils.getInt(json, "buckets", 10), 1, 256);
        int minHeight = JsonUtils.getInt(json, "minHeight", 0);
        int maxHeight = JsonUtils.getInt(json, "maxHeight", 255);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Hardness group {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        JsonObject adventure = JsonUtils.getJsonObject(json, "adventure", new JsonObject());
        JsonObject becomes = JsonUtils.getJsonObject(json, "becomes", new JsonObject());
        return new HardnessDef(key, blocks, matches(key, json, "except"),
                mining[0], mining[1], blast[0], blast[1], buckets,
                minHeight, maxHeight, strings(json, "requires"), field(json),
                JsonUtils.getBoolean(json, "keeps", false), json.has("adventure"),
                strings(adventure, "tools"), strings(adventure, "teams"), strings(adventure, "players"), strings(adventure, "entities"),
                JsonUtils.getString(json, "advancement", "").trim(),
                JsonUtils.getString(becomes, "advancement", "").trim(), JsonUtils.getString(becomes, "block", "").trim());
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
            values.add(ContentParser.match(key, element));
            return values;
        }
        for (JsonElement held : element.getAsJsonArray()) { values.add(ContentParser.match(key, held)); }
        return values;
    }
}
