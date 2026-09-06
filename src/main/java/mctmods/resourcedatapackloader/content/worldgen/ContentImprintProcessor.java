package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import javax.annotation.Nullable;

public final class ContentImprintProcessor extends StructureProcessor {
    private static final StructureProcessorType<ContentImprintProcessor> TYPE = () -> MapCodec.unit(new ContentImprintProcessor(null));
    @Nullable private final ContentPlacer placer;

    public ContentImprintProcessor(@Nullable ContentPlacer placer) { this.placer = placer; }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo raw,
            StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        if (placer == null) { return info; }
        return placer.occupied(info.pos().getX(), info.pos().getY(), info.pos().getZ()) ? null : info;
    }

    @Override protected StructureProcessorType<?> getType() { return TYPE; }
}
