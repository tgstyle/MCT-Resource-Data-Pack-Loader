package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;
import mctmods.resourcedatapackloader.content.worldgen.EncapsulateBeard;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Beardifier.class) public abstract class MixinBeardifier {
    @Inject(method = "forStructuresInChunk", at = @At("RETURN"), cancellable = true)
    private static void rdpl$spareDecks(StructureManager structureManager, ChunkPos chunkPos, CallbackInfoReturnable<Beardifier> cir) { cir.setReturnValue(CityDeckBeard.around(EncapsulateBeard.around(cir.getReturnValue(), structureManager, chunkPos), structureManager, chunkPos)); }
}
