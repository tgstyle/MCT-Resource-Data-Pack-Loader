package mctmods.resourcedatapackloader.core.integration.ftbchunks;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import dev.ftb.mods.ftbchunks.api.ClaimResult;
import dev.ftb.mods.ftbchunks.api.event.ChunkChangeEvent;
import dev.ftb.mods.ftbchunks.api.neoforge.FTBChunksEvent;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class FtbChunksGate {
    public static final String HELD = "rdpl.claim.ftbonrdpl";

    private FtbChunksGate() {}

    public static void onChange(FTBChunksEvent.ChunkChange.Pre event) {
        ChunkChangeEvent.Pre.Data data = event.getEventData();
        if (data.operation() != ChunkChangeEvent.Operation.CLAIM || !ClaimSettings.any() || !ClaimSettings.ftbClaimsDenied()) { return; }
        ChunkDimPos pos = data.claimedChunk().getPos();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel level = server == null ? null : server.getLevel(pos.dimension());
        if (level == null) { return; }
        ClaimIndex index = ClaimIndex.obtain(level);
        if (ClaimSettings.dimensionBarred(pos.dimension()) || index.ownerAt(pos.x(), pos.z()) != 0 || index.pendingAt(pos.x(), pos.z()) != null) { event.setResult(ClaimResult.customProblem(HELD)); }
    }
}
