package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.loot.LootFunctions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public final class ContentTreeTrunk extends TrunkPlacer {
    public static final String NAME = "straight_trunk_placer";
    public static final Codec<ContentTreeTrunk> CODEC = RecordCodecBuilder.create(parts -> trunkPlacerParts(parts).apply(parts, ContentTreeTrunk::new));
    public static final DeferredRegister<TrunkPlacerType<?>> REGISTER = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, LootFunctions.NAMESPACE);
    public static final RegistryObject<TrunkPlacerType<ContentTreeTrunk>> TYPE = REGISTER.register(NAME, () -> new TrunkPlacerType<>(CODEC));

    public ContentTreeTrunk(int baseHeight, int heightRandA, int heightRandB) { super(baseHeight, heightRandA, heightRandB); }

    @Nonnull @Override protected TrunkPlacerType<?> type() { return TYPE.get(); }

    @Nonnull @Override public List<FoliagePlacer.FoliageAttachment> placeTrunk(@Nonnull LevelSimulatedReader level, @Nonnull BiConsumer<BlockPos, BlockState> setter, @Nonnull RandomSource random, int height, @Nonnull BlockPos position, @Nonnull TreeConfiguration config) {
        BlockPos below = position.below();
        BlockState soil = ((LevelReader) level).getBlockState(below);
        if (!soil.onTreeGrow((LevelReader) level, setter, random, below, config) && (soil.is(Blocks.GRASS_BLOCK) || soil.is(Blocks.FARMLAND))) { setter.accept(below, config.dirtProvider.getState(random, below)); }
        for (int i = 0; i < height; i++) { placeLog(level, setter, random, position.above(i), config); }
        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(position.above(height), 0, false));
    }
}
