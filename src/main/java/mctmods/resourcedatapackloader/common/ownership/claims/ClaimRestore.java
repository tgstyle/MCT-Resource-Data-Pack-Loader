package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.pregen.ContentPristine;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ClaimRestore {
    private static final Codec<PalettedContainer<BlockState>> STATES = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
    private static String lastKey = "";
    @Nullable private static CompoundTag lastChunk;

    private ClaimRestore() {}

    public static boolean covers(ServerLevel level, int chunkX, int chunkZ) { return chunkOf(level, chunkX, chunkZ) != null; }

    public static boolean section(ServerLevel level, int chunkX, int chunkZ, int sectionY) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        CompoundTag saved = chunkOf(level, chunkX, chunkZ);
        if (saved == null || chunk == null || sectionY < level.getMinSection() || sectionY >= level.getMaxSection()) { return false; }
        PalettedContainer<BlockState> states = statesOf(saved, sectionY, chunkX, chunkZ);
        if (states == null) { return false; }
        replace(chunk, sectionY, states);
        ListTag held = saved.getList("block_entities", 10);
        for (int i = 0; i < held.size(); i++) {
            CompoundTag tag = held.getCompound(i);
            BlockPos at = BlockEntity.getPosFromTag(tag);
            if (at.getY() >> 4 != sectionY) { continue; }
            BlockEntity entity = BlockEntity.loadStatic(at, chunk.getBlockState(at), tag);
            if (entity != null) { chunk.setBlockEntity(entity); }
        }
        return true;
    }

    public static void finish(ServerLevel level, int chunkX, int chunkZ) {
        lastKey = "";
        lastChunk = null;
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (chunk == null) { return; }
        Heightmap.primeHeightmaps(chunk, ChunkStatus.FULL.heightmapsAfter());
        chunk.initializeLightSources();
        ChunkPos pos = chunk.getPos();
        ThreadedLevelLightEngine light = level.getChunkSource().getLightEngine();
        light.retainData(pos, false);
        light.setLightEnabled(pos, false);
        for (int y = light.getMinLightSection(); y < light.getMaxLightSection(); y++) {
            light.queueSectionData(LightLayer.BLOCK, SectionPos.of(pos, y), null);
            light.queueSectionData(LightLayer.SKY, SectionPos.of(pos, y), null);
        }
        for (int y = level.getMinSection(); y < level.getMaxSection(); y++) { light.updateSectionStatus(SectionPos.of(pos, y), true); }
        light.initializeLight(chunk, true);
        light.propagateLightSources(pos);
        chunk.setUnsaved(true);
        for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(pos, false)) { player.connection.send(new ClientboundLevelChunkWithLightPacket(chunk, light, null, null)); }
    }

    static void replace(LevelChunk chunk, int sectionY, PalettedContainerRO<BlockState> source) {
        for (BlockPos at : new ArrayList<>(chunk.getBlockEntitiesPos())) {
            if (at.getY() >> 4 == sectionY) { chunk.removeBlockEntity(at); }
        }
        LevelChunkSection live = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY));
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) { live.setBlockState(x, y, z, source.get(x, y, z), false); }
            }
        }
        chunk.setUnsaved(true);
    }

    @Nullable private static PalettedContainer<BlockState> statesOf(CompoundTag saved, int sectionY, int chunkX, int chunkZ) {
        ListTag sections = saved.getList("sections", 10);
        for (int i = 0; i < sections.size(); i++) {
            CompoundTag section = sections.getCompound(i);
            if (section.getByte("Y") != sectionY) { continue; }
            if (!section.contains("block_states", 10)) { break; }
            DataResult<PalettedContainer<BlockState>> parsed = STATES.parse(NbtOps.INSTANCE, section.getCompound("block_states"));
            return parsed.resultOrPartial(unreadable -> ContentLog.LOGGER.warn("The pristine copy holds unreadable blocks at section {}, {}, {}: {}", chunkX, sectionY, chunkZ, unreadable)).orElse(null);
        }
        return new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
    }

    @Nullable private static CompoundTag chunkOf(ServerLevel level, int chunkX, int chunkZ) {
        MinecraftServer server = level.getServer();
        Path folder = DimensionType.getStorageFolder(level.dimension(), ContentPristine.holdingFor(server)).resolve("region");
        String key = folder + ":" + chunkX + ":" + chunkZ;
        if (key.equals(lastKey)) { return lastChunk; }
        CompoundTag tag = read(server, folder, chunkX, chunkZ);
        lastKey = key;
        lastChunk = tag;
        return tag;
    }

    @Nullable private static CompoundTag read(MinecraftServer server, Path folder, int chunkX, int chunkZ) {
        if (!Files.isRegularFile(folder.resolve("r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca"))) { return null; }
        try (ChunkStorage storage = new ChunkStorage(folder, server.getFixerUpper(), false)) {
            return storage.read(new ChunkPos(chunkX, chunkZ)).join().filter(tag -> ChunkSerializer.getChunkTypeFromTag(tag) == ChunkStatus.ChunkType.LEVELCHUNK).orElse(null);
        }
        catch (IOException | RuntimeException unreadable) {
            ContentLog.LOGGER.warn("The pristine copy of {} could not be read at chunk {}, {}", folder, chunkX, chunkZ, unreadable);
            return null;
        }
    }
}
