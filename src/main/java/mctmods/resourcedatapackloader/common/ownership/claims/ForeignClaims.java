package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.world.World;

public interface ForeignClaims {
    ForeignClaims NONE = new ForeignClaims() {
        @Override public boolean isClaimed(World world, int chunkX, int chunkZ) { return false; }

        @Override public boolean isActive() { return false; }
    };

    boolean isClaimed(World world, int chunkX, int chunkZ);

    boolean isActive();
}
