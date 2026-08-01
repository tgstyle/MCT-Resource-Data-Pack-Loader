package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenVillage.class)
public abstract class MixinMapGenVillage {
    @Shadow private int distance;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$placement(CallbackInfo ci) {
        distance = Math.max(9, ContentStructurePlacement.spacing(ContentStructurePlacement.VILLAGES, distance));
        MapGenVillage.VILLAGE_SPAWN_BIOMES = ContentStructurePlacement.filtered(ContentStructurePlacement.VILLAGES, MapGenVillage.VILLAGE_SPAWN_BIOMES);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$spawnDistance(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
