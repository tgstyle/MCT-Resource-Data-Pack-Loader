package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentSeasonTint;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public abstract class MixinBiomeSeasonTint {
    @Inject(method = "getGrassColor", at = @At("RETURN"), cancellable = true) private void rdpl$grassTint(double x, double z, CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(ContentSeasonTint.grass((Biome) (Object) this, cir.getReturnValueI())); }

    @ModifyArg(method = "getGrassColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/BiomeSpecialEffects$GrassColorModifier;modifyColor(DDI)I"), index = 2) private int rdpl$grassBase(int color) { return ContentSeasonTint.grassBase((Biome) (Object) this, color); }

    @Inject(method = "getFoliageColor", at = @At("RETURN"), cancellable = true) private void rdpl$foliageTint(CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(ContentSeasonTint.foliage((Biome) (Object) this, cir.getReturnValueI())); }
}
