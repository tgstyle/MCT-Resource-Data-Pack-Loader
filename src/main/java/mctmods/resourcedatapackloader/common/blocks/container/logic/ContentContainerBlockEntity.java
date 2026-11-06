package mctmods.resourcedatapackloader.common.blocks.container.logic;

import mctmods.resourcedatapackloader.common.containers.ContentContainers;
import mctmods.resourcedatapackloader.common.containers.IContentContainer;
import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.common.items.ContentEnderInventory;
import mctmods.resourcedatapackloader.common.items.ContentPouchInventory;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.util.loot.StructureLoot;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public final class ContentContainerBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private static final String STOCKED = "RdplStocked";
    private static final String ROWS = "RdplRows";
    private static final String COLUMNS = "RdplColumns";
    private static final String OWNER = "RdplOwner";
    private final ChestLidController lid = new ChestLidController();
    private NonNullList<ItemStack> items;
    private int openers;
    private int rows;
    private int columns;
    private boolean stocked;
    private boolean creative;
    @Nullable private UUID owner;
    @Nullable private ContentEnderInventory pipes;
    private LazyOptional<IItemHandler> piped = LazyOptional.empty();
    private int seen;

    public ContentContainerBlockEntity(BlockPos pos, BlockState state) {
        super(ContentContainers.TYPE.get(), pos, state);
        ContainerDef def = def(state);
        this.rows = def.rows();
        this.columns = def.columns();
        this.items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
    }

    private static ContainerDef def(BlockState state) { return state.getBlock() instanceof IContentContainer held ? held.container() : ContentContainers.FALLBACK; }

    public ContainerDef def() { return def(getBlockState()); }

    public ContainerDef shape() { return def().ender() ? def() : def().sized(rows, columns); }

    @Override public int getContainerSize() { return items.size(); }

    @Override @Nonnull protected NonNullList<ItemStack> getItems() { return items; }

    @Override protected void setItems(@Nonnull NonNullList<ItemStack> held) { this.items = held; }

    @Override @Nonnull protected Component getDefaultName() { return getBlockState().getBlock().getName(); }

    @Override @Nonnull protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory) { return new ContentContainerMenu(id, inventory, def().ender() ? new ContentEnderInventory(inventory.player.level(), inventory.player.getUUID(), def(), this) : this, shape()); }

    @Override public void startOpen(@Nonnull Player player) {
        if (player.isSpectator()) { return; }
        openers++;
        onOpenerCountChanged();
    }

    @Override public void stopOpen(@Nonnull Player player) {
        if (player.isSpectator()) { return; }
        openers = Math.max(0, openers - 1);
        onOpenerCountChanged();
    }

    private void onOpenerCountChanged() {
        if (level == null) { return; }
        if (openers == 1) { sound(SoundEvents.CHEST_OPEN); }
        if (openers == 0) { sound(SoundEvents.CHEST_CLOSE); }
        level.blockEvent(getBlockPos(), getBlockState().getBlock(), 1, openers);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    }

    private void sound(SoundEvent event) {
        if (level == null || !def().chestModel()) { return; }
        BlockPos at = getBlockPos();
        level.playSound(null, at.getX() + 0.5D, at.getY() + 0.5D, at.getZ() + 0.5D, event, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override public float getOpenNess(float partial) { return lid.getOpenness(partial); }

    public static void lidTick(Level ignoredLevel, BlockPos ignoredPos, BlockState ignoredState, ContentContainerBlockEntity held) { held.lid.tickLid(); }

    @Override public boolean triggerEvent(int id, int value) {
        if (id != 1) { return super.triggerEvent(id, value); }
        openers = value;
        lid.shouldBeOpen(value > 0);
        return true;
    }

    @Override public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        resize(tag);
        this.owner = tag.hasUUID(OWNER) ? tag.getUUID(OWNER) : null;
        this.pipes = null;
        piped.invalidate();
        this.stocked = tag.getBoolean(STOCKED);
        if (!tryLoadLootTable(tag)) { ContainerHelper.loadAllItems(tag, this.items); }
    }

    @Override protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(STOCKED, stocked);
        saveSize(tag);
        if (owner != null) { tag.putUUID(OWNER, owner); }
        if (!trySaveLootTable(tag)) { ContainerHelper.saveAllItems(tag, this.items); }
    }

    private void resize(CompoundTag tag) {
        ContainerDef def = def();
        this.rows = tag.contains(ROWS) ? Math.max(1, tag.getInt(ROWS)) : def.rows();
        this.columns = tag.contains(COLUMNS) ? Math.max(1, tag.getInt(COLUMNS)) : def.columns();
        this.items = NonNullList.withSize(rows * columns, ItemStack.EMPTY);
    }

    private void saveSize(CompoundTag tag) {
        tag.putInt(ROWS, rows);
        tag.putInt(COLUMNS, columns);
    }

    public void placed() {
        stocked = true;
        setChanged();
    }

    public void own(UUID placer) {
        owner = placer;
        pipes = null;
        piped.invalidate();
        setChanged();
    }

    @Nullable private ContentEnderInventory pipes() {
        if (pipes == null && owner != null && level != null && !level.isClientSide() && def().ender() && def().enderAutomation()) { pipes = new ContentEnderInventory(level, owner, def(), null); }
        return pipes;
    }

    public Container automation() {
        ContentEnderInventory held = pipes();
        return held == null ? this : held;
    }

    public static void storeTick(Level ignoredLevel, BlockPos ignoredPos, BlockState ignoredState, ContentContainerBlockEntity held) { held.signal(); }

    private void signal() {
        ContentEnderInventory held = pipes();
        if (held == null || level == null || held.version() == seen) { return; }
        seen = held.version();
        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        ContentEnderInventory held = capability == ForgeCapabilities.ITEM_HANDLER && !remove ? pipes() : null;
        if (held == null) { return super.getCapability(capability, side); }
        if (!piped.isPresent()) { piped = LazyOptional.of(() -> new InvWrapper(held)); }
        return piped.cast();
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        piped.invalidate();
    }

    public boolean watched() { return openers > 0; }

    public boolean pendingLoot() { return lootTable != null || (!stocked && !def().lootTable().isEmpty()); }

    public CompoundTag vacate() {
        if (!stocked && level != null) { stock(level.getRandom().nextLong()); }
        stocked = true;
        CompoundTag tag = saveWithoutMetadata();
        lootTable = null;
        items = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        return tag;
    }

    public void adopt(CompoundTag tag) {
        if (rows * columns >= tag.getInt(ROWS) * tag.getInt(COLUMNS)) { saveSize(tag); }
        lootTable = null;
        load(tag);
        stocked = true;
        setChanged();
    }

    public void unloot() {
        stocked = true;
        lootTable = null;
    }

    public void brokenBy(Player player) {
        creative = player.isCreative();
        unpackLootTable(player);
    }

    public ItemStack kept(Level world, ItemStack block) {
        unpackLootTable(null);
        for (int at = 0; at < items.size(); at++) {
            if (!items.get(at).isEmpty() && !ContentContainerMenu.storable(items.get(at))) {
                Containers.dropItemStack(world, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), items.get(at));
                items.set(at, ItemStack.EMPTY);
            }
        }
        if (creative && isEmpty() && !hasCustomName()) { return ItemStack.EMPTY; }
        if (!isEmpty()) { ContentPouchInventory.store(block, items); }
        if (rows != def().rows() || columns != def().columns()) { saveSize(block.getOrCreateTagElement(ContentPouchInventory.HELD)); }
        if (hasCustomName()) { block.setHoverName(getCustomName()); }
        clearContent();
        return block;
    }

    public void restore(ItemStack stack) {
        CompoundTag held = stack.getTagElement(ContentPouchInventory.HELD);
        if (held == null) { return; }
        resize(held);
        ContentPouchInventory.load(stack, items);
        setChanged();
    }

    @Override public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return !def().ender() && (!def().keepContents() || ContentContainerMenu.storable(stack)); }

    @Override public boolean canOpen(@Nonnull Player player) { return true; }

    @Override public void unpackLootTable(@Nullable Player player) {
        if (!stocked && level != null && !level.isClientSide()) { stock(level.getRandom().nextLong()); }
        super.unpackLootTable(player);
    }

    private void stock(long seed) {
        stocked = true;
        setChanged();
        String named = def().lootTable();
        if (named.isEmpty() || this.lootTable != null) { return; }
        ResourceLocation table = ResourceLocation.tryParse(named);
        if (table == null) {
            ContentLog.LOGGER.error("The container at {} names the loot table '{}', which is not a valid id, so it starts empty", getBlockPos(), named);
            return;
        }
        StructureLoot.stock(this, table, seed);
    }

    public static int comparatorOutput(BlockEntity held) { return held instanceof ContentContainerBlockEntity pack ? AbstractContainerMenu.getRedstoneSignalFromContainer(pack.automation()) : held instanceof Container container ? AbstractContainerMenu.getRedstoneSignalFromContainer(container) : 0; }
}
