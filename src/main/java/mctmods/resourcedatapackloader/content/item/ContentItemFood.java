package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentItemFood extends ItemFood implements IContentItem {
    private final ItemDef def;

    public ContentItemFood(ItemDef def) {
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

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) { ItemVariants.subItems(this, def, list); }
    }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) { return ItemVariants.key(this, def, stack); }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? EnumRarity.COMMON : value.rarity;
    }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? 64 : value.maxSize;
    }

    @Override public int getHealAmount(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? 0 : value.healAmount;
    }

    @Override public float getSaturationModifier(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? 0.0F : value.saturation;
    }

    @Override public int getMetadata(int damage) { return damage; }

    @Override public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) { IContentItem.potionTooltip(variant(stack), tooltip); }

    @Override protected void onFoodEaten(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityPlayer player) {
        if (world.isRemote) { return; }
        if (def.cooldown > 0) { player.getCooldownTracker().setCooldown(this, def.cooldown); }
        ItemVariants.applyPotion(variant(stack), player);
    }
}
