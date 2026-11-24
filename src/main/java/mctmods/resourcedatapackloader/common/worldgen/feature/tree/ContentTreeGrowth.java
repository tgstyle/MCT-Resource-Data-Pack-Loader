package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreePickDef;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.common.util.plant.PlantWater;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.BlockSnapshot;

public final class ContentTreeGrowth {
    private static final Map<TreeEntry, Cached> CACHE = new WeakHashMap<>();

    private ContentTreeGrowth() {}

    private static final class Cached {
        private TreeShape shape;
        private TreeShape built;
        private int step = -1;
        private int gone = -1;
        private Map<BlockPos, BlockState> current = Collections.emptyMap();
    }

    public static boolean start(ServerLevel level, BlockPos pos, BlockState sapling, String rule, TreeGrowthDef growth, @Nullable Consumer<RandomSource> own) {
        long seed = mix(level.getSeed(), pos.asLong(), level.getGameTime());
        RandomSource random = RandomSource.create(seed);
        TreePickDef pick = WeightedPicks.pick(growth.trees(), TreePickDef::weight, random);
        if (pick == null) { return false; }
        Block block = sapling.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        boolean foreign = !(block instanceof ContentSaplingBlock) && !"minecraft".equals(id.getNamespace());
        boolean inSteps = pick.growsInSteps(foreign);
        int index = growth.trees().indexOf(pick);
        TreeEntry entry;
        if (pick.kind() == TreePickDef.Kind.STRUCTURE) {
            Rotation rotation = Turns.turn(pick.turns(), random);
            Mirror mirror = Turns.mirror(pick.mirrors(), random);
            TreeTemplates.Built built = TreeTemplates.get(level, pick.source(), rotation, mirror, growth.steps());
            if (built == null) {
                ContentLog.LOGGER.warn("Tree {}: template is missing, nothing grows at {} {} {}", pick.source(), pos.getX(), pos.getY(), pos.getZ());
                return false;
            }
            boolean logged = anyLog(built.blocks);
            boolean flat = built.overBounds || built.shape == null || built.shape.noTrunk();
            if (flat || !inSteps || !logged) {
                if (built.overBounds) { ContentLog.LOGGER.warn("Tree {}: over the bounds, placed whole", pick.source()); }
                else if (logged && flat) { ContentLog.LOGGER.warn("Tree {}: no trunk at the start column, placed whole", pick.source()); }
                placeWhole(level, pos, built);
                return true;
            }
            entry = entryOf(level, pos, rule, index, pick, (byte) rotation.ordinal(), (byte) mirror.ordinal(), seed, built.shape.steps(), single(sapling), built.hash);
        }
        else {
            BlockState state = pick.kind() == TreePickDef.Kind.OWN ? sapling : ContentStates.parse(pick.source(), id);
            if (state == null) {
                ContentLog.LOGGER.warn("Tree {}: the vanilla sapling state is not known, nothing grows at {} {} {}", pick.source(), pos.getX(), pos.getY(), pos.getZ());
                return false;
            }
            TreeShape[] shaped = new TreeShape[1];
            TreeRecorder.Result result = TreeRecorder.record(level, pos, state, seed, pick.source(), pick.kind() == TreePickDef.Kind.OWN ? own : null, found -> {
                if (found.overBounds) {
                    ContentLog.LOGGER.warn("Tree {}: over the bounds, placed whole", pick.source());
                    return true;
                }
                shaped[0] = shapeOf(found, growth.steps());
                boolean logged = anyLog(shaped[0].whole());
                if (shaped[0].noTrunk() && logged) { ContentLog.LOGGER.warn("Tree {}: no trunk at the start column, placed whole", pick.source()); }
                return shaped[0].noTrunk() || !inSteps;
            });
            if (result == null) { return false; }
            if (result.placed) { return true; }
            entry = recorded(level, pos, rule, index, pick, seed, shaped[0], result);
            entry.saplings.put(TreeEntry.pack(0, 0, 0), sapling);
        }
        ContentTreeStore.put(level, entry);
        for (int packed : entry.saplings.keySet()) {
            long other = pos.offset(TreeEntry.unpackX(packed), TreeEntry.unpackY(packed), TreeEntry.unpackZ(packed)).asLong();
            TreeEntry waiting = other == entry.pos ? null : ContentTreeStore.get(level, other);
            if (waiting != null && waiting.phase == 0) { ContentTreeStore.remove(level, other); }
        }
        step(level, entry, growth, growth.timed());
        return true;
    }

