package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCraters;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkGeneratorFlat.class) public abstract class MixinChunkGeneratorFlat {
    @Redirect(method = "generateChunk", at = @At(value = "NEW", target = "net/minecraft/world/chunk/Chunk")) private Chunk rdpl$craters(World worldIn, ChunkPrimer primer, int x, int z) {
        ContentCraters.carve(worldIn, (IChunkGenerator) this, x, z, primer);
        return new Chunk(worldIn, primer, x, z);
    }
}
