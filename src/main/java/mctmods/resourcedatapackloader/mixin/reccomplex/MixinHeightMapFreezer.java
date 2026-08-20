package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.reccomplex.world.gen.feature.HeightMapFreezer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HeightMapFreezer.class, remap = false) public abstract class MixinHeightMapFreezer {
    @Unique private static boolean rdpl$told;

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk rdpl$leaveLandAlone(World world, int chunkX, int chunkZ) {
        Chunk loaded = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
        if (loaded != null) { return loaded; }
        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("A structure held the ground level still across land that has not been made yet. Making it there and then would have made everything around it in turn, so a stand-in is held instead and the real land keeps its own level when it comes");
        }
        return new Chunk(world, chunkX, chunkZ);
    }
}
