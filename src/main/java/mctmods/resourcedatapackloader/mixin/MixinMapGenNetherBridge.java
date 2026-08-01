package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(MapGenNetherBridge.class)
public abstract class MixinMapGenNetherBridge {
    @Shadow @Final private List<Biome.SpawnListEntry> spawnList;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$spawns(CallbackInfo ci) { ContentStructurePlacement.spawns(ContentStructurePlacement.FORTRESSES, spawnList); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.FORTRESSES, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
