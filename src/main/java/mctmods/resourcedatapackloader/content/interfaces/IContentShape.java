package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public interface IContentShape { boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin); }
