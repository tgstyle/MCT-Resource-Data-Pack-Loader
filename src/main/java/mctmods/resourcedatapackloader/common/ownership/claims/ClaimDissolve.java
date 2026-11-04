package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.DissolvePass;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public final class ClaimDissolve {
    private static final int SECTIONS = 16;
    private static final Set<Job> JOBS = new LinkedHashSet<>();

    private ClaimDissolve() {}

    private static final class Job {
        private final int dimension;
        private final int x;
        private final int z;
        private boolean touched;
        private boolean checked;
        private boolean covered;

        private Job(int dimension, int x, int z) {
            this.dimension = dimension;
            this.x = x;
            this.z = z;
        }

        private boolean covered(WorldServer world) {
            if (!checked) {
                covered = ClaimRestore.covers(world, x, z);
                checked = true;
            }
            return covered;
        }

        @Override public boolean equals(Object other) {
            if (!(other instanceof Job)) { return false; }
            Job job = (Job) other;
            return dimension == job.dimension && x == job.x && z == job.z;
        }

        @Override public int hashCode() { return Objects.hash(dimension, x, z); }
    }

    private static final class Budget {
        private int sections;
        private int blocks;

        private Budget(int sections, int blocks) {
            this.sections = sections;
            this.blocks = blocks;
        }
    }

    public static void apply(int handle, OwnershipSettings.Dissolve mode) {
        if (mode == OwnershipSettings.Dissolve.KEEP) { return; }
        for (WorldServer world : DimensionManager.getWorlds()) {
            ClaimIndex index = ClaimIndex.obtain(world);
            if (index == null) { continue; }
            for (ChunkPos pos : index.chunksOf(handle)) {
                index.release(pos.x, pos.z);
                if (mode == OwnershipSettings.Dissolve.REMOVE && index.addPending(pos.x, pos.z, handle, ClaimIndex.MODE_REMOVE) && world.getChunkProvider().getLoadedChunk(pos.x, pos.z) != null) { JOBS.add(new Job(world.provider.getDimension(), pos.x, pos.z)); }
            }
            for (ClaimIndex.Anchor anchor : index.anchorList()) {
                if (anchor.h == handle) { index.removeAnchor(anchor.id); }
            }
        }
    }

    public static void onLoad(World world, int chunkX, int chunkZ) {
        if (world.isRemote || !ClaimSettings.any()) { return; }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index != null && index.pendingAt(chunkX, chunkZ) != null) { JOBS.add(new Job(world.provider.getDimension(), chunkX, chunkZ)); }
    }

    public static void drain() {
        if (JOBS.isEmpty()) { return; }
        Budget budget = new Budget(ClaimSettings.restorePerTick(), OwnershipSettings.perTick());
        for (Iterator<Job> each = JOBS.iterator(); each.hasNext() && budget.sections > 0 && budget.blocks > 0; ) {
            Job job = each.next();
            WorldServer world = DimensionManager.getWorld(job.dimension);
            ClaimIndex index = ClaimIndex.obtain(world);
            ClaimIndex.Pending pending = index == null ? null : index.pendingAt(job.x, job.z);
            if (world == null || pending == null || world.getChunkProvider().getLoadedChunk(job.x, job.z) == null || work(world, index, pending, job, budget)) { each.remove(); }
        }
    }

    private static boolean work(WorldServer world, ClaimIndex index, ClaimIndex.Pending pending, Job job, Budget budget) {
        boolean replacing = ClaimSettings.restore() || ClaimSettings.regenerate();
        if (replacing && ClaimPlayers.occupied(world, job.x, job.z)) {
            if (!ClaimSettings.movePlayers()) { return false; }
            ClaimPlayers.move(world, index, job.x, job.z);
        }
        if (replacing && pending.doneIndexes().length > 0) { job.touched = true; }
        for (int sectionY : sections(world, job)) {
            if (pending.isDone(sectionY)) { continue; }
            if (budget.sections <= 0 || budget.blocks <= 0) { return false; }
            budget.sections--;
            if (!section(world, job, sectionY, budget)) { return false; }
            index.markDone(job.x, job.z, sectionY);
        }
        if (job.touched) { ClaimRestore.finish(world, job.x, job.z); }
        index.removePending(job.x, job.z);
        return true;
    }

    private static boolean section(WorldServer world, Job job, int sectionY, Budget budget) {
        if (replace(world, job, sectionY)) {
            job.touched = true;
            DissolvePass.forget(world, job.x, job.z, sectionY);
            if (ClaimSettings.removeEntities()) { ClaimPlayers.removeEntities(world, job.x, job.z, sectionY); }
            return true;
        }
        budget.blocks -= DissolvePass.removeSection(world, job.x, job.z, sectionY, budget.blocks);
        return budget.blocks > 0;
    }

    private static boolean replace(WorldServer world, Job job, int sectionY) {
        if (ClaimSettings.restore() && job.covered(world) && ClaimRestore.section(world, job.x, job.z, sectionY)) { return true; }
        if (!ClaimSettings.regenerate()) { return false; }
        ClaimRegenerate.section(world, job.x, job.z, sectionY);
        return true;
    }

    private static int[] sections(WorldServer world, Job job) {
        if (!GenHeights.rubic(world)) {
            int[] all = new int[SECTIONS];
            for (int index = 0; index < SECTIONS; index++) { all[index] = index; }
            return all;
        }
        @Nullable Chunk column = world.getChunkProvider().getLoadedChunk(job.x, job.z);
        if (column == null) { return new int[0]; }
        return ((IColumn) column).getLoadedCubes().stream().mapToInt(ClaimDissolve::cubeY).sorted().toArray();
    }

    private static int cubeY(ICube cube) { return cube.getCoords().getY(); }
}
