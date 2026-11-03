package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.logic.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class ContentStorageSwap {
    private static final double GUARD_RANGE = 4.0D;
    private static final double NEAR = 256.0D;
    private static final String LOOT_TABLE = "LootTable";
    private static final String CANNOT_MOVE = "cannot_move";
    private static final List<String> CARRIED = Arrays.asList("facing", "axis", "rotation", "waterlogged");
    private static final List<String> NAME_KEYS = Arrays.asList("CustomName", "Lock");
    private static final List<String> LOOT_KEYS = Arrays.asList(LOOT_TABLE, "LootTableSeed");
    @Nullable private static Guard guard;
    private final ContainerUpgradeDef upgrade;
    private final ContainerUpgradeDef.Path path;
    private final ServerPlayer player;
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final boolean nbt;
    private final List<ItemStack> held = new ArrayList<>();
    private BlockEntity tile;
    private BlockState target;
    private CompoundTag before;
    private boolean waitingLoot;
    private boolean linked;
    private boolean returning;
    @Nullable private IItemHandler source;
    private List<ItemStack> shown = new ArrayList<>();
    @Nullable private List<StorageContents.Placed> plan;
    @Nullable private BlockEntity probe;

    private ContentStorageSwap(ContainerUpgradeDef upgrade, ContainerUpgradeDef.Path path, ServerPlayer player, Level level, BlockPos pos, BlockState state) {
        this.upgrade = upgrade;
        this.path = path;
        this.player = player;
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.nbt = path.contents() == ContainerUpgradeDef.Contents.NBT;
    }

    static InteractionResult use(ContainerUpgradeDef upgrade, ContainerUpgradeDef.Path path, Item kit, Player player, InteractionHand hand, Level level, BlockPos pos, Direction side, BlockState state) {
        if (player.getCooldowns().isOnCooldown(kit)) { return InteractionResult.FAIL; }
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!(player instanceof ServerPlayer server)) { return InteractionResult.PASS; }
        ContentStorageSwap swap = new ContentStorageSwap(upgrade, path, server, level, pos, state);
        InteractionResult refused = swap.preflight();
        if (refused != null) { return refused; }
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand)) || !ContentCarry.asks(server, pos)) { return ContentContainerUpgrades.denied(player, pos, "not allowed to change it"); }
        return swap.move();
    }

    public static void onItemJoin(EntityJoinLevelEvent event) {
        Guard open = guard;
        if (open == null || event.getLevel() != open.level || !(event.getEntity() instanceof ItemEntity item)) { return; }
        if (open.pos.distToCenterSqr(item.getX(), item.getY(), item.getZ()) > GUARD_RANGE) { return; }
        event.setCanceled(true);
        open.keep(item.getItem());
    }

    @Nullable private InteractionResult preflight() {
        try { return checked(); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be read for an upgrade, so it is left alone", pos, ex);
            return ContentContainerUpgrades.refuse(player, "it could not be read", CANNOT_MOVE);
        }
    }

    @Nullable private InteractionResult checked() {
        tile = level.getBlockEntity(pos);
        if (tile == null) { return ContentContainerUpgrades.refuse(player, "it holds nothing", CANNOT_MOVE); }
        BlockState named = ContentStates.known(path.to(), path.to());
        if (named == null || !named.hasBlockEntity()) { return ContentContainerUpgrades.refuse(player, "nothing to become", "unavailable"); }
        target = carried(named);
        if (target == state) { return ContentContainerUpgrades.refuse(player, "nothing to become", "unavailable"); }
        if (state.getDestroySpeed(level, pos) < 0.0F) { return ContentContainerUpgrades.denied(player, pos, "unbreakable"); }
        if (ContentCarry.twoBlock(state) || ContentCarry.twoBlock(target)) { return ContentContainerUpgrades.denied(player, pos, "two blocks"); }
        if (inUse()) { return ContentContainerUpgrades.refuse(player, "someone has it open", "in_use"); }
        before = tile.saveWithFullMetadata(level.registryAccess());
        waitingLoot = before.contains(LOOT_TABLE, Tag.TAG_STRING) || tile instanceof ContentContainerBlockEntity container && container.pendingLoot();
        if (waitingLoot && !upgrade.carryLoot()) { return ContentContainerUpgrades.refuse(player, "loot not rolled", "loot"); }
        if (waitingLoot && !before.contains(LOOT_TABLE, Tag.TAG_STRING) && tile instanceof ContentContainerBlockEntity container) { before.putString(LOOT_TABLE, container.def().lootTable()); }
        linked = StorageContents.linked(level, tile);
        if (linked && (nbt || !path.linkedHalf())) { return ContentContainerUpgrades.refuse(player, "it shares storage with another block", "linked"); }
        InteractionResult refused = nbt ? null : readable();
        if (refused != null) { return refused; }
        return probed();
    }

    @Nullable private InteractionResult readable() {
        if (path.strict() && (StorageContents.holdsFluid(level, tile) || StorageContents.savesFluid(before))) { return ContentContainerUpgrades.refuse(player, "it holds a fluid", CANNOT_MOVE); }
        if (waitingLoot) { return tile instanceof RandomizableContainerBlockEntity && StorageContents.census(before).isEmpty() ? null : ContentContainerUpgrades.refuse(player, "waiting loot cannot be kept apart from its items", CANNOT_MOVE); }
        StorageContents.Source found = StorageContents.find(level, tile, before, linked, path.strict());
        if (found.handler() == null) { return ContentContainerUpgrades.refuse(player, found.why(), CANNOT_MOVE); }
        source = found.handler();
        if (StorageContents.withholds(source)) { return ContentContainerUpgrades.refuse(player, "a slot will not give up what it shows", CANNOT_MOVE); }
        shown = StorageContents.shown(source);
        return null;
    }

    @Nullable private InteractionResult probed() {
        try {
            probe = created(target);
            if (probe == null) { return ContentContainerUpgrades.refuse(player, "nothing to become", "unavailable"); }
            shaped(probe);
            probe.setLevel(level);
            if (nbt) { return roundTrips() ? null : ContentContainerUpgrades.refuse(player, "the target saves differently", CANNOT_MOVE); }
            if (waitingLoot) { return lootSurvives() ? null : ContentContainerUpgrades.refuse(player, "the target cannot hold waiting loot", CANNOT_MOVE); }
            return planned();
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.info("Container upgrade: the probe of {} threw {}", target, ex.toString());
            plan = null;
            probe = null;
            return nbt || waitingLoot ? ContentContainerUpgrades.refuse(player, "the target saves differently", CANNOT_MOVE) : null;
        }
    }

    private boolean roundTrips() {
        if (probe == null) { return false; }
        load(probe, before);
        CompoundTag saved = probe.saveWithFullMetadata(level.registryAccess());
        return StorageContents.census(saved).equals(StorageContents.census(before)) && (!waitingLoot || before.getString(LOOT_TABLE).equals(saved.getString(LOOT_TABLE)));
    }

    private boolean lootSurvives() {
        if (probe == null) { return false; }
        merge(probe, LOOT_KEYS);
        return before.getString(LOOT_TABLE).equals(probe.saveWithFullMetadata(level.registryAccess()).getString(LOOT_TABLE));
    }

    @Nullable private InteractionResult planned() {
        if (probe == null) { return null; }
        IItemHandler into = StorageContents.forProbe(level, probe);
        if (into == null) { return ContentContainerUpgrades.refuse(player, "the target has no inventory", CANNOT_MOVE); }
        if (into.getSlots() < shown.size() && !path.fit()) { return ContentContainerUpgrades.refuse(player, "target is smaller", "smaller"); }
        plan = StorageContents.plan(into, shown);
        return plan == null ? ContentContainerUpgrades.refuse(player, "the contents do not fit", CANNOT_MOVE) : null;
    }

    private boolean inUse() {
        if (tile instanceof ContentContainerBlockEntity container && container.watched()) { return true; }
        for (Player other : level.players()) {
            if (other.containerMenu == other.inventoryMenu) { continue; }
            for (Slot slot : other.containerMenu.slots) {
                if (StorageContents.shows(slot, level, tile)) { return true; }
            }
            if (pos.distToCenterSqr(other.getX(), other.getY(), other.getZ()) <= NEAR && other.containerMenu.stillValid(other)) { return true; }
        }
        return false;
    }

    private BlockState carried(BlockState named) {
        if (!path.carryState() || path.to().contains("[")) { return named; }
        BlockState result = named;
        for (Property<?> wanted : named.getProperties()) {
            if (!CARRIED.contains(wanted.getName())) { continue; }
            Property<?> had = state.getBlock().getStateDefinition().getProperty(wanted.getName());
            if (had != null) { result = withValue(result, wanted, state.getValue(had)); }
        }
        return result;
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState into, Property<T> property, Object value) {
        for (T allowed : property.getPossibleValues()) {
            if (allowed.equals(value)) { return into.setValue(property, allowed); }
        }
        return into;
    }

    private InteractionResult move() {
        if (path.contents() == ContainerUpgradeDef.Contents.SLOTS && stuck()) { return ContentContainerUpgrades.refuse(player, "its contents would not come out", CANNOT_MOVE); }
        Guard forward = new Guard(level, pos, nbt, state.getBlock().asItem());
        try { forward(forward); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage upgrade at {} failed, so the block is put back", pos, ex);
            recover(forward);
            return ContentContainerUpgrades.refuse(player, "the swap failed", CANNOT_MOVE);
        }
        spill(forward.kept);
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the storage at {} to {}", player.getName().getString(), pos, BuiltInRegistries.BLOCK.getKey(target.getBlock()));
        return InteractionResult.SUCCESS;
    }

    private void forgetLoot() { ((RandomizableContainerBlockEntity) tile).setLootTable(null); }

    private boolean stuck() {
        if (waitingLoot) {
            forgetLoot();
            return false;
        }
        try {
            if (source != null && StorageContents.drain(source, held) && (!path.strict() || emptied())) { return false; }
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("The storage at {} failed while being emptied", pos, ex); }
        undo();
        return true;
    }

    private boolean emptied() { return StorageContents.census(tile.saveWithFullMetadata(level.registryAccess())).isEmpty() && StorageContents.totals(held).equals(StorageContents.census(before)); }

    private void undo() {
        try {
            if (path.strict()) {
                tile.loadWithComponents(before.copy(), level.registryAccess());
                tile.setChanged();
            }
            else if (source != null) { putBack(source); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be put back, its saved data was {}", pos, before, ex);
            spill(path.strict() && cleared() ? StorageContents.stacksIn(level.registryAccess(), before) : held);
        }
    }

    private boolean cleared() {
        try { return source != null && StorageContents.drain(source, new ArrayList<>()); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be cleared", pos, ex);
            return false;
        }
    }

    private void forward(Guard forward) {
        if (nbt) { removed(forward); }
        boolean set = placed(target, forward, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        BlockEntity now = level.getBlockEntity(pos);
        if (!set || now == null || probe != null && now.getClass() != probe.getClass()) { throw new IllegalStateException("the block became " + level.getBlockState(pos) + " holding " + now); }
        fill(now);
        finish();
    }

    private void removed(Guard open) {
        guard = open;
        try { level.removeBlockEntity(pos); }
        finally { guard = null; }
    }

    private boolean placed(BlockState into, Guard open, int flags) {
        guard = open;
        try { return ContentContainerUpgrades.place(level, pos, into, flags); }
        finally { guard = null; }
    }

    private void fill(BlockEntity now) {
        shaped(now);
        if (nbt) { load(now, before); }
        else {
            merge(now, NAME_KEYS);
            if (waitingLoot) { merge(now, LOOT_KEYS); }
            if (!held.isEmpty()) {
                IItemHandler into = StorageContents.forFill(level, now);
                if (into == null) { throw new IllegalStateException("the target has no inventory"); }
                StorageContents.fill(into, plan, held);
            }
        }
        now.setChanged();
    }

    private void finish() {
        BlockState placed = level.getBlockState(pos);
        level.sendBlockUpdated(pos, placed, placed, 3);
        placed.updateNeighbourShapes(level, pos, Block.UPDATE_CLIENTS);
        placed.updateIndirectNeighbourShapes(level, pos, Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(pos, placed.getBlock());
        @SuppressWarnings("deprecation") SoundType sound = placed.getSoundType();
        level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        if (linked) { parted(); }
    }

    private void parted() {
        if (!(state.getBlock() instanceof ChestBlock) || state.getValue(ChestBlock.TYPE) == ChestType.SINGLE) { return; }
        BlockPos beside = pos.relative(ChestBlock.getConnectedDirection(state));
        BlockState besideState = level.getBlockState(beside);
        if (besideState.getBlock() instanceof ChestBlock && besideState.getValue(ChestBlock.TYPE) != ChestType.SINGLE && beside.relative(ChestBlock.getConnectedDirection(besideState)).equals(pos)) {
            level.setBlock(beside, besideState.setValue(ChestBlock.TYPE, ChestType.SINGLE), Block.UPDATE_ALL);
        }
    }

    private void recover(Guard forward) {
        boolean trusted = nbt || path.strict() || waitingLoot;
        try {
            BlockEntity back = restored();
            load(back, before);
            if (!trusted) { reinsert(back, forward); }
            back.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be put back, so its items are dropped; its saved data was {}", pos, before, ex);
            if (!returning) { wipe(); }
            spill(nbt ? StorageContents.stacksIn(level.registryAccess(), before) : held);
            if (!nbt) { spill(forward.kept); }
        }
    }

    private void reinsert(BlockEntity back, Guard forward) {
        IItemHandler into = StorageContents.forFill(level, back);
        if (into == null) { throw new IllegalStateException("no inventory at " + pos); }
        if (!StorageContents.drain(into, new ArrayList<>())) { throw new IllegalStateException("could not clear " + pos); }
        returning = true;
        forward.kept.clear();
        putBack(into);
    }

    private void wipe() {
        try { removed(new Guard(level, pos, true, null)); }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("The block entity at {} could not be cleared", pos, ex); }
    }

    private BlockEntity restored() {
        if (level.getBlockState(pos) != state) { placed(state, new Guard(level, pos, true, null), Block.UPDATE_CLIENTS); }
        if (level.getBlockState(pos).getBlock() != state.getBlock()) { throw new IllegalStateException("the block did not go back at " + pos); }
        BlockEntity back = level.getBlockEntity(pos);
        if (back == null) {
            back = created(state);
            if (back == null) { throw new IllegalStateException("no block entity at " + pos); }
            level.setBlockEntity(back);
        }
        shaped(back);
        return back;
    }

    @Nullable private BlockEntity created(BlockState as) { return as.getBlock() instanceof EntityBlock maker ? maker.newBlockEntity(pos, as) : null; }

    private void putBack(IItemHandler into) {
        for (Iterator<ItemStack> stacks = held.iterator(); stacks.hasNext();) {
            ItemStack left = ItemHandlerHelper.insertItemStacked(into, stacks.next().copy(), false);
            stacks.remove();
            dropAt(left);
        }
    }

    private void spill(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) { dropAt(stack); }
    }

    private void dropAt(ItemStack stack) {
        if (!stack.isEmpty()) { Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copy()); }
    }

    private void merge(BlockEntity into, List<String> keys) {
        boolean any = false;
        for (String key : keys) { any |= before.contains(key); }
        if (!any) { return; }
        CompoundTag saved = into.saveWithFullMetadata(level.registryAccess());
        for (String key : keys) {
            Tag value = before.get(key);
            if (value != null) { saved.put(key, value.copy()); }
        }
        into.loadWithComponents(saved, level.registryAccess());
    }

    private void load(BlockEntity into, CompoundTag tag) {
        CompoundTag copy = tag.copy();
        if (into instanceof ContentContainerBlockEntity container) {
            container.adopt(copy, level.registryAccess());
            container.placed();
        }
        else { into.loadWithComponents(copy, level.registryAccess()); }
    }

    private static void shaped(BlockEntity into) {
        if (into instanceof ContentContainerBlockEntity container) { container.placed(); }
    }

    private static final class Guard {
        private final Level level;
        private final BlockPos pos;
        private final boolean discardAll;
        @Nullable private final Item own;
        private final List<ItemStack> kept = new ArrayList<>();
        private boolean ownSeen;

        private Guard(Level level, BlockPos pos, boolean discardAll, @Nullable Item own) {
            this.level = level;
            this.pos = pos;
            this.discardAll = discardAll;
            this.own = own;
        }

        private void keep(ItemStack stack) {
            if (discardAll) { return; }
            if (stack.getItem() == own && stack.getCount() == 1 && !ownSeen) {
                ownSeen = true;
                return;
            }
            kept.add(stack.copy());
        }
    }
}
