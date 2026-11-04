package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.server.level.ServerLevel;

public interface ForeignClaims {
    ForeignClaims NONE = new ForeignClaims() {
        @Override public boolean isClaimed(ServerLevel level, int chunkX, int chunkZ) { return false; }

        @Override public boolean isActive() { return false; }
    };

    boolean isClaimed(ServerLevel level, int chunkX, int chunkZ);

    boolean isActive();
}
