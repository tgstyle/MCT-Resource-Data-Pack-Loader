package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.container.logic.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentContainerUpgrades {
    static final String CANNOT_MOVE = "cannot_move";
    private static final String UNAVAILABLE = "unavailable";
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean dropsOnRemove() { return !swapping; }

    public static InteractionResult use(ContainerUpgradeDef upgrade, Item kit, Player player, InteractionHand hand, Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        ContainerUpgradeDef.Path path = pathFor(upgrade, state);
        if (path != null && path.contents() != ContainerUpgradeDef.Contents.CONTAINER) { return ContentStorageSwap.use(upgrade, path, kit, player, hand, level, pos, side, state); }
        if (!(state.getBlock() instanceof ContentContainerBlock)) { return InteractionResult.PASS; }
        InteractionResult early = early(player, kit, level);
        if (early != null) { return early; }
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity old)) { return InteractionResult.PASS; }
        if (path == null) { return refuse(player, "no path for that container", "wrong"); }
        Block toBlock = ContentStates.registered(path.to());
        if (!(toBlock instanceof ContentContainerBlock)) { return unavailable(player); }
        BlockState target = toBlock.defaultBlockState();
        if (state.hasProperty(HorizontalDirectionalBlock.FACING) && target.hasProperty(HorizontalDirectionalBlock.FACING)) { target = target.setValue(HorizontalDirectionalBlock.FACING, state.getValue(HorizontalDirectionalBlock.FACING)); }
        if (target == state) { return unavailable(player); }
        if (((ContentContainerBlock) target.getBlock()).container().size() < old.getContainerSize()) { return smaller(player); }
        if (old.watched()) { return open(player); }
        if (old.pendingLoot() && !upgrade.carryLoot()) { return unrolled(player); }
        InteractionResult barred = barred((ServerPlayer) player, hand, pos, side);
        if (barred != null) { return barred; }
        if (!swap(state, target, level, pos, old)) { return refuse(player, "the swap failed", UNAVAILABLE); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", player.getName().getString(), pos, BuiltInRegistries.BLOCK.getKey(target.getBlock()));
        return InteractionResult.SUCCESS;
    }

    @Nullable static InteractionResult early(Player player, Item kit, Level level) {
        if (player.getCooldowns().isOnCooldown(kit)) { return InteractionResult.FAIL; }
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        return player instanceof ServerPlayer ? null : InteractionResult.PASS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade() == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade().paths()) {
            boolean storage = path.contents() != ContainerUpgradeDef.Contents.CONTAINER;
            BlockState from = ContentStates.known(path.from(), def.key());
            BlockState to = ContentStates.known(path.to(), def.key());
            if (from == null || to == null) { continue; }
            if (storage) { validate(def, path, from, to, BlockState::hasBlockEntity, "holds nothing", true); }
            else { validate(def, path, from, to, held -> held.getBlock() instanceof ContentContainerBlock, "is not a container", false); }
        }
    }

    private static void validate(ItemDef def, ContainerUpgradeDef.Path path, BlockState from, BlockState to, Predicate<BlockState> holds, String lacking, boolean storage) {
        if (!holds.test(from)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a fromBlock, which {}", def.key(), path.from(), lacking); }
        else if (!holds.test(to)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a toBlock, which {}", def.key(), path.to(), lacking); }
        else if (path.from().equals(path.to())) { ContentLog.LOGGER.error("The container upgrade {} changes {} into itself", def.key(), path.from()); }
        else if (storage && ContentCarry.twoBlock(from)) { ContentLog.LOGGER.error("The container upgrade {} names {}, which is two blocks, so it never works", def.key(), path.from()); }
        else if (storage && ContentCarry.twoBlock(to)) { ContentLog.LOGGER.error("The container upgrade {} names {}, which is two blocks, so it never works", def.key(), path.to()); }
        else if ((!storage || path.contents() == ContainerUpgradeDef.Contents.SLOTS && !path.fit()) && shrinks(from, to)) { ContentLog.LOGGER.error("The container upgrade {} changes {} into {}, which holds fewer slots", def.key(), path.from(), path.to()); }
    }

    private static boolean shrinks(BlockState from, BlockState to) { return from.getBlock() instanceof ContentContainerBlock fromContainer && to.getBlock() instanceof ContentContainerBlock toContainer && toContainer.container().size() < fromContainer.container().size(); }

    @Nullable private static ContainerUpgradeDef.Path pathFor(ContainerUpgradeDef upgrade, BlockState state) {
        for (ContainerUpgradeDef.Path path : upgrade.paths()) {
            if (matches(path, state)) { return path; }
        }
        return null;
    }

    private static boolean matches(ContainerUpgradeDef.Path path, BlockState state) { return path.contents() != ContainerUpgradeDef.Contents.CONTAINER ? ContentStates.fits(path.from(), state, path.from()) : ContentStates.registered(path.from()) == state.getBlock(); }

    static InteractionResult refuse(Player player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getName().getString(), why);
        player.displayClientMessage(Component.translatable("rdpl.container_upgrade." + key), true);
        return InteractionResult.FAIL;
    }

    static InteractionResult unavailable(Player player) { return refuse(player, "nothing to become", UNAVAILABLE); }

    static InteractionResult smaller(Player player) { return refuse(player, "target is smaller", "smaller"); }

    static InteractionResult open(Player player) { return refuse(player, "someone has it open", "in_use"); }

    static InteractionResult unrolled(Player player) { return refuse(player, "loot not rolled", "loot"); }

    @Nullable static InteractionResult barred(ServerPlayer player, InteractionHand hand, BlockPos pos, Direction side) {
        return player.mayUseItemAt(pos, side, player.getItemInHand(hand)) && ContentCarry.asks(player, pos) ? null : denied(player, pos, "not allowed to change it");
    }

    static InteractionResult denied(Player player, BlockPos pos, String why) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused at {} ({})", player.getName().getString(), pos, why);
        player.displayClientMessage(Component.translatable(ContentCarry.DENIED), true);
        return InteractionResult.FAIL;
    }

    static boolean place(Level level, BlockPos pos, BlockState state) {
        return place(level, pos, state, Block.UPDATE_CLIENTS);
    }

    static boolean place(Level level, BlockPos pos, BlockState state, int flags) {
        swapping = true;
        try { return level.setBlock(pos, state, flags); }
        finally { swapping = false; }
    }

    private static boolean swap(BlockState state, BlockState target, Level level, BlockPos pos, ContentContainerBlockEntity old) {
        CompoundTag tag = old.vacate(level.registryAccess());
        try {
            boolean set = place(level, pos, target);
            BlockEntity tile = level.getBlockEntity(pos);
            if (!set || !(tile instanceof ContentContainerBlockEntity)) {
                ContentLog.LOGGER.error("The container at {} could not become {} (set {}, found {}), so it is put back", pos, target, set, tile);
                recover(level, pos, state, tag);
                return false;
            }
            ((ContentContainerBlockEntity) tile).adopt(tag, level.registryAccess());
            level.sendBlockUpdated(pos, target, target, 3);
            level.updateNeighborsAt(pos, target.getBlock());
            @SuppressWarnings("deprecation") SoundType sound = target.getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            return true;
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container upgrade at {} failed, so the container is put back", pos, ex);
            recover(level, pos, state, tag);
            return false;
        }
    }

    private static void recover(Level level, BlockPos pos, BlockState state, CompoundTag tag) {
        try {
            if (level.getBlockState(pos) != state) { place(level, pos, state); }
            BlockEntity tile = level.getBlockEntity(pos);
            if (!(tile instanceof ContentContainerBlockEntity)) { throw new IllegalStateException("no container block entity at " + pos); }
            ((ContentContainerBlockEntity) tile).adopt(tag, level.registryAccess());
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container at {} could not be put back, its saved data was {}", pos, tag, ex);
        }
    }
}
