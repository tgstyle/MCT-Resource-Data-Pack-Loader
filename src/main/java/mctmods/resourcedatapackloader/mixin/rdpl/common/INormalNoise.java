package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NormalNoise.class) public interface INormalNoise {
    @Accessor("first") PerlinNoise rdpl$getFirst();
}
