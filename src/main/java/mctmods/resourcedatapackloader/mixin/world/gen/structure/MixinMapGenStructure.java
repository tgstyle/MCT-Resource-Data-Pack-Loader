package mctmods.resourcedatapackloader.mixin.world.gen.structure;

import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.util.worldgen.IForgettingStarts;
import mctmods.resourcedatapackloader.common.util.worldgen.IPackingStructureData;
import mctmods.resourcedatapackloader.common.villages.CityGrid;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructures;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenStructure.class) public abstract class MixinMapGenStructure implements IForgettingStarts {
    @Unique private static final int rdpl$KEEP_CHUNKS = 96;
    @Shadow public Long2ObjectMap<StructureStart> structureMap;
    @Shadow public MapGenStructureData structureData;

    @Inject(method = "generateStructure", at = @At("HEAD"), cancellable = true) private void rdpl$skipStructure(World worldIn, Random randomIn, ChunkPos chunkCoord, CallbackInfoReturnable<Boolean> cir) { if (ContentStructures.blocks(worldIn, (MapGenBase) (Object) this)) { cir.setReturnValue(Boolean.FALSE); } }

    @Inject(method = "recursiveGenerate", at = @At("HEAD")) private void rdpl$recallStart(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn, CallbackInfo cbi) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        if (structureData == null || structureMap.containsKey(key)) { return; }
        NBTTagCompound packed = ((IPackingStructureData) structureData).rdpl$recall(chunkX, chunkZ);
        if (packed == null) { return; }
        StructureStart start = MapGenStructureIO.getStructureStart(packed, worldIn);
        if (start == null) { return; }
        structureMap.put(key, start);
        ContentLog.LOGGER.debug("The {} start at chunk {}, {} had been packed away as the land was made far from it, and is read back rather than born again, {} piece(s) strong", ((MapGenStructure) (Object) this).getStructureName(), chunkX, chunkZ, start.getComponents().size());
    }

    @Inject(method = "recursiveGenerate", at = @At("TAIL")) private void rdpl$noticeStarts(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn, CallbackInfo cbi) { if (ContentPregen.makingLand(worldIn) || CityGrid.on()) { ContentStructures.watchStarts(worldIn, (MapGenStructure) (Object) this); } }

    @Override public void rdpl$forgetFarStarts(long[] centers) {
        int before = structureMap.size();
        structureMap.long2ObjectEntrySet().removeIf(entry -> !ContentStructures.near(entry.getLongKey(), centers, rdpl$KEEP_CHUNKS));
        if (structureData != null) { ((IPackingStructureData) structureData).rdpl$packFarStarts(centers, rdpl$KEEP_CHUNKS); }
        if (before != structureMap.size()) { ContentLog.LOGGER.debug("The {} generator lets go of {} start(s) more than {} chunks from every player and the spawn, keeping {}; each is read back from its packed record when the land near it is made again", ((MapGenStructure) (Object) this).getStructureName(), before - structureMap.size(), rdpl$KEEP_CHUNKS, structureMap.size()); }
    }
}
