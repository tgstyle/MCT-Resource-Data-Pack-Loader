package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.StructureMapDef;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentMapFillProcessor extends StructureProcessor {
    private static final StructureProcessorType<ContentMapFillProcessor> TYPE = () -> MapCodec.unit(new ContentMapFillProcessor(null, 0L));
    @Nullable private final StructureMapDef def;
    private final long seed;

    public ContentMapFillProcessor(@Nullable StructureMapDef def, long seed) {
        this.def = def;
        this.seed = seed;
    }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw,
            @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        return def == null ? info : ContentMapFill.fill(def, seed, info);
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
