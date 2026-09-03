package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IMapGenVillageHold;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentSites;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Random;

@Mixin(MapGenBase.class) public abstract class MixinMapGenBase {
    @Shadow protected int range;
    @Shadow protected Random rand;
    @Shadow protected World world;

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$skipCarving(World worldIn, int x, int z, ChunkPrimer primer, CallbackInfo ci) {
        if (ContentStructures.blocks(worldIn, (MapGenBase) (Object) this)) { ci.cancel(); }
    }

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$scanSitesOnly(World worldIn, int x, int z, ChunkPrimer primer, CallbackInfo ci) {
        if (!(this instanceof IMapGenVillageHold) || IMapGenVillageHold.class.isAssignableFrom(((Object) this).getClass().getSuperclass())) { return; }
        if (!ContentBeard.wanted() || BeardSurface.unreadable(worldIn)) { return; }
        ci.cancel();
        world = worldIn;
        ((IMapGenVillageHold) this).rdpl$holdDistance();
        rand.setSeed(worldIn.getSeed());
        long saltX = rand.nextLong();
        long saltZ = rand.nextLong();
        ContentSites known = ContentSites.of(worldIn, ((IMapGenVillage) this).rdpl$distance());
        int grid = known.spacing();
        for (int cellX = Math.floorDiv(x - range, grid); cellX <= Math.floorDiv(x + range, grid); cellX++) {
            for (int cellZ = Math.floorDiv(z - range, grid); cellZ <= Math.floorDiv(z + range, grid); cellZ++) {
                long chosen = BeardSite.siteFor(worldIn, known, cellX, cellZ, grid);
                if (chosen == ContentBeard.NO_SITE) { continue; }
                rdpl$visit(worldIn, (int) (chosen >> 32), (int) chosen, x, z, primer, saltX, saltZ);
            }
        }
        List<long[]> pinned = ContentStructurePlacement.pins(ContentStructurePlacement.VILLAGES);
        if (pinned == null) { return; }
        for (long[] at : pinned) { rdpl$visit(worldIn, (int) at[0] >> 4, (int) at[1] >> 4, x, z, primer, saltX, saltZ); }
    }

    @Unique private void rdpl$visit(World worldIn, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer, long saltX, long saltZ) {
        if (Math.abs(chunkX - x) > range || Math.abs(chunkZ - z) > range) { return; }
        rand.setSeed((long) chunkX * saltX ^ (long) chunkZ * saltZ ^ worldIn.getSeed());
        ((IMapGenStructureSpawn) this).rdpl$recursiveGenerate(worldIn, chunkX, chunkZ, x, z, primer);
    }
}
