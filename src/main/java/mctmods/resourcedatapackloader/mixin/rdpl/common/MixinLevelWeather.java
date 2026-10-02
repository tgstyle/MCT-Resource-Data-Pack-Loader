package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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

    @ModifyExpressionValue(method = "isRainingAt(Lnet/minecraft/core/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;precipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation rdpl$rainWhereSnowIsOff(Biome.Precipitation found, BlockPos pos) {
        Level level = Level.class.cast(this);
        return ContentWeather.fallsAs(level, level.getBiome(pos).value(), found);
    }

    @Inject(method = "canHaveWeather()Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$cycleHasWeather(CallbackInfoReturnable<Boolean> cir) {
        DimensionTraitsDef traits = ContentWeather.traits(Level.class.cast(this));
        if (traits.precipitation() && traits.cycle() != null) { cir.setReturnValue(true); }
    }
}
