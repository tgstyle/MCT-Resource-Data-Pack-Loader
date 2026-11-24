package mctmods.resourcedatapackloader.common.enchantments;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.google.common.collect.MapMaker;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public final class TableLapis implements ICapabilitySerializable<NBTTagCompound> {
    private static final ResourceLocation KEY = ResourceDataPackLoader.id("table_lapis");
    private static final String LAPIS = "Lapis";
    private static final int SLOT = 1;
    private static final Map<TileEntity, TableLapis> HELD = new MapMaker().weakKeys().makeMap();
    private ItemStack lapis = ItemStack.EMPTY;

    private TableLapis() {}

    @Nullable private static TableLapis at(World world, BlockPos pos) {
        TileEntity table = world.getTileEntity(pos);
        return table == null ? null : HELD.get(table);
    }

    private static void dirty(World world, BlockPos pos) {
        TileEntity table = world.getTileEntity(pos);
        if (table != null) { table.markDirty(); }
    }

    @SubscribeEvent public static void onAttach(AttachCapabilitiesEvent<TileEntity> event) {
        if (!(event.getObject() instanceof TileEntityEnchantmentTable)) { return; }
        TableLapis held = new TableLapis();
        HELD.put(event.getObject(), held);
        event.addCapability(KEY, held);
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof ContainerEnchantment)) { return; }
        ContainerEnchantment menu = (ContainerEnchantment) event.getContainer();
        World world = event.getEntityPlayer().world;
        TableLapis held = at(world, menu.position);
        if (held == null || held.lapis.isEmpty()) { return; }
        ItemStack lapis = held.lapis;
        held.lapis = ItemStack.EMPTY;
        dirty(world, menu.position);
        menu.tableInventory.setInventorySlotContents(SLOT, lapis);
    }

    public static void closing(ContainerEnchantment menu, World world) {
        TableLapis held = ContentEnchanting.keepLapis() && !world.isRemote ? at(world, menu.position) : null;
        if (held == null) { return; }
        IInventory slots = menu.tableInventory;
        if (held.lapis.isEmpty()) { held.lapis = slots.removeStackFromSlot(SLOT); }
        else if (ItemHandlerHelper.canItemStacksStack(held.lapis, slots.getStackInSlot(SLOT))) {
            int moved = Math.min(slots.getStackInSlot(SLOT).getCount(), held.lapis.getMaxStackSize() - held.lapis.getCount());
            held.lapis.grow(moved);
            slots.decrStackSize(SLOT, moved);
        }
        dirty(world, menu.position);
    }

    public static void broken(World world, BlockPos pos) {
        TableLapis held = world.isRemote ? null : at(world, pos);
        if (held == null || held.lapis.isEmpty()) { return; }
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), held.lapis);
        held.lapis = ItemStack.EMPTY;
    }

    @SubscribeEvent public static void onClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().getTileEntity(event.getPos()) instanceof TileEntityEnchantmentTable && replacedTable(event.getWorld().getBlockState(event.getPos()).getBlock())) {
            broken(event.getWorld(), event.getPos());
        }
    }

    public static boolean replacedTable(Block block) {
        return block.getClass() != BlockEnchantmentTable.class;
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return false; }

    @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) { return null; }

    @Override public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        if (!lapis.isEmpty()) { tag.setTag(LAPIS, lapis.writeToNBT(new NBTTagCompound())); }
        return tag;
    }

    @Override public void deserializeNBT(NBTTagCompound tag) { lapis = tag.hasKey(LAPIS, Constants.NBT.TAG_COMPOUND) ? new ItemStack(tag.getCompoundTag(LAPIS)) : ItemStack.EMPTY; }
}
