package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.util.math.ChunkPos;
import java.util.Map;

public interface IRubicTicket { Map<ChunkPos, IntSet> rdpl$getAllForcedChunkCubes(); }
