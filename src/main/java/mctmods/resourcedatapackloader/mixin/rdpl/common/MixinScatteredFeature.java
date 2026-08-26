package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces$Feature") public abstract class MixinScatteredFeature {
    @Redirect(method = "offsetToAverageGroundLevel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getTopSolidOrLiquidBlock(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos rdpl$seatInWindow(World world, BlockPos pos) { return GroundLevel.inWindow(world, pos); }
}
