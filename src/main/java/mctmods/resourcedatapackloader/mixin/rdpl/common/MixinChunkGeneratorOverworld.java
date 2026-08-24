package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.worldgen.generator.DeepGeneration;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentFreezeCheck;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;

@Mixin(ChunkGeneratorOverworld.class) public abstract class MixinChunkGeneratorOverworld {
    @Shadow @Final private World world;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true) private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (!ContentVoidWorld.appliesTo(world)) { return; }
        Chunk chunk = new Chunk(world, new ChunkPrimer(), x, z);
        chunk.generateSkylightMap();
        cir.setReturnValue(chunk);
    }

    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN")) private void rdpl$packStone(int x, int z, ChunkPrimer primer, Biome[] biomesIn, CallbackInfo ci) { ContentBiomes.replaceStone(primer, biomesIn); }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true) private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/terraingen/TerrainGen;populate(Lnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/World;Ljava/util/Random;IIZLnet/minecraftforge/event/terraingen/PopulateChunkEvent$Populate$EventType;)Z"), remap = false)
    private boolean rdpl$skipIceWhereNothingFreezes(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, PopulateChunkEvent.Populate.EventType type) {
        boolean wanted = TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, type);
        if (wanted && type == PopulateChunkEvent.Populate.EventType.LAKE && DeepGeneration.reworksBand(world)) { return false; }
        if (!wanted || type != PopulateChunkEvent.Populate.EventType.ICE) { return wanted; }
        if (ContentFreezeCheck.couldFreeze(world, chunkX, chunkZ)) { return true; }
        ContentChunkWatch.warmChunk();
        return false;
    }

    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true) private void rdpl$biomeOwnsSpawns(EnumCreatureType creatureType, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> cir) {
        if (creatureType != EnumCreatureType.MONSTER) { return; }
        if (!ContentStructures.blocks(world, "Temple") && !ContentStructures.blocks(world, "Monument")) { return; }
        cir.setReturnValue(world.getBiome(pos).getSpawnableList(creatureType));
    }

    @Shadow @Final private boolean mapFeaturesEnabled;
    @Shadow @Final private double[] heightMap;
    @Shadow private MapGenVillage villageGenerator;
    @Shadow private MapGenStronghold strongholdGenerator;
    @Shadow private MapGenMineshaft mineshaftGenerator;
    @Shadow private StructureOceanMonument oceanMonumentGenerator;
    @Shadow private WoodlandMansion woodlandMansionGenerator;

    @Inject(method = "generateHeightmap", at = @At("RETURN")) private void rdpl$seatStructures(int x, int y, int z, CallbackInfo ci) {
        if (!mapFeaturesEnabled || !ContentBeard.wanted()) { return; }
        MapGenStructure[] generators = { villageGenerator, strongholdGenerator, mineshaftGenerator, oceanMonumentGenerator, woodlandMansionGenerator };
        String[] names = { "villages", "strongholds", "mineshafts", "monuments", "mansions" };
        ContentBeard.apply(world, (ChunkGeneratorOverworld) (Object) this, generators, names, heightMap, x / 4, z / 4);
    }
}
