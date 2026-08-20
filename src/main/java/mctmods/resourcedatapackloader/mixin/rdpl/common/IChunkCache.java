package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkCache.class) public interface IChunkCache { @Accessor("world") World rdpl$getWorld(); }
