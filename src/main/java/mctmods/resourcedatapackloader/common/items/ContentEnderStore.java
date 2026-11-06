package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.util.world.SavedTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class ContentEnderStore extends SavedData {
    private static final String NAME = "rdpl_ender";
    private static final String STORES = "Stores";
    private static final SavedDataType<ContentEnderStore> TYPE = SavedTypes.of(NAME, ContentEnderStore::new, ContentEnderStore::read, ContentEnderStore::write);
    private final Map<UUID, NonNullList<ItemStack>> stores = new HashMap<>();
    private final Map<UUID, Integer> versions = new HashMap<>();
    private CompoundTag saved = new CompoundTag();
    @Nullable private HolderLookup.Provider registries;

    static ContentEnderStore get(MinecraftServer server) {
        ContentEnderStore data = server.overworld().getDataStorage().computeIfAbsent(TYPE);
        data.registries = server.registryAccess();
        return data;
    }

    private static ContentEnderStore read(CompoundTag tag) {
        ContentEnderStore data = new ContentEnderStore();
        data.saved = tag.getCompoundOrEmpty(STORES).copy();
        return data;
    }

    NonNullList<ItemStack> items(UUID owner) {
        return stores.computeIfAbsent(owner, key -> {
            NonNullList<ItemStack> items = NonNullList.withSize(ContentEnderInventory.STORE, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, Objects.requireNonNull(registries), saved.getCompoundOrEmpty(key.toString())), items);
            return items;
        });
    }

    int version(UUID owner) { return versions.getOrDefault(owner, 0); }

    void changed(UUID owner) {
        versions.merge(owner, 1, Integer::sum);
        setDirty();
    }

    private CompoundTag write() {
        if (registries != null) {
            stores.forEach((owner, items) -> {
                TagValueOutput held = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, Objects.requireNonNull(registries));
                ContainerHelper.saveAllItems(held, items, true);
                saved.put(owner.toString(), held.buildResult());
            });
        }
        CompoundTag tag = new CompoundTag();
        tag.put(STORES, saved.copy());
        return tag;
    }
}
