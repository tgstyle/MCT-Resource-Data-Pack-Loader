package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.world.chunk.BlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockStateContainer.class) public abstract class MixinBlockStatePalette {
    @ModifyArg(method = "onResize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockStateContainer;setBits(I)V"), index = 0)
    private int rdpl$growOnceNotFourTimes(int bits) { return Math.max(bits, 8); }

    @ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockStateContainer;setBits(I)V"), index = 0)
    private int rdpl$roomWhileMaking(int bits) { return ContentPregen.busy() && !ContentPregen.lightingOnly() ? Math.max(bits, 8) : bits; }
}
