package mctmods.resourcedatapackloader.content.tile;

import mctmods.resourcedatapackloader.content.block.ContentBlockContainer;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.gui.ContainerPack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityPackContainer extends TileEntityLockableLoot implements ITickable {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private int rows = 3;
    private int columns = 9;
    private String named = "";
    private int watchers;
    private EnumFacing facing = EnumFacing.SOUTH;
    private float lid;
    private float lidBefore;

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
        return state != null && state.getBlock() instanceof ContentBlockContainer ? ((ContentBlockContainer) state.getBlock()).container() : null;
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
        rows = Math.max(1, tag.getInteger("RdplRows"));
        columns = Math.max(1, tag.getInteger("RdplColumns"));
        named = tag.getString("RdplNamed");
        facing = EnumFacing.byHorizontalIndex(tag.getInteger("RdplFacing"));
        items = NonNullList.withSize(rows * columns, ItemStack.EMPTY);
        if (!checkLootAndRead(tag)) { net.minecraft.inventory.ItemStackHelper.loadAllItems(tag, items); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("RdplRows", rows);
        tag.setInteger("RdplColumns", columns);
        tag.setString("RdplNamed", named);
        tag.setInteger("RdplFacing", facing.getHorizontalIndex());
        if (!checkLootAndWrite(tag)) { net.minecraft.inventory.ItemStackHelper.saveAllItems(tag, items); }
        return tag;
    }

    @Override @Nonnull public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        super.writeToNBT(tag);
        tag.setInteger("RdplRows", rows);
        tag.setInteger("RdplColumns", columns);
        tag.setString("RdplNamed", named);
        tag.setInteger("RdplFacing", facing.getHorizontalIndex());
        return tag;
    }

    @Override @Nullable public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }

    @Override public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity packet) {
        NBTTagCompound tag = packet.getNbtCompound();
        rows = Math.max(1, tag.getInteger("RdplRows"));
        columns = Math.max(1, tag.getInteger("RdplColumns"));
        named = tag.getString("RdplNamed");
        facing = EnumFacing.byHorizontalIndex(tag.getInteger("RdplFacing"));
        if (items.size() != rows * columns) { items = NonNullList.withSize(rows * columns, ItemStack.EMPTY); }
    }

    @Override public void openInventory(@Nonnull EntityPlayer player) {
        if (player.isSpectator()) { return; }
        watchers = Math.max(0, watchers) + 1;
        sound(true);
        tell();
    }

    @Override public void closeInventory(@Nonnull EntityPlayer player) {
        if (player.isSpectator()) { return; }
        watchers = Math.max(0, watchers - 1);
        if (watchers == 0) { sound(false); }
        tell();
    }

    private void tell() {
        if (world == null || world.isRemote) { return; }
        world.addBlockEvent(pos, getBlockType(), 1, watchers);
        world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
    }

    private void sound(boolean opening) {
        ContainerDef def = def();
        if (world == null || def == null || !def.chestModel) { return; }
        if (opening && watchers > 1) { return; }
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

    public void loot(@Nullable ResourceLocation table, long seed) {
        if (table == null) { return; }
        setLootTable(table, seed);
    }

    public void unloot() { lootTable = null; }

    @Nonnull public EnumFacing facing() { return facing; }

    public void face(@Nonnull EnumFacing turned) { facing = turned.getAxis().isHorizontal() ? turned : EnumFacing.SOUTH; }

    public int watchers() { return watchers; }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        if (world == null || world.getTileEntity(pos) != this) { return false; }
        return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override public boolean shouldRefresh(@Nonnull net.minecraft.world.World world, @Nonnull BlockPos pos, @Nonnull IBlockState was, @Nonnull IBlockState is) {
        return was.getBlock() != is.getBlock();
    }
}
