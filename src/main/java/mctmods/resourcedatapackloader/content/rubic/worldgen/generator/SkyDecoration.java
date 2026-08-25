package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeForest;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomePlains;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeSnow;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeTaiga;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeSnow;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenDesertWells;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;

public final class SkyDecoration {
    private static final int SPREAD = Cube.SIZE / 2;
    private static final int GRID = 4;
    private static final double SPARSE_PLAINS = -0.8D;
    private static final NoiseGeneratorPerlin GRASS_COLOR_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
    private static final WorldGenDoublePlant DOUBLE_PLANT = new WorldGenDoublePlant();
    private static final WorldGenBlockBlob FOREST_ROCK = new WorldGenBlockBlob(Objects.requireNonNull(Blocks.MOSSY_COBBLESTONE), 0);
    private static final BlockDoublePlant.EnumPlantType[] FOREST_PLANTS = {
            BlockDoublePlant.EnumPlantType.SYRINGA, BlockDoublePlant.EnumPlantType.ROSE, BlockDoublePlant.EnumPlantType.PAEONIA };
    private SkyDecoration() {}

    public static void decorate(World world, ICube cube) {
        if (!(world instanceof WorldServer) || cube.isEmpty()) { return; }
        int cubeX = cube.getX();
        int cubeY = cube.getY();
        int cubeZ = cube.getZ();
        Random random = new Random(world.getSeed() ^ (cubeX * 341873128712L + cubeZ * 132897987541L + cubeY * 1052717L));
        BlockPos middle = new BlockPos((cubeX << 4) + 8, (cubeY << 4) + 8, (cubeZ << 4) + 8);
        ChunkPos column = new ChunkPos(cubeX, cubeZ);
        Biome biome = world.getBiome(middle);
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, random, column));
        plants(world, random, cubeX, cubeY, cubeZ, biome);
        extras(world, random, cubeX, cubeY, cubeZ, biome);
        herds((WorldServer) world, random, cubeX, cubeY, cubeZ, biome);
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, random, column));
    }

    private static void plants(World world, Random random, int cubeX, int cubeY, int cubeZ, Biome biome) {
        BiomeDecorator decorator = biome.decorator;
        int flowers = decorator.flowersPerChunk;
        int grass = decorator.grassPerChunk;
        if (biome instanceof BiomePlains) {
            boolean sparse = sparsePlains(cubeX, cubeZ);
            flowers = sparse ? 15 : 4;
            grass = sparse ? 5 : 10;
        }
        patches(world, random, cubeX, cubeY, cubeZ, decorator.sandPatchesPerChunk, decorator.sandGen);
        patches(world, random, cubeX, cubeY, cubeZ, decorator.clayPerChunk, decorator.clayGen);
        patches(world, random, cubeX, cubeY, cubeZ, decorator.gravelPatchesPerChunk, decorator.gravelGen);
        int trees = random.nextFloat() < decorator.extraTreeChance ? decorator.treesPerChunk + 1 : decorator.treesPerChunk;
        for (int attempt = 0; attempt < trees; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top == null) { continue; }
            WorldGenAbstractTree tree = biome.getRandomTreeFeature(random);
            tree.setDecorationDefaults();
            if (tree.generate(world, random, top)) { tree.generateSaplings(world, random, top); }
        }
        for (int attempt = 0; attempt < decorator.bigMushroomsPerChunk; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { decorator.bigMushroomGen.generate(world, random, top); }
        }
        for (int attempt = 0; attempt < flowers; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
            if (top == null) { continue; }
            BlockFlower.EnumFlowerType kind = biome.pickRandomFlower(random, top);
            BlockFlower flower = kind.getBlockType().getBlock();
            if (flower.getDefaultState().getMaterial() == Material.AIR) { continue; }
            decorator.flowerGen.setGeneratedBlock(flower, kind);
            decorator.flowerGen.generate(world, random, top);
        }
        for (int attempt = 0; attempt < grass; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
            if (top != null) { biome.getRandomWorldGenForGrass(random).generate(world, random, top); }
        }
        for (int attempt = 0; attempt < decorator.deadBushPerChunk; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
            if (top != null) { new WorldGenDeadBush().generate(world, random, top); }
        }
        for (int attempt = 0; attempt < decorator.waterlilyPerChunk; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { decorator.waterlilyGen.generate(world, random, top); }
        }
        for (int attempt = 0; attempt < Math.max(decorator.mushroomsPerChunk + 1, 1); attempt++) {
            if (random.nextInt(4) == 0) {
                BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
                if (top != null) { decorator.mushroomBrownGen.generate(world, random, top); }
            }
            if (random.nextInt(8) == 0) {
                BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
                if (top != null) { decorator.mushroomRedGen.generate(world, random, top); }
            }
        }
        for (int attempt = 0; attempt < decorator.reedsPerChunk; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { decorator.reedGen.generate(world, random, top); }
        }
        for (int attempt = 0; attempt < decorator.cactiPerChunk; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { decorator.cactusGen.generate(world, random, top); }
        }
    }

    private static void patches(World world, Random random, int cubeX, int cubeY, int cubeZ, int count, WorldGenerator generator) {
        for (int attempt = 0; attempt < count; attempt++) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
            if (top != null) { generator.generate(world, random, top); }
        }
    }

    private static void extras(World world, Random random, int cubeX, int cubeY, int cubeZ, Biome biome) {
        if (biome instanceof BiomeDesert && random.nextInt(1000) == 0) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { new WorldGenDesertWells().generate(world, random, top); }
        }
        if (biome instanceof BiomeJungle && random.nextInt(10) == 0) {
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { new WorldGenMelon().generate(world, random, top); }
        }
        if (biome instanceof BiomeForest) {
            BiomeForest.Type kind = ((IBiomeForest) biome).rdpl$type();
            if (kind == BiomeForest.Type.ROOFED) { canopy(world, random, cubeX, cubeY, cubeZ, biome); }
            forestPlants(world, random, cubeX, cubeY, cubeZ, random.nextInt(5) - 3 + (kind == BiomeForest.Type.FLOWER ? 2 : 0));
        }
        if (biome instanceof BiomePlains) {
            if (!sparsePlains(cubeX, cubeZ)) { doublePlants(world, random, cubeX, cubeY, cubeZ, BlockDoublePlant.EnumPlantType.GRASS, 7); }
            if (((IBiomePlains) biome).rdpl$sunflowers()) { doublePlants(world, random, cubeX, cubeY, cubeZ, BlockDoublePlant.EnumPlantType.SUNFLOWER, 10); }
        }
        if (biome instanceof BiomeSavanna) { doublePlants(world, random, cubeX, cubeY, cubeZ, BlockDoublePlant.EnumPlantType.GRASS, 7); }
        if (biome instanceof BiomeTaiga) {
            BiomeTaiga.Type kind = ((IBiomeTaiga) biome).rdpl$type();
            if (kind == BiomeTaiga.Type.MEGA || kind == BiomeTaiga.Type.MEGA_SPRUCE) {
                int rocks = random.nextInt(3);
                for (int attempt = 0; attempt < rocks; attempt++) {
                    BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
                    if (top != null) { FOREST_ROCK.generate(world, random, top); }
                }
            }
            doublePlants(world, random, cubeX, cubeY, cubeZ, BlockDoublePlant.EnumPlantType.FERN, 7);
        }
        if (biome instanceof BiomeSnow && ((IBiomeSnow) biome).rdpl$superIcy()) {
            for (int attempt = 0; attempt < 3; attempt++) {
                BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
                if (top != null) { new WorldGenIceSpike().generate(world, random, top); }
            }
            for (int attempt = 0; attempt < 2; attempt++) {
                BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, false);
                if (top != null) { new WorldGenIcePath(4).generate(world, random, top); }
            }
        }
    }

    private static void canopy(World world, Random random, int cubeX, int cubeY, int cubeZ, Biome biome) {
        for (int gridX = 0; gridX < Cube.SIZE / GRID; gridX++) {
            for (int gridZ = 0; gridZ < Cube.SIZE / GRID; gridZ++) {
                int x = (cubeX << 4) + gridX * GRID + 1 + SPREAD + random.nextInt(GRID / 2 + 1);
                int z = (cubeZ << 4) + gridZ * GRID + 1 + SPREAD + random.nextInt(GRID / 2 + 1);
                BlockPos top = surfaceAt(world, x, z, cubeY, true);
                if (top == null) { continue; }
                if (random.nextInt(20) == 0) { new WorldGenBigMushroom().generate(world, random, top); }
                else {
                    WorldGenAbstractTree tree = biome.getRandomTreeFeature(random);
                    tree.setDecorationDefaults();
                    if (tree.generate(world, random, top)) { tree.generateSaplings(world, random, top); }
                }
            }
        }
    }

    private static void forestPlants(World world, Random random, int cubeX, int cubeY, int cubeZ, int amount) {
        for (int plant = 0; plant < amount; plant++) {
            DOUBLE_PLANT.setPlantType(FOREST_PLANTS[random.nextInt(FOREST_PLANTS.length)]);
            for (int attempt = 0; attempt < 5; attempt++) {
                if (random.nextInt(7) != 0) { continue; }
                BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
                if (top != null && DOUBLE_PLANT.generate(world, random, top)) { break; }
            }
        }
    }

    private static void doublePlants(World world, Random random, int cubeX, int cubeY, int cubeZ, BlockDoublePlant.EnumPlantType kind, int rolls) {
        DOUBLE_PLANT.setPlantType(kind);
        for (int roll = 0; roll < rolls; roll++) {
            if (random.nextInt(7) != 0) { continue; }
            BlockPos top = surface(world, random, cubeX, cubeY, cubeZ, true);
            if (top != null) { DOUBLE_PLANT.generate(world, random, top); }
        }
    }

    private static boolean sparsePlains(int cubeX, int cubeZ) {
        return GRASS_COLOR_NOISE.getValue(((cubeX << 4) + 8) / 200.0D, ((cubeZ << 4) + 8) / 200.0D) < SPARSE_PLAINS;
    }

    private static void herds(WorldServer world, Random random, int cubeX, int cubeY, int cubeZ, Biome biome) {
        List<Biome.SpawnListEntry> spawnable = biome.getSpawnableList(EnumCreatureType.CREATURE);
        if (spawnable.isEmpty()) { return; }
        while (random.nextFloat() < biome.getSpawningChance()) {
            Biome.SpawnListEntry entry = WeightedRandom.getRandomItem(world.rand, spawnable);
            int group = MathHelper.getInt(random, entry.minGroupCount, entry.maxGroupCount);
            IEntityLivingData data = null;
            for (int member = 0; member < group; member++) {
                BlockPos spot = surface(world, random, cubeX, cubeY, cubeZ, false);
                if (spot == null) { continue; }
                if (!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, spot)) { continue; }
                EntityLiving living;
                try {
                    living = entry.newInstance(world);
                } catch (Exception refused) {
                    return;
                }
                float x = spot.getX() + 0.5F;
                float z = spot.getZ() + 0.5F;
                living.setLocationAndAngles(x, spot.getY(), z, random.nextFloat() * 360.0F, 0.0F);
                if (ForgeEventFactory.canEntitySpawn(living, world, x, spot.getY(), z, null) == Event.Result.DENY) {
                    living.setDead();
                    continue;
                }
                world.spawnEntity(living);
                data = living.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(living)), data);
            }
        }
    }

    @Nullable private static BlockPos surface(World world, Random random, int cubeX, int cubeY, int cubeZ, boolean opaque) {
        return surfaceAt(world, (cubeX << 4) + random.nextInt(Cube.SIZE) + SPREAD, (cubeZ << 4) + random.nextInt(Cube.SIZE) + SPREAD, cubeY, opaque);
    }

    @Nullable private static BlockPos surfaceAt(World world, int x, int z, int cubeY, boolean opaque) {
        int highest = (cubeY << 4) + Cube.SIZE - 1;
        int lowest = cubeY << 4;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        boolean openAbove = world.isAirBlock(at.setPos(x, highest + 1, z));
        for (int y = highest; y >= lowest; y--) {
            IBlockState state = world.getBlockState(at.setPos(x, y, z));
            boolean seat = opaque ? state.isOpaqueCube() : state.getMaterial().isSolid();
            if (seat && openAbove) { return new BlockPos(x, y + 1, z); }
            openAbove = state.getMaterial() == Material.AIR;
        }
        return null;
    }
}
