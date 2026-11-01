package mctmods.resourcedatapackloader.mixin.world.gen.structure;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;

import java.util.List;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenNetherBridge.class) public abstract class MixinMapGenNetherBridge {
    @Shadow @Final private List<Biome.SpawnListEntry> spawnList;

    @Inject(method = "<init>()V", at = @At("RETURN")) private void rdpl$spawns(CallbackInfo ci) { ContentStructurePlacement.spawns(ContentStructurePlacement.FORTRESSES, spawnList); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true) private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.FORTRESSES, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true) private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.FORTRESSES, ((MapGenBase) (Object) this).world, chunkX, chunkZ)) { return; }
        cir.setReturnValue(false);
    }
}