    public static void drive(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (!(state.getBlock() instanceof BonemealableBlock growable)) { return; }
        List<BlockSnapshot> list = level.capturedBlockSnapshots;
        int saved = list.size();
        boolean prior = level.captureBlockSnapshots;
        level.captureBlockSnapshots = true;
        try { growable.performBonemeal(level, random, pos, state); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Tree drive at {} {} {} failed: {}", pos.getX(), pos.getY(), pos.getZ(), ex.toString());
            TreeRecorder.restore(level, saved, prior);
            return;
        }
        if (list.size() <= saved) {
            level.captureBlockSnapshots = prior;
            return;
        }
        boolean stage = true;
        for (int i = saved; i < list.size(); i++) { stage &= list.get(i).getPos().equals(pos); }
        if (stage) {
            TreeRecorder.commitWhole(level, saved, prior);
            return;
        }
        TreeRecorder.restore(level, saved, prior);
        TreeGrowthDef growth = ContentTreeRules.growth(state);
        if (growth != null && growth.growing()) { start(level, pos, state, ContentTreeRules.ruleId(state), growth, null); }
    }

    @Nullable public static String step(ServerLevel level, TreeEntry entry, TreeGrowthDef growth, boolean timed) {
        String stale = stale(level, entry, growth);
        if (stale != null) { return stale; }
        TreeShape shape = shape(level, entry);
        if (shape == null) { return drop(level, entry, "template changed"); }
        int k = Math.min(entry.step + 1, entry.steps);
        Map<BlockPos, BlockState> previous = current(level, entry);
        Map<BlockPos, BlockState> next = shape.stepAt(k, goneSet(entry));
        Step run = new Step(level, entry, shape, previous, next, k >= entry.steps);
        if (run.unloaded()) { return "chunk not loaded"; }
        BlockPos blocked = run.noRoom();
        if (blocked != null) {
            String reason = "no room at " + blocked.getX() + " " + blocked.getY() + " " + blocked.getZ();
            if (growth.blocked() == TreeGrowthDef.Blocked.WAIT) { return reason; }
            boolean done = growth.blocked() == TreeGrowthDef.Blocked.RESET ? ContentTreeFelling.reset(level, entry, previous, timed) : ContentTreeFelling.drop(level, entry, previous);
            return done ? reason : "chunk not loaded";
        }
        run.apply();
        return null;
    }

    @Nullable public static String conditions(ServerLevel level, TreeEntry entry, TreeGrowthDef growth) {
        BlockPos start = BlockPos.of(entry.pos);
        BlockPos base = start;
        BlockPos tip = start;
        if (entry.phase != 0) {
            TreeShape shape = shape(level, entry);
            if (shape == null || shape.base() == null) { return "template missing"; }
            BlockPos column = shape.base();
            base = start.offset(column);
            int top = column.getY();
            for (BlockPos pos : current(level, entry).keySet()) {
                if (pos.getX() == column.getX() && pos.getZ() == column.getZ()) { top = Math.max(top, pos.getY()); }
            }
            tip = start.offset(column.getX(), top + 1, column.getZ());
        }
        return blockedAt(level, base, tip, growth);
    }

    @Nullable public static String conditions(ServerLevel level, BlockPos pos, TreeGrowthDef growth) { return blockedAt(level, pos, pos, growth); }

    public static void plant(ServerLevel level, BlockPos pos, BlockState sapling, String rule) {
        if (ContentTreeStore.get(level, pos.asLong()) != null) { return; }
        long now = level.getGameTime();
        ContentTreeStore.put(level, TreeEntry.fresh(pos.asLong(), rule, (byte) 0, -1, (byte) 0, "", (byte) 0, (byte) 0, 0L, 0, now, single(sapling), 0, new ArrayList<>(), new int[0], new int[0], new int[0], new LinkedHashMap<>()));
    }

    @Nullable private static String blockedAt(ServerLevel level, BlockPos base, BlockPos tip, TreeGrowthDef growth) {
        boolean above = tip.getY() >= level.getMaxBuildHeight();
        if (!level.isLoaded(base) || !above && !level.isLoaded(tip)) { return "chunk not loaded"; }
        if (!BiomeNames.matches(growth.biomes(), level.getBiome(base))) { return "biome"; }
        if (growth.needsWater() && !PlantWater.near(level, base.below(), growth.waterRange())) { return "no water"; }
        if (growth.needsSky() && !above && !level.canSeeSky(tip)) { return "no sky"; }
        if (growth.light() >= 0 && !above && level.getMaxLocalRawBrightness(tip) < growth.light()) { return "no light"; }
        return null;
    }

