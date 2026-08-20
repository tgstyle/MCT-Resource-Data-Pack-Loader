package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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

@SuppressWarnings("deprecation") public class ContentBlockBannerWall extends BlockBanner.BlockBannerHanging implements IContentBlock {
    public static final String WALL_SUFFIX = "_wall";
    private final BlockDef def;
    @Nullable private ContentBlockBanner standing;

    public ContentBlockBannerWall(BlockDef def) {
        this.def = def;
        BlockVariant variant = def.at(0);
        ContentSetup.material(this, def);
        setRegistryName(new ResourceLocation(def.registryName.getNamespace(), def.registryName.getPath() + WALL_SUFFIX));
        setTranslationKey(def.registryName + "." + variant.name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        ContentSetup.properties(this, def);
    }

    public void pair(ContentBlockBanner other) { this.standing = other; }

    @Nullable private Item item() { return standing == null ? null : standing.getBannerItem(); }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public Item createItem() { return null; }

    @Override @Nonnull public String getLocalizedName() { return I18n.translateToLocal(getTranslationKey() + ".name"); }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return false; }

    @Override @Nonnull public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) { return EnumBlockRenderType.MODEL; }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
        Item item = item();
        return item == null ? Item.getItemFromBlock(this) : item;
    }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        Item item = item();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        Item item = item();
        if (item != null) { drops.add(new ItemStack(item)); }
    }

    @Override public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) {
        return def.at(0).resistance / def.explosionResistanceDivisor;
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
