package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.util.compat.ISheddingData;

import ivorius.reccomplex.world.gen.feature.WorldGenStructures;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import java.util.List;

@Mixin(value = WorldGenStructures.class, remap = false) public abstract class MixinWorldGenStructures {
    @Inject(method = "complementStructuresInChunk", at = @At("RETURN"), remap = false)
    private static void rdpl$shedComplementedStructures(ChunkPos chunkPos, WorldServer world, List<WorldStructureGenerationData.StructureEntry> complement, CallbackInfo cbi) {
        Object data = WorldStructureGenerationData.get(world);
        if (!(data instanceof ISheddingData)) { return; }
        for (WorldStructureGenerationData.StructureEntry entry : complement) { ((ISheddingData) data).rdpl$shedIfComplete(entry); }
    }
}
