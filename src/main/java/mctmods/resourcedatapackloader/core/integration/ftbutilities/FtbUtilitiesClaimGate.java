package mctmods.resourcedatapackloader.core.integration.ftbutilities;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.events.chunks.ChunkModifiedEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FtbUtilitiesClaimGate {
    private static final String HELD = "rdpl.claim.ftbonrdpl";

    private FtbUtilitiesClaimGate() {}

    @SubscribeEvent public static void onClaim(ChunkModifiedEvent.Claim event) {
        if (!ClaimSettings.ftbClaimsDenied() || !ClaimSettings.any()) { return; }
        ChunkDimPos pos = event.getChunkDimPos();
        WorldServer world = DimensionManager.getWorld(pos.dim);
        if (world == null) { return; }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index.ownerAt(pos.posX, pos.posZ) == 0 && index.pendingAt(pos.posX, pos.posZ) == null) { return; }
        event.setCanceled(true);
        EntityPlayerMP player = event.getPlayer().getNullablePlayer();
        if (player != null) { player.sendStatusMessage(new TextComponentTranslation(HELD), true); }
    }
}
