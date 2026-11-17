package mctmods.resourcedatapackloader.common.raids.base;

import net.minecraft.util.math.BlockPos;

public final class Base {
    private final BlockPos center;
    private final int radius;
    private final double score;
    private final int ownerHandle;
    private final boolean claimed;

    public Base(BlockPos center, int radius, double score, int ownerHandle, boolean claimed) {
        this.center = center;
        this.radius = radius;
        this.score = score;
        this.ownerHandle = ownerHandle;
        this.claimed = claimed;
    }

    public BlockPos center() {
        return center;
    }

    public int radius() {
        return radius;
    }

    public double score() {
        return score;
    }

    public int ownerHandle() {
        return ownerHandle;
    }

    public boolean claimed() {
        return claimed;
    }
}
