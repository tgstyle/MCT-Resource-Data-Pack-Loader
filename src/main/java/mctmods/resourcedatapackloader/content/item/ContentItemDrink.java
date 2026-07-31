package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;
import mctmods.resourcedatapackloader.util.RomanNumerals;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ContentItemDrink extends Item implements IContentItem {
    private final ItemDef def;

    public ContentItemDrink(ItemDef def) {
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        setHasSubtypes(true);
        setMaxDamage(0);
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

    @Override public int getMetadata(int damage) { return damage; }

    @Override public int getMaxItemUseDuration(@Nonnull ItemStack stack) { return def.useDuration; }

    @Override @Nonnull public EnumAction getItemUseAction(@Nonnull ItemStack stack) { return def.eat ? EnumAction.EAT : EnumAction.DRINK; }

    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override @Nonnull public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
        ItemVariant value = variant(stack);

        if (!world.isRemote && value != null) {
            PotionEffect effect = value.getResolvedPotion();
            if (effect != null) { entity.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(), false)); }
        }

        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) { return stack; }

        stack.shrink(1);
        ItemStack container = def.getResolvedContainer();
        if (container.isEmpty()) { return stack; }
        if (stack.isEmpty()) { return container.copy(); }

        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).inventory.addItemStackToInventory(container.copy())) {
            ((EntityPlayer) entity).dropItem(container.copy(), false);
        }
        return stack;
    }

    @Override public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        ItemVariant value = variant(stack);
        if (value == null) { return; }

        PotionEffect effect = value.getResolvedPotion();
        if (effect == null) { return; }

        Potion potion = effect.getPotion();
        if (!potion.isBeneficial()) { return; }

        String name = new TextComponentTranslation(effect.getEffectName()).getFormattedText();
        String level = RomanNumerals.of(effect.getAmplifier());
        tooltip.add(TextFormatting.GREEN + (level.isEmpty() ? name : name + " " + level));
    }
}
