package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ContentSaplingBlock extends BushBlock implements BonemealableBlock {
    private static final ThreadLocal<IntegerProperty> PENDING = new ThreadLocal<>();
    private final BlockDef def;
    private final SaplingDef sapling;
    private final IntegerProperty stage;
    private final TreeGrower grower;
    private Set<Block> soil = Collections.emptySet();

    public static ContentSaplingBlock create(BlockDef def, ResourceLocation id, Properties properties) {
        SaplingDef sapling = def.sapling() == null ? new SaplingDef(List.of(), 2, 7, 9, "", List.of(), "minecraft:oak_log", "minecraft:oak_leaves", 4, false) : def.sapling();
        PENDING.set(IntegerProperty.create("stage", 0, Math.max(1, sapling.stages() - 1)));
        try { return new ContentSaplingBlock(def, sapling, id, properties, PENDING.get()); }
        finally { PENDING.remove(); }
    }

    private ContentSaplingBlock(BlockDef def, SaplingDef sapling, ResourceLocation id, Properties properties, IntegerProperty stage) {
        super(properties);
        this.def = def;
        this.sapling = sapling;
        this.stage = stage;
        this.grower = new TreeGrower(id.toString(), Optional.empty(), Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_tree"))), Optional.empty());
        registerDefaultState(stateDefinition.any().setValue(stage, 0));
    }

    @Override @Nonnull protected MapCodec<? extends BushBlock> codec() { return MapCodec.unit(this); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(PENDING.get()); }

    public void resolveSoil() { soil = ContentRegistry.resolveSoil(sapling.soil(), def.key()); }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return ContentBushBlock.SHAPE; }

    @Override protected boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) { return ContentRegistry.sustains(soil, level, pos.below(), state); }

    @Override protected void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) { return; }
        if (level.getMaxLocalRawBrightness(pos.above()) < sapling.light()) { return; }
        if (random.nextInt(Math.max(1, sapling.chance())) != 0) { return; }
        advance(level, pos, state, random);
    }

    @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) { return true; }

    @Override public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) { return true; }

    @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) { advance(level, pos, state, random); }

    private void advance(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int current = state.getValue(stage);
        if (current < Math.max(1, sapling.stages()) - 1) {
            level.setBlock(pos, state.setValue(stage, current + 1), 4);
            return;
        }
        if (!sapling.growsVanilla()) { placeStructure(level, pos, random); }
        else if (ContentRegistry.sustains(soil, level, pos.below(), state)) { grower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random); }
    }

    private void placeStructure(ServerLevel level, BlockPos pos, RandomSource random) {
        String grown = sapling.growsInto(random);
        ResourceLocation named = ResourceLocation.tryParse(grown);
        Optional<StructureTemplate> held = named == null ? Optional.empty() : level.getStructureManager().get(named);
        if (held.isEmpty()) {
            ContentLog.LOGGER.error("Sapling {} grows into structure '{}', which could not be loaded, so it stays a sapling", def.key(), grown);
            return;
        }
        Vec3i size = held.get().getSize();
        BlockPos origin = pos.offset(-(size.getX() / 2), 0, -(size.getZ() / 2));
        level.removeBlock(pos, false);
        held.get().placeInWorld(level, origin, origin, new StructurePlaceSettings(), random, Block.UPDATE_ALL);
    }
}
