package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.BlockFalling;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentRetrogen {
    public static final String TAG = "rdpl_retrogen";
    private static final Map<Integer, Deque<Pending>> QUEUES = new HashMap<>();
    private static final Map<Integer, Map<ChunkPos, Set<String>>> DONE = new HashMap<>();
    private static List<WorldgenDef> defs = new ArrayList<>();
    @Nullable private static ContentWorldgen generator;
    private static int completed;
    private static int flattened;
    private static int queued;

    private ContentRetrogen() {}

    public static void setup(List<WorldgenDef> active, @Nullable ContentWorldgen worldgen) {
        ContentLog.LOGGER.info("Retrogen armed: {} vein definition(s), bedrock={}", active.size(), bedrockWanted());
        defs = active;
        generator = worldgen;
        for (WorldgenDef def : defs) { def.buildToken(Config.worldgen.retrogenKey); }
    }

    public static String bedrockToken() {
        return "bedrock:" + ContentBedrock.layers()
                + (ContentBedrock.roofWanted() ? ":roof" : "")
                + "#" + Config.worldgen.flatBedrockRetrogenKey;
    }

    public static boolean bedrockWanted() { return ContentBedrock.enabled() && ContentControl.flag(ContentControl.BEDROCK, "flatBedrockRetrogen", Config.worldgen.flatBedrockRetrogen); }

    public static boolean wanted() {
        if (bedrockWanted() || ContentReplacements.wanted()) { return true; }

        for (WorldgenDef def : defs) {
            if (def.retrogen) { return true; }
        }
        return false;
    }

    public static void markGenerated(World world, int chunkX, int chunkZ) {
        if (world.isRemote) { return; }

        Set<String> already = done(world.provider.getDimension()).computeIfAbsent(new ChunkPos(chunkX, chunkZ), k -> new HashSet<>());
        for (WorldgenDef def : defs) { already.add(def.getToken()); }
        if (ContentBedrock.enabled()) { already.add(bedrockToken()); }
    }

    private static Map<ChunkPos, Set<String>> done(int dimension) { return DONE.computeIfAbsent(dimension, k -> new HashMap<>()); }

    @SubscribeEvent public static void onChunkLoad(ChunkDataEvent.Load event) {
        if (event.getWorld().isRemote) { return; }

        int dimension = event.getWorld().provider.getDimension();
        Set<String> already = read(event.getData());
        if (Config.worldgen.adoptExistingChunks && !hasVeinTokens(already) && !defs.isEmpty()) {
            for (WorldgenDef def : defs) { already.add(def.getToken()); }
            ContentLog.LOGGER.debug("Adopted chunk {} as already generated", event.getChunk().getPos());
        }
        done(dimension).put(event.getChunk().getPos(), already);
        ContentLog.LOGGER.debug("Chunk {} loaded with retrogen tokens {}", event.getChunk().getPos(), already);
        boolean replace = ContentReplacements.wanted() && !already.contains(ContentReplacements.token()) && ContentReplacements.appliesTo(dimension);
        if (!replace && (!Config.worldgen.retrogen || (defs.isEmpty() && !bedrockWanted()))) { return; }

        List<WorldgenDef> pending = new ArrayList<>();
        boolean bedrock = false;
        if (Config.worldgen.retrogen) {
            for (WorldgenDef def : defs) {
                if (def.retrogen && !already.contains(def.getToken())) { pending.add(def); }
            }
            bedrock = bedrockWanted() && !already.contains(bedrockToken()) && ContentBedrock.appliesTo(dimension);
        }
        if (pending.isEmpty() && !bedrock && !replace) {
            ContentLog.LOGGER.debug("Nothing to do for chunk {}: bedrockToken={} present={} appliesTo={}",
                    event.getChunk().getPos(), bedrockToken(), already.contains(bedrockToken()), ContentBedrock.appliesTo(dimension));
            return;
        }

        QUEUES.computeIfAbsent(dimension, k -> new ArrayDeque<>()).add(new Pending(event.getChunk().getPos(), pending, bedrock, replace));
        queued++;
        ContentLog.LOGGER.debug("Queued chunk {} for retrogen: {} vein(s), bedrock={}, replace={}", event.getChunk().getPos(), pending.size(), bedrock, replace);
    }

    @SubscribeEvent
    public static void onChunkSave(ChunkDataEvent.Save event) {
        Map<ChunkPos, Set<String>> byChunk = DONE.get(event.getWorld().provider.getDimension());
        if (byChunk == null) { return; }

        Set<String> already = byChunk.get(event.getChunk().getPos());
        if (already == null || already.isEmpty()) { return; }

        NBTTagList list = new NBTTagList();
        for (String name : already) { list.appendTag(new NBTTagString(name)); }
        event.getData().setTag(TAG, list);
        if (!event.getChunk().isLoaded()) { byChunk.remove(event.getChunk().getPos()); }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) { return; }

        Deque<Pending> queue = QUEUES.remove(event.getWorld().provider.getDimension());
        if (queue != null) { queued -= queue.size(); }
        if (QUEUES.isEmpty()) { queued = 0; }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END) { return; }
        if (queued == 0 || (!Config.worldgen.retrogen && !ContentReplacements.wanted())) { return; }

        Deque<Pending> queue = QUEUES.get(event.world.provider.getDimension());
        if (queue == null || queue.isEmpty()) { return; }

        int budget = Math.max(1, Config.worldgen.retrogenChunksPerTick);
        for (int i = 0; i < budget && !queue.isEmpty(); i++) {
            run(event.world, queue.poll());
            queued--;
        }

        if (queue.isEmpty() && completed > 0) {
            Summary.info("retrogen", "Caught up " + completed + " existing chunk(s)"
                    + (flattened > 0 ? ", flattening bedrock in " + flattened + " of them" : ""));
            ContentReplacements.report();
            completed = 0;
            flattened = 0;
        }
    }

    private static void run(World world, Pending pending) {
        if (!world.isChunkGeneratedAt(pending.pos.x, pending.pos.z)) { return; }

        boolean falling = BlockFalling.fallInstantly;
        BlockFalling.fallInstantly = true;
        try {
            if (pending.replace) { ContentReplacements.replace(world, pending.pos.x, pending.pos.z); }
            if (pending.bedrock) {
                ContentBedrock.flatten(world, pending.pos.x, pending.pos.z, world.provider.getDimension());
                flattened++;
            }
            ContentWorldgen worldgen = generator;
            if (!pending.defs.isEmpty() && worldgen != null) { generateVeins(worldgen, world, pending); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Retrogen failed for chunk {}", pending.pos, ex);
            return;
        }
        finally { BlockFalling.fallInstantly = falling; }

        Set<String> already = done(world.provider.getDimension()).computeIfAbsent(pending.pos, k -> new HashSet<>());
        for (WorldgenDef def : pending.defs) { already.add(def.getToken()); }
        if (pending.bedrock) { already.add(bedrockToken()); }
        if (pending.replace && settled(world, pending.pos)) { already.add(ContentReplacements.token()); }
        world.getChunk(pending.pos.x, pending.pos.z).markDirty();
        completed++;
    }

    private static void generateVeins(ContentWorldgen worldgen, World world, Pending pending) {
        Random random = new Random(world.getSeed());
        random.setSeed(pending.pos.x * (random.nextLong() | 1L) + pending.pos.z * (random.nextLong() | 1L) ^ world.getSeed());
        worldgen.generate(random, pending.pos.x, pending.pos.z, world, pending.defs);
    }

    private static Set<String> read(NBTTagCompound data) {
        Set<String> already = new HashSet<>();
        if (!data.hasKey(TAG, 9)) { return already; }

        NBTTagList list = data.getTagList(TAG, 8);
        for (int i = 0; i < list.tagCount(); i++) { already.add(list.getStringTagAt(i)); }
        return already;
    }

    private static boolean settled(World world, ChunkPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!world.isChunkGeneratedAt(pos.x + x, pos.z + z)) { return false; }
            }
        }
        return true;
    }

    private static boolean hasVeinTokens(Set<String> already) {
        for (String token : already) {
            if (!token.startsWith("bedrock:") && !token.startsWith("replace:")) { return true; }
        }
        return false;
    }

    private static final class Pending {
        private final ChunkPos pos;
        private final List<WorldgenDef> defs;
        private final boolean bedrock;
        private final boolean replace;

        private Pending(ChunkPos pos, List<WorldgenDef> defs, boolean bedrock, boolean replace) {
            this.pos = pos;
            this.defs = defs;
            this.bedrock = bedrock;
            this.replace = replace;
        }
    }
}
