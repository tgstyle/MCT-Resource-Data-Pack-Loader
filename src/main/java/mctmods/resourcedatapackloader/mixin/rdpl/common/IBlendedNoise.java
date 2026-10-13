package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlendedNoise.class) public interface IBlendedNoise {
    @Accessor("minLimitNoise") PerlinNoise rdpl$getMinLimitNoise();

    @Accessor("maxLimitNoise") PerlinNoise rdpl$getMaxLimitNoise();

    @Accessor("mainNoise") PerlinNoise rdpl$getMainNoise();

    @Accessor("smearScaleMultiplier") double rdpl$getSmearScaleMultiplier();
}
