package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.portways.Portways;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

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

    public record Result(boolean ok, String key, List<Object> args) {
        static Result yes(String key, Object... args) { return new Result(true, key, List.of(args)); }

        static Result no(String key, Object... args) { return new Result(false, key, List.of(args)); }

        public MutableComponent text() { return Component.translatable(key, args.toArray()); }
    }

    public static Result claim(ServerPlayer player, ServerLevel level, int chunkX, int chunkZ, int via) {
        int handle = ClaimOwners.claimantOf(player);
        return handle == 0 ? Result.no(NO_CLAIMANT) : take(level, chunkX, chunkZ, handle, via, false);
    }

    public static Result claimFor(ServerLevel level, int chunkX, int chunkZ, int handle) { return take(level, chunkX, chunkZ, handle, 0, true); }

    public static Result claimRadius(ServerPlayer player, ServerLevel level, int chunkX, int chunkZ, int radius) {
        if (radius <= 0) { return claim(player, level, chunkX, chunkZ, 0); }
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return Result.no(NO_CLAIMANT); }
        if (ClaimSettings.dimensionBarred(level.dimension())) { return Result.no(ClaimRules.DIMENSION); }
        ClaimIndex index = ClaimIndex.obtain(level);
        List<ChunkPos> open = new ArrayList<>();
        int taken = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (index.ownerAt(chunkX + dx, chunkZ + dz) != 0 || index.pendingAt(chunkX + dx, chunkZ + dz) != null) { taken++; }
                else { open.add(new ChunkPos(chunkX + dx, chunkZ + dz)); }
            }
        }
        MinecraftServer server = level.getServer();
        String owner = ClaimOwners.labelOf(server, handle);
        if (open.isEmpty()) { return Result.no(RADIUS_NONE); }
        int room = ClaimRules.limitFor(server, handle) - index.claimCountOf(handle);
        if (open.size() > room) { return Result.no(RADIUS_LIMIT, open.size(), owner, Math.max(room, 0)); }
        open.sort(Comparator.comparingInt(pos -> Math.max(Math.abs(pos.x() - chunkX), Math.abs(pos.z() - chunkZ))));
        int claimed = claimOpen(level, index, open, handle, 0);
        if (claimed == 0) {
            String why = ClaimRules.canClaim(level, open.getFirst().x(), open.getFirst().z(), handle, false);
            return Result.no(why == null ? ClaimRules.TAKEN : why);
        }
        return Result.yes(RADIUS_DONE, claimed, owner, taken, open.size(), index.claimCountOf(handle), ClaimRules.limitFor(server, handle));
    }

    public static Result unclaim(ServerPlayer player, ServerLevel level, int chunkX, int chunkZ) {
        ClaimIndex index = ClaimIndex.obtain(level);
        int owner = index.ownerAt(chunkX, chunkZ);
        if (owner == 0) { return Result.no(UNCLAIM_FREE); }
        String label = ClaimOwners.labelOf(level.getServer(), owner);
        if (owner != ClaimOwners.claimantOf(player) && !ClaimOwners.bypasses(player)) { return Result.no(UNCLAIM_NOT_YOURS, label); }
        index.release(chunkX, chunkZ);
        return Result.yes(UNCLAIM_DONE, chunkX, chunkZ, label);
    }

    public static Result unclaimRadius(ServerPlayer player, ServerLevel level, int chunkX, int chunkZ, int radius) {
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return Result.no(NO_CLAIMANT); }
        String label = ClaimOwners.labelOf(level.getServer(), handle);
        ClaimIndex index = ClaimIndex.obtain(level);
        int released = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (index.ownerAt(chunkX + dx, chunkZ + dz) == handle) {
                    index.release(chunkX + dx, chunkZ + dz);
                    released++;
                }
            }
        }
        return released == 0 ? Result.no(UNCLAIM_RADIUS_NONE, label) : Result.yes(UNCLAIM_RADIUS, released, label);
    }

    public static Result unclaimAll(ServerPlayer player) {
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return Result.no(NO_CLAIMANT); }
        MinecraftServer server = player.level().getServer();
        String label = ClaimOwners.labelOf(server, handle);
        int released = 0;
        for (ServerLevel level : server.getAllLevels()) {
            ClaimIndex index = ClaimIndex.obtain(level);
            for (ChunkPos pos : index.chunksOf(handle)) {
                index.release(pos.x(), pos.z());
                released++;
            }
        }
        return released == 0 ? Result.no(UNCLAIM_ALL_NONE, label) : Result.yes(UNCLAIM_ALL, released, label);
    }

    public static List<Result> listFor(MinecraftServer server, int handle) {
        String label = ClaimOwners.labelOf(server, handle);
        List<Result> lines = new ArrayList<>();
        int total = 0;
        for (ServerLevel level : server.getAllLevels()) {
            List<ChunkPos> chunks = ClaimIndex.obtain(level).chunksOf(handle);
            if (chunks.isEmpty()) { continue; }
            total += chunks.size();
            lines.add(Result.yes(LIST_DIMENSION, Portways.dimensionName(level.dimension()), runs(chunks)));
        }
        if (total == 0) { return List.of(Result.yes(LIST_NONE, label)); }
        lines.addFirst(Result.yes(LIST_HEAD, label, total, ClaimRules.limitFor(server, handle)));
        return lines;
    }

    private static Result take(ServerLevel level, int chunkX, int chunkZ, int handle, int via, boolean op) {
        String denied = ClaimRules.canClaim(level, chunkX, chunkZ, handle, op);
        if (denied != null) { return Result.no(denied); }
        ClaimIndex index = ClaimIndex.obtain(level);
        if (!index.claim(chunkX, chunkZ, handle, via)) { return Result.no(ClaimRules.TAKEN); }
        MinecraftServer server = level.getServer();
        return Result.yes(DONE, chunkX, chunkZ, ClaimOwners.labelOf(server, handle), index.claimCountOf(handle), ClaimRules.limitFor(server, handle));
    }

    public static int claimOpen(ServerLevel level, ClaimIndex index, List<ChunkPos> open, int handle, int via) {
        int claimed = 0;
        boolean progress = true;
        while (progress && !open.isEmpty()) {
            progress = false;
            for (Iterator<ChunkPos> each = open.iterator(); each.hasNext(); ) {
                ChunkPos pos = each.next();
                if (ClaimRules.canClaim(level, pos.x(), pos.z(), handle, false) == null && index.claim(pos.x(), pos.z(), handle, via)) {
                    each.remove();
                    claimed++;
                    progress = true;
                }
            }
        }
        return claimed;
    }

    private static String runs(List<ChunkPos> chunks) {
        chunks.sort(Comparator.comparingInt(ChunkPos::z).thenComparingInt(ChunkPos::x));
        StringBuilder text = new StringBuilder();
        int index = 0;
        while (index < chunks.size()) {
            ChunkPos first = chunks.get(index);
            int end = index;
            while (end + 1 < chunks.size() && chunks.get(end + 1).z() == first.z() && chunks.get(end + 1).x() == chunks.get(end).x() + 1) { end++; }
            if (!text.isEmpty()) { text.append(' '); }
            text.append('(').append(first.x());
            if (end > index) { text.append("..").append(chunks.get(end).x()); }
            text.append(", ").append(first.z()).append(')');
            index = end + 1;
        }
        return text.toString();
    }
}
