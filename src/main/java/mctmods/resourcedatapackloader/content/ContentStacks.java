package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;

public final class ContentStacks {
    private ContentStacks() {}

    public static ItemStack parse(ResourceLocation key, String value, int count) {
        Item item = find(key, value);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Nullable public static Item find(ResourceLocation key, @Nullable String value) {
        if (value == null || value.isEmpty()) { return null; }
        ResourceLocation name = value.indexOf(':') < 0 ? null : ResourceLocation.tryParse(value);
        if (name == null) {
            ContentLog.LOGGER.error("Item '{}' in {} needs a namespace, such as minecraft:iron_ingot", value, key);
            return null;
        }
        if (!registered(name)) {
            ContentLog.LOGGER.error("Unknown item '{}' in {}, skipping it", value, key);
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(name);
    }

    public static boolean registered(ResourceLocation name) { return ForgeRegistries.ITEMS.containsKey(name); }

    public static String namespaceOf(Item item) {
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(item);
        return name == null ? "minecraft" : name.getNamespace();
    }
}
