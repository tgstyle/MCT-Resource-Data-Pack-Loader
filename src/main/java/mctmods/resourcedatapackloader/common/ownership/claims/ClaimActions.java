package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.portways.Portways;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public final class ClaimActions {
    public static final String NO_CLAIMANT = "rdpl.claim.noclaimant";
    public static final String DONE = "rdpl.claim.done";
    public static final String RADIUS_DONE = "rdpl.claim.radius.done";
    public static final String RADIUS_NONE = "rdpl.claim.radius.none";
    public static final String RADIUS_LIMIT = "rdpl.claim.radius.limit";
    public static final String UNCLAIM_DONE = "rdpl.claim.unclaim.done";
    public static final String UNCLAIM_FREE = "rdpl.claim.unclaim.free";
    public static final String UNCLAIM_NOT_YOURS = "rdpl.claim.unclaim.notyours";
    public static final String UNCLAIM_RADIUS = "rdpl.claim.unclaim.radius";
    public static final String UNCLAIM_RADIUS_NONE = "rdpl.claim.unclaim.radiusnone";
    public static final String UNCLAIM_ALL = "rdpl.claim.unclaim.all";
    public static final String UNCLAIM_ALL_NONE = "rdpl.claim.unclaim.allnone";
    public static final String LIST_HEAD = "rdpl.claim.list.head";
    public static final String LIST_NONE = "rdpl.claim.list.none";
    public static final String LIST_DIMENSION = "rdpl.claim.list.dimension";

    private ClaimActions() {}

    public static final class Result {
        public final boolean ok;
        public final String key;
        public final Object[] args;

        private Result(boolean ok, String key, Object[] args) {
            this.ok = ok;
            this.key = key;
            this.args = args;
        }

        static Result yes(String key, Object... args) { return new Result(true, key, args); }

        static Result no(String key, Object... args) { return new Result(false, key, args); }
    }

    public static Result claim(EntityPlayer player, World world, int chunkX, int chunkZ, int via) {
        int handle = ClaimOwners.claimantOf(player);
        return handle == 0 ? Result.no(NO_CLAIMANT) : take(world, chunkX, chunkZ, handle, via, false);
    }

    public static Result claimFor(World world, int chunkX, int chunkZ, int handle) { return take(world, chunkX, chunkZ, handle, 0, true); }

    public static Result claimRadius(EntityPlayer player, World world, int chunkX, int chunkZ, int radius) {
        if (radius <= 0) { return claim(player, world, chunkX, chunkZ, 0); }
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return Result.no(NO_CLAIMANT); }
        if (ClaimSettings.dimensionBarred(world.provider.getDimension())) { return Result.no(ClaimRules.DIMENSION); }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index == null) { return Result.no(ClaimRules.DIMENSION); }
        List<ChunkPos> open = new ArrayList<>();
        int taken = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (index.ownerAt(chunkX + dx, chunkZ + dz) != 0 || index.pendingAt(chunkX + dx, chunkZ + dz) != null) { taken++; }
                else { open.add(new ChunkPos(chunkX + dx, chunkZ + dz)); }
            }
        }
        String owner = ClaimOwners.labelOf(world.getMinecraftServer(), handle);
        if (open.isEmpty()) { return Result.no(RADIUS_NONE); }
        int room = ClaimRules.limitFor(world.getMinecraftServer(), handle) - index.claimCountOf(handle);
        if (open.size() > room) { return Result.no(RADIUS_LIMIT, open.size(), owner, Math.max(room, 0)); }
        open.sort(Comparator.comparingInt(pos -> Math.max(Math.abs(pos.x - chunkX), Math.abs(pos.z - chunkZ))));
        int claimed = claimPasses(world, index, open, handle, 0);
        if (claimed == 0) {
            String why = ClaimRules.canClaim(world, open.get(0).x, open.get(0).z, handle, false);
            return Result.no(why == null ? ClaimRules.TAKEN : why);
        }
        return Result.yes(RADIUS_DONE, claimed, owner, taken, open.size(), index.claimCountOf(handle), ClaimRules.limitFor(world.getMinecraftServer(), handle));
    }

    public static Result unclaim(EntityPlayer player, World world, int chunkX, int chunkZ) {
        ClaimIndex index = ClaimIndex.obtain(world);
        int owner = index == null ? 0 : index.ownerAt(chunkX, chunkZ);
        if (owner == 0) { return Result.no(UNCLAIM_FREE); }
        String label = ClaimOwners.labelOf(world.getMinecraftServer(), owner);
        if (owner != ClaimOwners.claimantOf(player) && !ClaimOwners.bypasses(player)) { return Result.no(UNCLAIM_NOT_YOURS, label); }
        index.release(chunkX, chunkZ);
        return Result.yes(UNCLAIM_DONE, chunkX, chunkZ, label);
    }

    public static Result unclaimRadius(EntityPlayer player, World world, int chunkX, int chunkZ, int radius) {
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return Result.no(NO_CLAIMANT); }
        String label = ClaimOwners.labelOf(world.getMinecraftServer(), handle);
        ClaimIndex index = ClaimIndex.obtain(world);
        int released = 0;
        if (index != null) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (index.ownerAt(chunkX + dx, chunkZ + dz) == handle) {
                        index.release(chunkX + dx, chunkZ + dz);
                        released++;
                    }
                }
            }
        }
        return released == 0 ? Result.no(UNCLAIM_RADIUS_NONE, label) : Result.yes(UNCLAIM_RADIUS, released, label);
    }

    public static Result unclaimAll(EntityPlayer player) {
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return Result.no(NO_CLAIMANT); }
        String label = ClaimOwners.labelOf(player.getServer(), handle);
        int released = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            ClaimIndex index = ClaimIndex.obtain(world);
            if (index == null) { continue; }
            for (ChunkPos pos : index.chunksOf(handle)) {
                index.release(pos.x, pos.z);
                released++;
            }
        }
        return released == 0 ? Result.no(UNCLAIM_ALL_NONE, label) : Result.yes(UNCLAIM_ALL, released, label);
    }

    public static List<Result> listFor(@Nullable MinecraftServer server, int handle) {
        String label = ClaimOwners.labelOf(server, handle);
        List<Result> lines = new ArrayList<>();
        int total = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            ClaimIndex index = ClaimIndex.obtain(world);
            List<ChunkPos> chunks = index == null ? new ArrayList<>() : index.chunksOf(handle);
            if (chunks.isEmpty()) { continue; }
            total += chunks.size();
            lines.add(Result.yes(LIST_DIMENSION, Portways.dimensionName(world.provider.getDimension()), runs(chunks)));
        }
        if (total == 0) { return Collections.singletonList(Result.yes(LIST_NONE, label)); }
        lines.add(0, Result.yes(LIST_HEAD, label, total, ClaimRules.limitFor(server, handle)));
        return lines;
    }

    private static Result take(World world, int chunkX, int chunkZ, int handle, int via, boolean op) {
        String denied = ClaimRules.canClaim(world, chunkX, chunkZ, handle, op);
        if (denied != null) { return Result.no(denied); }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index == null || !index.claim(chunkX, chunkZ, handle, via)) { return Result.no(ClaimRules.TAKEN); }
        MinecraftServer server = world.getMinecraftServer();
        return Result.yes(DONE, chunkX, chunkZ, ClaimOwners.labelOf(server, handle), index.claimCountOf(handle), ClaimRules.limitFor(server, handle));
    }

    public static int claimOpen(World world, ClaimIndex index, List<ChunkPos> open, int handle, int via) { return claimPasses(world, index, open, handle, via); }

    private static int claimPasses(World world, ClaimIndex index, List<ChunkPos> open, int handle, int via) {
        int claimed = 0;
        boolean progress = true;
        while (progress && !open.isEmpty()) {
            progress = false;
            for (Iterator<ChunkPos> each = open.iterator(); each.hasNext(); ) {
                ChunkPos pos = each.next();
                if (ClaimRules.canClaim(world, pos.x, pos.z, handle, false) == null && index.claim(pos.x, pos.z, handle, via)) {
                    each.remove();
                    claimed++;
                    progress = true;
                }
            }
        }
        return claimed;
    }

    private static String runs(List<ChunkPos> chunks) {
        chunks.sort(Comparator.<ChunkPos>comparingInt(pos -> pos.z).thenComparingInt(pos -> pos.x));
        StringBuilder text = new StringBuilder();
        int index = 0;
        while (index < chunks.size()) {
            ChunkPos first = chunks.get(index);
            int end = index;
            while (end + 1 < chunks.size() && chunks.get(end + 1).z == first.z && chunks.get(end + 1).x == chunks.get(end).x + 1) { end++; }
            if (text.length() > 0) { text.append(' '); }
            text.append('(').append(first.x);
            if (end > index) { text.append("..").append(chunks.get(end).x); }
            text.append(", ").append(first.z).append(')');
            index = end + 1;
        }
        return text.toString();
    }
}
