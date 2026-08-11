package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(StructureOceanMonument.class)
public abstract class MixinStructureOceanMonument {
    @Shadow private int spacing;
    @Shadow private int separation;
    @Shadow @Final private static List<Biome.SpawnListEntry> MONUMENT_ENEMIES;
    @Mutable @Shadow @Final public static List<Biome> SPAWN_BIOMES;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$spacing(CallbackInfo ci) {
        spacing = ContentStructurePlacement.spacing(ContentStructurePlacement.MONUMENTS, spacing);
        separation = ContentStructurePlacement.separation(ContentStructurePlacement.MONUMENTS, separation);
        if (spacing <= separation) {
            ContentLog.LOGGER.warn("structureSpacing asks for monuments every {} chunk(s) while structureSeparation keeps them {} apart, which leaves the game no room to place one. Separation is brought down to {}", spacing, separation, spacing - 1);
            separation = Math.max(0, spacing - 1);
        }
        ContentStructurePlacement.spawns(ContentStructurePlacement.MONUMENTS, MONUMENT_ENEMIES);
        SPAWN_BIOMES = ContentStructurePlacement.filtered(ContentStructurePlacement.MONUMENTS, SPAWN_BIOMES);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.MONUMENTS, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.MONUMENTS, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
