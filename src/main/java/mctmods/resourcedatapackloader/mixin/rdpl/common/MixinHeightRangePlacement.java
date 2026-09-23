package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.VanillaWindow;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.stream.Stream;

@Mixin(HeightRangePlacement.class) public abstract class MixinHeightRangePlacement {
    @Shadow @Final private HeightProvider height;

    @Inject(method = "getPositions", at = @At("HEAD"), cancellable = true)
    private void rdpl$vanillaWindow(PlacementContext context, RandomSource random, BlockPos pos, CallbackInfoReturnable<Stream<BlockPos>> cir) {
        VanillaWindow window = VanillaWindow.of(context);
        if (window == null) { return; }
        int y = height.sample(random, window);
        cir.setReturnValue(window.under(y) ? Stream.empty() : Stream.of(pos.atY(y)));
    }
}
