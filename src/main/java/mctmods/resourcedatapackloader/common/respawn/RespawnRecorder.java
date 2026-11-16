package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldgenDef;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class RespawnRecorder {
    private RespawnRecorder() {}

    @Nullable public static RespawnDef resolve(int kind, String def) {
        if (kind == RespawnInstance.IMPRINT) {
            ResourceLocation key = ResourceLocation.tryParse(def);
            WorldgenDef found = key == null ? null : ContentWorldgen.def(key);
            return found == null ? null : found.shape().respawn();
        }
        if (kind == RespawnInstance.MAP) {
            ResourceLocation key = ResourceLocation.tryParse(def);
            StructureMapDef map = key == null ? null : ContentStructureMaps.def(key);
            return map == null ? null : map.respawn();
        }
        if (kind == RespawnInstance.CAVE) {
            ResourceLocation key = ResourceLocation.tryParse(def);
            CaveRegionDef region = key == null ? null : ContentCaveRegions.def(key);
            return region == null ? null : region.respawn();
        }
        if (kind == RespawnInstance.DUNGEON) {
            DungeonDef dungeon = ContentDungeons.byName(def);
            return dungeon == null ? null : dungeon.respawn();
        }
        VillageDef village = ContentVillages.byKey(def);
        return village == null ? null : village.respawn();
    }

    public static void record(WorldGenLevel level, @Nullable RespawnDef respawn, RespawnInstance fresh, BoundingBox window) {
        if (respawn == null) { return; }
        ServerLevel world = level.getLevel();
        RespawnData data = RespawnData.of(world);
        if (!data.admits(fresh.id)) { return; }
        fresh.last = world.getGameTime();
        scan(level, fresh, window);
        data.add(fresh);
    }

    private static void scan(WorldGenLevel level, RespawnInstance fresh, BoundingBox window) {
        int minX = Math.max(window.minX(), fresh.box[0]);
        int minY = Math.max(window.minY(), fresh.box[1]);
        int minZ = Math.max(window.minZ(), fresh.box[2]);
        int maxX = Math.min(window.maxX(), fresh.box[3]);
        int maxY = Math.min(window.maxY(), fresh.box[4]);
        int maxZ = Math.min(window.maxZ(), fresh.box[5]);
        if (minX > maxX || minY > maxY || minZ > maxZ) { return; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    at.set(x, y, z);
                    BlockEntity found = level.getBlockEntity(at);
                    if (found != null) { take(fresh, found); }
                }
            }
        }
    }

    private static void take(RespawnInstance fresh, BlockEntity found) {
        long at = found.getBlockPos().asLong();
        CompoundTag saved = found.saveWithoutMetadata();
        if (saved.contains("LootTable", Tag.TAG_STRING)) { fresh.loot.put(at, saved.getString("LootTable")); }
        if (found instanceof SpawnerBlockEntity) { fresh.spawn.put(at, found.saveWithFullMetadata()); }
    }
}
