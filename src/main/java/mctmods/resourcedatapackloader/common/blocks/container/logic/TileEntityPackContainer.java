package mctmods.resourcedatapackloader.common.blocks.container.logic;

import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.gui.ContainerPack;
import mctmods.resourcedatapackloader.common.gui.PouchInventory;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityPackContainer extends TileEntityLockableLoot implements ITickable {
    private static final String ROWS = "RdplRows";
    private static final String COLUMNS = "RdplColumns";
    private static final String NAMED = "RdplNamed";
    private static final String FACING = "RdplFacing";
    private static final String CUSTOM_NAME = "CustomName";
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private int rows = 3;
    private int columns = 9;
    private String named = "";
    private int watchers;
    private EnumFacing facing = EnumFacing.SOUTH;
    private float lid;
    private float lidBefore;
    private boolean creative;

    public void shape(ContainerDef def, String named) {
        this.rows = def.rows;
        this.columns = def.columns;
        this.named = named;
        this.items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
    }

    public int rows() { return rows; }

    public int columns() { return columns; }

    public float lid(float partial) { return lidBefore + (lid - lidBefore) * partial; }

    @Nullable private ContainerDef def() {
        IBlockState state = world == null ? null : world.getBlockState(pos);
        return state != null && state.getBlock() instanceof ContentContainerBlock ? ((ContentContainerBlock) state.getBlock()).container() : null;
    }

    @Override public int getSizeInventory() { return items.size(); }

    @Override public boolean isEmpty() {
        for (ItemStack held : items) {
            if (!held.isEmpty()) { return false; }
        }
        return true;
    }

    @Override @Nonnull protected NonNullList<ItemStack> getItems() { return items; }

    @Override @Nonnull public String getGuiID() { return "resourcedatapackloader:container"; }

    @Override public int getInventoryStackLimit() { return 64; }

    @Override public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        ContainerDef def = def();
        return def == null || !def.keepContents || ContainerPack.storable(stack);
    }

    @Override @Nonnull public String getName() {
        if (hasCustomName() && customName != null) { return customName; }
        return named.isEmpty() ? "container.rdpl" : named;
    }

    @Override @Nonnull public Container createContainer(@Nonnull InventoryPlayer player, @Nonnull EntityPlayer opening) {
        fillWithLoot(opening);
        return new ContainerPack(player, this);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        super.readFromNBT(tag);
        readShape(tag);
        items = NonNullList.withSize(rows * columns, ItemStack.EMPTY);
        if (!checkLootAndRead(tag)) { net.minecraft.inventory.ItemStackHelper.loadAllItems(tag, items); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        writeShape(tag);
        if (!checkLootAndWrite(tag)) { net.minecraft.inventory.ItemStackHelper.saveAllItems(tag, items); }
        return tag;
    }

    @Override @Nonnull public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        super.writeToNBT(tag);
        writeShape(tag);
        return tag;
    }

    @Override @Nullable public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }

    @Override public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity packet) {
        readShape(packet.getNbtCompound());
        fit();
    }

    private void fit() { if (items.size() != rows * columns) { items = NonNullList.withSize(rows * columns, ItemStack.EMPTY); } }

    private void readShape(NBTTagCompound tag) {
        readSize(tag);
        named = tag.getString(NAMED);
        facing = EnumFacing.byHorizontalIndex(tag.getInteger(FACING));
        customName = tag.hasKey(CUSTOM_NAME, 8) ? tag.getString(CUSTOM_NAME) : null;
    }

    private void writeShape(NBTTagCompound tag) {
        writeSize(tag);
        tag.setString(NAMED, named);
        tag.setInteger(FACING, facing.getHorizontalIndex());
        if (hasCustomName()) { tag.setString(CUSTOM_NAME, customName); }
    }

    private void readSize(NBTTagCompound tag) {
        rows = Math.max(1, tag.getInteger(ROWS));
        columns = Math.max(1, tag.getInteger(COLUMNS));
    }

    private void writeSize(NBTTagCompound tag) {
        tag.setInteger(ROWS, rows);
        tag.setInteger(COLUMNS, columns);
    }

    @Override public void openInventory(@Nonnull EntityPlayer player) {
        if (player.isSpectator()) { return; }
        watchers = Math.max(0, watchers) + 1;
        tell();
    }

    @Override public void closeInventory(@Nonnull EntityPlayer player) {
        if (player.isSpectator()) { return; }
        watchers = Math.max(0, watchers - 1);
        tell();
    }

    private void tell() {
        if (world == null || world.isRemote) { return; }
        world.addBlockEvent(pos, getBlockType(), 1, watchers);
        world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
    }

    private void sound(boolean opening) {
        ContainerDef def = def();
        if (world == null || world.isRemote || def == null || !def.chestModel) { return; }
        world.playSound(null, pos, opening ? net.minecraft.init.SoundEvents.BLOCK_CHEST_OPEN : net.minecraft.init.SoundEvents.BLOCK_CHEST_CLOSE,
                net.minecraft.util.SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override public boolean receiveClientEvent(int id, int value) {
        if (id != 1) { return super.receiveClientEvent(id, value); }
        watchers = value;
        return true;
    }

    @Override public void update() {
        lidBefore = lid;
        if (watchers > 0 && lid == 0.0F) { sound(true); }
        if (watchers == 0 && lid > 0.0F || watchers > 0 && lid < 1.0F) {
            lid = net.minecraft.util.math.MathHelper.clamp(lid + (watchers > 0 ? 0.1F : -0.1F), 0.0F, 1.0F);
            if (lid < 0.5F && lidBefore >= 0.5F && watchers == 0) { sound(false); }
        }
    }

    public boolean watched() { return watchers > 0; }

    public boolean pendingLoot() { return lootTable != null; }

    public NBTTagCompound vacate() {
        NBTTagCompound tag = writeToNBT(new NBTTagCompound());
        lootTable = null;
        items = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        return tag;
    }

    public void adopt(NBTTagCompound tag) {
        if (rows * columns >= tag.getInteger(ROWS) * tag.getInteger(COLUMNS)) { writeSize(tag); }
        tag.setString(NAMED, named);
        lootTable = null;
        readFromNBT(tag);
        markDirty();
    }

    public void loot(@Nullable ResourceLocation table, long seed) {
        if (table == null) { return; }
        setLootTable(table, seed);
    }

    public void unloot() { lootTable = null; }

    public void brokenBy(@Nonnull EntityPlayer player) {
        creative = player.capabilities.isCreativeMode;
        fillWithLoot(player);
    }

    @Nonnull public ItemStack kept(@Nonnull ItemStack block, @Nonnull ContainerDef def) {
        fillWithLoot(null);
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty() && !ContainerPack.storable(items.get(i))) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
        }
        if (creative && isEmpty() && !hasCustomName()) { return ItemStack.EMPTY; }
        if (!isEmpty()) { PouchInventory.store(block, items); }
        if (rows != def.rows || columns != def.columns) { writeSize(block.getOrCreateSubCompound(PouchInventory.HELD)); }
        if (hasCustomName() && customName != null) { block.setStackDisplayName(customName); }
        clear();
        return block;
    }

    public void restore(@Nonnull ItemStack stack) {
        NBTTagCompound held = stack.getSubCompound(PouchInventory.HELD);
        if (held != null && held.hasKey(ROWS)) {
            readSize(held);
            fit();
        }
        PouchInventory.load(stack, items);
    }

    @Nonnull public EnumFacing facing() { return facing; }

    public void face(@Nonnull EnumFacing turned) { facing = turned.getAxis().isHorizontal() ? turned : EnumFacing.SOUTH; }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        if (world == null || world.getTileEntity(pos) != this) { return false; }
        return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override public boolean shouldRefresh(@Nonnull net.minecraft.world.World world, @Nonnull BlockPos pos, @Nonnull IBlockState was, @Nonnull IBlockState is) { return was.getBlock() != is.getBlock(); }
}
