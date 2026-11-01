package mctmods.resourcedatapackloader.mixin.world.level.levelgen.placement;

import mctmods.resourcedatapackloader.common.worldgen.surface.VanillaWindow;

import java.util.function.Consumer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeightRangePlacement.class) public abstract class HeightRangePlacementMixin {
    @Shadow @Final private HeightProvider height;

    @Inject(method = "modify(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true) private void rdpl$vanillaWindow(PlacementContext context, RandomSource random, BlockPos origin, Consumer<BlockPos> output, CallbackInfo ci) {
        VanillaWindow window = VanillaWindow.of(context);
        if (window == null) { return; }
        int y = height.sample(random, window);
        if (!window.under(y)) { output.accept(origin.atY(y)); }
        ci.cancel();
    }
}
