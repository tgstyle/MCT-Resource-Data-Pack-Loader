package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.util.plant.LeafDecay;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public final class ContentTreeFelling {
    private static final WorldGenAbstractTree TEST = new ContentTreeGenerator(false, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Collections.emptySet());

    private ContentTreeFelling() {}

    static boolean replaceable(World world, BlockPos pos) { return world.isAirBlock(pos) || TEST.isReplaceable(world, pos); }

    public static boolean visit(WorldServer world, TreeEntry entry, Map<BlockPos, IBlockState> step, @Nullable TreeShape shape) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        if (entry.phase == TreeEntry.PHASE_SAPLING || entry.step == 0) {
            IBlockState sapling = entry.originSapling();
            if (!world.isBlockLoaded(start)) { return true; }
            if (sapling != null && world.getBlockState(start).getBlock() != sapling.getBlock()) { fell(world, entry, start, Collections.emptyMap()); }
            return false;
        }
        Map<Integer, IBlockState> expected = packed(step);
        checkOwnership(world, entry, start, expected, shape);
        return shape != null && checkFelled(world, entry, start, expected, step, shape);
    }

    public static void release(WorldServer world, TreeEntry entry) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        IntList kept = new IntArrayList();
        for (int packed : entry.release) {
            BlockPos pos = TreeEntry.at(start, packed);
            if (!world.isBlockLoaded(pos)) { kept.add(packed); }
            else { loosen(world, pos, null); }
        }
        if (kept.isEmpty()) { ContentTreeStore.remove(world, entry.pos); }
        else if (kept.size() != entry.release.length) {
            entry.release = kept.toIntArray();
            ContentTreeStore.dirty(world, entry);
        }
    }

    public static boolean reset(WorldServer world, TreeEntry entry, Map<BlockPos, IBlockState> step, boolean timed) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        if (touchesUnloaded(world, entry, start)) { return false; }
        shed(world, entry, start, packed(step));
        for (Map.Entry<Integer, IBlockState> sapling : entry.saplings.entrySet()) {
            BlockPos pos = TreeEntry.at(start, sapling.getKey());
            if (replaceable(world, pos)) { TreeRecorder.write(world, pos, sapling.getValue(), BlockFlags.UPDATE_ALL); }
        }
        if (timed) {
            entry.phase = TreeEntry.PHASE_SAPLING;
            entry.step = 0;
            entry.gone.clear();
            ContentTreeStore.dirty(world, entry);
        }
        else { ContentTreeStore.remove(world, entry.pos); }
        return true;
    }

    public static boolean drop(WorldServer world, TreeEntry entry, Map<BlockPos, IBlockState> step) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        if (touchesUnloaded(world, entry, start)) { return false; }
        shed(world, entry, start, packed(step));
        for (Map.Entry<Integer, IBlockState> sapling : entry.saplings.entrySet()) {
            IBlockState state = sapling.getValue();
            state.getBlock().dropBlockAsItem(world, TreeEntry.at(start, sapling.getKey()), state, 0);
        }
        ContentTreeStore.remove(world, entry.pos);
        return true;
    }

    private static void checkOwnership(WorldServer world, TreeEntry entry, BlockPos start, Map<Integer, IBlockState> expected, @Nullable TreeShape shape) {
        boolean changed = false;
        for (int packed : entry.owned.toIntArray()) {
            IBlockState want = expected.get(packed);
            BlockPos pos = TreeEntry.at(start, packed);
            if (want == null || !world.isBlockLoaded(pos) || world.getBlockState(pos).getBlock() == want.getBlock()) { continue; }
            ContentTreeStore.removeOwned(world, entry, packed);
            BlockPos rel = TreeEntry.unpack(packed);
            IBlockState last = shape == null ? null : shape.whole().get(rel);
            if (last == null || !TreeShape.isLog(rel, last) || TreeShape.isLog(rel, want)) { entry.gone.add(packed); }
            changed = true;
        }
        if (changed) { ContentTreeStore.dirty(world, entry); }
    }

    private static boolean checkFelled(WorldServer world, TreeEntry entry, BlockPos start, Map<Integer, IBlockState> expected, Map<BlockPos, IBlockState> step, TreeShape shape) {
        boolean skipped = false;
        BlockPos base = shape.base();
        IBlockState baseLog = base == null ? null : shape.whole().get(base);
        if (baseLog != null) {
            BlockPos pos = start.add(base);
            if (!world.isBlockLoaded(pos)) { skipped = true; }
            else if (world.getBlockState(pos).getBlock() != baseLog.getBlock()) {
                fell(world, entry, start, expected);
                return false;
            }
        }
        for (BlockPos log : shape.trunk()) {
            IBlockState wanted = step.get(log);
            if (wanted == null || !TreeShape.isLog(log, wanted)) { continue; }
            if (!world.isBlockLoaded(start.add(log))) {
                skipped = true;
                continue;
            }
            if (!entry.owned.contains(TreeEntry.pack(log))) {
                fell(world, entry, start, expected);
                return false;
            }
        }
        return skipped;
    }

    private static void fell(WorldServer world, TreeEntry entry, BlockPos start, Map<Integer, IBlockState> expected) {
        IntList pending = new IntArrayList();
        for (int packed : entry.owned.toIntArray()) {
            IBlockState want = expected.get(packed);
            if (want != null && LeafDecay.decays(want)) {
                BlockPos pos = TreeEntry.at(start, packed);
                if (world.isBlockLoaded(pos)) { loosen(world, pos, want.getBlock()); }
                else { pending.add(packed); }
            }
            ContentTreeStore.removeOwned(world, entry, packed);
        }
        entry.gone.clear();
        entry.phase = TreeEntry.PHASE_FELLED;
        entry.release = pending.toIntArray();
        if (pending.isEmpty()) { ContentTreeStore.remove(world, entry.pos); }
        else { ContentTreeStore.dirty(world, entry); }
    }

    private static void loosen(WorldServer world, BlockPos pos, @Nullable Block block) {
        IBlockState now = world.getBlockState(pos);
        if (block != null && now.getBlock() != block) { return; }
        if (!LeafDecay.decays(now) || now.getValue(BlockLeaves.DECAYABLE)) { return; }
        TreeRecorder.write(world, pos, LeafDecay.loose(now), BlockFlags.UPDATE_ALL);
    }

    private static void shed(WorldServer world, TreeEntry entry, BlockPos start, Map<Integer, IBlockState> expected) {
        for (int packed : entry.owned.toIntArray()) {
            IBlockState want = expected.get(packed);
            BlockPos pos = TreeEntry.at(start, packed);
            if (want != null && world.getBlockState(pos).getBlock() == want.getBlock()) { TreeRecorder.clear(world, pos); }
            ContentTreeStore.removeOwned(world, entry, packed);
        }
    }

    private static boolean touchesUnloaded(WorldServer world, TreeEntry entry, BlockPos start) {
        for (int packed : entry.owned.toIntArray()) {
            if (!world.isBlockLoaded(TreeEntry.at(start, packed))) { return true; }
        }
        for (int packed : entry.saplings.keySet()) {
            if (!world.isBlockLoaded(TreeEntry.at(start, packed))) { return true; }
        }
        return false;
    }

    private static Map<Integer, IBlockState> packed(Map<BlockPos, IBlockState> step) {
        Map<Integer, IBlockState> out = new HashMap<>();
        for (Map.Entry<BlockPos, IBlockState> entry : step.entrySet()) { out.put(TreeEntry.pack(entry.getKey()), entry.getValue()); }
        return out;
    }
}
