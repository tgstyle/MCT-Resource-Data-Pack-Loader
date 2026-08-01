package mctmods.resourcedatapackloader.mixin.vanillatweaks;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.BlockGrassPath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockGrassPath.class)
public abstract class MixinBlockGrassPath {
    @Inject(method = "updateBlockState", at = @At("HEAD"), cancellable = true)
    private void rdpl$keepPath(World world, BlockPos pos, CallbackInfo ci) {
        if (!Config.tweaks.lenientPaths) { return; }

        ci.cancel();
    }
}
