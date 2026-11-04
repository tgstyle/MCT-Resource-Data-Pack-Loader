package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.DissolvePass;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

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

        private boolean covered(ServerLevel world) {
            if (!checked) {
                covered = ClaimRestore.covers(world, x, z);
                checked = true;
            }
            return covered;
        }

        @Override public boolean equals(Object other) { return other instanceof Job job && dimension.equals(job.dimension) && x == job.x && z == job.z; }

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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || mode == OwnershipSettings.Dissolve.KEEP) { return; }
        for (ServerLevel world : server.getAllLevels()) {
            ClaimIndex index = ClaimIndex.obtain(world);
            for (ChunkPos pos : index.chunksOf(handle)) {
                index.release(pos.x(), pos.z());
                if (mode == OwnershipSettings.Dissolve.REMOVE && index.addPending(pos.x(), pos.z(), handle, ClaimIndex.MODE_REMOVE) && world.getChunkSource().getChunkNow(pos.x(), pos.z()) != null) { JOBS.add(new Job(world.dimension(), pos.x(), pos.z())); }
            }
            for (ClaimIndex.Anchor anchor : index.anchorList()) {
                if (anchor.h == handle) { index.removeAnchor(anchor.id); }
            }
        }
    }

    public static void onLoad(ServerLevel world, int chunkX, int chunkZ) {
        if (!ClaimSettings.any()) { return; }
        if (ClaimIndex.obtain(world).pendingAt(chunkX, chunkZ) != null) { JOBS.add(new Job(world.dimension(), chunkX, chunkZ)); }
    }

    public static void drain(MinecraftServer server) {
        if (JOBS.isEmpty()) { return; }
        Budget budget = new Budget(ClaimSettings.restorePerTick(), OwnershipSettings.perTick());
        for (Iterator<Job> each = JOBS.iterator(); each.hasNext() && budget.sections > 0 && budget.blocks > 0; ) {
            Job job = each.next();
            ServerLevel world = server.getLevel(job.dimension);
            ClaimIndex index = world == null ? null : ClaimIndex.obtain(world);
            ClaimIndex.Pending pending = index == null ? null : index.pendingAt(job.x, job.z);
            if (pending == null || world.getChunkSource().getChunkNow(job.x, job.z) == null || work(world, index, pending, job, budget)) { each.remove(); }
        }
    }

    private static boolean work(ServerLevel world, ClaimIndex index, ClaimIndex.Pending pending, Job job, Budget budget) {
        boolean replacing = ClaimSettings.restore() || ClaimSettings.regenerate();
        if (replacing && ClaimPlayers.occupied(world, job.x, job.z)) {
            if (!ClaimSettings.movePlayers()) { return false; }
            ClaimPlayers.move(world, index, job.x, job.z);
        }
        if (replacing && pending.doneIndexes().length > 0) { job.touched = true; }
        for (int sectionY : sections(world)) {
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

    private static boolean section(ServerLevel world, Job job, int sectionY, Budget budget) {
        if (replace(world, job, sectionY)) {
            job.touched = true;
            DissolvePass.forget(world, job.x, job.z, sectionY);
            if (ClaimSettings.removeEntities()) { ClaimPlayers.removeEntities(world, job.x, job.z, sectionY); }
            return true;
        }
        budget.blocks -= DissolvePass.removeSection(world, job.x, job.z, sectionY, budget.blocks);
        return budget.blocks > 0;
    }

    private static boolean replace(ServerLevel world, Job job, int sectionY) {
        if (ClaimSettings.restore() && job.covered(world) && ClaimRestore.section(world, job.x, job.z, sectionY)) { return true; }
        if (!ClaimSettings.regenerate()) { return false; }
        ClaimRegenerate.section(world, job.x, job.z, sectionY);
        return true;
    }

    private static int[] sections(ServerLevel world) {
        int[] all = new int[world.getSectionsCount()];
        for (int index = 0; index < all.length; index++) { all[index] = world.getSectionYFromSectionIndex(index); }
        return all;
    }
}
