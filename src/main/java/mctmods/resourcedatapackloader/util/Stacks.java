package mctmods.resourcedatapackloader.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import java.util.Locale;
import javax.annotation.Nullable;

public final class Stacks {
    private Stacks() {}

    @Nullable public static String namespace(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        ResourceLocation name = stack.getItem().getRegistryName();
        return name == null ? null : name.getNamespace().toLowerCase(Locale.ROOT);
    }

    public static boolean matches(ItemStack wanted, ItemStack found) {
        if (wanted.isEmpty() || found.isEmpty() || wanted.getItem() != found.getItem()) { return false; }
        return wanted.getItemDamage() == OreDictionary.WILDCARD_VALUE || found.getItemDamage() == OreDictionary.WILDCARD_VALUE || wanted.getItemDamage() == found.getItemDamage();
    }
}
