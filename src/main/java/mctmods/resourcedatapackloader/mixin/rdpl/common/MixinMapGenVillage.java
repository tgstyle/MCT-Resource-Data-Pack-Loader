package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IMapGenVillageHold;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.village.CitySeams;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenVillage.class) public abstract class MixinMapGenVillage implements IMapGenVillageHold {
    @Shadow private int distance;
    @Shadow private int size;
    @Unique private int rdpl$asked;
    @Unique private boolean rdpl$stated;
    @Unique private boolean rdpl$told;

    @Inject(method = "getStructureStart", at = @At("RETURN")) private void rdpl$grownVillage(int chunkX, int chunkZ, CallbackInfoReturnable<StructureStart> cir) {
        World world = ((IMapGenBase) this).rdpl$getWorld();
        if (world == null) { return; }
        if (!CityLayout.lay(cir.getReturnValue(), world, ((IMapGenBase) this).rdpl$rand())) { CityGrowth.grow(cir.getReturnValue(), world, ((IMapGenBase) this).rdpl$rand(), size); }
        BeardRoads.pierOut(world, cir.getReturnValue());
        ContentBeard.attachAll(cir.getReturnValue(), world, ((IMapGenBase) this).rdpl$rand());
        CitySeams.tie(cir.getReturnValue(), world, ((IMapGenBase) this).rdpl$rand());
        CityGrowth.culDeSacs(cir.getReturnValue(), world, ((IMapGenBase) this).rdpl$rand());
        CityGrowth.alleyFill(cir.getReturnValue(), ((IMapGenBase) this).rdpl$rand());
        CityGrowth.roadsFirst(cir.getReturnValue());
        BeardSite.gradeRoads(world, cir.getReturnValue(), "once every road of the village is laid");
        ((IStructureStartGrow) cir.getReturnValue()).rdpl$updateBoundingBox();
    }

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$placement(CallbackInfo ci) {
        int stated = ContentStructurePlacement.spacing(ContentStructurePlacement.VILLAGES, distance);
        rdpl$stated = stated != distance;
        rdpl$asked = Math.max(9, stated);
        distance = rdpl$asked;
        MapGenVillage.VILLAGE_SPAWN_BIOMES = ContentStructurePlacement.filtered(ContentStructurePlacement.VILLAGES, MapGenVillage.VILLAGE_SPAWN_BIOMES);
        if (ContentVillages.plotsLeast() > 0) { ((IMapGenBase) this).rdpl$setRange(CityGrowth.chunkRange()); }
    }

    @Override public void rdpl$holdDistance() { rdpl$hold(); }

    @Unique private void rdpl$hold() {
        if (!rdpl$stated || distance == rdpl$asked) { return; }
        int found = distance;
        distance = rdpl$asked;
        if (rdpl$told) { return; }
        rdpl$told = true;
        ContentLog.LOGGER.warn("The pack asks for villages every {} chunk(s). Another mod set {} after this mod had already asked, Mo' Villages does this from its own villageDistance setting, so the pack's number is put back and is what generates", rdpl$asked, found);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true) private void rdpl$flatSite(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        rdpl$hold();
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, chunkX, chunkZ)) {
            cir.setReturnValue(true);
            return;
        }
        if (!ContentBeard.wanted()) { return; }
        World world = ((IMapGenBase) this).rdpl$getWorld();
        if (world == null) { return; }
        Boolean flat = ContentBeard.flatSite(world, chunkX, chunkZ, distance);
        if (flat == null) { return; }
        if (!flat) {
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) && !ContentBeard.mansionCandidateNear(world, chunkX, chunkZ));
    }

    @Inject(method = "getNearestStructurePos", at = @At("HEAD"), cancellable = true) private void rdpl$nearestSite(World worldIn, BlockPos pos, boolean findUnexplored, CallbackInfoReturnable<BlockPos> cir) {
        rdpl$hold();
        if (!ContentBeard.wanted() || !ContentBeard.adapts(worldIn)) { return; }
        cir.setReturnValue(ContentBeard.nearestSite(worldIn, pos, distance, findUnexplored, 100_000_000L));
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true) private void rdpl$spawnDistance(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        World world = ((IMapGenBase) this).rdpl$getWorld();
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ)) {
            cir.setReturnValue(false);
            return;
        }
        if (ContentBeard.wanted() && ContentBeard.mansionCandidateNear(world, chunkX, chunkZ)) { cir.setReturnValue(false); }
    }
}
