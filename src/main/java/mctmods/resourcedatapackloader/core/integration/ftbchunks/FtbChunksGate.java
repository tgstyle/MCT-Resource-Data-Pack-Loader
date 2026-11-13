package mctmods.resourcedatapackloader.core.integration.ftbchunks;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import dev.architectury.event.CompoundEventResult;
import dev.ftb.mods.ftbchunks.api.ClaimResult;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.event.ClaimedChunkEvent;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

public final class FtbChunksGate {
    public static final String HELD = "rdpl.claim.ftbonrdpl";

    private FtbChunksGate() {}

    public static void register() { ClaimedChunkEvent.BEFORE_CLAIM.register(FtbChunksGate::before); }

    private static CompoundEventResult<ClaimResult> before(CommandSourceStack source, ClaimedChunk chunk) {
        if (!ClaimSettings.ftbClaimsDenied() || !ClaimSettings.any()) { return CompoundEventResult.pass(); }
        ChunkDimPos pos = chunk.getPos();
        ServerLevel level = source.getServer().getLevel(pos.dimension());
        if (level == null) { return CompoundEventResult.pass(); }
        ClaimIndex index = ClaimIndex.get(level);
        boolean held = ClaimSettings.dimensionBarred(pos.dimension()) || index.ownerAt(pos.x(), pos.z()) != 0 || index.pendingAt(pos.x(), pos.z()) != null;
        return held ? CompoundEventResult.interruptFalse(ClaimResult.customProblem(HELD)) : CompoundEventResult.pass();
    }
}
