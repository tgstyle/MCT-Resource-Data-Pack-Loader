package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.World;
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

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.TEMPLES, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }

        World world = ((AccessorMapGenBase) this).rdpl$getWorld();
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.TEMPLES, world, chunkX, chunkZ)) {
            cir.setReturnValue(false);
            return;
        }
        if (ContentBeard.wanted() && ContentBeard.roughGround(world, chunkX * 16 + 8, chunkZ * 16 + 8, 12, 6)) { cir.setReturnValue(false); }
    }
}
