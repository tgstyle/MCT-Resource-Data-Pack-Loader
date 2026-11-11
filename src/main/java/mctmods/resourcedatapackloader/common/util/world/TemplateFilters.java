package mctmods.resourcedatapackloader.common.util.world;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class TemplateFilters extends StructureProcessor {
    private static final StructureProcessorType<TemplateFilters> TYPE = () -> MapCodec.unit(new TemplateFilters(false));
    private final boolean brokenOnly;

    private TemplateFilters(boolean brokenOnly) { this.brokenOnly = brokenOnly; }

    public static StructureProcessor broken() { return new TemplateFilters(true); }

    public static StructureProcessor nothing() { return new TemplateFilters(false); }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw,
            @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        if (!brokenOnly) { return null; }
        BlockState there = level.getBlockState(info.pos());
        return there != info.state() && (there.isAir() || there.canBeReplaced()) ? info : null;
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
