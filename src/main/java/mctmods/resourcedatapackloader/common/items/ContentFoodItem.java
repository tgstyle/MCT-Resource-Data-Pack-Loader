package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

@SuppressWarnings("deprecation") public class ContentFoodItem extends ItemFood implements IContentItem {
    private final ItemDef def;

    public ContentFoodItem(ItemDef def) {
        super(0, 0.0F, false);
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        setHasSubtypes(true);
        setMaxDamage(0);
        if (def.alwaysEdible) { setAlwaysEdible(); }
        ContentSetup.apply(this, def.creativeTab);
    }

    @Nullable private ItemVariant variant(ItemStack stack) { return ItemVariants.at(def, stack); }

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { ItemVariants.subItems(this, def, tab, list); }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) { return ItemVariants.key(this, def, stack); }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) { return ItemVariants.rarity(variant(stack)); }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) { return ItemVariants.stackLimit(variant(stack)); }

    @Override public int getHealAmount(@Nonnull ItemStack stack) { return ItemVariants.heal(variant(stack)); }

    @Override public float getSaturationModifier(@Nonnull ItemStack stack) { return ItemVariants.saturation(variant(stack)); }

    @Override public int getMetadata(int damage) { return damage; }

    @Override public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) { IContentItem.potionTooltip(variant(stack), tooltip); }

    @Override protected void onFoodEaten(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityPlayer player) {
        if (world.isRemote) { return; }
        ItemVariants.cooldown(player, this, def.cooldown);
        ItemVariants.applyPotion(variant(stack), player);
    }
}
