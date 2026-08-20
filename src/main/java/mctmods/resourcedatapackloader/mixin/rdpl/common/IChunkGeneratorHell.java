package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorHell.class) public interface IChunkGeneratorHell { @Accessor("genNetherBridge") MapGenNetherBridge rdpl$fortresses(); }
