package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentHardness;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Mixin(ExplosionDamageCalculator.class) public abstract class MixinExplosionDamageCalculator {
    @Inject(method = "getBlockExplosionResistance", at = @At("RETURN"), cancellable = true) private void rdpl$blastByGroup(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> cir) {
        if (ContentHardness.idle()) { return; }
        Optional<Float> held = cir.getReturnValue();
        if (held.isEmpty()) { return; }
        float multiplier = ContentHardness.blastAt(state, pos.getX(), pos.getY(), pos.getZ());
        if (multiplier != 1.0F) { cir.setReturnValue(Optional.of(held.get() * multiplier)); }
    }
}
