package mctmods.resourcedatapackloader.mixin.world.end;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("target") @Mixin(DragonFightManager.class) public class MixinDragonFightManagerRubic {
    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getTopSolidOrLiquidBlock(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"), require = 0, expect = 0) private BlockPos rdpl$getTopSolidOrLiquidBlockRedirect(WorldServer world, BlockPos pos) { return ((IRubicWorldInternal) world).getTopSolidOrLiquidBlockVanilla(pos); }
}
