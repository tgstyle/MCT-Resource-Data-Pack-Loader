package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.util.compat.interfaces.ISheddingData;

import ivorius.reccomplex.world.gen.feature.WorldGenStructures;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

@Mixin(value = WorldGenStructures.class, remap = false) public abstract class MixinWorldGenStructures {
    @Inject(method = "decorate", at = @At("HEAD"), cancellable = true, remap = false)
    private static void rdpl$holdTheStructures(WorldServer world, Random random, ChunkPos chunkPos, @Nullable Predicate<Structure<?>> structurePredicate, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructures.blocks(world, "reccomplex")) { cir.setReturnValue(Boolean.FALSE); }
    }

    @Inject(method = "complementStructuresInChunk", at = @At("RETURN"), remap = false)
    private static void rdpl$shedComplementedStructures(ChunkPos chunkPos, WorldServer world, List<WorldStructureGenerationData.StructureEntry> complement, CallbackInfo cbi) {
        Object data = WorldStructureGenerationData.get(world);
        if (!(data instanceof ISheddingData)) { return; }
        for (WorldStructureGenerationData.StructureEntry entry : complement) { ((ISheddingData) data).rdpl$shedIfComplete(entry); }
    }
}
