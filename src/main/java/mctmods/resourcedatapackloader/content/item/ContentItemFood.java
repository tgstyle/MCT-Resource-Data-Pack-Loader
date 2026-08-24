package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentParser;
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
import net.minecraft.potion.PotionEffect;
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

    @Override public ItemDef getDef() { return def; }

    @Nullable private ItemVariant variant(ItemStack stack) { return def.byMeta.get(stack.getMetadata()); }

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        if (!isInCreativeTab(tab)) { return; }
        for (ItemVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return super.getTranslationKey() + "." + (value == null ? ContentParser.PLACEHOLDER : value.name);
    }

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
        ItemVariant value = variant(stack);
        if (value == null) { return; }
        PotionEffect effect = value.getResolvedPotion();
        if (effect == null) { return; }
        player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(), false));
    }
}
