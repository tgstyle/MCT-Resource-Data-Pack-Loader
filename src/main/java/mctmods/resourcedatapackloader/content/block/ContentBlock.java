package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentDrops;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlock extends Block {
    private final BlockDef def;
    @Nullable private final VoxelShape shape;
    private final AmountDef expDrop;

    public ContentBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        double[] bounds = def.bounds();
        this.shape = bounds == null ? null : Block.box(bounds[0] * 16.0D, bounds[1] * 16.0D, bounds[2] * 16.0D, bounds[3] * 16.0D, bounds[4] * 16.0D, bounds[5] * 16.0D);
        this.expDrop = def.expDropMax() <= def.expDropMin() ? AmountDef.of(def.expDropMin()) : new AmountDef(def.expDropMin(), def.expDropMax());
    }

    public BlockDef getDef() { return def; }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override public int getExpDrop(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull RandomSource random, @Nonnull BlockPos pos, int fortune, int silkTouch) {
        if (silkTouch > 0 || !def.dropsExperience()) { return 0; }
        return Math.max(0, expDrop.pick(random));
    }

    @Override public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nonnull IPlantable plantable) {
        if (!def.plantTypes().isEmpty() && def.plantTypes().contains(plantable.getPlantType(level, pos.relative(facing)).getName())) { return true; }
        return super.canSustainPlant(state, level, pos, facing, plantable);
    }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }

    @Override @Nonnull public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (def.opensWith() == null) { return super.use(state, level, pos, player, hand, hit); }
        ItemStack held = player.getItemInHand(hand);
        Item key = ContentStacks.item(def.opensWith());
        if (key == null || !held.is(key)) {
            if (!level.isClientSide) { player.displayClientMessage(Component.translatable(getDescriptionId() + ".locked"), true); }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level instanceof ServerLevel server) { open(server, pos, state, player, held); }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void open(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack held) {
        SoundEvent opening = Registered.find(ForgeRegistries.SOUND_EVENTS, ContentParser.location(def.openSound()));
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