    @Nullable public static TreeShape shape(ServerLevel level, TreeEntry entry) {
        if (entry.kind == TreeEntry.KIND_STRUCTURE) {
            TreeTemplates.Built built = TreeTemplates.get(level, entry.source, Rotation.values()[entry.turn], Mirror.values()[entry.mirror], entry.steps);
            return built == null ? null : built.shape;
        }
        Cached cached = CACHE.computeIfAbsent(entry, key -> new Cached());
        if (cached.shape == null) { cached.shape = rebuild(entry); }
        return cached.shape;
    }

    public static Map<BlockPos, BlockState> current(ServerLevel level, TreeEntry entry) {
        TreeShape shape = shape(level, entry);
        if (entry.step <= 0 || shape == null) { return Collections.emptyMap(); }
        Cached cached = CACHE.computeIfAbsent(entry, key -> new Cached());
        if (cached.step != entry.step || cached.gone != entry.gone.size() || cached.built != shape) {
            cached.current = shape.stepAt(entry.step, goneSet(entry));
            cached.step = entry.step;
            cached.gone = entry.gone.size();
            cached.built = shape;
        }
        return cached.current;
    }

    private static long mix(long worldSeed, long pos, long time) {
        long h = worldSeed * 0x9E3779B97F4A7C15L + pos;
        h = (h ^ (h >>> 31)) * 0xBF58476D1CE4E5B9L + time;
        h = (h ^ (h >>> 29)) * 0x94D049BB133111EBL;
        return h ^ (h >>> 32);
    }

    private static BlockPos rel(int packed) { return new BlockPos(TreeEntry.unpackX(packed), TreeEntry.unpackY(packed), TreeEntry.unpackZ(packed)); }

    private static int pack(BlockPos rel) { return TreeEntry.pack(rel.getX(), rel.getY(), rel.getZ()); }

    private static boolean anyLog(Map<BlockPos, BlockState> blocks) {
        for (BlockState state : blocks.values()) {
            if (TreeShape.isLog(state)) { return true; }
        }
        return false;
    }

    private static Map<Integer, BlockState> single(BlockState sapling) {
        Map<Integer, BlockState> saplings = new LinkedHashMap<>();
        saplings.put(TreeEntry.pack(0, 0, 0), sapling);
        return saplings;
    }

    private static Set<BlockPos> goneSet(TreeEntry entry) {
        Set<BlockPos> gone = new HashSet<>();
        for (int packed : entry.gone) { gone.add(rel(packed)); }
        return gone;
    }

    private static TreeShape shapeOf(TreeRecorder.Result result, int steps) {
        Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();
        Map<BlockPos, CompoundTag> tiles = new HashMap<>();
        for (int i = 0; i < result.offsets.size(); i++) {
            BlockPos at = rel(result.offsets.get(i));
            blocks.put(at, result.states.get(i));
            if (result.tiles.get(i) != null) { tiles.put(at, result.tiles.get(i)); }
        }
        return new TreeShape(blocks, tiles, steps);
    }

    private static TreeShape rebuild(TreeEntry entry) {
        Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();
        Map<BlockPos, CompoundTag> tiles = new HashMap<>();
        for (int i = 0; i < entry.blocks.length; i++) {
            BlockPos at = rel(entry.blocks[i]);
            blocks.put(at, entry.palette.get(entry.states[i]));
            CompoundTag tile = entry.blockEntities.get(i);
            if (tile != null) { tiles.put(at, tile); }
        }
        return new TreeShape(blocks, tiles, entry.steps);
    }

    private static TreeEntry entryOf(ServerLevel level, BlockPos pos, String rule, int pick, TreePickDef def, byte turn, byte mirror, long seed, int steps, Map<Integer, BlockState> saplings, int hash) {
        long now = level.getGameTime();
        return TreeEntry.fresh(pos.asLong(), rule, (byte) 1, pick, (byte) def.kind().ordinal(), def.source(), turn, mirror, seed, steps, now, saplings, hash, new ArrayList<>(), new int[0], new int[0], new int[0], new LinkedHashMap<>());
    }

