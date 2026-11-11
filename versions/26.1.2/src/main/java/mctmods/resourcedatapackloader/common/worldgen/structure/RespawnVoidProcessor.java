package mctmods.resourcedatapackloader.common.worldgen.structure;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class RespawnVoidProcessor extends StructureProcessor {
    private static final StructureProcessorType<RespawnVoidProcessor> TYPE = () -> MapCodec.unit(new RespawnVoidProcessor());

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) { return null; }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
