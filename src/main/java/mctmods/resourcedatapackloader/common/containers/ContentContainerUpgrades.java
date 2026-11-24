package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.logic.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.function.Predicate;
import javax.annotation.Nullable;
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

public final class ContentContainerUpgrades {
    static final String CANNOT_MOVE = "cannot_move";
    private static final String UNAVAILABLE = "unavailable";
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean swapping() { return swapping; }

    public static InteractionResult use(ContainerUpgradeDef upgrade, Item kit, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) { return InteractionResult.PASS; }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Identifier key = BuiltInRegistries.ITEM.getKey(kit);
        ContainerUpgradeDef.Path path = pathFor(upgrade, state, key);
        if (path != null && path.contents() != ContainerUpgradeDef.Contents.CONTAINER) { return ContentStorageSwap.use(upgrade, path, kit, player, context, state); }
        if (!(state.getBlock() instanceof IContentContainer)) { return InteractionResult.PASS; }
        InteractionResult early = early(player, kit, level);
        if (early != null) { return early; }
        ServerLevel server = (ServerLevel) level;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity old)) { return InteractionResult.PASS; }
        if (path == null) { return refuse(serverPlayer, "no path for that container", "wrong"); }
        BlockState parsed = ContentStates.parse(path.to(), key);
        if (parsed == null || !(parsed.getBlock() instanceof IContentContainer)) { return unavailable(serverPlayer); }
        BlockState target = facing(parsed.getBlock().defaultBlockState(), state);
        if (target == state) { return unavailable(serverPlayer); }
        if (container(target).size() < old.getContainerSize()) { return smaller(serverPlayer); }
        if (old.watched()) { return open(serverPlayer); }
        if (old.pendingLoot() && !upgrade.carryLoot()) { return unrolled(serverPlayer); }
        InteractionResult barred = barred(serverPlayer, context);
        if (barred != null) { return barred; }
        if (!swap(server, state, target, pos, old)) { return refuse(serverPlayer, "the swap failed", UNAVAILABLE); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", serverPlayer.getGameProfile().name(), pos, BuiltInRegistries.BLOCK.getKey(target.getBlock()));
        return InteractionResult.SUCCESS;
    }

    @Nullable static InteractionResult early(Player player, Item kit, Level level) {
        if (player.getCooldowns().isOnCooldown(kit.getDefaultInstance())) { return InteractionResult.FAIL; }
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        return level instanceof ServerLevel && player instanceof ServerPlayer ? null : InteractionResult.PASS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade() == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade().paths()) {
            boolean storage = path.contents() != ContainerUpgradeDef.Contents.CONTAINER;
            BlockState from = ContentStates.known(path.from(), def.key());
            BlockState to = ContentStates.known(path.to(), def.key());
            if (from == null || to == null) { continue; }
            if (storage) { validate(def, path, from, to, BlockState::hasBlockEntity, "holds nothing", true); }
            else { validate(def, path, from, to, held -> held.getBlock() instanceof IContentContainer, "is not a container", false); }
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

    private static boolean shrinks(BlockState from, BlockState to) { return from.getBlock() instanceof IContentContainer && to.getBlock() instanceof IContentContainer && container(to).size() < container(from).size(); }

    private static ContainerDef container(BlockState state) { return ((IContentContainer) state.getBlock()).container(); }

    @Nullable private static ContainerUpgradeDef.Path pathFor(ContainerUpgradeDef upgrade, BlockState state, Identifier key) {
        for (ContainerUpgradeDef.Path path : upgrade.paths()) {
            if (ContentStates.fits(path.from(), state, key)) { return path; }
        }
        return null;
    }

    private static BlockState facing(BlockState target, BlockState old) {
        if (!target.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || !old.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) { return target; }
        return target.setValue(BlockStateProperties.HORIZONTAL_FACING, old.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    static InteractionResult refuse(ServerPlayer player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getGameProfile().name(), why);
        player.sendOverlayMessage(Component.translatable("rdpl.container_upgrade." + key));
        return InteractionResult.FAIL;
    }

    static InteractionResult unavailable(ServerPlayer player) { return refuse(player, "nothing to become", UNAVAILABLE); }

    static InteractionResult smaller(ServerPlayer player) { return refuse(player, "target is smaller", "smaller"); }

    static InteractionResult open(ServerPlayer player) { return refuse(player, "someone has it open", "in_use"); }

    static InteractionResult unrolled(ServerPlayer player) { return refuse(player, "loot not rolled", "loot"); }

    @Nullable static InteractionResult barred(ServerPlayer player, UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        return player.mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand()) && ContentCarry.asks(player, pos) ? null : denied(player, pos, "not allowed to change it");
    }

    static InteractionResult denied(ServerPlayer player, BlockPos pos, String why) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused at {} ({})", player.getGameProfile().name(), pos, why);
        player.sendOverlayMessage(Component.translatable(ContentCarry.DENIED));
        return InteractionResult.FAIL;
    }

    static boolean place(ServerLevel level, BlockPos pos, BlockState state) {
        return place(level, pos, state, Block.UPDATE_CLIENTS);
    }

    static boolean place(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        swapping = true;
        try { return level.setBlock(pos, state, flags); }
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
