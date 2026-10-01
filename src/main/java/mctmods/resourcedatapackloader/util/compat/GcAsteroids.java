package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.util.world.SavedData;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.TeleportTypeAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.BiomeAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldSavedData;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class GcAsteroids {
    private static final ITeleportType LANDING = new TeleportTypeAsteroids();

    private GcAsteroids() {}

    static IChunkGenerator generator(World world) { return new Generator(world); }

    static ITeleportType landing() { return LANDING; }

    static Biome biome() { return BiomeAsteroids.asteroid; }

    static WorldProvider store(World world) { return new Store(world); }

    private static final class Generator extends ChunkProviderAsteroids {
        Generator(World world) { super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled()); }

        @Override public void recreateStructures(@Nonnull Chunk chunk, int x, int z) { }
    }

    private static final class Store extends WorldProviderAsteroids {
        Store(World world) { this.world = world; }

        private Asteroids held() { return SavedData.get(world.getPerWorldStorage(), Asteroids.class, Asteroids.ID, Asteroids::new); }

        @Override public synchronized void addAsteroid(int x, int y, int z, int size, int core) {
            Asteroids held = held();
            if (held.landed.putIfAbsent(new BlockPos(x, y, z), false) == null) { held.markDirty(); }
        }

        @Override public synchronized void removeAsteroid(int x, int y, int z) {
            Asteroids held = held();
            if (held.landed.remove(new BlockPos(x, y, z)) != null) { held.markDirty(); }
        }

        @Override @Nullable public synchronized BlockVec3 getClosestAsteroidXZ(int x, int y, int z, boolean mark) {
            Asteroids held = held();
            BlockPos closest = null;
            long nearest = Long.MAX_VALUE;
            for (Map.Entry<BlockPos, Boolean> entry : held.landed.entrySet()) {
                if (mark && entry.getValue()) { continue; }
                long dx = x - entry.getKey().getX();
                long dz = z - entry.getKey().getZ();
                long distance = dx * dx + dz * dz;
                if (distance < nearest) {
                    nearest = distance;
                    closest = entry.getKey();
                }
            }
            if (closest == null) { return null; }
            if (mark) {
                held.landed.put(closest, true);
                held.markDirty();
            }
            return new BlockVec3(closest);
        }
    }

    public static final class Asteroids extends WorldSavedData {
        private static final String ID = "rdpl_asteroids";
        final Map<BlockPos, Boolean> landed = new HashMap<>();

        public Asteroids(String name) { super(name); }

        @Override public void readFromNBT(NBTTagCompound nbt) {
            landed.clear();
            NBTTagList list = nbt.getTagList("asteroids", 10);
            for (int index = 0; index < list.tagCount(); index++) {
                NBTTagCompound entry = list.getCompoundTagAt(index);
                landed.put(BlockPos.fromLong(entry.getLong("pos")), entry.getBoolean("landed"));
            }
        }

        @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<BlockPos, Boolean> entry : landed.entrySet()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setLong("pos", entry.getKey().toLong());
                tag.setBoolean("landed", entry.getValue());
                list.appendTag(tag);
            }
            nbt.setTag("asteroids", list);
            return nbt;
        }
    }
}
