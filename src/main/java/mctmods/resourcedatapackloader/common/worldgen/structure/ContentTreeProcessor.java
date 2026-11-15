package mctmods.resourcedatapackloader.common.worldgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentTreeProcessor extends StructureProcessor {
    private static final StructureProcessorType<ContentTreeProcessor> TYPE = () -> Codec.unit(new ContentTreeProcessor());
    private final List<StructureTemplate.StructureBlockInfo> recorded = new ArrayList<>();

    public ContentTreeProcessor() {}

    @Nonnull public List<StructureTemplate.StructureBlockInfo> recorded() { return recorded; }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        recorded.add(info);
        return null;
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
