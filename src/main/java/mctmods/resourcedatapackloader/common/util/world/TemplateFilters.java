package mctmods.resourcedatapackloader.common.util.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TemplateFilters {
    private static final StructureProcessor BROKEN = new Broken();
    private static final StructureProcessor NONE = new None();

    private TemplateFilters() {}

    public static StructureProcessor broken() { return BROKEN; }

    public static StructureProcessor none() { return NONE; }

    private static final class Broken extends StructureProcessor {
        private static final StructureProcessorType<Broken> TYPE = () -> Codec.unit(new Broken());

        @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
            BlockState there = level.getBlockState(info.pos());
            return there != info.state() && (there.isAir() || there.canBeReplaced()) ? info : null;
        }

        @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
    }

    private static final class None extends StructureProcessor {
        private static final StructureProcessorType<None> TYPE = () -> Codec.unit(new None());

        @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) { return null; }

        @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
    }
}
