package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenVillage.class)
public abstract class MixinMapGenVillage {
    @Shadow private int distance;
    @Unique private int rdpl$asked;
    @Unique private boolean rdpl$stated;
    @Unique private boolean rdpl$told;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$placement(CallbackInfo ci) {
        int stated = ContentStructurePlacement.spacing(ContentStructurePlacement.VILLAGES, distance);
        rdpl$stated = stated != distance;
        rdpl$asked = Math.max(9, stated);
        distance = rdpl$asked;
        MapGenVillage.VILLAGE_SPAWN_BIOMES = ContentStructurePlacement.filtered(ContentStructurePlacement.VILLAGES, MapGenVillage.VILLAGE_SPAWN_BIOMES);
    }

    @Unique private void rdpl$hold() {
        if (!rdpl$stated || distance == rdpl$asked) { return; }

        int found = distance;
        distance = rdpl$asked;
        if (rdpl$told) { return; }

        rdpl$told = true;
        ContentLog.LOGGER.warn("The pack asks for villages every {} chunk(s). Another mod set {} after this mod had already asked, Mo' Villages does this from its own villageDistance setting, so the pack's number is put back and is what generates", rdpl$asked, found);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$flatSite(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        rdpl$hold();
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, chunkX, chunkZ)) {
            cir.setReturnValue(true);
            return;
        }
        if (!ContentBeard.wanted()) { return; }

        World world = ((AccessorMapGenBase) this).rdpl$getWorld();
        if (world == null) { return; }

        Boolean flat = ContentBeard.flatSite(world, chunkX, chunkZ, distance);
        if (flat == null) { return; }
        if (!flat) {
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) && !ContentBeard.mansionCandidateNear(world, chunkX, chunkZ));
    }

    @Inject(method = "getNearestStructurePos", at = @At("HEAD"), cancellable = true)
    private void rdpl$nearestSite(World worldIn, BlockPos pos, boolean findUnexplored, CallbackInfoReturnable<BlockPos> cir) {
        rdpl$hold();
        if (!ContentBeard.wanted() || !ContentBeard.adapts(worldIn)) { return; }

        cir.setReturnValue(ContentBeard.nearestSite(worldIn, pos, distance, findUnexplored, 100_000_000L));
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
