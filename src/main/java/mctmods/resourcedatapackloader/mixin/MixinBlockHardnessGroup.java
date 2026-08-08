package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.ContentHardness;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(Block.class)
public abstract class MixinBlockHardnessGroup {
    @Inject(method = "getExplosionResistance(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/world/Explosion;)F", at = @At("RETURN"), cancellable = true, remap = false)
    private void rdpl$blastByGroup(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion, CallbackInfoReturnable<Float> cir) {
        if (!ContentHardness.wanted()) { return; }

        float multiplier = ContentHardness.blastAt(world.getBlockState(pos), pos.getX(), pos.getY(), pos.getZ());
        if (multiplier == 1.0F) { return; }

        cir.setReturnValue(cir.getReturnValue() * multiplier);
    }
}
