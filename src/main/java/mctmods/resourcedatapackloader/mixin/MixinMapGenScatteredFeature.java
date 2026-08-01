package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(MapGenScatteredFeature.class)
public abstract class MixinMapGenScatteredFeature {
    @Shadow private int maxDistanceBetweenScatteredFeatures;
    @Shadow @Final private List<Biome.SpawnListEntry> monsters;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$spacing(CallbackInfo ci) {
        maxDistanceBetweenScatteredFeatures = ContentStructurePlacement.spacing(ContentStructurePlacement.TEMPLES, maxDistanceBetweenScatteredFeatures);
        ContentStructurePlacement.spawns(ContentStructurePlacement.TEMPLES, monsters);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.TEMPLES, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
