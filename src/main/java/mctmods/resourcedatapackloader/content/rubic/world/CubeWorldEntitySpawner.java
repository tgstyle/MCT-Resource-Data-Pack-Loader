package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.CubeWatcher;
import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IWorldEntitySpawner;
import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CubeWorldEntitySpawner implements IWorldEntitySpawner {
    private static final int CUBES_PER_CHUNK = 16;
    private static final int MOB_COUNT_DIV = (int) Math.pow(17.0D, 2.0D) * CUBES_PER_CHUNK;
    private static final int SPAWN_RADIUS = 8;
    @Nonnull private final Set<CubePos> cubesForSpawn = new HashSet<>();

    @Override public int findChunksForSpawning(WorldServer world, boolean hostileEnable, boolean peacefulEnable, boolean spawnOnSetTickRate) {
        if (!hostileEnable) { hostileOff++; }
        tellWhySpawnsFail(world);
        if (!hostileEnable && !peacefulEnable) { return 0; }
        this.cubesForSpawn.clear();
        int chunkCount = addEligibleChunks(world, this.cubesForSpawn);
        int totalSpawnCount = 0;
        for (EnumCreatureType mobType : EnumCreatureType.values()) {
            if (!shouldSpawnType(mobType, hostileEnable, peacefulEnable, spawnOnSetTickRate)) { continue; }
            int worldEntityCount = world.countEntities(mobType, true);
            int maxEntityCount = mobType.getMaxNumberOfCreature() * chunkCount / MOB_COUNT_DIV;
            if (worldEntityCount > maxEntityCount) {
                if (mobType == EnumCreatureType.MONSTER) { capped++; }
                continue;
            }
            ArrayList<CubePos> shuffled = getShuffledCopy(this.cubesForSpawn);
            totalSpawnCount += spawnCreatureTypeInAllChunks(mobType, world, shuffled);
        }
        return totalSpawnCount;
    }

    private int addEligibleChunks(WorldServer world, Set<CubePos> possibleChunks) {
        int chunkCount = 0;
        Random r = world.rand;
        Set<CubePos> allCubes = new HashSet<>();
        for (EntityPlayer player : world.playerEntities) {
            if (player.isSpectator()) { continue; }
            CubePos center = CubePos.fromEntity(player);
            for (int cubeXRel = -SPAWN_RADIUS; cubeXRel <= SPAWN_RADIUS; ++cubeXRel) {
                for (int cubeYRel = -SPAWN_RADIUS; cubeYRel <= SPAWN_RADIUS; ++cubeYRel) {
                    for (int cubeZRel = -SPAWN_RADIUS; cubeZRel <= SPAWN_RADIUS; ++cubeZRel) {
                        CubePos chunkPos = center.add(cubeXRel, cubeYRel, cubeZRel);
                        if (allCubes.contains(chunkPos)) { continue; }
                        assert !possibleChunks.contains(chunkPos);
                        ++chunkCount;
                        boolean isEdge = cubeXRel == -SPAWN_RADIUS || cubeXRel == SPAWN_RADIUS ||
                                cubeYRel == -SPAWN_RADIUS || cubeYRel == SPAWN_RADIUS ||
                                cubeZRel == -SPAWN_RADIUS || cubeZRel == SPAWN_RADIUS;
                        if (isEdge || !world.getWorldBorder().contains(chunkPos.chunkPos())) { continue; }
                        CubeWatcher chunkInfo = ((PlayerCubeMap) world.getPlayerChunkMap()).getCubeWatcher(chunkPos);
                        if (chunkInfo != null && chunkInfo.isSentToPlayers()) {
                            allCubes.add(chunkPos);
                            if (r.nextInt(SPAWN_RADIUS * 2 + 1) == 0) { possibleChunks.add(chunkPos); }
                        }
                    }
                }
            }
        }
        return chunkCount;
    }

    private int spawnCreatureTypeInAllChunks(EnumCreatureType mobType, WorldServer world, ArrayList<CubePos> chunkList) {
        BlockPos spawnPoint = world.getSpawnPoint();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        int totalSpawned = 0;
        nextChunk:
        for (CubePos currentChunkPos : chunkList) {
            BlockPos blockpos = getRandomChunkPosition(world, currentChunkPos);
            if (blockpos == null) {
                aboveHeight++;
                continue;
            }
            IBlockState block = world.getBlockState(blockpos);
            if (block.isNormalCube()) {
                insideSolid++;
                continue;
            }
            openPicks++;
            int blockX = blockpos.getX();
            int blockY = blockpos.getY();
            int blockZ = blockpos.getZ();
            int currentPackSize = 0;
            for (int k2 = 0; k2 < 3; ++k2) {
                int entityBlockX = blockX;
                int entityBlockZ = blockZ;
                int searchRadius = 6;
                Biome.SpawnListEntry biomeMobs = null;
                IEntityLivingData entityData = null;
                int numSpawnAttempts = MathHelper.ceil(Math.random() * 4.0D);
                Random rand = world.rand;
                for (int spawnAttempt = 0; spawnAttempt < numSpawnAttempts; ++spawnAttempt) {
                    entityBlockX += rand.nextInt(searchRadius) - rand.nextInt(searchRadius);
                    entityBlockZ += rand.nextInt(searchRadius) - rand.nextInt(searchRadius);
                    blockPos.setPos(entityBlockX, blockY, entityBlockZ);
                    float entityX = (float) entityBlockX + 0.5F;
                    float entityZ = (float) entityBlockZ + 0.5F;
                    if (world.isAnyPlayerWithinRangeAt(entityX, blockY, entityZ, 24.0D) ||
                            spawnPoint.distanceSq(entityX, blockY, entityZ) < 576.0D) {
                        nearSomebody++;
                        continue;
                    }
                    if (biomeMobs == null) {
                        biomeMobs = world.getSpawnListEntryForTypeAt(mobType, blockPos);
                        if (biomeMobs == null) {
                            noBiomeEntry++;
                            if (!world.getChunkProvider().getPossibleCreatures(mobType, blockPos).isEmpty()) { emptiedByEvent++; }
                            break;
                        }
                    }
                    if (!world.canCreatureTypeSpawnHere(mobType, biomeMobs, blockPos) ||
                            !WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry
                                    .getPlacementForEntity(biomeMobs.entityClass), world, blockPos)) {
                        badFooting++;
                        continue;
                    }
                    EntityLiving toSpawn;
                    try {
                        toSpawn = biomeMobs.entityClass.getConstructor(new Class[]{
                                World.class
                        }).newInstance(world);
                    } catch (Exception exception) {
                        Rubic.LOGGER.error("Failed to construct entity {} for spawning", biomeMobs.entityClass, exception);
                        return totalSpawned;
                    }
                    toSpawn.setLocationAndAngles(entityX, blockY, entityZ, rand.nextFloat() * 360.0F, 0.0F);
                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(toSpawn, world, entityX, blockY, entityZ, null);
                    if (canSpawn == Event.Result.DENY) { forgeSaidNo++; }
                    else if (canSpawn == Event.Result.DEFAULT && !toSpawn.getCanSpawnHere()) { mobSaidNo++; }
                    if (canSpawn == Event.Result.ALLOW ||
                            (canSpawn == Event.Result.DEFAULT && toSpawn.getCanSpawnHere() &&
                                    toSpawn.isNotColliding())) {
                        if (!ForgeEventFactory.doSpecialSpawn(toSpawn, world, entityX, blockY, entityZ, null)) {
                            entityData = toSpawn.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(toSpawn)), entityData);
                        }
                        if (toSpawn.isNotColliding()) {
                            ++currentPackSize;
                            spawned++;
                            world.spawnEntity(toSpawn);
                        }
                        else { toSpawn.setDead(); }
                        if (blockZ >= ForgeEventFactory.getMaxSpawnPackSize(toSpawn)) { continue nextChunk; }
                    }
                    totalSpawned += currentPackSize;
                }
            }
        }
        return totalSpawned;
    }

    private long hostileOff;
    private long capped;
    private long aboveHeight;
    private long insideSolid;
    private long openPicks;
    private long nearSomebody;
    private long noBiomeEntry;
    private long emptiedByEvent;
    private long badFooting;
    private long forgeSaidNo;
    private long mobSaidNo;
    private long spawned;
    private long lastTold;

    private void tellWhySpawnsFail(WorldServer world) {
        long now = world.getTotalWorldTime();
        if (now - lastTold < 200L) { return; }
        lastTold = now;
        Rubic.LOGGER.info("Spawn counters: hostileOff={} capped={} aboveHeight={} insideSolid={} openPicks={} nearSomebody={} noBiomeEntry={} emptiedByEvent={} badFooting={} forgeSaidNo={} mobSaidNo={} spawned={} skylightSubtracted={} difficulty={}",
                hostileOff, capped, aboveHeight, insideSolid, openPicks, nearSomebody, noBiomeEntry, emptiedByEvent, badFooting, forgeSaidNo, mobSaidNo, spawned, world.getSkylightSubtracted(), world.getDifficulty());
    }

    private static <T> ArrayList<T> getShuffledCopy(Collection<T> collection) {
        ArrayList<T> list = new ArrayList<>(collection);
        Collections.shuffle(list);
        return list;
    }

    private static boolean shouldSpawnType(EnumCreatureType type, boolean hostile, boolean peaceful, boolean spawnOnSetTickRate) {
        return !((type.getPeacefulCreature() && !peaceful) ||
                (!type.getPeacefulCreature() && !hostile) ||
                (type.getAnimal() && !spawnOnSetTickRate));
    }

    @Nullable private static BlockPos getRandomChunkPosition(WorldServer world, CubePos pos) {
        int blockX = pos.getMinBlockX() + world.rand.nextInt(Cube.SIZE);
        int blockZ = pos.getMinBlockZ() + world.rand.nextInt(Cube.SIZE);
        int height = world.getHeight(blockX, blockZ);
        if (pos.getMinBlockY() > height) { return null; }
        int blockY = pos.getMinBlockY() + world.rand.nextInt(Cube.SIZE);
        return new BlockPos(blockX, blockY, blockZ);
    }
}
