package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SurfaceSystem.class) public interface ISurfaceSystem {
    @Invoker("getSurfaceDepth") int rdpl$getSurfaceDepth(int x, int z);

    @Invoker("getBand") BlockState rdpl$getBand(int x, int y, int z);

    @Accessor("surfaceNoise") NormalNoise rdpl$getSurfaceNoise();
}
