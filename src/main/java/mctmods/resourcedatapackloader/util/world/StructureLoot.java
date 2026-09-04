package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class StructureLoot {
    private StructureLoot() {}

    public static boolean stock(TileEntity tile, String lootTable, Random random) {
        if (lootTable == null || lootTable.isEmpty() || !(tile instanceof TileEntityLockableLoot)) { return false; }
        ((TileEntityLockableLoot) tile).setLootTable(new ResourceLocation(lootTable), random.nextLong());
        return true;
    }

    public static void stock(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, String lootTable, Random random) {
        if (lootTable == null || lootTable.isEmpty()) { return; }
        int stocked = 0;
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!world.isChunkGeneratedAt(chunkX, chunkZ)) { continue; }
                Chunk chunk = world.getChunk(chunkX, chunkZ);
                List<TileEntity> held = new ArrayList<>(chunk.getTileEntityMap().values());
                for (TileEntity tile : held) {
                    BlockPos at = tile.getPos();
                    if (at.getX() < minX || at.getX() > maxX || at.getY() < minY || at.getY() > maxY || at.getZ() < minZ || at.getZ() > maxZ) { continue; }
                    if (stock(tile, lootTable, random)) { stocked++; }
                }
            }
        }
        if (stocked > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Stocked {} container(s) from {}, {}, {} to {}, {}, {} with the loot table {}", stocked, minX, minY, minZ, maxX, maxY, maxZ, lootTable); }
    }
}
