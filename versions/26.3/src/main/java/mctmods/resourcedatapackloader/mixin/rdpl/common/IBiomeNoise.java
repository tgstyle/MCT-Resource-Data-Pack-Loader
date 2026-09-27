package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.synth.Noise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class) public interface IBiomeNoise {
    @Accessor("TEMPERATURE_NOISE") static Noise rdpl$temperatureNoise() { throw new AssertionError(); }
}
