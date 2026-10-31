package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import javax.annotation.Nullable;

public final class ContentContainerUpgrades {
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean swapping() { return swapping; }

    public static InteractionResult use(ContainerUpgradeDef upgrade, Item kit, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) { return InteractionResult.PASS; }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IContentContainer)) { return InteractionResult.PASS; }
        if (player.getCooldowns().isOnCooldown(kit.getDefaultInstance())) { return InteractionResult.FAIL; }
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) { return InteractionResult.PASS; }
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity old)) { return InteractionResult.PASS; }
        ContainerUpgradeDef.Path path = pathFor(upgrade, state);
        if (path == null) { return refuse(serverPlayer, "no path for that container", "wrong"); }
        Identifier key = BuiltInRegistries.ITEM.getKey(kit);
        BlockState parsed = ContentStates.parse(path.to(), key);
        if (parsed == null || !(parsed.getBlock() instanceof IContentContainer)) { return refuse(serverPlayer, "nothing to become", "unavailable"); }
        BlockState target = facing(parsed.getBlock().defaultBlockState(), state);
        if (target == state) { return refuse(serverPlayer, "nothing to become", "unavailable"); }
        if (container(target).size() < old.getContainerSize()) { return refuse(serverPlayer, "target is smaller", "smaller"); }
        if (old.watched()) { return refuse(serverPlayer, "someone has it open", "in_use"); }
        if (old.pendingLoot() && !upgrade.carryLoot()) { return refuse(serverPlayer, "loot not rolled", "loot"); }
        if (!player.mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand()) || !ContentCarry.asks(serverPlayer, pos)) {
            ContentLog.LOGGER.info("Container upgrade: {} was refused at {} (not allowed to change it)", serverPlayer.getGameProfile().name(), pos);
            serverPlayer.sendOverlayMessage(Component.translatable("rdpl.carry.denied"));
            return InteractionResult.FAIL;
        }
        if (!swap(server, state, target, pos, old)) { return refuse(serverPlayer, "the swap failed", "unavailable"); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", serverPlayer.getGameProfile().name(), pos, BuiltInRegistries.BLOCK.getKey(target.getBlock()));
        return InteractionResult.SUCCESS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade() == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade().paths()) {
            BlockState from = ContentStates.parse(path.from(), def.key());
            BlockState to = ContentStates.parse(path.to(), def.key());
            if (from == null || to == null) { continue; }
            if (!(from.getBlock() instanceof IContentContainer)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a fromBlock, which is not a container", def.key(), path.from()); }
            else if (!(to.getBlock() instanceof IContentContainer)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a toBlock, which is not a container", def.key(), path.to()); }
            else if (path.from().equals(path.to())) { ContentLog.LOGGER.error("The container upgrade {} changes {} into itself", def.key(), path.from()); }
            else if (container(to).size() < container(from).size()) { ContentLog.LOGGER.error("The container upgrade {} changes {} into {}, which holds fewer slots", def.key(), path.from(), path.to()); }
        }
    }

    private static ContainerDef container(BlockState state) { return ((IContentContainer) state.getBlock()).container(); }

    @Nullable private static ContainerUpgradeDef.Path pathFor(ContainerUpgradeDef upgrade, BlockState state) {
        for (ContainerUpgradeDef.Path path : upgrade.paths()) {
            if (matches(path.from(), state)) { return path; }
        }
        return null;
    }

    private static boolean matches(String from, BlockState state) {
        Identifier named = Identifier.tryParse(from);
        return named != null && named.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
    }

    private static BlockState facing(BlockState target, BlockState old) {
        if (!target.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || !old.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) { return target; }
        return target.setValue(BlockStateProperties.HORIZONTAL_FACING, old.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    private static InteractionResult refuse(ServerPlayer player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getGameProfile().name(), why);
        player.sendOverlayMessage(Component.translatable("rdpl.container_upgrade." + key));
        return InteractionResult.FAIL;
    }

    private static boolean place(ServerLevel level, BlockPos pos, BlockState state) {
        swapping = true;
        try { return level.setBlock(pos, state, Block.UPDATE_CLIENTS); }
        finally { swapping = false; }
    }

    private static boolean swap(ServerLevel level, BlockState state, BlockState target, BlockPos pos, ContentContainerBlockEntity old) {
        CompoundTag tag = old.vacate();
        try {
            boolean set = place(level, pos, target);
            BlockEntity tile = level.getBlockEntity(pos);
            if (!set || !(tile instanceof ContentContainerBlockEntity next)) {
                ContentLog.LOGGER.error("The container at {} could not become {} (set {}, found {}), so it is put back", pos, target, set, tile);
                recover(level, pos, state, tag);
                return false;
            }
            next.reshape(container(target));
            next.adopt(tag);
            level.sendBlockUpdated(pos, target, target, Block.UPDATE_ALL);
            level.updateNeighborsAt(pos, target.getBlock());
            SoundType sound = target.getBlock().getSoundType(target, level, pos, null);
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            return true;
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container upgrade at {} failed, so the container is put back", pos, ex);
            recover(level, pos, state, tag);
            return false;
        }
    }

    private static void recover(ServerLevel level, BlockPos pos, BlockState state, CompoundTag tag) {
        try {
            if (level.getBlockState(pos) != state) { place(level, pos, state); }
            BlockEntity tile = level.getBlockEntity(pos);
            if (!(tile instanceof ContentContainerBlockEntity back)) { throw new IllegalStateException("no container block entity at " + pos); }
            back.reshape(container(state));
            back.adopt(tag);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container at {} could not be put back, its saved data was {}", pos, tag, ex);
        }
    }
}
