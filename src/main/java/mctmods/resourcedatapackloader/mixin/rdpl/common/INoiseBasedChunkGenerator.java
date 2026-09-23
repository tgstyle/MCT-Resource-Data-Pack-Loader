package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.function.Supplier;

@Mixin(NoiseBasedChunkGenerator.class) public interface INoiseBasedChunkGenerator {
    @Accessor("globalFluidPicker") Supplier<Aquifer.FluidPicker> rdpl$getGlobalFluidPicker();
}
