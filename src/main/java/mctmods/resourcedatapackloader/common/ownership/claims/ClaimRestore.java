package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.pregen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.common.pregen.ContentPristine;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ClaimRestore {
    private static final Codec<PalettedContainer<BlockState>> BLOCK_STATES = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
    private static String lastKey = "";
    @Nullable private static CompoundTag lastChunk;

    private ClaimRestore() {}

    public static boolean covers(ServerLevel level, int chunkX, int chunkZ) { return savedChunk(level, chunkX, chunkZ) != null; }

    public static boolean section(ServerLevel level, int chunkX, int chunkZ, int sectionY) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        CompoundTag saved = savedChunk(level, chunkX, chunkZ);
        if (saved == null || chunk == null || ClaimSections.outOfRange(chunk, sectionY)) { return false; }
        CompoundTag held = sectionOf(saved, sectionY);
        PalettedContainer<BlockState> states = null;
        if (held != null && held.contains("block_states", Tag.TAG_COMPOUND)) {
            states = BLOCK_STATES.parse(NbtOps.INSTANCE, held.getCompound("block_states")).resultOrPartial(error -> ContentLog.LOGGER.warn("The pristine copy of chunk {}, {} has a damaged section {}: {}", chunkX, chunkZ, sectionY, error)).orElse(null);
            if (states == null) { return false; }
        }
        ClaimSections.write(level, chunk, sectionY, states);
        ListTag entities = saved.getList("block_entities", Tag.TAG_COMPOUND);
        for (int i = 0; i < entities.size(); i++) {
            CompoundTag tag = entities.getCompound(i);
            if (tag.getInt("y") >> 4 != sectionY) { continue; }
            BlockPos at = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            BlockEntity entity = BlockEntity.loadStatic(at, chunk.getBlockState(at), tag, level.registryAccess());
            if (entity != null) { chunk.addAndRegisterBlockEntity(entity); }
        }
        return true;
    }

    public static void finish(ServerLevel level, int chunkX, int chunkZ) {
        lastKey = "";
        lastChunk = null;
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (chunk == null) { return; }
        Heightmap.primeHeightmaps(chunk, ChunkStatus.FINAL_HEIGHTMAPS);
        chunk.initializeLightSources();
        level.getChunkSource().getLightEngine().propagateLightSources(chunk.getPos());
        chunk.setUnsaved(true);
        ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null);
        for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)) { player.connection.send(packet); }
    }

    @Nullable private static CompoundTag savedChunk(ServerLevel level, int chunkX, int chunkZ) {
        String key = level.dimension().location() + ":" + chunkX + ":" + chunkZ;
        if (key.equals(lastKey)) { return lastChunk; }
        CompoundTag found = read(level, chunkX, chunkZ);
        lastKey = key;
        lastChunk = found;
        return found;
    }

    @Nullable private static CompoundTag read(ServerLevel level, int chunkX, int chunkZ) {
        Path holding = ContentPristine.holdingFor(level.getServer());
        Path folder = ContentPregenDimensions.regionFolder(level, holding);
        if (!Files.isRegularFile(folder.resolve("r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca"))) { return null; }
        try (ChunkStorage storage = new ChunkStorage(new RegionStorageInfo(holding.getFileName().toString(), level.dimension(), "chunk"), folder, level.getServer().getFixerUpper(), false)) {
            Optional<CompoundTag> data = storage.read(new ChunkPos(chunkX, chunkZ)).join();
            return data.isPresent() && ChunkSerializer.getChunkTypeFromTag(data.get()) == ChunkType.LEVELCHUNK ? data.get() : null;
        }
        catch (IOException | RuntimeException unreadable) {
            ContentLog.LOGGER.warn("The pristine copy in {} could not be read at chunk {}, {}", folder, chunkX, chunkZ, unreadable);
            return null;
        }
    }

    @Nullable private static CompoundTag sectionOf(CompoundTag saved, int sectionY) {
        ListTag sections = saved.getList("sections", Tag.TAG_COMPOUND);
        for (int i = 0; i < sections.size(); i++) {
            CompoundTag section = sections.getCompound(i);
            if (section.getByte("Y") == sectionY) { return section; }
        }
        return null;
    }
}
