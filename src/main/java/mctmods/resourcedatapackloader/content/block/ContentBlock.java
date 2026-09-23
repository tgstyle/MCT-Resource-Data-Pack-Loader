package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentDrops;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import java.util.function.BiConsumer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBlock extends Block {
    public static final String BUSH = "bush";
    private final BlockDef def;
    @Nullable private final VoxelShape shape;
    private final AmountDef expDrop;

    public ContentBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.shape = shape(def);
        this.expDrop = expRange(def);
    }

    @Nullable public static VoxelShape shape(BlockDef def) {
        double[] bounds = def.bounds();
        return bounds == null ? null : Block.box(bounds[0] * 16.0D, bounds[1] * 16.0D, bounds[2] * 16.0D, bounds[3] * 16.0D, bounds[4] * 16.0D, bounds[5] * 16.0D);
    }

    public static AmountDef expRange(BlockDef def) { return def.expDropMax() <= def.expDropMin() ? AmountDef.of(def.expDropMin()) : new AmountDef(def.expDropMin(), def.expDropMax()); }

    public BlockDef getDef() { return def; }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override public int getExpDrop(@Nonnull BlockState state, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, @Nonnull ItemStack tool) { return experience(def, expDrop, level, breaker); }

    public static int experience(BlockDef def, AmountDef range, LevelAccessor level, @Nullable Entity breaker) { return breaker instanceof Player ? rolled(def, range, level) : 0; }

    public static int dug(BlockState state, LevelAccessor level) {
        BlockDef def = defOf(state);
        return def == null ? 0 : rolled(def, expRange(def), level);
    }

    private static int rolled(BlockDef def, AmountDef range, LevelAccessor level) { return def.dropsExperience() ? Math.max(0, range.pick(level.getRandom())) : 0; }

    @Nullable private static BlockDef defOf(BlockState state) { return state.getBlock() instanceof ContentBlock block ? block.getDef() : state.getBlock() instanceof ContentContainerBlock held ? held.getDef() : null; }

    public static void onDrops(BlockDropsEvent event) {
        BlockDef def = defOf(event.getState());
        if (def == null || def.silkHarvest() || event.getDroppedExperience() > 0 || event.getTool().isEmpty()) { return; }
        Holder<Enchantment> silk = event.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SILK_TOUCH);
        if (event.getTool().getEnchantmentLevel(silk) <= 0) { return; }
        event.setDroppedExperience(event.getState().getExpDrop(event.getLevel(), event.getPos(), event.getBlockEntity(), event.getBreaker(), event.getTool()));
    }

    @Override protected void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState replaced, boolean moving) {
        ContentDrops.removed(level, pos, state, replaced);
        super.onRemove(state, level, pos, replaced, moving);
    }

    @Override public boolean onTreeGrow(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BiConsumer<BlockPos, BlockState> placeFunction, @Nonnull RandomSource randomSource, @Nonnull BlockPos pos, @Nonnull TreeConfiguration config) { return def.behavesAs().contains(BUSH); }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }

    @Override @Nonnull public TriState canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos soilPosition, @Nonnull Direction facing, @Nonnull BlockState plant) { return sustains(def, facing, plant); }

    public static TriState sustains(BlockDef def, Direction facing, BlockState plant) {
        if (facing != Direction.UP || !def.behavesAs().contains(BUSH)) { return TriState.DEFAULT; }
        Block block = plant.getBlock();
        boolean dirtPlant = block instanceof FlowerBlock || block instanceof TallFlowerBlock || block instanceof TallGrassBlock || block instanceof SaplingBlock || block instanceof ContentSaplingBlock || block instanceof ContentBushBlock || block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN;
        return dirtPlant ? TriState.TRUE : TriState.DEFAULT;
    }

    @Override @Nonnull protected ItemInteractionResult useItemOn(@Nonnull ItemStack held, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (def.opensWith() == null) { return super.useItemOn(held, state, level, pos, player, hand, hit); }
        Item key = ContentStacks.item(def.opensWith());
        if (key == null || !held.is(key)) { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
        if (level instanceof ServerLevel server) { open(server, pos, state, player, held); }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override @Nonnull protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hit) {
        if (def.opensWith() == null) { return super.useWithoutItem(state, level, pos, player, hit); }
        if (!level.isClientSide) { player.displayClientMessage(Component.translatable(getDescriptionId() + ".locked"), true); }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void open(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack held) {
        SoundEvent opening = ContentSounds.find(def.openSound());
        if (opening == null) { opening = getSoundType(state, level, pos, player).getBreakSound(); }
        level.playSound(null, pos, opening, SoundSource.BLOCKS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(this);
        if (entry != null) {
            for (ItemStack stack : ContentDrops.roll(entry.variant(), level.getRandom(), 0)) { popResource(level, pos, stack); }
            ContentDrops.release(level, pos, entry.variant());
        }
        if (!player.getAbilities().instabuild) { held.shrink(1); }
        level.levelEvent(2001, pos, Block.getId(state));
        level.removeBlock(pos, false);
    }
}
