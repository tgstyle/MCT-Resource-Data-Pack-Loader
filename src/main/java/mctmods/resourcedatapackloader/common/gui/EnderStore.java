package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public final class EnderStore extends WorldSavedData {
    private static final String NAME = "rdpl_ender";
    private static final String STORES = "Stores";
    private final Map<UUID, NonNullList<ItemStack>> stores = new HashMap<>();
    private final Map<UUID, Integer> versions = new HashMap<>();
    private NBTTagCompound saved = new NBTTagCompound();

    public EnderStore(String name) { super(name); }

    @Nullable static EnderStore get(World world) {
        return SavedData.of(world.getMapStorage(), EnderStore.class, NAME, EnderStore::new);
    }

    NonNullList<ItemStack> items(UUID owner) {
        return stores.computeIfAbsent(owner, key -> {
            NonNullList<ItemStack> items = NonNullList.withSize(EnderInventory.STORE, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(saved.getCompoundTag(key.toString()), items);
            return items;
        });
    }

    int version(UUID owner) { return versions.getOrDefault(owner, 0); }

    void changed(UUID owner) {
        versions.merge(owner, 1, Integer::sum);
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        saved = compound.getCompoundTag(STORES);
        stores.clear();
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        stores.forEach((owner, items) -> saved.setTag(owner.toString(), ItemStackHelper.saveAllItems(new NBTTagCompound(), items, true)));
        compound.setTag(STORES, saved);
        return compound;
    }
}
