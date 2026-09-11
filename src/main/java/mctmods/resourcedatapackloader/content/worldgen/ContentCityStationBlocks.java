package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityStationBlocks extends StructureProcessor {
    private static final StructureProcessorType<ContentCityStationBlocks> TYPE = () -> Codec.unit(new ContentCityStationBlocks(Blocks.STONE.defaultBlockState()));
    private final BlockState lining;

    public ContentCityStationBlocks(BlockState lining) { this.lining = lining; }

    @Override @Nonnull public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos origin, @Nonnull BlockPos pivot, @Nonnull StructureTemplate.StructureBlockInfo was, StructureTemplate.StructureBlockInfo now, @Nonnull StructurePlaceSettings how, @Nullable StructureTemplate template) {
        if (!now.state().is(Blocks.SPONGE)) { return now; }
        return new StructureTemplate.StructureBlockInfo(now.pos(), lining, now.nbt());
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}
