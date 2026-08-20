package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBanner;

import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockBanner extends BlockBanner.BlockBannerStanding implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;
    @Nullable private ContentBlockBannerWall wall;
    @Nullable private Item banner;

    public ContentBlockBanner(BlockDef def) {
        this.def = def;
        BlockVariant variant = def.at(0);
        ContentSetup.material(this, def);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + variant.name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        ContentSetup.properties(this, def);
    }

    public void pair(ContentBlockBannerWall other) { this.wall = other; }

    @Nullable public ContentBlockBannerWall getWall() { return wall; }

    @Nullable public Item getBannerItem() { return banner; }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public Item createItem() {
        ContentItemBanner item = new ContentItemBanner(this);
        item.setTranslationKey(def.registryName + "." + def.at(0).name);
        ContentSetup.apply(item, def.creativeTab);
        this.banner = item;
        return item;
    }

    @Override @Nonnull public String getLocalizedName() { return I18n.translateToLocal(getTranslationKey() + ".name"); }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return false; }

    @Override @Nonnull public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) { return EnumBlockRenderType.MODEL; }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
        return banner == null ? Item.getItemFromBlock(this) : banner;
    }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return banner == null ? ItemStack.EMPTY : new ItemStack(banner);
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (banner != null) { drops.add(new ItemStack(banner)); }
    }

    @Override public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) {
        return def.at(0).resistance / def.explosionResistanceDivisor;
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
