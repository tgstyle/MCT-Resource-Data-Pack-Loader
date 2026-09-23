package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Aquifer.NoiseBasedAquifer.class) public abstract class MixinAquifer {
    @Inject(method = "computeSubstance", at = @At("RETURN"), cancellable = true)
    private void rdpl$dryCityCut(DensityFunction.FunctionContext context, double substance, CallbackInfoReturnable<BlockState> cir) {
        BlockState held = cir.getReturnValue();
        if (held == null || held.getFluidState().isEmpty() || !(context instanceof INoiseChunk chunk)) { return; }
        if (chunk.rdpl$getBeardifier() instanceof CityDeckBeard city && city.dug(context, substance)) { cir.setReturnValue(Blocks.AIR.defaultBlockState()); }
    }
}
