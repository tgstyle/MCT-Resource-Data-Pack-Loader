package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class) public abstract class MixinPlayerListDimensionOverlay {
    @WrapOperation(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z")) private boolean rdpl$existingRespawn(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingRespawn(provider, original.call(provider)); }

    @WrapOperation(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getRespawnDimension(Lnet/minecraft/entity/player/EntityPlayerMP;)I", remap = false)) private int rdpl$existingRespawnDimension(WorldProvider provider, EntityPlayerMP player, Operation<Integer> original) { return ContentDimensionRules.existingRespawnDimension(provider, original.call(provider, player)); }

    @WrapOperation(method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraftforge/common/util/ITeleporter;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getMovementFactor()D"), remap = false) private double rdpl$existingMovementFactor(WorldProvider provider, Operation<Double> original) { return ContentDimensionRules.existingMovementFactor(provider, original.call(provider)); }
}
