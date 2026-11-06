package mctmods.resourcedatapackloader.common.items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

public final class ContentEnderStore extends SavedData {
    private static final String NAME = "rdpl_ender";
    private static final String STORES = "Stores";
    private final Map<UUID, NonNullList<ItemStack>> stores = new HashMap<>();
    private final Map<UUID, Integer> versions = new HashMap<>();
    private CompoundTag saved = new CompoundTag();
    private HolderLookup.Provider registries;

    private static final Factory<ContentEnderStore> FACTORY = new Factory<>(ContentEnderStore::new, (tag, lookup) -> read(tag));

    static ContentEnderStore get(MinecraftServer server) {
        ContentEnderStore data = server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME);
        data.registries = server.registryAccess();
        return data;
    }

    private static ContentEnderStore read(CompoundTag tag) {
        ContentEnderStore data = new ContentEnderStore();
        data.saved = tag.getCompound(STORES);
        return data;
    }

    NonNullList<ItemStack> items(UUID owner) {
        return stores.computeIfAbsent(owner, key -> {
            NonNullList<ItemStack> items = NonNullList.withSize(ContentEnderInventory.STORE, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(saved.getCompound(key.toString()), items, registries);
            return items;
        });
    }

    int version(UUID owner) { return versions.getOrDefault(owner, 0); }

    void changed(UUID owner) {
        versions.merge(owner, 1, Integer::sum);
        setDirty();
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        stores.forEach((owner, items) -> saved.put(owner.toString(), ContainerHelper.saveAllItems(new CompoundTag(), items, true, lookup)));
        tag.put(STORES, saved);
        return tag;
    }
}
