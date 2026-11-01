package mctmods.resourcedatapackloader.common.blocks.banner;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation") final class BannerParts {
    private BannerParts() {}

    @Nonnull static String name(Block block) { return I18n.translateToLocal(block.getTranslationKey() + ".name"); }

    @Nonnull static Item dropped(Block block, @Nullable Item banner) { return banner == null ? Item.getItemFromBlock(block) : banner; }

    @Nonnull static ItemStack stack(@Nullable Item banner) { return banner == null ? ItemStack.EMPTY : new ItemStack(banner); }

    static void drops(NonNullList<ItemStack> drops, @Nullable Item banner) { if (banner != null) { drops.add(new ItemStack(banner)); } }

    static float resistance(BlockDef def) { return ContentSetup.resistanceOf(def, def.at(0)); }
}
