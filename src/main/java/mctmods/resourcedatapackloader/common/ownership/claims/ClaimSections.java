package mctmods.resourcedatapackloader.common.ownership.claims;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.lighting.LightEngine;

final class ClaimSections {
    private ClaimSections() {}

    static boolean outOfRange(LevelChunk chunk, int sectionY) {
        int index = chunk.getSectionIndexFromSectionY(sectionY);
        return index < 0 || index >= chunk.getSectionsCount();
    }

    static void write(ServerLevel level, LevelChunk chunk, int sectionY, @Nullable PalettedContainerRO<BlockState> source) {
        for (BlockPos held : new ArrayList<>(chunk.getBlockEntitiesPos())) {
            if (held.getY() >> 4 == sectionY) { chunk.removeBlockEntity(held); }
        }
        LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY));
        boolean wasEmpty = section.hasOnlyAir();
        List<BlockPos> changed = new ArrayList<>();
        List<BlockState> before = new ArrayList<>();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    BlockState next = source == null ? air : source.get(x, y, z);
                    BlockState old = section.setBlockState(x, y, z, next, false);
                    if (old == next) { continue; }
                    changed.add(new BlockPos(chunk.getPos().getBlockX(x), (sectionY << 4) + y, chunk.getPos().getBlockZ(z)));
                    before.add(old);
                }
            }
        }
        ThreadedLevelLightEngine light = level.getChunkSource().getLightEngine();
        if (wasEmpty != section.hasOnlyAir()) { light.updateSectionStatus(SectionPos.of(chunk.getPos(), sectionY), section.hasOnlyAir()); }
        for (int i = 0; i < changed.size(); i++) {
            BlockPos at = changed.get(i);
            BlockState old = before.get(i);
            BlockState next = section.getBlockState(at.getX() & 15, at.getY() & 15, at.getZ() & 15);
            if (LightEngine.hasDifferentLightProperties(chunk, at, old, next)) {
                chunk.getSkyLightSources().update(chunk, at.getX() & 15, at.getY(), at.getZ() & 15);
                light.checkBlock(at);
            }
            level.onBlockStateChange(at, old, next);
        }
        chunk.setUnsaved(true);
    }
}
