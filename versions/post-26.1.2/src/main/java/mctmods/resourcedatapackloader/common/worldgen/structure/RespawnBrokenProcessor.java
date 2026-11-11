package mctmods.resourcedatapackloader.common.worldgen.structure;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class RespawnBrokenProcessor implements StructureProcessor {
    private static final MapCodec<RespawnBrokenProcessor> CODEC = MapCodec.unit(RespawnBrokenProcessor::new);

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        BlockState there = level.getBlockState(info.pos());
        return there != info.state() && (there.isAir() || there.canBeReplaced()) ? info : null;
    }

    @Override @Nonnull public MapCodec<? extends StructureProcessor> codec() { return CODEC; }
}
