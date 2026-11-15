package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreePickDef;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.util.plant.PlantWater;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.BlockSnapshot;

public final class ContentTreeGrowth {
    private static final Map<TreeEntry, Cached> CACHE = new WeakHashMap<>();

    private ContentTreeGrowth() {}

    private static final class Cached {
        private TreeShape shape;
        private TreeShape built;
        private int step = -1;
        private int gone = -1;
        private Map<BlockPos, IBlockState> current = Collections.emptyMap();
    }

    public static boolean start(WorldServer world, BlockPos pos, IBlockState sapling, String rule, TreeGrowthDef growth, @Nullable Consumer<Random> own) {
        long seed = mix(world.getSeed(), pos.toLong(), world.getTotalWorldTime());
        Random random = new Random(seed);
        TreePickDef pick = WeightedPicks.pick(growth.trees, def -> def.weight, random);
        if (pick == null) { return false; }
        Block block = sapling.getBlock();
        boolean foreign = !(block instanceof ContentSaplingBlock) && (block.getRegistryName() == null || !"minecraft".equals(block.getRegistryName().getNamespace()));
        boolean inSteps = pick.growsInSteps(foreign);
        int index = growth.trees.indexOf(pick);
        TreeEntry entry;
        if (pick.kind == TreePickDef.Kind.STRUCTURE) {
            Rotation rotation = Turns.turn(pick.turns, random);
            Mirror mirror = Turns.mirror(pick.mirrors, random);
            TreeTemplates.Built built = TreeTemplates.get(world, pick.source, rotation, mirror, growth.steps);
            if (built == null) {
                ContentLog.LOGGER.warn("Tree {}: template is missing, nothing grows at {} {} {}", pick.source, pos.getX(), pos.getY(), pos.getZ());
                return false;
            }
            boolean logged = anyLog(built.blocks);
            boolean flat = built.overBounds || built.shape == null || built.shape.noTrunk();
            if (flat || !inSteps || !logged) {
                if (built.overBounds) { ContentLog.LOGGER.warn("Tree {}: over the bounds, placed whole", pick.source); }
                else if (logged && flat) { ContentLog.LOGGER.warn("Tree {}: no trunk at the start column, placed whole", pick.source); }
                placeWhole(world, pos, built, rotation, mirror);
                return true;
            }
            entry = entryOf(world, pos, rule, index, pick, (byte) rotation.ordinal(), (byte) mirror.ordinal(), seed, built.shape.steps(), single(sapling), built.hash);
        }
        else {
            IBlockState state = pick.kind == TreePickDef.Kind.OWN ? sapling : ContentStates.parse(pick.source, block.getRegistryName());
            if (state == null) {
                ContentLog.LOGGER.warn("Tree {}: the vanilla sapling state is not known, nothing grows at {} {} {}", pick.source, pos.getX(), pos.getY(), pos.getZ());
                return false;
            }
            TreeShape[] shaped = new TreeShape[1];
            TreeRecorder.Result result = TreeRecorder.record(world, pos, state, seed, pick.source, pick.kind == TreePickDef.Kind.OWN ? own : null, found -> {
                if (found.overBounds) {
                    ContentLog.LOGGER.warn("Tree {}: over the bounds, placed whole", pick.source);
                    return true;
                }
                shaped[0] = shapeOf(found, growth.steps);
                boolean logged = anyLog(shaped[0].whole());
                if (shaped[0].noTrunk() && logged) { ContentLog.LOGGER.warn("Tree {}: no trunk at the start column, placed whole", pick.source); }
                return shaped[0].noTrunk() || !inSteps;
            });
            if (result == null) { return false; }
            if (result.placed) { return true; }
            entry = recorded(world, pos, rule, index, pick, seed, shaped[0], result);
        }
        ContentTreeStore.put(world, entry);
        for (int packed : entry.saplings.keySet()) {
            long other = pos.add(TreeEntry.unpackX(packed), TreeEntry.unpackY(packed), TreeEntry.unpackZ(packed)).toLong();
            TreeEntry waiting = other == entry.pos ? null : ContentTreeStore.get(world, other);
            if (waiting != null && waiting.phase == 0) { ContentTreeStore.remove(world, other); }
        }
        step(world, entry, growth, growth.timed());
        return true;
    }

