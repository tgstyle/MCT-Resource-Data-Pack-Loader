package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockPortal;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockPortal.class) public abstract class MixinBlockPortalDimensionOverlay {
    @WrapOperation(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;isSurfaceWorld()Z")) private boolean rdpl$existingSurface(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingSurface(provider, original.call(provider)); }
}
