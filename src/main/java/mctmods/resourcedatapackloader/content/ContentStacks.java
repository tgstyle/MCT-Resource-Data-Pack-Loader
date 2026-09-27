package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import javax.annotation.Nullable;

public final class ContentStacks {
    private ContentStacks() {}

    public static ItemStack parse(Identifier key, String value, int count) {
        Item item = find(key, value);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Nullable public static ItemStackTemplate template(Identifier key, String value, int count) {
        Item item = find(key, value);
        return item == null || item == Items.AIR || count <= 0 ? null : new ItemStackTemplate(item, count);
    }

    public static boolean matches(ItemStack found, ItemStack wanted) { return wanted.getComponentsPatch().isEmpty() ? ItemStack.isSameItem(found, wanted) : ItemStack.isSameItemSameComponents(found, wanted); }

    @Nullable public static Item find(Identifier key, @Nullable String value) {
        if (value == null || value.isEmpty()) { return null; }
        Identifier name = value.indexOf(':') < 0 ? null : ContentParser.location(value);
        if (name == null) {
            ContentLog.LOGGER.error("Item '{}' in {} needs a namespace, such as minecraft:iron_ingot", value, key);
            return null;
        }
        Item item = Registered.find(BuiltInRegistries.ITEM, name);
        if (item == null) { ContentLog.LOGGER.error("Unknown item '{}' in {}, skipping it", value, key); }
        return item;
    }

    @Nullable public static Item item(@Nullable Identifier name) { return Registered.find(BuiltInRegistries.ITEM, name); }

    public static boolean registered(Identifier name) { return BuiltInRegistries.ITEM.containsKey(name); }

    public static String id(Item item) { return BuiltInRegistries.ITEM.getKey(item).toString(); }

    public static String namespaceOf(Item item) { return BuiltInRegistries.ITEM.getKey(item).getNamespace(); }
}
