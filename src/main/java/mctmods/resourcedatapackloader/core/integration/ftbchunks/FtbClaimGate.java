package mctmods.resourcedatapackloader.core.integration.ftbchunks;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import dev.architectury.event.CompoundEventResult;
import dev.ftb.mods.ftbchunks.api.ClaimResult;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.event.ClaimedChunkEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

final class FtbClaimGate {
    private static final String HELD = "rdpl.claim.ftbonrdpl";

    private FtbClaimGate() {}

    static void register() { ClaimedChunkEvent.BEFORE_CLAIM.register(FtbClaimGate::beforeClaim); }

    private static CompoundEventResult<ClaimResult> beforeClaim(CommandSourceStack source, ClaimedChunk chunk) {
        if (!ClaimSettings.ftbClaimsDenied()) { return CompoundEventResult.pass(); }
        ServerLevel level = source.getServer().getLevel(chunk.getPos().dimension());
        if (level == null) { return CompoundEventResult.pass(); }
        ClaimIndex index = ClaimIndex.obtain(level);
        boolean held = ClaimSettings.dimensionBarred(level.dimension()) || index != null && (index.ownerAt(chunk.getPos().x(), chunk.getPos().z()) != 0 || index.pendingAt(chunk.getPos().x(), chunk.getPos().z()) != null);
        return held ? CompoundEventResult.interruptFalse(ClaimResult.customProblem(HELD)) : CompoundEventResult.pass();
    }
}
