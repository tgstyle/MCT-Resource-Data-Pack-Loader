package mctmods.resourcedatapackloader.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import javax.annotation.Nullable;

public final class StructureLoot {
    private StructureLoot() {}

    public static boolean stock(@Nullable BlockEntity entity, ResourceLocation table, long seed) {
        if (!(entity instanceof RandomizableContainerBlockEntity container)) { return false; }
        container.setLootTable(table, seed);
        return true;
    }

    public static void stock(BlockGetter level, RandomSource random, BlockPos at, ResourceLocation table) { RandomizableContainerBlockEntity.setLootTable(level, random, at, table); }
}
