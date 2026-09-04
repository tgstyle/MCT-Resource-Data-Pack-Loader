package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.util.compat.interfaces.IForgettingStarts;
import mctmods.resourcedatapackloader.util.compat.interfaces.IPackingStructureData;
import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;
import java.util.Random;

@Mixin(MapGenStructure.class) public abstract class MixinMapGenStructure implements IForgettingStarts {
    @Unique private static final int KEEP_CHUNKS = 96;
    @Shadow protected Long2ObjectMap<StructureStart> structureMap;
    @Shadow private MapGenStructureData structureData;

    @Inject(method = "generateStructure", at = @At("HEAD"), cancellable = true) private void rdpl$skipStructure(World worldIn, Random randomIn, ChunkPos chunkCoord, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructures.blocks(worldIn, (MapGenBase) (Object) this)) { cir.setReturnValue(Boolean.FALSE); }
    }

    @Inject(method = "recursiveGenerate", at = @At("HEAD"))
    private void rdpl$recallStart(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn, CallbackInfo cbi) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        if (structureData == null || structureMap.containsKey(key)) { return; }
        NBTTagCompound packed = ((IPackingStructureData) structureData).rdpl$recall(chunkX, chunkZ);
        if (packed == null) { return; }
        StructureStart start = MapGenStructureIO.getStructureStart(packed, worldIn);
        if (start == null) { return; }
        structureMap.put(key, start);
        ContentLog.LOGGER.debug("The {} start at chunk {}, {} had been packed away as the land was made far from it, and is read back rather than born again, {} piece(s) strong", ((MapGenStructure) (Object) this).getStructureName(), chunkX, chunkZ, start.getComponents().size());
    }

    @Inject(method = "recursiveGenerate", at = @At("TAIL"))
    private void rdpl$noticeStarts(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn, CallbackInfo cbi) {
        if (ContentPregen.makingLand(worldIn)) { ContentStructures.watchStarts(worldIn, (MapGenStructure) (Object) this); }
    }

    @Override public void rdpl$forgetFarStarts(int chunkX, int chunkZ) {
        ObjectIterator<Long2ObjectMap.Entry<StructureStart>> each = structureMap.long2ObjectEntrySet().iterator();
        while (each.hasNext()) {
            long key = each.next().getLongKey();
            int x = (int) key;
            int z = (int) (key >>> 32);
            if (Math.abs(x - chunkX) > KEEP_CHUNKS || Math.abs(z - chunkZ) > KEEP_CHUNKS) { each.remove(); }
        }
        if (structureData != null) { ((IPackingStructureData) structureData).rdpl$packFarStarts(chunkX, chunkZ, KEEP_CHUNKS); }
    }
}
