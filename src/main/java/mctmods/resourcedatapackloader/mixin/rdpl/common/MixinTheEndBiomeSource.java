package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.stream.Stream;

@Mixin(TheEndBiomeSource.class) public abstract class MixinTheEndBiomeSource {
    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("RETURN"), cancellable = true)
    private void rdpl$blockedBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) { cir.setReturnValue(ContentBiomeControl.inEnd(cir.getReturnValue())); }

    @Inject(method = "collectPossibleBiomes", at = @At("RETURN"), cancellable = true)
    private void rdpl$blockedBiomes(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) { cir.setReturnValue(cir.getReturnValue().map(ContentBiomeControl::inEnd).distinct()); }
}
