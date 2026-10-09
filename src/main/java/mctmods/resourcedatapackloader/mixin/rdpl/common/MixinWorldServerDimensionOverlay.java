package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldServer.class) public abstract class MixinWorldServerDimensionOverlay {
    @WrapOperation(method = "createChunkProvider", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;createChunkGenerator()Lnet/minecraft/world/gen/IChunkGenerator;")) private IChunkGenerator rdpl$existingGenerator(WorldProvider provider, Operation<IChunkGenerator> original) { return ContentDimensionRules.existingGenerator((World) (Object) this, () -> original.call(provider)); }

    @WrapOperation(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDoLightning(Lnet/minecraft/world/chunk/Chunk;)Z", remap = false)) private boolean rdpl$existingLightning(WorldProvider provider, Chunk chunk, Operation<Boolean> original) { return ContentDimensionRules.existingLightning((World) (Object) this, original.call(provider, chunk)); }

    @WrapOperation(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDoRainSnowIce(Lnet/minecraft/world/chunk/Chunk;)Z", remap = false)) private boolean rdpl$existingPrecipitation(WorldProvider provider, Chunk chunk, Operation<Boolean> original) { return ContentDimensionRules.existingPrecipitation((World) (Object) this, original.call(provider, chunk)); }

    @WrapOperation(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z")) private boolean rdpl$existingRespawn(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingRespawn(provider, original.call(provider)); }

    @WrapOperation(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getAverageGroundLevel()I")) private int rdpl$existingGroundLevel(WorldProvider provider, Operation<Integer> original) { return ContentDimensionRules.existingGroundLevel((World) (Object) this, original.call(provider)); }
}
