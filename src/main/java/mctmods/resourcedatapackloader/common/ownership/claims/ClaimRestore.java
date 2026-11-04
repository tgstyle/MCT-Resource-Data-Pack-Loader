package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.pregen.ContentPristine;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ClaimRestore {
    private static String lastKey = "";
    @Nullable private static SerializableChunkData lastData;

    private ClaimRestore() {}

    public static boolean covers(ServerLevel world, int chunkX, int chunkZ) { return dataOf(world, chunkX, chunkZ) != null; }

    public static boolean section(ServerLevel world, int chunkX, int chunkZ, int sectionY) {
        LevelChunk chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);
        SerializableChunkData data = dataOf(world, chunkX, chunkZ);
        if (chunk == null || data == null || sectionY < world.getMinSectionY() || sectionY > world.getMaxSectionY()) { return false; }
        clear(chunk, sectionY);
        LevelChunkSection restored = new LevelChunkSection(world.palettedContainerFactory());
        for (SerializableChunkData.SectionData saved : data.sectionData()) {
            if (saved.y() == sectionY && saved.chunkSection() != null) { restored = saved.chunkSection().copy(); }
        }
        chunk.getSections()[world.getSectionIndexFromSectionY(sectionY)] = restored;
        for (CompoundTag tag : data.blockEntities()) {
            BlockPos at = BlockEntity.getPosFromTag(chunk.getPos(), tag);
            if (at.getY() >> 4 != sectionY) { continue; }
            BlockEntity entity = BlockEntity.loadStatic(at, chunk.getBlockState(at), tag, world.registryAccess());
            if (entity != null) { chunk.setBlockEntity(entity); }
        }
        chunk.markUnsaved();
        return true;
    }

    public static void finish(ServerLevel world, int chunkX, int chunkZ) {
        lastKey = "";
        lastData = null;
        LevelChunk chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (chunk == null) { return; }
        Heightmap.primeHeightmaps(chunk, ChunkStatus.FULL.heightmapsAfter());
        chunk.initializeLightSources();
        ThreadedLevelLightEngine light = world.getChunkSource().getLightEngine();
        ChunkPos pos = chunk.getPos();
        for (int y = light.getMinLightSection(); y < light.getMaxLightSection(); y++) {
            light.queueSectionData(LightLayer.BLOCK, SectionPos.of(pos, y), null);
            light.queueSectionData(LightLayer.SKY, SectionPos.of(pos, y), null);
        }
        LevelChunkSection[] sections = chunk.getSections();
        for (int index = 0; index < sections.length; index++) { light.updateSectionStatus(SectionPos.of(pos, world.getSectionYFromSectionIndex(index)), sections[index].hasOnlyAir()); }
        light.propagateLightSources(pos);
        chunk.markUnsaved();
        for (ServerPlayer player : world.getChunkSource().chunkMap.getPlayers(pos, false)) { player.connection.send(new ClientboundLevelChunkWithLightPacket(chunk, light, null, null)); }
    }

    static void clear(LevelChunk chunk, int sectionY) {
        for (BlockPos at : new ArrayList<>(chunk.getBlockEntitiesPos())) {
            if (at.getY() >> 4 == sectionY) { chunk.removeBlockEntity(at); }
        }
    }

    @Nullable private static SerializableChunkData dataOf(ServerLevel world, int chunkX, int chunkZ) {
        Path folder = DimensionType.getStorageFolder(world.dimension(), ContentPristine.holdingFor(world.getServer())).resolve("region");
        String key = folder + ":" + chunkX + ":" + chunkZ;
        if (key.equals(lastKey)) { return lastData; }
        SerializableChunkData data = read(world, folder, chunkX, chunkZ);
        lastKey = key;
        lastData = data;
        return data;
    }

    @Nullable private static SerializableChunkData read(ServerLevel world, Path folder, int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        if (!Files.isRegularFile(folder.resolve("r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca"))) { return null; }
        RegionStorageInfo info = new RegionStorageInfo(ContentPristine.FOLDER, world.dimension(), "chunk");
        try (SimpleRegionStorage storage = new SimpleRegionStorage(info, folder, DataFixers.getDataFixer(), false, DataFixTypes.CHUNK)) {
            Optional<CompoundTag> held = storage.read(pos).join();
            if (held.isEmpty()) { return null; }
            CompoundTag tag = storage.upgradeChunkTag(held.get(), -1);
            return SerializableChunkData.getChunkStatusFromTag(tag) == ChunkStatus.FULL ? SerializableChunkData.parse(world, world.palettedContainerFactory(), tag) : null;
        }
        catch (IOException | RuntimeException unreadable) {
            ContentLog.LOGGER.warn("The pristine copy of {} could not be read at chunk {}, {}", folder, chunkX, chunkZ, unreadable);
            return null;
        }
    }
}
