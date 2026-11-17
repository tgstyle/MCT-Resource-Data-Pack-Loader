package mctmods.resourcedatapackloader.common.raids.base;

import net.minecraft.core.BlockPos;

public record Base(BlockPos center, int radius, double score, int ownerHandle, boolean claimed) {}
