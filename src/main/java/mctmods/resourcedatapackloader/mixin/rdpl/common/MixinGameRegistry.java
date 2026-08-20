package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;

@Mixin(value = GameRegistry.class, remap = false) public abstract class MixinGameRegistry {
    @Redirect(method = "generateWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/IWorldGenerator;generate(Ljava/util/Random;IILnet/minecraft/world/World;Lnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/chunk/IChunkProvider;)V"))
    private static void rdpl$filterGenerator(IWorldGenerator generator, Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (ContentGeneratorControl.rejects(generator, world)) { return; }
        if (!ContentChunkWatch.watching()) {
            generator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            return;
        }
        long start = System.nanoTime();
        generator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        ContentChunkWatch.byMod(ContentGeneratorControl.owner(generator), System.nanoTime() - start);
    }
}
