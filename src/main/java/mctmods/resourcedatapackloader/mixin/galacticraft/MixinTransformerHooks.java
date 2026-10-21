package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcPlanetWorldgen;

import micdoodle8.mods.galacticraft.core.TransformerHooks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TransformerHooks.class, remap = false) public class MixinTransformerHooks {
    @Inject(method = "otherModGenerate(IILnet/minecraft/world/World;Lnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/chunk/IChunkProvider;)V", at = @At("HEAD")) private static void rdpl$ownWorldgen(int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) { GcPlanetWorldgen.generateOwn(chunkX, chunkZ, world, chunkGenerator, chunkProvider); }
}
