package mctmods.resourcedatapackloader.common.enchantments;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.google.common.collect.MapMaker;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public final class TableLapis implements ICapabilitySerializable<CompoundTag> {
    private static final ResourceLocation KEY = ResourceDataPackLoader.id("table_lapis");
    private static final String LAPIS = "Lapis";
    private static final int SLOT = 1;
    private static final Map<BlockEntity, TableLapis> HELD = new MapMaker().weakKeys().makeMap();
    private ItemStack lapis = ItemStack.EMPTY;

    private TableLapis() {}

    @Nullable private static TableLapis at(Level level, BlockPos pos) {
        BlockEntity table = level.getBlockEntity(pos);
        return table == null ? null : HELD.get(table);
    }

    public static void onAttach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!(event.getObject() instanceof EnchantmentTableBlockEntity)) { return; }
        TableLapis held = new TableLapis();
        HELD.put(event.getObject(), held);
        event.addCapability(KEY, held);
    }

    public static void onOpen(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof EnchantmentMenu menu)) { return; }
        ((ITableAccessHolder) menu).rdpl$access().execute((level, pos) -> {
            TableLapis held = at(level, pos);
            if (held == null || held.lapis.isEmpty()) { return; }
            ItemStack lapis = held.lapis;
            held.lapis = ItemStack.EMPTY;
            level.blockEntityChanged(pos);
            menu.getSlot(SLOT).set(lapis);
        });
    }

    public static void closing(Container slots, ContainerLevelAccess access) {
        if (!ContentEnchanting.keepLapis()) { return; }
        access.execute((level, pos) -> {
            TableLapis held = at(level, pos);
            if (held == null) { return; }
            if (held.lapis.isEmpty()) { held.lapis = slots.removeItemNoUpdate(SLOT); }
            else if (ItemHandlerHelper.canItemStacksStack(held.lapis, slots.getItem(SLOT))) {
                int moved = Math.min(slots.getItem(SLOT).getCount(), held.lapis.getMaxStackSize() - held.lapis.getCount());
                held.lapis.grow(moved);
                slots.removeItem(SLOT, moved);
            }
            level.blockEntityChanged(pos);
        });
    }

    public static void broken(Level level, BlockPos pos) {
        TableLapis held = level.isClientSide ? null : at(level, pos);
        if (held == null || held.lapis.isEmpty()) { return; }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), held.lapis);
        held.lapis = ItemStack.EMPTY;
    }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) { return LazyOptional.empty(); }

    @Override public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (!lapis.isEmpty()) { tag.put(LAPIS, lapis.save(new CompoundTag())); }
        return tag;
    }

    @Override public void deserializeNBT(CompoundTag tag) { lapis = tag.contains(LAPIS, Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound(LAPIS)) : ItemStack.EMPTY; }
}
