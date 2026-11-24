package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldgenDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.util.world.ChunkSpan;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.Constants;

public final class RespawnRecorder {
    private RespawnRecorder() {}

    public static long coordinateSeed(BlockPos at) {
        int x = at.getX();
        int z = at.getZ();
        int fromX = x * x * 4987142 + x * 5947611;
        int squareZ = z * z;
        int fromZ = z * 389711;
        return fromX + squareZ * 4392871L + fromZ ^ 987234911L;
    }

    @Nullable public static RespawnDef resolve(int kind, String def) {
        if (kind == RespawnInstance.IMPRINT) {
            WorldgenDef found = ContentRegistry.worldgen(new ResourceLocation(def));
            return found == null ? null : found.shape.respawn;
        }
        if (kind == RespawnInstance.MAP) {
            StructureMapDef map = ContentStructureMaps.byName(def);
            return map == null ? null : map.respawn;
        }
        if (kind == RespawnInstance.CAVE) {
            for (CaveRegionDef region : ContentRegistry.caveRegions()) {
                if (region.key.toString().equals(def)) { return region.respawn; }
            }
            return null;
        }
        if (kind == RespawnInstance.DUNGEON) {
            DungeonDef dungeon = ContentDungeons.byName(def);
            return dungeon == null ? null : dungeon.respawn;
        }
        VillageDef village = ContentVillages.byName(def);
        return village == null ? null : village.respawn;
    }

    public static void record(World world, @Nullable RespawnDef respawn, RespawnInstance fresh, StructureBoundingBox window) {
        if (respawn == null || !(world instanceof WorldServer)) { return; }
        RespawnData data = RespawnData.of(world);
        if (!data.admits(fresh.id)) { return; }
        fresh.last = world.getTotalWorldTime();
        scan(world, fresh, window);
        data.add(fresh);
    }

    private static void scan(World world, RespawnInstance fresh, StructureBoundingBox window) {
        int minX = Math.max(window.minX, fresh.box[0]);
        int minY = Math.max(window.minY, fresh.box[1]);
        int minZ = Math.max(window.minZ, fresh.box[2]);
        int maxX = Math.min(window.maxX, fresh.box[3]);
        int maxY = Math.min(window.maxY, fresh.box[4]);
        int maxZ = Math.min(window.maxZ, fresh.box[5]);
        if (minX > maxX || minY > maxY || minZ > maxZ) { return; }
        if (GenHeights.rubic(world)) {
            for (BlockPos at : BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ)) {
                if (!world.isBlockLoaded(at)) { continue; }
                TileEntity tile = world.getTileEntity(at);
                if (tile != null) { take(fresh, tile); }
            }
            return;
        }
        StructureBoundingBox overlap = new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        ChunkSpan.forEachChunk(minX, minZ, maxX, maxZ, (chunkX, chunkZ) -> {
            if (!world.isChunkGeneratedAt(chunkX, chunkZ)) { return; }
            Chunk chunk = world.getChunk(chunkX, chunkZ);
            for (TileEntity tile : new ArrayList<>(chunk.getTileEntityMap().values())) {
                BlockPos at = tile.getPos();
                if (!overlap.isVecInside(at)) { continue; }
                take(fresh, tile);
            }
        });
    }

    private static void take(RespawnInstance fresh, TileEntity tile) {
        long at = tile.getPos().toLong();
        NBTTagCompound saved = tile.writeToNBT(new NBTTagCompound());
        if (saved.hasKey("LootTable", Constants.NBT.TAG_STRING)) { fresh.loot.put(at, saved.getString("LootTable")); }
        if (tile instanceof TileEntityMobSpawner) { fresh.spawn.put(at, saved); }
    }
}
