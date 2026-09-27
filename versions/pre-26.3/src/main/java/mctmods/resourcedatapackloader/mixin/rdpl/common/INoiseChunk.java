package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NoiseChunk.class) public interface INoiseChunk {
    @Invoker("getInterpolatedState") BlockState rdpl$getInterpolatedState();

    @Accessor("beardifier") DensityFunctions.BeardifierOrMarker rdpl$getBeardifier();
}
