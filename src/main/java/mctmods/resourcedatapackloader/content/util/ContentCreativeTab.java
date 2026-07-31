package mctmods.resourcedatapackloader.content.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class ContentCreativeTab extends CreativeTabs {
    private final Supplier<ItemStack> icon;

    public ContentCreativeTab(String label, Supplier<ItemStack> icon) {
        super(label);
        this.icon = icon;
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public ItemStack createIcon() {
        ItemStack stack = icon.get();
        return stack.isEmpty() ? new ItemStack(Objects.requireNonNull(Blocks.STONE)) : stack;
    }
}
