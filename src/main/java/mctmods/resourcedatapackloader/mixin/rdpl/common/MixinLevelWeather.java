package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class) public abstract class MixinLevelWeather {
    @Inject(method = "isRainingAt(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$rainBelowCeiling(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above(Level.class.cast(this), pos.getY())) { cir.setReturnValue(false); }
    }

    @WrapOperation(method = "isRainingAt(Lnet/minecraft/core/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation rdpl$rainWhereSnowIsOff(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        return ContentWeather.fallsAs(Level.class.cast(this), biome, original.call(biome, pos));
    }
}
