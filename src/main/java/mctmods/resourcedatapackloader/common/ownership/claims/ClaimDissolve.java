package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.DissolvePass;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class ClaimDissolve {
    private static final Set<Job> JOBS = new LinkedHashSet<>();

    private ClaimDissolve() {}

    private static final class Job {
        private final ResourceKey<Level> dimension;
        private final int x;
        private final int z;
        private boolean touched;
        private boolean checked;
        private boolean covered;

        private Job(ResourceKey<Level> dimension, int x, int z) {
            this.dimension = dimension;
            this.x = x;
            this.z = z;
        }

        private boolean covered(ServerLevel level) {
            if (!checked) {
                covered = ClaimRestore.covers(level, x, z);
                checked = true;
            }
            return covered;
        }

        @Override public boolean equals(Object other) {
            return other instanceof Job job && dimension == job.dimension && x == job.x && z == job.z;
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

    public static void apply(MinecraftServer server, int handle, OwnershipSettings.Dissolve mode) {
        if (mode == OwnershipSettings.Dissolve.KEEP) { return; }
        for (ServerLevel level : server.getAllLevels()) {
            ClaimIndex index = ClaimIndex.obtain(level);
            if (index == null) { continue; }
            for (ChunkPos pos : index.chunksOf(handle)) {
                index.release(pos.x, pos.z);
                if (mode == OwnershipSettings.Dissolve.REMOVE && index.addPending(pos.x, pos.z, handle, ClaimIndex.MODE_REMOVE) && loaded(level, pos.x, pos.z)) { JOBS.add(new Job(level.dimension(), pos.x, pos.z)); }
            }
            for (ClaimIndex.Anchor anchor : index.anchorList()) {
                if (anchor.h == handle) { index.removeAnchor(anchor.id); }
            }
        }
    }

    public static void onLoad(ServerLevel level, int chunkX, int chunkZ) {
        if (!ClaimSettings.any()) { return; }
        ClaimIndex index = ClaimIndex.obtain(level);
        if (index != null && index.pendingAt(chunkX, chunkZ) != null) { JOBS.add(new Job(level.dimension(), chunkX, chunkZ)); }
    }

    public static void drain(MinecraftServer server) {
        if (JOBS.isEmpty()) { return; }
        Budget budget = new Budget(ClaimSettings.restorePerTick(), OwnershipSettings.perTick());
        for (Iterator<Job> each = JOBS.iterator(); each.hasNext() && budget.sections > 0 && budget.blocks > 0; ) {
            Job job = each.next();
            ServerLevel level = server.getLevel(job.dimension);
            ClaimIndex index = ClaimIndex.obtain(level);
            ClaimIndex.Pending pending = index == null ? null : index.pendingAt(job.x, job.z);
            if (level == null || pending == null || !loaded(level, job.x, job.z) || work(level, index, pending, job, budget)) { each.remove(); }
        }
    }

    private static boolean loaded(ServerLevel level, int chunkX, int chunkZ) { return level.getChunkSource().getChunkNow(chunkX, chunkZ) != null; }

    private static boolean work(ServerLevel level, ClaimIndex index, ClaimIndex.Pending pending, Job job, Budget budget) {
        boolean replacing = ClaimSettings.restore() || ClaimSettings.regenerate();
        if (replacing && ClaimPlayers.occupied(level, job.x, job.z)) {
            if (!ClaimSettings.movePlayers()) { return false; }
            ClaimPlayers.move(level, index, job.x, job.z);
        }
        if (replacing && pending.doneIndexes().length > 0) { job.touched = true; }
        for (int sectionY : sections(level)) {
            if (pending.isDone(sectionY)) { continue; }
            if (budget.sections <= 0 || budget.blocks <= 0) { return false; }
            budget.sections--;
            if (!section(level, job, sectionY, budget)) { return false; }
            index.markDone(job.x, job.z, sectionY);
        }
        if (job.touched) { ClaimRestore.finish(level, job.x, job.z); }
        index.removePending(job.x, job.z);
        return true;
    }

    private static boolean section(ServerLevel level, Job job, int sectionY, Budget budget) {
        if (replace(level, job, sectionY)) {
            job.touched = true;
            DissolvePass.forget(level, job.x, job.z, sectionY);
            if (ClaimSettings.removeEntities()) { ClaimPlayers.removeEntities(level, job.x, job.z, sectionY); }
            return true;
        }
        budget.blocks -= DissolvePass.removeSection(level, job.x, job.z, sectionY, budget.blocks);
        return budget.blocks > 0;
    }

    private static boolean replace(ServerLevel level, Job job, int sectionY) {
        if (ClaimSettings.restore() && job.covered(level) && ClaimRestore.section(level, job.x, job.z, sectionY)) { return true; }
        if (!ClaimSettings.regenerate()) { return false; }
        ClaimRegenerate.section(level, job.x, job.z, sectionY);
        return true;
    }

    private static int[] sections(ServerLevel level) { return IntStream.range(level.getMinSection(), level.getMaxSection()).toArray(); }
}
