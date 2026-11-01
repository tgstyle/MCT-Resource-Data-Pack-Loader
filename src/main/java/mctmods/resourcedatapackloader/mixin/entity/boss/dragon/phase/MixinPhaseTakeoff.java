package mctmods.resourcedatapackloader.mixin.entity.boss.dragon.phase;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.entity.boss.dragon.phase.PhaseTakeoff;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("target") @Mixin(PhaseTakeoff.class) public class MixinPhaseTakeoff {
    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTopSolidOrLiquidBlock(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"), require = 0, expect = 0) private BlockPos rdpl$getTopSolidOrLiquidBlockRedirect(World world, BlockPos pos) { return ((IRubicWorldInternal) world).getTopSolidOrLiquidBlockVanilla(pos); }
}
