package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcPlanetWorldgen;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import micdoodle8.mods.galacticraft.core.TransformerHooks;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransformerHooks.class, remap = false) public class MixinTransformerHooks {
    @Inject(method = "otherModGenerate(IILnet/minecraft/world/World;Lnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/chunk/IChunkProvider;)V", at = @At("HEAD")) private static void rdpl$ownWorldgen(int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) { GcPlanetWorldgen.generateOwn(chunkX, chunkZ, world, chunkGenerator, chunkProvider); }

    @Inject(method = "getRenderPosY", at = @At("HEAD"), cancellable = true) private static void rdpl$honestCameraHeight(Entity viewEntity, double regular, CallbackInfoReturnable<Double> cir) {
        if (GenHeights.rubic(viewEntity.world)) { cir.setReturnValue(regular + viewEntity.getEyeHeight()); }
    }
}
