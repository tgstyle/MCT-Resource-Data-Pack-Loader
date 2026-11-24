package mctmods.resourcedatapackloader.common.util.loot;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class Stacks {
    private Stacks() {}

    @Nullable public static String namespace(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        ResourceLocation name = stack.getItem().getRegistryName();
        return name == null ? null : name.getNamespace().toLowerCase(Locale.ROOT);
    }

    public static List<ItemStack> held(EntityPlayer player) {
        List<ItemStack> all = new ArrayList<>();
        for (List<ItemStack> part : Arrays.asList(player.inventory.mainInventory, player.inventory.armorInventory, player.inventory.offHandInventory)) {
            for (ItemStack stack : part) {
                if (!stack.isEmpty()) { all.add(stack); }
            }
        }
        return all;
    }

    public static void dropAt(World world, double x, double y, double z, ItemStack stack, int delay) {
        EntityItem item = new EntityItem(world, x, y, z, stack);
        item.setPickupDelay(delay);
        world.spawnEntity(item);
    }

    public static boolean matches(ItemStack wanted, ItemStack found) {
        if (wanted.isEmpty() || found.isEmpty() || wanted.getItem() != found.getItem()) { return false; }
        return found.getItemDamage() == OreDictionary.WILDCARD_VALUE || ContentStacks.matches(found, wanted.getItem(), wanted.getItemDamage());
    }
}
