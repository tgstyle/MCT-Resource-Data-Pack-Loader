package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

public final class ContentTreeFelling {
    private ContentTreeFelling() {}

    static boolean replaceable(Level level, BlockPos pos) { return TreeFeature.validTreePos(level, pos); }

    public static boolean visit(ServerLevel level, TreeEntry entry, Map<BlockPos, BlockState> step, @Nullable TreeShape shape) {
        BlockPos start = BlockPos.of(entry.pos);
        if (entry.phase == 0 || entry.step == 0) {
            BlockState sapling = entry.saplings.get(TreeEntry.pack(0, 0, 0));
            if (!level.isLoaded(start)) { return true; }
            if (sapling != null && level.getBlockState(start).getBlock() != sapling.getBlock()) { fell(level, entry, start, Collections.emptyMap()); }
            return false;
        }
        Map<Integer, BlockState> expected = packed(step);
        checkOwnership(level, entry, start, expected, shape);
        return shape != null && checkFelled(level, entry, start, expected, step, shape);
    }

    public static void release(ServerLevel level, TreeEntry entry) {
        BlockPos start = BlockPos.of(entry.pos);
        IntList kept = new IntArrayList();
        for (int packed : entry.release) {
            BlockPos pos = at(start, packed);
            if (!level.isLoaded(pos)) { kept.add(packed); }
            else { loosen(level, pos, null); }
        }
        if (kept.isEmpty()) { ContentTreeStore.remove(level, entry.pos); }
        else if (kept.size() != entry.release.length) {
            entry.release = kept.toIntArray();
            ContentTreeStore.dirty(level, entry);
        }
    }

    public static boolean reset(ServerLevel level, TreeEntry entry, Map<BlockPos, BlockState> step, boolean timed) {
        BlockPos start = BlockPos.of(entry.pos);
        if (touchesUnloaded(level, entry, start)) { return false; }
        shed(level, entry, start, packed(step));
        for (Map.Entry<Integer, BlockState> sapling : entry.saplings.entrySet()) {
            BlockPos pos = at(start, sapling.getKey());
            if (replaceable(level, pos)) { TreeRecorder.write(level, pos, sapling.getValue(), 3); }
        }
        if (timed) {
            entry.phase = 0;
            entry.step = 0;
            entry.gone.clear();
            ContentTreeStore.dirty(level, entry);
        }
        else { ContentTreeStore.remove(level, entry.pos); }
        return true;
    }

    public static boolean drop(ServerLevel level, TreeEntry entry, Map<BlockPos, BlockState> step) {
        BlockPos start = BlockPos.of(entry.pos);
        if (touchesUnloaded(level, entry, start)) { return false; }
        shed(level, entry, start, packed(step));
        for (Map.Entry<Integer, BlockState> sapling : entry.saplings.entrySet()) { Block.dropResources(sapling.getValue(), level, at(start, sapling.getKey())); }
        ContentTreeStore.remove(level, entry.pos);
        return true;
    }

    private static void checkOwnership(ServerLevel level, TreeEntry entry, BlockPos start, Map<Integer, BlockState> expected, @Nullable TreeShape shape) {
        boolean changed = false;
        for (int packed : entry.owned.toIntArray()) {
            BlockState want = expected.get(packed);
            BlockPos pos = at(start, packed);
            if (want == null || !level.isLoaded(pos) || level.getBlockState(pos).getBlock() == want.getBlock()) { continue; }
            ContentTreeStore.removeOwned(level, entry, packed);
            if (shape == null || TreeShape.isLog(want) || !TreeShape.isLog(shape.whole().getOrDefault(at(BlockPos.ZERO, packed), want))) { entry.gone.add(packed); }
            changed = true;
        }
        if (changed) { ContentTreeStore.dirty(level, entry); }
    }

    private static boolean checkFelled(ServerLevel level, TreeEntry entry, BlockPos start, Map<Integer, BlockState> expected, Map<BlockPos, BlockState> step, TreeShape shape) {
        boolean skipped = false;
        BlockPos base = shape.base();
        BlockState baseLog = base == null ? null : shape.whole().get(base);
        if (baseLog != null) {
            BlockPos pos = start.offset(base);
            if (!level.isLoaded(pos)) { skipped = true; }
            else if (level.getBlockState(pos).getBlock() != baseLog.getBlock()) {
                fell(level, entry, start, expected);
                return false;
            }
        }
        for (BlockPos log : shape.trunk()) {
            BlockState wanted = step.get(log);
            if (wanted == null || !TreeShape.isLog(wanted)) { continue; }
            if (!level.isLoaded(start.offset(log))) {
                skipped = true;
                continue;
            }
            if (!entry.owned.contains(TreeEntry.pack(log.getX(), log.getY(), log.getZ()))) {
                fell(level, entry, start, expected);
                return false;
            }
        }
        return skipped;
    }

    private static void fell(ServerLevel level, TreeEntry entry, BlockPos start, Map<Integer, BlockState> expected) {
        IntList pending = new IntArrayList();
        for (int packed : entry.owned.toIntArray()) {
            BlockState want = expected.get(packed);
            if (want != null && want.hasProperty(LeavesBlock.PERSISTENT)) {
                BlockPos pos = at(start, packed);
                if (level.isLoaded(pos)) { loosen(level, pos, want.getBlock()); }
                else { pending.add(packed); }
            }
            ContentTreeStore.removeOwned(level, entry, packed);
        }
        entry.gone.clear();
        entry.phase = TreeEntry.PHASE_FELLED;
        entry.release = pending.toIntArray();
        if (pending.isEmpty()) { ContentTreeStore.remove(level, entry.pos); }
        else { ContentTreeStore.dirty(level, entry); }
    }

    private static void loosen(ServerLevel level, BlockPos pos, @Nullable Block block) {
        BlockState now = level.getBlockState(pos);
        if (block != null && now.getBlock() != block) { return; }
        if (!now.hasProperty(LeavesBlock.PERSISTENT) || !now.getValue(LeavesBlock.PERSISTENT)) { return; }
        TreeRecorder.write(level, pos, now.setValue(LeavesBlock.PERSISTENT, false), 3);
        level.scheduleTick(pos, now.getBlock(), 1);
    }

    private static void shed(ServerLevel level, TreeEntry entry, BlockPos start, Map<Integer, BlockState> expected) {
        for (int packed : entry.owned.toIntArray()) {
            BlockState want = expected.get(packed);
            BlockPos pos = at(start, packed);
            if (want != null && level.getBlockState(pos).getBlock() == want.getBlock()) { TreeRecorder.write(level, pos, Blocks.AIR.defaultBlockState(), 3); }
            ContentTreeStore.removeOwned(level, entry, packed);
        }
    }

    private static boolean touchesUnloaded(ServerLevel level, TreeEntry entry, BlockPos start) {
        for (int packed : entry.owned.toIntArray()) {
            if (!level.isLoaded(at(start, packed))) { return true; }
        }
        for (int packed : entry.saplings.keySet()) {
            if (!level.isLoaded(at(start, packed))) { return true; }
        }
        return false;
    }

    private static Map<Integer, BlockState> packed(Map<BlockPos, BlockState> step) {
        Map<Integer, BlockState> out = new HashMap<>();
        for (Map.Entry<BlockPos, BlockState> entry : step.entrySet()) { out.put(TreeEntry.pack(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()), entry.getValue()); }
        return out;
    }

    private static BlockPos at(BlockPos start, int packed) { return start.offset(TreeEntry.unpackX(packed), TreeEntry.unpackY(packed), TreeEntry.unpackZ(packed)); }
}
