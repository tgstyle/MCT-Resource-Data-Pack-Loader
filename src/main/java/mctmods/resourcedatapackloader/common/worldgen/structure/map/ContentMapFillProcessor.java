package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentMapFillProcessor extends StructureProcessor {
    private static final StructureProcessorType<ContentMapFillProcessor> TYPE = () -> Codec.unit(new ContentMapFillProcessor(null, 0L));
    @Nullable private final FillRules rules;
    private final long seed;

    public ContentMapFillProcessor(@Nullable StructureMapDef def, long seed) {
        this.rules = def == null ? null : new FillRules(def.key(), def.spawners(), def.loot());
        this.seed = seed;
    }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) { return rules == null ? info : ContentMapFill.fill(rules, seed, info, null, null); }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