    public static void drive(WorldServer world, BlockPos pos, IBlockState state, Random random) {
        Block block = state.getBlock();
        if (!(block instanceof IGrowable)) { return; }
        List<BlockSnapshot> list = world.capturedBlockSnapshots;
        int saved = list.size();
        boolean prior = world.captureBlockSnapshots;
        world.captureBlockSnapshots = true;
        try { ((IGrowable) block).grow(world, random, pos, state); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Tree drive at {} {} {} failed: {}", pos.getX(), pos.getY(), pos.getZ(), ex.toString());
            TreeRecorder.restore(world, saved, prior);
            return;
        }
        if (list.size() <= saved) {
            world.captureBlockSnapshots = prior;
            return;
        }
        boolean stage = true;
        for (int i = saved; i < list.size(); i++) { stage &= list.get(i).getPos().equals(pos); }
        if (stage) {
            TreeRecorder.commitWhole(world, saved, prior);
            return;
        }
        TreeRecorder.restore(world, saved, prior);
        TreeGrowthDef growth = ContentTreeRules.growth(state);
        if (growth != null && growth.growing()) { start(world, pos, state, ContentTreeRules.ruleId(state), growth, null); }
    }

    @Nullable public static String step(WorldServer world, TreeEntry entry, TreeGrowthDef growth, boolean timed) {
        String stale = stale(world, entry, growth);
        if (stale != null) { return stale; }
        TreeShape shape = shape(world, entry);
        if (shape == null) { return drop(world, entry, "template changed"); }
        int k = Math.min(entry.step + 1, entry.steps);
        Map<BlockPos, IBlockState> previous = current(world, entry);
        Map<BlockPos, IBlockState> next = shape.stepAt(k, goneSet(entry));
        Step run = new Step(world, entry, shape, previous, next, k >= entry.steps);
        if (run.unloaded()) { return "chunk not loaded"; }
        BlockPos blocked = run.noRoom();
        if (blocked != null) {
            String reason = "no room at " + blocked.getX() + " " + blocked.getY() + " " + blocked.getZ();
            if (growth.blocked == TreeGrowthDef.Blocked.WAIT) { return reason; }
            boolean done = growth.blocked == TreeGrowthDef.Blocked.RESET ? ContentTreeFelling.reset(world, entry, previous, timed) : ContentTreeFelling.drop(world, entry, previous);
            return done ? reason : "chunk not loaded";
        }
        run.apply();
        return null;
    }

