package mctmods.resourcedatapackloader.mixin.bop;

import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import biomesoplenty.common.world.ChunkGeneratorHellBOP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkGeneratorHellBOP.class, remap = false)
public abstract class MixinChunkGeneratorHellBOP {
    @Shadow @Final
    private World world;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$emptyChunk(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        if (!ContentVoidWorld.appliesTo(world)) { return; }

        Chunk chunk = new Chunk(world, new ChunkPrimer(), chunkX, chunkZ);
        chunk.generateSkylightMap();
        cir.setReturnValue(chunk);
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$skipPopulate(int chunkX, int chunkZ, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }

    @Redirect(method = "populate", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraftforge/event/terraingen/PopulateChunkEvent$Populate$EventType;NETHER_LAVA2:Lnet/minecraftforge/event/terraingen/PopulateChunkEvent$Populate$EventType;", opcode = Opcodes.GETSTATIC, remap = false)), at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;", remap = false), remap = false)
    private BlockPos rdpl$keepLavaTrapsInside(BlockPos pos, int x, int y, int z) { return pos.add(x + 8, y, z + 8); }
}
