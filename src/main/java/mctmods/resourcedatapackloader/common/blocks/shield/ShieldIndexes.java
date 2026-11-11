package mctmods.resourcedatapackloader.common.blocks.shield;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SpawnShieldDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public final class ShieldIndexes {
    private static final Map<Block, BlockShield> TABLE = new HashMap<>();
    private static int maxSpawnRadius;
    private static int maxWeatherRadius;
    private static boolean active;

    private ShieldIndexes() {}

    public static void resolve() {
        TABLE.clear();
        maxSpawnRadius = 0;
        maxWeatherRadius = 0;
        for (Map.Entry<ResourceLocation, Block> entry : ContentRegistry.registeredBlocks()) {
            BlockDef def = ContentRegistry.blockDef(entry.getKey());
            if (def == null || def.blocksSpawns == null && def.blocksWeather == null) { continue; }
            int spawnRadius = 0;
            SpawnShieldDef spawns = def.blocksSpawns;
            Set<ResourceLocation> entities = new HashSet<>();
            if (spawns != null) {
                for (ResourceLocation id : spawns.entities) {
                    if (EntityList.isRegistered(id)) { entities.add(id); }
                    else { ContentLog.LOGGER.error("Block {} blocksSpawns lists the unknown entity '{}', so it is skipped", entry.getKey(), id); }
                }
                if (spawns.entities.isEmpty() || !entities.isEmpty()) { spawnRadius = spawns.radius; }
                else { ContentLog.LOGGER.error("Block {} blocksSpawns lists no known entity, so it blocks no spawns", entry.getKey()); }
            }
            int weatherRadius = def.blocksWeather == null ? 0 : def.blocksWeather.radius;
            if (spawnRadius == 0 && weatherRadius == 0) { continue; }
            TABLE.put(entry.getValue(), new BlockShield(spawnRadius, spawns == null ? SpawnShieldDef.Mobs.ALL : spawns.mobs, entities, weatherRadius));
            maxSpawnRadius = Math.max(maxSpawnRadius, spawnRadius);
            maxWeatherRadius = Math.max(maxWeatherRadius, weatherRadius);
        }
        active = !TABLE.isEmpty();
    }

    public static boolean active() { return active; }

    private static int reach(int blocks) { return (blocks + 15) >> 4; }

    private static ShieldIndex of(World world) {
        IShieldHolder holder = (IShieldHolder) world;
        ShieldIndex index = holder.rdpl$shields();
        if (index == null) {
            index = new ShieldIndex();
            holder.rdpl$setShields(index);
        }
        return index;
    }

    @Nullable private static ShieldIndex existing(World world) { return ((IShieldHolder) world).rdpl$shields(); }

    public static void scan(World world, Chunk chunk) {
        if (!active) { return; }
        ShieldIndex index = of(world);
        long key = ChunkPos.asLong(chunk.x, chunk.z);
        index.drop(key);
        for (ExtendedBlockStorage storage : chunk.getBlockStorageArray()) { addStorage(index, key, storage, chunk.x << 4, storage == null ? 0 : storage.getYLocation(), chunk.z << 4); }
    }

    private static long column(int x, int z) { return (x & 0x3FFFFFFL) << 38 | (z & 0x3FFFFFFL); }

    private static void addStorage(ShieldIndex index, long key, @Nullable ExtendedBlockStorage storage, int baseX, int baseY, int baseZ) {
        if (storage == null || storage.isEmpty()) { return; }
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    BlockShield shield = TABLE.get(storage.get(x, y, z).getBlock());
                    if (shield != null) { index.add(key, column(baseX + x, baseZ + z), baseY + y, shield); }
                }
            }
        }
    }

    public static void scanCube(World world, Cube cube) {
        if (!active) { return; }
        ShieldIndex index = of(world);
        CubePos at = cube.getCoords();
        long key = ChunkPos.asLong(at.getX(), at.getZ());
        index.removeRange(key, at.getMinBlockY(), at.getMaxBlockY());
        addStorage(index, key, cube.getStorage(), at.getMinBlockX(), at.getMinBlockY(), at.getMinBlockZ());
    }

    public static void dropCube(World world, Cube cube) {
        if (!active) { return; }
        ShieldIndex index = existing(world);
        if (index == null) { return; }
        CubePos at = cube.getCoords();
        index.removeRange(ChunkPos.asLong(at.getX(), at.getZ()), at.getMinBlockY(), at.getMaxBlockY());
    }

    public static void changed(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        if (!active) { return; }
        BlockShield was = TABLE.get(oldState.getBlock());
        BlockShield now = TABLE.get(newState.getBlock());
        if (was == now) { return; }
        ShieldIndex index = of(world);
        long key = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        long column = column(pos.getX(), pos.getZ());
        if (was != null) { index.remove(key, column, pos.getY()); }
        if (now != null) { index.add(key, column, pos.getY(), now); }
    }

    public static void drop(World world, int chunkX, int chunkZ) {
        if (!active) { return; }
        ShieldIndex index = existing(world);
        if (index != null) { index.drop(ChunkPos.asLong(chunkX, chunkZ)); }
    }

    public static boolean spawnDenied(World world, Entity entity, int x, int y, int z) {
        if (!active || maxSpawnRadius == 0) { return false; }
        ShieldIndex index = existing(world);
        return index != null && index.spawnDenied(entity, x, y, z, reach(maxSpawnRadius));
    }

    public static boolean weatherShut(World world, int x, int z) {
        if (!active || maxWeatherRadius == 0) { return false; }
        ShieldIndex index = existing(world);
        return index != null && index.weatherShut(x, z, reach(maxWeatherRadius));
    }

    public static void near(World world, int x, int z, int range, ShieldIndex.Near scratch) {
        ShieldIndex index = active && maxWeatherRadius > 0 ? existing(world) : null;
        if (index == null) {
            scratch.clear();
            return;
        }
        index.near(x, z, range, maxWeatherRadius, reach(range + maxWeatherRadius), scratch);
    }
}