    @Nullable public static String conditions(WorldServer world, TreeEntry entry, TreeGrowthDef growth) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        BlockPos base = start;
        BlockPos tip = start;
        if (entry.phase != 0) {
            TreeShape shape = shape(world, entry);
            if (shape == null || shape.base() == null) { return "template missing"; }
            BlockPos column = shape.base();
            base = start.add(column);
            int top = column.getY();
            for (BlockPos pos : current(world, entry).keySet()) {
                if (pos.getX() == column.getX() && pos.getZ() == column.getZ()) { top = Math.max(top, pos.getY()); }
            }
            tip = start.add(column.getX(), top + 1, column.getZ());
        }
        return blockedAt(world, base, tip, growth);
    }

    @Nullable public static String conditions(WorldServer world, BlockPos pos, TreeGrowthDef growth) { return blockedAt(world, pos, pos, growth); }

    public static void plant(WorldServer world, BlockPos pos, IBlockState sapling, String rule) {
        if (ContentTreeStore.get(world, pos.toLong()) != null) { return; }
        long now = world.getTotalWorldTime();
        ContentTreeStore.put(world, new TreeEntry(pos.toLong(), rule, (byte) 0, new int[0], -1, (byte) 0, "", (byte) 0, (byte) 0, 0L, 0, 0, now, now, 0L, single(sapling), new IntOpenHashSet(), new IntOpenHashSet(), 0, new ArrayList<>(), new int[0], new int[0], new int[0], new LinkedHashMap<>()));
    }

    @Nullable private static String blockedAt(WorldServer world, BlockPos base, BlockPos tip, TreeGrowthDef growth) {
        boolean above = tip.getY() >= maxY(world);
        if (!world.isBlockLoaded(base) || !above && !world.isBlockLoaded(tip)) { return "chunk not loaded"; }
        if (!BiomeNames.matches(growth.biomes, world.getBiome(base))) { return "biome"; }
        if (growth.needsWater && !PlantWater.near(world, base.down(), growth.waterRange)) { return "no water"; }
        if (growth.needsSky && !above && !world.canSeeSky(tip)) { return "no sky"; }
        if (growth.light >= 0 && !above && world.getLightFromNeighbors(tip) < growth.light) { return "no light"; }
        return null;
    }

    @Nullable public static TreeShape shape(WorldServer world, TreeEntry entry) {
        if (entry.kind == TreeEntry.KIND_STRUCTURE) {
            TreeTemplates.Built built = TreeTemplates.get(world, entry.source, Rotation.values()[entry.turn], Mirror.values()[entry.mirror], entry.steps);
            return built == null ? null : built.shape;
        }
        Cached cached = CACHE.computeIfAbsent(entry, key -> new Cached());
        if (cached.shape == null) { cached.shape = rebuild(entry); }
        return cached.shape;
    }

    public static Map<BlockPos, IBlockState> current(WorldServer world, TreeEntry entry) {
        TreeShape shape = shape(world, entry);
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

    private static boolean anyLog(Map<BlockPos, IBlockState> blocks) {
        for (Map.Entry<BlockPos, IBlockState> entry : blocks.entrySet()) {
            if (TreeShape.isLog(entry.getKey(), entry.getValue())) { return true; }
        }
        return false;
    }

    private static boolean rubic(World world) { return world instanceof IRubicWorld && ((IRubicWorld) world).rdpl$isRubicWorld(); }

    private static int maxY(World world) { return rubic(world) ? ((IRubicWorld) world).rdpl$getMaxHeight() : world.getHeight(); }

    private static boolean outOfHeight(World world, BlockPos at) { return at.getY() < (rubic(world) ? ((IRubicWorld) world).rdpl$getMinHeight() : 0) || at.getY() >= maxY(world); }

    private static Map<Integer, IBlockState> single(IBlockState sapling) {
        Map<Integer, IBlockState> saplings = new LinkedHashMap<>();
        saplings.put(TreeEntry.pack(0, 0, 0), sapling);
        return saplings;
    }

    private static Set<BlockPos> goneSet(TreeEntry entry) {
        Set<BlockPos> gone = new HashSet<>();
        for (int packed : entry.gone) { gone.add(rel(packed)); }
        return gone;
    }

    private static TreeShape shapeOf(TreeRecorder.Result result, int steps) {
        Map<BlockPos, IBlockState> blocks = new LinkedHashMap<>();
        Map<BlockPos, NBTTagCompound> tiles = new HashMap<>();
        for (int i = 0; i < result.offsets.size(); i++) {
            BlockPos at = rel(result.offsets.get(i));
            blocks.put(at, result.states.get(i));
            if (result.tiles.get(i) != null) { tiles.put(at, result.tiles.get(i)); }
        }
        return new TreeShape(blocks, tiles, steps);
    }

    private static TreeShape rebuild(TreeEntry entry) {
        Map<BlockPos, IBlockState> blocks = new LinkedHashMap<>();
        Map<BlockPos, NBTTagCompound> tiles = new HashMap<>();
        for (int i = 0; i < entry.blocks.length; i++) {
            BlockPos at = rel(entry.blocks[i]);
            blocks.put(at, entry.palette.get(entry.states[i]));
            NBTTagCompound tile = entry.blockEntities.get(i);
            if (tile != null) { tiles.put(at, tile); }
        }
        return new TreeShape(blocks, tiles, entry.steps);
    }

    private static TreeEntry entryOf(WorldServer world, BlockPos pos, String rule, int pick, TreePickDef def, byte turn, byte mirror, long seed, int steps, Map<Integer, IBlockState> saplings, int hash) {
        long now = world.getTotalWorldTime();
        return new TreeEntry(pos.toLong(), rule, (byte) 1, new int[0], pick, (byte) def.kind.ordinal(), def.source, turn, mirror, seed, 0, steps, now, now, 0L, saplings, new IntOpenHashSet(), new IntOpenHashSet(), hash, new ArrayList<>(), new int[0], new int[0], new int[0], new LinkedHashMap<>());
    }

    private static TreeEntry recorded(WorldServer world, BlockPos pos, String rule, int pick, TreePickDef def, long seed, TreeShape shape, TreeRecorder.Result result) {
        int size = result.offsets.size();
        List<IBlockState> palette = new ArrayList<>();
        int[] blocks = new int[size];
        int[] states = new int[size];
        int[] under = new int[size];
        Map<Integer, NBTTagCompound> tiles = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            blocks[i] = result.offsets.get(i);
            states[i] = paletteIndex(palette, result.states.get(i));
            under[i] = paletteIndex(palette, result.unders.get(i));
            if (result.tiles.get(i) != null) { tiles.put(i, result.tiles.get(i)); }
        }
        Map<Integer, IBlockState> saplings = new LinkedHashMap<>();
        for (int i = 0; i < result.saplingOffsets.size(); i++) { saplings.put(result.saplingOffsets.get(i), result.saplingStates.get(i)); }
        long now = world.getTotalWorldTime();
        return new TreeEntry(pos.toLong(), rule, (byte) 1, new int[0], pick, (byte) def.kind.ordinal(), def.source, (byte) 0, (byte) 0, seed, 0, shape.steps(), now, now, 0L, saplings, new IntOpenHashSet(), new IntOpenHashSet(), 0, palette, blocks, states, under, tiles);
    }

    private static int paletteIndex(List<IBlockState> palette, IBlockState state) {
        int at = palette.indexOf(state);
        if (at >= 0) { return at; }
        palette.add(state);
        return palette.size() - 1;
    }

    private static void placeWhole(WorldServer world, BlockPos start, TreeTemplates.Built built, Rotation rotation, Mirror mirror) {
        if (!built.blocks.containsKey(BlockPos.ORIGIN)) { TreeRecorder.write(world, start, Blocks.AIR.getDefaultState(), 3); }
        for (Map.Entry<BlockPos, IBlockState> block : built.blocks.entrySet()) { TreeRecorder.write(world, start.add(block.getKey()), block.getValue(), 3); }
        for (Map.Entry<BlockPos, NBTTagCompound> tile : built.blockEntities.entrySet()) { placeTile(world, start.add(tile.getKey()), tile.getValue(), rotation, mirror); }
    }

    private static void placeTile(World world, BlockPos pos, @Nullable NBTTagCompound data, Rotation rotation, Mirror mirror) {
        TileEntity tile = data == null ? null : world.getTileEntity(pos);
        if (tile == null) { return; }
        NBTTagCompound copy = data.copy();
        copy.setInteger("x", pos.getX());
        copy.setInteger("y", pos.getY());
        copy.setInteger("z", pos.getZ());
        tile.readFromNBT(copy);
        tile.mirror(mirror);
        tile.rotate(rotation);
        tile.markDirty();
    }

    @Nullable private static String stale(WorldServer world, TreeEntry entry, TreeGrowthDef growth) {
        if (entry.pick < 0 || entry.pick >= growth.trees.size() || !growth.trees.get(entry.pick).source.equals(entry.source)) { return drop(world, entry, "template changed"); }
        if (entry.kind != TreeEntry.KIND_STRUCTURE) { return null; }
        TreeTemplates.Built built = TreeTemplates.get(world, entry.source, Rotation.values()[entry.turn], Mirror.values()[entry.mirror], entry.steps);
        if (built == null) { return drop(world, entry, "template missing"); }
        return built.hash == entry.hash ? null : drop(world, entry, "template changed");
    }

    private static String drop(WorldServer world, TreeEntry entry, String reason) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        ContentLog.LOGGER.warn("Tree {} at {} {} {}: {}, so it stops growing and its blocks stay", entry.source, start.getX(), start.getY(), start.getZ(), reason);
        ContentTreeStore.remove(world, entry.pos);
        return reason;
    }

    private static final class Step {
        private final WorldServer world;
        private final TreeEntry entry;
        private final TreeShape shape;
        private final Map<BlockPos, IBlockState> previous;
        private final Map<BlockPos, IBlockState> next;
        private final boolean full;
        private final BlockPos start;
        private final Map<Integer, IBlockState> unders = new HashMap<>();
        private final boolean rubic;

        private Step(WorldServer world, TreeEntry entry, TreeShape shape, Map<BlockPos, IBlockState> previous, Map<BlockPos, IBlockState> next, boolean full) {
            this.world = world;
            this.entry = entry;
            this.shape = shape;
            this.previous = previous;
            this.next = next;
            this.full = full;
            this.start = BlockPos.fromLong(entry.pos);
            this.rubic = rubic(world);
            for (int i = 0; i < entry.under.length; i++) { unders.put(entry.blocks[i], entry.palette.get(entry.under[i])); }
        }

        private boolean unloaded() {
            Set<Long> seen = new HashSet<>();
            List<BlockPos> touched = new ArrayList<>(next.keySet());
            touched.addAll(previous.keySet());
            for (BlockPos rel : touched) {
                BlockPos at = start.add(rel);
                if (outOfHeight(world, at) || !seen.add(new BlockPos(at.getX() >> 4, rubic ? at.getY() >> 4 : 0, at.getZ() >> 4).toLong())) { continue; }
                if (!world.isBlockLoaded(at) || !populated(at)) { return true; }
            }
            return false;
        }

        private boolean populated(BlockPos at) {
            if (!rubic) { return world.getChunk(at).isPopulated(); }
            ICube cube = ((IRubicWorld) world).rdpl$getCubeCache().getLoadedCube(at.getX() >> 4, at.getY() >> 4, at.getZ() >> 4);
            return cube instanceof Cube && ((Cube) cube).isPopulated();
        }

        @Nullable private BlockPos noRoom() {
            for (Map.Entry<BlockPos, IBlockState> block : next.entrySet()) {
                IBlockState was = previous.get(block.getKey());
                if (was != null && TreeShape.isLog(block.getKey(), was) || !TreeShape.isLog(block.getKey(), block.getValue())) { continue; }
                if (blocked(block.getKey(), block.getValue(), true)) { return start.add(block.getKey()); }
            }
            return null;
        }

        private boolean blocked(BlockPos rel, IBlockState wanted, boolean log) {
            BlockPos at = start.add(rel);
            if (outOfHeight(world, at) || ContentPlacer.keepsAny(at.getX(), at.getZ(), at.getX(), at.getZ())) { return true; }
            TreeEntry other = ContentTreeStore.ownerOf(world, at.toLong());
            if (other != null && other != entry) { return true; }
            IBlockState now = world.getBlockState(at);
            Block block = now.getBlock();
            int packed = pack(rel);
            IBlockState was = previous.get(rel);
            if (block == wanted.getBlock()) { return false; }
            if (entry.owned.contains(packed) && was != null && block == was.getBlock()) { return false; }
            IBlockState sapling = entry.saplings.get(packed);
            if (sapling != null && block == sapling.getBlock()) { return false; }
            if (log) { return !ContentTreeFelling.replaceable(world, at); }
            IBlockState under = unders.get(packed);
            if (under != null && block == under.getBlock()) { return false; }
            if (entry.kind == TreeEntry.KIND_STRUCTURE && (block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.MYCELIUM)) { return false; }
            return !(block.isAir(now, world, at) || block.isReplaceable(world, at) || block.isLeaves(now, world, at) || now.getMaterial() == Material.VINE);
        }

        private IBlockState target(BlockPos rel, IBlockState wanted) {
            if (!full || TreeShape.isLog(rel, wanted) || !wanted.getPropertyKeys().contains(BlockLeaves.DECAYABLE) || !wanted.getValue(BlockLeaves.DECAYABLE) || !wanted.getPropertyKeys().contains(BlockLeaves.CHECK_DECAY)) { return wanted; }
            return wanted.withProperty(BlockLeaves.CHECK_DECAY, true);
        }

        private void apply() {
            List<BlockPos> order = new ArrayList<>(next.keySet());
            order.sort((a, b) -> {
                int byClass = Integer.compare(rank(a), rank(b));
                return byClass != 0 ? byClass : Integer.compare(a.getY(), b.getY());
            });
            for (BlockPos rel : order) { place(rel, next.get(rel)); }
            if (entry.step == 0 && !next.containsKey(BlockPos.ORIGIN)) {
                IBlockState sapling = entry.saplings.get(pack(BlockPos.ORIGIN));
                if (sapling != null && world.getBlockState(start).getBlock() == sapling.getBlock()) { TreeRecorder.write(world, start, Blocks.AIR.getDefaultState(), 3); }
            }
            for (Map.Entry<BlockPos, IBlockState> old : previous.entrySet()) { shed(old.getKey(), old.getValue()); }
            entry.step++;
            if (full) { ContentTreeStore.remove(world, entry.pos); }
            else { ContentTreeStore.dirty(world, entry); }
        }

        private int rank(BlockPos rel) {
            IBlockState state = next.get(rel);
            if (TreeShape.isLog(rel, state)) { return 0; }
            return state.getBlock().isLeaves(state, new TreeBlockAccess(rel, state), rel) ? 1 : 2;
        }

        private void place(BlockPos rel, IBlockState wanted) {
            if (blocked(rel, wanted, TreeShape.isLog(rel, wanted))) { return; }
            BlockPos at = start.add(rel);
            IBlockState goal = target(rel, wanted);
            if (world.getBlockState(at) != goal) { TreeRecorder.write(world, at, goal, 3); }
            int packed = pack(rel);
            if (!entry.owned.contains(packed)) { ContentTreeStore.addOwned(world, entry, packed); }
            if (full) { placeTile(world, at, shape.entity(rel), Rotation.values()[entry.turn], Mirror.values()[entry.mirror]); }
        }

        private void shed(BlockPos rel, IBlockState old) {
            int packed = pack(rel);
            if (next.containsKey(rel) || !entry.owned.contains(packed)) { return; }
            BlockPos at = start.add(rel);
            if (world.getBlockState(at).getBlock() == old.getBlock()) { TreeRecorder.write(world, at, Blocks.AIR.getDefaultState(), 3); }
            ContentTreeStore.removeOwned(world, entry, packed);
        }
    }
}
