package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public abstract class MixinBiomeWeather {
    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
    private void rdpl$snowBelowCeiling(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above(level, pos.getY())) { cir.setReturnValue(false); }
    }
}
