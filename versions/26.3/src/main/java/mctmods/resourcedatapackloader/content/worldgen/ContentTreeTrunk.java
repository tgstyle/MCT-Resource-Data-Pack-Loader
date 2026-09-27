package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.loot.LootFunctions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public final class ContentTreeTrunk extends TrunkPlacer {
    public static final String NAME = "straight_trunk_placer";
    public static final MapCodec<ContentTreeTrunk> CODEC = RecordCodecBuilder.mapCodec(parts -> trunkPlacerParts(parts).apply(parts, ContentTreeTrunk::new));
    public static final DeferredRegister<TrunkPlacerType<?>> REGISTER = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, LootFunctions.NAMESPACE);
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<ContentTreeTrunk>> TYPE = REGISTER.register(NAME, () -> new TrunkPlacerType<>(CODEC));

    public ContentTreeTrunk(int baseHeight, int heightRandA, int heightRandB) { super(baseHeight, heightRandA, heightRandB); }

    @Nonnull @Override protected TrunkPlacerType<?> type() { return TYPE.get(); }

    @Nonnull @Override public List<FoliagePlacer.FoliageAttachment> placeTrunk(@Nonnull WorldGenLevel level, @Nonnull BiConsumer<BlockPos, BlockState> setter, @Nonnull RandomSource random, int height, @Nonnull BlockPos position, @Nonnull TreeFeature tree) {
        BlockPos below = position.below();
        BlockState soil = level.getBlockState(below);
        if (!soil.onTreeGrow(level, setter, random, below, tree) && (soil.is(Blocks.GRASS_BLOCK) || soil.is(Blocks.FARMLAND))) {
            BlockState dirt = tree.belowTrunkProvider().value().getOptionalState(level, random, below);
            if (dirt != null) { setter.accept(below, dirt); }
        }
        for (int i = 0; i < height; i++) { placeLog(level, setter, random, position.above(i), tree); }
        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(position.above(height), 0, false));
    }
}
