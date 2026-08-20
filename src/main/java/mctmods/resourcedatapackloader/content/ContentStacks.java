package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import javax.annotation.Nullable;

public final class ContentStacks {
    private ContentStacks() {}

    public static ItemStack parse(ResourceLocation key, String value, int count) {
        if (value == null || value.isEmpty()) { return ItemStack.EMPTY; }
        String[] parts = value.split(":");
        if (parts.length < 2) {
            ContentLog.LOGGER.error("Item '{}' in {} needs a namespace, such as minecraft:iron_ingot", value, key);
            return ItemStack.EMPTY;
        }
        Item item = find(parts[0] + ":" + parts[1]);
        if (item == null) {
            ContentLog.LOGGER.error("Unknown item '{}' in {}, skipping it", value, key);
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count, meta(key, parts));
    }

    private static int meta(ResourceLocation key, String[] parts) {
        if (parts.length < 3) { return 0; }
        if ("*".equals(parts[2])) { return OreDictionary.WILDCARD_VALUE; }
        try { return Integer.parseInt(parts[2]); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Metadata '{}' in {} is not a number, using 0", parts[2], key);
            return 0;
        }
    }

    @Nullable private static Item find(String name) {
        ResourceLocation location = new ResourceLocation(name);
        return ForgeRegistries.ITEMS.containsKey(location) ? ForgeRegistries.ITEMS.getValue(location) : null;
    }
}
