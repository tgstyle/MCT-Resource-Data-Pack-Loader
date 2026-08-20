package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStronghold;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(MapGenStronghold.class) public abstract class MixinMapGenStronghold {
    @Shadow @Final public List<Biome> allowedBiomes;
    @Shadow private double distance;
    @Shadow private int spread;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$placement(CallbackInfo ci) {
        distance = ContentStructurePlacement.spacing(ContentStructurePlacement.STRONGHOLDS, (int) distance);
        spread = ContentStructurePlacement.separation(ContentStructurePlacement.STRONGHOLDS, spread);
        ContentStructurePlacement.filter(ContentStructurePlacement.STRONGHOLDS, allowedBiomes);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true) private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.STRONGHOLDS, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }
}
