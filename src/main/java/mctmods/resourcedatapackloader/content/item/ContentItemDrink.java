package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentItemDrink extends Item implements IContentItem {
    private final ItemDef def;

    public ContentItemDrink(ItemDef def) {
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        setHasSubtypes(true);
        setMaxDamage(0);
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

    @Override public int getMetadata(int damage) { return damage; }

    @Override public int getMaxItemUseDuration(@Nonnull ItemStack stack) { return def.useDuration; }

    @Override @Nonnull public EnumAction getItemUseAction(@Nonnull ItemStack stack) { return def.eat ? EnumAction.EAT : EnumAction.DRINK; }

    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override @Nonnull public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
        if (!world.isRemote) { ItemVariants.applyPotion(variant(stack), entity); }
        if (entity instanceof EntityPlayer && def.cooldown > 0) { ((EntityPlayer) entity).getCooldownTracker().setCooldown(this, def.cooldown); }
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

    @Override public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) { IContentItem.potionTooltip(variant(stack), tooltip); }
}
