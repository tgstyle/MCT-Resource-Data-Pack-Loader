package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class) public abstract class MixinEntityDimensionOverlay {
    @WrapOperation(method = "changeDimension(ILnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getMovementFactor()D"), remap = false) private double rdpl$existingMovementFactor(WorldProvider provider, Operation<Double> original) { return ContentDimensionRules.existingMovementFactor(provider, original.call(provider)); }
}
