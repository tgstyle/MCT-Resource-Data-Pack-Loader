package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public class ContentSaplingBlock extends SaplingBlock {
    private final BlockDef def;
    private final SaplingDef sapling;
    private Set<Block> soil = Collections.emptySet();

    public ContentSaplingBlock(BlockDef def, ResourceLocation id, Properties properties) {
        super(new Grower(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_tree"))), properties);
        this.def = def;
        this.sapling = def.sapling() == null ? new SaplingDef(List.of(), 2, 7, 9, "", "minecraft:oak_log", "minecraft:oak_leaves", 4, false) : def.sapling();
        if (this.sapling.usesStructure()) { ContentLog.LOGGER.warn("Sapling {} grows into structure '{}', which this line does not carry yet, so it grows a generated tree instead", id, this.sapling.structure()); }
    }

    public BlockDef getDef() { return def; }

    public void resolveSoil() {
        Set<Block> resolved = new HashSet<>();
        for (String name : sapling.soil()) {
            ResourceLocation key = ResourceLocation.tryParse(name);
            Block block = key == null ? null : ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) { resolved.add(block); }
            else { ContentLog.LOGGER.error("Sapling {} names soil {}, which is not registered, leaving it out", def.key(), name); }
        }
        soil = resolved;
    }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        if (soil.isEmpty()) { return super.mayPlaceOn(state, level, pos); }
        return soil.contains(state.getBlock());
    }

    @Override public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) < sapling.light()) { return; }
        if (random.nextInt(Math.max(1, sapling.chance())) != 0) { return; }
        advanceTree(level, pos, state, random);
    }

    private static final class Grower extends AbstractTreeGrower {
        private final ResourceKey<ConfiguredFeature<?, ?>> feature;

        Grower(ResourceKey<ConfiguredFeature<?, ?>> feature) { this.feature = feature; }

        @Override @Nonnull protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@Nonnull RandomSource random, boolean flowers) { return feature; }
    }
}
