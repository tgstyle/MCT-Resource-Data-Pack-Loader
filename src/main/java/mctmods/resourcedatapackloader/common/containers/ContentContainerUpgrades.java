package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.container.logic.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

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
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean dropsOnRemove() { return !swapping; }

    public static InteractionResult use(ContainerUpgradeDef upgrade, Item kit, Player player, InteractionHand hand, Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ContentContainerBlock)) { return InteractionResult.PASS; }
        if (player.getCooldowns().isOnCooldown(kit)) { return InteractionResult.FAIL; }
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!(player instanceof ServerPlayer server)) { return InteractionResult.PASS; }
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity old)) { return InteractionResult.PASS; }
        ContainerUpgradeDef.Path path = pathFor(upgrade, state);
        if (path == null) { return refuse(player, "no path for that container", "wrong"); }
        Block toBlock = ContentStates.registered(path.to());
        if (!(toBlock instanceof ContentContainerBlock)) { return refuse(player, "nothing to become", "unavailable"); }
        BlockState target = toBlock.defaultBlockState();
        if (state.hasProperty(HorizontalDirectionalBlock.FACING) && target.hasProperty(HorizontalDirectionalBlock.FACING)) { target = target.setValue(HorizontalDirectionalBlock.FACING, state.getValue(HorizontalDirectionalBlock.FACING)); }
        if (target == state) { return refuse(player, "nothing to become", "unavailable"); }
        if (((ContentContainerBlock) target.getBlock()).container().size() < old.getContainerSize()) { return refuse(player, "target is smaller", "smaller"); }
        if (old.watched()) { return refuse(player, "someone has it open", "in_use"); }
        if (old.pendingLoot() && !upgrade.carryLoot()) { return refuse(player, "loot not rolled", "loot"); }
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand)) || !ContentCarry.asks(server, pos)) {
            ContentLog.LOGGER.info("Container upgrade: {} was refused at {} (not allowed to change it)", player.getName().getString(), pos);
            player.displayClientMessage(Component.translatable("rdpl.carry.denied"), true);
            return InteractionResult.FAIL;
        }
        if (!swap(state, target, level, pos, old)) { return refuse(player, "the swap failed", "unavailable"); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", player.getName().getString(), pos, BuiltInRegistries.BLOCK.getKey(target.getBlock()));
        return InteractionResult.SUCCESS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade() == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade().paths()) {
            Block from = ContentStates.registered(path.from());
            Block to = ContentStates.registered(path.to());
            if (from == null || to == null) { continue; }
            if (!(from instanceof ContentContainerBlock)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a fromBlock, which is not a container", def.key(), path.from()); }
            else if (!(to instanceof ContentContainerBlock)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a toBlock, which is not a container", def.key(), path.to()); }
            else if (path.from().equals(path.to())) { ContentLog.LOGGER.error("The container upgrade {} changes {} into itself", def.key(), path.from()); }
            else if (((ContentContainerBlock) to).container().size() < ((ContentContainerBlock) from).container().size()) { ContentLog.LOGGER.error("The container upgrade {} changes {} into {}, which holds fewer slots", def.key(), path.from(), path.to()); }
        }
    }

    @Nullable private static ContainerUpgradeDef.Path pathFor(ContainerUpgradeDef upgrade, BlockState state) {
        for (ContainerUpgradeDef.Path path : upgrade.paths()) {
            if (matches(path.from(), state)) { return path; }
        }
        return null;
    }

    private static boolean matches(String from, BlockState state) { return ContentStates.registered(from) == state.getBlock(); }

    private static InteractionResult refuse(Player player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getName().getString(), why);
        player.displayClientMessage(Component.translatable("rdpl.container_upgrade." + key), true);
        return InteractionResult.FAIL;
    }

    private static boolean place(Level level, BlockPos pos, BlockState state) {
        swapping = true;
        try { return level.setBlock(pos, state, Block.UPDATE_CLIENTS); }
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