    private static TreeEntry recorded(ServerLevel level, BlockPos pos, String rule, int pick, TreePickDef def, long seed, TreeShape shape, TreeRecorder.Result result) {
        int size = result.offsets.size();
        List<BlockState> palette = new ArrayList<>();
        int[] blocks = new int[size];
        int[] states = new int[size];
        int[] under = new int[size];
        Map<Integer, CompoundTag> tiles = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            blocks[i] = result.offsets.get(i);
            states[i] = paletteIndex(palette, result.states.get(i));
            under[i] = paletteIndex(palette, result.unders.get(i));
            if (result.tiles.get(i) != null) { tiles.put(i, result.tiles.get(i)); }
        }
        Map<Integer, BlockState> saplings = new LinkedHashMap<>();
        for (int i = 0; i < result.saplingOffsets.size(); i++) { saplings.put(result.saplingOffsets.get(i), result.saplingStates.get(i)); }
        long now = level.getGameTime();
        return TreeEntry.fresh(pos.asLong(), rule, (byte) 1, pick, (byte) def.kind().ordinal(), def.source(), (byte) 0, (byte) 0, seed, shape.steps(), now, saplings, 0, palette, blocks, states, under, tiles);
    }

    private static int paletteIndex(List<BlockState> palette, BlockState state) {
        int at = palette.indexOf(state);
        if (at >= 0) { return at; }
        palette.add(state);
        return palette.size() - 1;
    }

    private static void placeWhole(ServerLevel level, BlockPos start, TreeTemplates.Built built) {
        if (!built.blocks.containsKey(BlockPos.ZERO)) { TreeRecorder.write(level, start, Blocks.AIR.defaultBlockState(), 3); }
        for (Map.Entry<BlockPos, BlockState> block : built.blocks.entrySet()) { TreeRecorder.write(level, start.offset(block.getKey()), block.getValue(), 3); }
        for (Map.Entry<BlockPos, CompoundTag> tile : built.blockEntities.entrySet()) { placeTile(level, start.offset(tile.getKey()), tile.getValue()); }
    }

    private static void placeTile(ServerLevel level, BlockPos pos, @Nullable CompoundTag data) {
        BlockEntity tile = data == null ? null : level.getBlockEntity(pos);
        if (tile == null) { return; }
        tile.loadWithComponents(data.copy(), level.registryAccess());
        tile.setChanged();
    }

    @Nullable private static String stale(ServerLevel level, TreeEntry entry, TreeGrowthDef growth) {
        if (entry.pick < 0 || entry.pick >= growth.trees().size() || !growth.trees().get(entry.pick).source().equals(entry.source)) { return drop(level, entry, "template changed"); }
        if (entry.kind != TreeEntry.KIND_STRUCTURE) { return null; }
        TreeTemplates.Built built = TreeTemplates.get(level, entry.source, Rotation.values()[entry.turn], Mirror.values()[entry.mirror], entry.steps);
        if (built == null) { return drop(level, entry, "template missing"); }
        return built.hash == entry.hash ? null : drop(level, entry, "template changed");
    }

    private static String drop(ServerLevel level, TreeEntry entry, String reason) {
        BlockPos start = BlockPos.of(entry.pos);
        ContentLog.LOGGER.warn("Tree {} at {} {} {}: {}, so it stops growing and its blocks stay", entry.source, start.getX(), start.getY(), start.getZ(), reason);
        ContentTreeStore.remove(level, entry.pos);
        return reason;
    }

    private static final class Step {
        private final ServerLevel level;
        private final TreeEntry entry;
        private final TreeShape shape;
        private final Map<BlockPos, BlockState> previous;
        private final Map<BlockPos, BlockState> next;
        private final boolean full;
        private final BlockPos start;
        private final Map<Integer, BlockState> unders = new HashMap<>();

        private Step(ServerLevel level, TreeEntry entry, TreeShape shape, Map<BlockPos, BlockState> previous, Map<BlockPos, BlockState> next, boolean full) {
            this.level = level;
            this.entry = entry;
            this.shape = shape;
            this.previous = previous;
            this.next = next;
            this.full = full;
            this.start = BlockPos.of(entry.pos);
            for (int i = 0; i < entry.under.length; i++) { unders.put(entry.blocks[i], entry.palette.get(entry.under[i])); }
        }

        private boolean unloaded() {
            Set<Long> seen = new HashSet<>();
            List<BlockPos> touched = new ArrayList<>(next.keySet());
            touched.addAll(previous.keySet());
            for (BlockPos rel : touched) {
                BlockPos at = start.offset(rel);
                if (level.isOutsideBuildHeight(at) || !seen.add(ChunkPos.asLong(at.getX() >> 4, at.getZ() >> 4))) { continue; }
                if (!level.isLoaded(at)) { return true; }
            }
            return false;
        }

        @Nullable private BlockPos noRoom() {
            for (Map.Entry<BlockPos, BlockState> block : next.entrySet()) {
                BlockState was = previous.get(block.getKey());
                if (was != null && TreeShape.isLog(was) || !TreeShape.isLog(block.getValue())) { continue; }
                if (blocked(block.getKey(), block.getValue(), true)) { return start.offset(block.getKey()); }
            }
            return null;
        }

        private boolean blocked(BlockPos rel, BlockState wanted, boolean log) {
            BlockPos at = start.offset(rel);
            if (level.isOutsideBuildHeight(at)) { return true; }
            TreeEntry other = ContentTreeStore.ownerOf(level, at.asLong());
            if (other != null && other != entry) { return true; }
            BlockState now = level.getBlockState(at);
            Block block = now.getBlock();
            int packed = pack(rel);
            BlockState was = previous.get(rel);
            if (block == wanted.getBlock()) { return false; }
            if (entry.owned.contains(packed) && was != null && block == was.getBlock()) { return false; }
            BlockState sapling = entry.saplings.get(packed);
            if (sapling != null && block == sapling.getBlock()) { return false; }
            if (!log) {
                BlockState under = unders.get(packed);
                if (under != null && block == under.getBlock()) { return false; }
                if (entry.kind == TreeEntry.KIND_STRUCTURE && now.is(BlockTags.DIRT)) { return false; }
            }
            return !ContentTreeFelling.replaceable(level, at);
        }

        private void apply() {
            List<BlockPos> order = new ArrayList<>(next.keySet());
            order.sort((a, b) -> {
                int byClass = Integer.compare(rank(a), rank(b));
                return byClass != 0 ? byClass : Integer.compare(a.getY(), b.getY());
            });
            for (BlockPos rel : order) { place(rel, next.get(rel)); }
            if (entry.step == 0 && !next.containsKey(BlockPos.ZERO)) {
                BlockState sapling = entry.saplings.get(pack(BlockPos.ZERO));
                if (sapling != null && level.getBlockState(start).getBlock() == sapling.getBlock()) { TreeRecorder.write(level, start, Blocks.AIR.defaultBlockState(), 3); }
            }
            for (Map.Entry<BlockPos, BlockState> old : previous.entrySet()) { shed(old.getKey(), old.getValue()); }
            entry.step++;
            if (full) { ContentTreeStore.remove(level, entry.pos); }
            else { ContentTreeStore.dirty(level, entry); }
        }

        private int rank(BlockPos rel) {
            BlockState state = next.get(rel);
            if (TreeShape.isLog(state)) { return 0; }
            return state.is(BlockTags.LEAVES) ? 1 : 2;
        }

        private void place(BlockPos rel, BlockState wanted) {
            if (blocked(rel, wanted, TreeShape.isLog(wanted))) { return; }
            BlockPos at = start.offset(rel);
            if (level.getBlockState(at) != wanted) { TreeRecorder.write(level, at, wanted, 3); }
            int packed = pack(rel);
            if (!entry.owned.contains(packed)) { ContentTreeStore.addOwned(level, entry, packed); }
            if (!full) { return; }
            placeTile(level, at, shape.entity(rel));
            if (wanted.hasProperty(LeavesBlock.DISTANCE) && wanted.hasProperty(LeavesBlock.PERSISTENT) && !wanted.getValue(LeavesBlock.PERSISTENT)) { level.scheduleTick(at, wanted.getBlock(), 1); }
        }

        private void shed(BlockPos rel, BlockState old) {
            int packed = pack(rel);
            if (next.containsKey(rel) || !entry.owned.contains(packed)) { return; }
            BlockPos at = start.offset(rel);
            if (level.getBlockState(at).getBlock() == old.getBlock()) { TreeRecorder.write(level, at, Blocks.AIR.defaultBlockState(), 3); }
            ContentTreeStore.removeOwned(level, entry, packed);
        }
    }
}
