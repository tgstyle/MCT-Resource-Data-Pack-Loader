package mctmods.resourcedatapackloader.core.integration.ftbutilities;

import mctmods.resourcedatapackloader.common.ownership.claims.ForeignClaims;

import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.world.World;

public final class FtbUtilitiesClaims implements ForeignClaims {
    @Override public boolean isClaimed(World world, int chunkX, int chunkZ) {
        return ClaimedChunks.isActive() && ClaimedChunks.instance.getChunk(new ChunkDimPos(chunkX, chunkZ, world.provider.getDimension())) != null;
    }

    @Override public boolean isActive() { return ClaimedChunks.isActive(); }
}
