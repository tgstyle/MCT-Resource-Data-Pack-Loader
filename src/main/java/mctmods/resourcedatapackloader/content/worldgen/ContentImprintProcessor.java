package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentImprintProcessor extends StructureProcessor {
    private static final StructureProcessorType<ContentImprintProcessor> TYPE = () -> Codec.unit(new ContentImprintProcessor(null));
    @Nullable private final ContentPlacer placer;

    public ContentImprintProcessor(@Nullable ContentPlacer placer) { this.placer = placer; }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        if (placer == null) { return info; }
        return placer.occupied(info.pos().getX(), info.pos().getY(), info.pos().getZ()) ? null : info;
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
