package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.IContentSoil;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.types.ContentItemTypes;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation") public final class ContentCropBlock extends CropBlock implements IContentSoil {
    private final BlockDef def;
    private final ContentSoil soil;
    private Item seed = Items.WHEAT_SEEDS;
    private Item produce = Items.WHEAT;

    public ContentCropBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.soil = new ContentSoil(def.key());
    }

    @Override public void resolveSoil(@Nonnull ContentRegistry.BlockEntry entry) { soil.resolve(soilNames(entry)); }

    private static List<String> soilNames(ContentRegistry.BlockEntry crop) {
        if (crop.def().growth() != null && !crop.def().growth().soil().isEmpty()) { return crop.def().growth().soil(); }
        List<String> names = new ArrayList<>();
        for (ItemDef item : ContentRegistry.itemDefs()) {
            if (!ContentItemTypes.SEED.equals(item.type()) || !crop.id().toString().equals(item.crop()) || item.soil().isEmpty()) { continue; }
            names.add(item.soil());
        }
        return names;
    }

    public void resolve(@Nullable Item seedItem, @Nullable Item produceItem) {
        if (seedItem != null) { seed = seedItem; }
        if (produceItem != null) { produce = produceItem; }
    }

    public BlockDef def() { return def; }

    public Item seed() { return seed; }

    public Item produce() { return produce; }

    @Override public int getMaxAge() { return def.cropMaxAge(); }

    @Override public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (def.tall() != null) { settle(level, pos); }
    }

    @Override public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (def.tall() != null && !level.isClientSide && oldState.getBlock() != this) { TallPlants.sync(level, pos); }
    }

    @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
        if (!super.isValidBonemealTarget(level, pos, state, isClient)) { return false; }
        int next = getAge(state) + 1;
        return def.tall() == null || !(level instanceof Level world) || TallPlants.limit(world, pos, next) == next;
    }

    @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        super.performBonemeal(level, random, pos, state);
        if (def.tall() != null) { settle(level, pos); }
    }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        TallDef tall = def.tall();
        return tall != null && getAge(state) >= tall.riseAt(1) ? Shapes.block() : super.getShape(state, level, pos, context);
    }

    private void settle(Level level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() != this) { return; }
        int age = getAge(current);
        int limited = TallPlants.limit(level, pos, age);
        if (limited != age) { level.setBlock(pos, getStateForAge(limited), Block.UPDATE_CLIENTS); }
        TallPlants.sync(level, pos);
    }

    @Override @Nonnull protected ItemLike getBaseSeedId() { return seed; }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        if (soil.isEmpty()) { return super.mayPlaceOn(state, level, pos); }
        return soil.contains(state.getBlock());
    }
}
