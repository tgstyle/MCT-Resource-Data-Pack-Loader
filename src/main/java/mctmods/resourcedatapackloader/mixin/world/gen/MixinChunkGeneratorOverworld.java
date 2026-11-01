package mctmods.resourcedatapackloader.mixin.world.gen;

import mctmods.resourcedatapackloader.common.pregen.ContentChunkWatch;
import mctmods.resourcedatapackloader.common.rubic.worldgen.generator.DeepGeneration;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomes;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructures;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentBeard;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.dress.ContentBeardSeat;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentFreezeCheck;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentOceanFluid;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGeneratorOverworld.class) public abstract class MixinChunkGeneratorOverworld {
    @Shadow @Final private World world;
    @Shadow private IBlockState oceanBlock;

    @Inject(method = "<init>(Lnet/minecraft/world/World;JZLjava/lang/String;)V", at = @At("TAIL")) private void rdpl$oceanFluid(World worldIn, long seed, boolean mapFeaturesEnabledIn, String generatorOptions, CallbackInfo ci) {
        IBlockState sea = ContentOceanFluid.chosen(worldIn, generatorOptions);
        if (sea != null) { oceanBlock = sea; }
    }

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true) private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(world, x, z);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN")) private void rdpl$packStone(int x, int z, ChunkPrimer primer, Biome[] biomesIn, CallbackInfo ci) { ContentBiomes.replaceStone(primer, biomesIn); }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true) private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/terraingen/TerrainGen;populate(Lnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/World;Ljava/util/Random;IIZLnet/minecraftforge/event/terraingen/PopulateChunkEvent$Populate$EventType;)Z"), remap = false) private boolean rdpl$skipIceWhereNothingFreezes(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, PopulateChunkEvent.Populate.EventType type) {
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

    @Shadow @Final public boolean mapFeaturesEnabled;
    @Shadow @Final private double[] heightMap;
    @Shadow public MapGenVillage villageGenerator;
    @Shadow public MapGenStronghold strongholdGenerator;
    @Shadow public MapGenMineshaft mineshaftGenerator;
    @Shadow public StructureOceanMonument oceanMonumentGenerator;
    @Shadow public WoodlandMansion woodlandMansionGenerator;

    @Inject(method = "generateHeightmap", at = @At("RETURN")) private void rdpl$seatStructures(int x, int y, int z, CallbackInfo ci) {
        if (!mapFeaturesEnabled || !ContentBeard.wanted()) { return; }
        MapGenStructure[] generators = { villageGenerator, strongholdGenerator, mineshaftGenerator, oceanMonumentGenerator, woodlandMansionGenerator };
        String[] names = { "villages", "strongholds", "mineshafts", "monuments", "mansions" };
        ContentBeardSeat.apply(world, generators, names, heightMap, x / 4, z / 4);
    }
}
