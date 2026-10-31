package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentContainerBlock;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.content.def.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;

public final class ContentContainerUpgrades {
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean idle() { return !swapping; }

    public static InteractionResult use(ContainerUpgradeDef upgrade, Item kit, @Nullable Player player, InteractionHand hand, Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ContentContainerBlock)) { return InteractionResult.PASS; }
        if (player == null) { return InteractionResult.PASS; }
        if (player.getCooldowns().isOnCooldown(kit)) { return InteractionResult.FAIL; }
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!(player instanceof ServerPlayer server)) { return InteractionResult.PASS; }
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity old)) { return InteractionResult.PASS; }
        ContainerUpgradeDef.Path path = pathFor(upgrade, state);
        if (path == null) { return refuse(player, "no path for that container", "wrong"); }
        BlockState target = target(path.to(), state, kit);
        if (target == null || !(target.getBlock() instanceof ContentContainerBlock) || target == state) { return refuse(player, "nothing to become", "unavailable"); }
        if (((IContentContainer) target.getBlock()).container().size() < old.getContainerSize()) { return refuse(player, "target is smaller", "smaller"); }
        if (old.watched()) { return refuse(player, "someone has it open", "in_use"); }
        if (old.pendingLoot() && !upgrade.carryLoot()) { return refuse(player, "loot not rolled", "loot"); }
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand)) || !ContentCarry.asks(server, pos)) {
            ContentLog.LOGGER.info("Container upgrade: {} was refused at {} (not allowed to change it)", player.getGameProfile().getName(), pos);
            player.displayClientMessage(Component.translatable("rdpl.carry.denied"), true);
            return InteractionResult.FAIL;
        }
        if (!swap(state, target, level, pos, old)) { return refuse(player, "the swap failed", "unavailable"); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", player.getGameProfile().getName(), pos, ForgeRegistries.BLOCKS.getKey(target.getBlock()));
        return InteractionResult.SUCCESS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade() == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade().paths()) {
            BlockState from = ContentStates.parse(path.from(), def.key());
            BlockState to = ContentStates.parse(path.to(), def.key());
            if (from == null || to == null) { continue; }
            if (!(from.getBlock() instanceof ContentContainerBlock)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a fromBlock, which is not a container", def.key(), path.from()); }
            else if (!(to.getBlock() instanceof ContentContainerBlock)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a toBlock, which is not a container", def.key(), path.to()); }
            else if (path.from().equals(path.to())) { ContentLog.LOGGER.error("The container upgrade {} changes {} into itself", def.key(), path.from()); }
            else if (((IContentContainer) to.getBlock()).container().size() < ((IContentContainer) from.getBlock()).container().size()) { ContentLog.LOGGER.error("The container upgrade {} changes {} into {}, which holds fewer slots", def.key(), path.from(), path.to()); }
        }
    }

    @Nullable private static ContainerUpgradeDef.Path pathFor(ContainerUpgradeDef upgrade, BlockState state) {
        for (ContainerUpgradeDef.Path path : upgrade.paths()) {
            if (matches(path.from(), state)) { return path; }
        }
        return null;
    }

    private static boolean matches(String from, BlockState state) {
        BlockState named = ContentStates.parse(from, from);
        return named != null && named.getBlock() == state.getBlock();
    }

    @Nullable private static BlockState target(String to, BlockState old, Item kit) {
        BlockState named = ContentStates.parse(to, ForgeRegistries.ITEMS.getKey(kit));
        return named == null ? null : facing(named.getBlock().defaultBlockState(), old);
    }

    private static BlockState facing(BlockState next, BlockState old) {
        BlockState out = next;
        for (Property<?> property : next.getProperties()) {
            if (!property.getName().equals("facing") || !old.hasProperty(property)) { continue; }
            Comparable<?> value = old.getValue(property);
            if (property.getPossibleValues().contains(value)) { out = ContentStates.with(out, property, value); }
        }
        return out;
    }

    private static InteractionResult refuse(Player player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getGameProfile().getName(), why);
        player.displayClientMessage(Component.translatable("rdpl.container_upgrade." + key), true);
        return InteractionResult.FAIL;
    }

    private static boolean place(Level level, BlockPos pos, BlockState state) {
        swapping = true;
        try { return level.setBlock(pos, state, Block.UPDATE_CLIENTS); }
        finally { swapping = false; }
    }

    private static boolean swap(BlockState state, BlockState target, Level level, BlockPos pos, ContentContainerBlockEntity old) {
        CompoundTag tag = old.vacate();
        try {
            boolean set = place(level, pos, target);
            BlockEntity tile = level.getBlockEntity(pos);
            if (!set || !(tile instanceof ContentContainerBlockEntity held)) {
                ContentLog.LOGGER.error("The container at {} could not become {} (set {}, found {}), so it is put back", pos, target, set, tile);
                recover(level, pos, state, tag);
                return false;
            }
            held.adopt(tag);
            level.sendBlockUpdated(pos, target, target, Block.UPDATE_ALL);
            level.updateNeighborsAt(pos, target.getBlock());
            SoundType sound = target.getSoundType(level, pos, null);
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
            if (!(tile instanceof ContentContainerBlockEntity held)) { throw new IllegalStateException("no container block entity at " + pos); }
            held.adopt(tag);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container at {} could not be put back, its saved data was {}", pos, tag, ex);
        }
    }
}
