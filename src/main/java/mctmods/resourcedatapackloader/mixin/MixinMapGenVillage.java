package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.World;
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

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$flatSite(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentBeard.wanted()) { return; }

        World world = ((AccessorMapGenBase) this).rdpl$getWorld();
        if (world == null) { return; }

        if (ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, chunkX, chunkZ)) {
            cir.setReturnValue(true);
            return;
        }

        Boolean flat = ContentBeard.flatSite(world, chunkX, chunkZ, distance);
        if (flat == null) { return; }
        if (!flat) {
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) && !ContentBeard.mansionCandidateNear(world, chunkX, chunkZ));
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$spawnDistance(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        World world = ((AccessorMapGenBase) this).rdpl$getWorld();
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ)) {
            cir.setReturnValue(false);
            return;
        }
        if (ContentBeard.wanted() && ContentBeard.mansionCandidateNear(world, chunkX, chunkZ)) { cir.setReturnValue(false); }
    }
}
