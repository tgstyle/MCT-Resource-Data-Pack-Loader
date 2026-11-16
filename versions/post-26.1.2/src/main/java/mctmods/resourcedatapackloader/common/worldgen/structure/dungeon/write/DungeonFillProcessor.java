package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPopulation;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

final class DungeonFillProcessor implements StructureProcessor {
    private static final MapCodec<DungeonFillProcessor> CODEC = MapCodec.unit(() -> new DungeonFillProcessor(null, null, 0L));
    @Nullable private final FillRules rules;
    @Nullable private final DungeonPopulation.Draw draw;
    private final long seed;

    DungeonFillProcessor(@Nullable FillRules rules, @Nullable DungeonPopulation.Draw draw, long seed) {
        this.rules = rules;
        this.draw = draw;
        this.seed = seed;
    }

    @Override public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) { return rules == null || draw == null ? info : DungeonTemplates.fill(rules, draw, seed, info); }

    @Override @Nonnull public MapCodec<? extends StructureProcessor> codec() { return CODEC; }
}
