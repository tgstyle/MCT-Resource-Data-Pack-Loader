package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class RespawnRecorder {
    private RespawnRecorder() {}

    @Nullable public static RespawnDef resolve(int kind, String def) {
        if (kind == RespawnInstance.DUNGEON) {
            DungeonDef dungeon = ContentDungeons.byName(def);
            return dungeon == null ? null : dungeon.respawn();
        }
        Identifier key = Identifier.tryParse(def);
        if (key == null) { return null; }
        if (kind == RespawnInstance.IMPRINT) {
            ContentWorldgen.Entry found = ContentWorldgen.entry(key);
            return found == null ? null : found.def().shape().respawn();
        }
        if (kind == RespawnInstance.MAP) {
            StructureMapDef map = ContentStructureMaps.def(key);
            return map == null ? null : map.respawn();
        }
        if (kind == RespawnInstance.CAVE) {
            CaveRegionDef region = ContentCaveRegions.def(key);
            return region == null ? null : region.respawn();
        }
        VillageDef village = ContentVillages.byKey(def);
        return village == null ? null : village.respawn();
    }

    public static void record(ServerLevel level, @Nullable RespawnDef respawn, RespawnInstance fresh, BoundingBox window, LevelAccessor source) {
        if (respawn == null) { return; }
        RespawnData data = RespawnData.of(level);
        if (!data.admits(fresh.id)) { return; }
        fresh.last = level.getGameTime();
        scan(level, fresh, window, source);
        data.add(fresh);
    }

    private static void scan(ServerLevel level, RespawnInstance fresh, BoundingBox window, LevelAccessor source) {
        int minX = Math.max(window.minX(), fresh.box[0]);
        int minY = Math.max(window.minY(), fresh.box[1]);
        int minZ = Math.max(window.minZ(), fresh.box[2]);
        int maxX = Math.min(window.maxX(), fresh.box[3]);
        int maxY = Math.min(window.maxY(), fresh.box[4]);
        int maxZ = Math.min(window.maxZ(), fresh.box[5]);
        if (minX > maxX || minY > maxY || minZ > maxZ) { return; }
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!source.hasChunk(chunkX, chunkZ)) { continue; }
                ChunkAccess chunk = source.getChunk(chunkX, chunkZ);
                for (BlockPos at : new ArrayList<>(chunk.getBlockEntitiesPos())) {
                    if (at.getX() < minX || at.getX() > maxX || at.getY() < minY || at.getY() > maxY || at.getZ() < minZ || at.getZ() > maxZ) { continue; }
                    BlockEntity tile = chunk.getBlockEntity(at);
                    if (tile != null) { take(level, fresh, tile); }
                }
            }
        }
    }

    private static void take(ServerLevel level, RespawnInstance fresh, BlockEntity tile) {
        long at = tile.getBlockPos().asLong();
        if (tile instanceof RandomizableContainer container && container.getLootTable() != null) { fresh.loot.put(at, container.getLootTable().identifier().toString()); }
        if (tile instanceof SpawnerBlockEntity) {
            CompoundTag saved = tile.saveWithFullMetadata(level.registryAccess());
            fresh.spawn.put(at, saved);
        }
    }
}
