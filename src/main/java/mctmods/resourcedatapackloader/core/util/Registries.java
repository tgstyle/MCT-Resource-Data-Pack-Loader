package mctmods.resourcedatapackloader.core.util;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;

import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import javax.annotation.Nullable;

public final class Registries {
    private Registries() {}

    @Nullable public static Item item(String name) {
        if (name.isEmpty()) { return null; }
        Item found = Item.REGISTRY.getObject(new ResourceLocation(name));
        return found == null || found == Items.AIR ? null : found;
    }

    @Nullable public static Item itemNamed(String name) { return find(ForgeRegistries.ITEMS, new ResourceLocation(name)); }

    public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, String namespace, Supplier<? extends T> entry) { ContentOwners.as(namespace, () -> registry.register(entry.get())); }

    @Nullable public static <T extends IForgeRegistryEntry<T>> T find(IForgeRegistry<T> registry, String name) { return find(registry, new ResourceLocation(name)); }

    @Nullable public static <T extends IForgeRegistryEntry<T>> T find(IForgeRegistry<T> registry, ResourceLocation key) { return registry.containsKey(key) ? registry.getValue(key) : null; }
}
