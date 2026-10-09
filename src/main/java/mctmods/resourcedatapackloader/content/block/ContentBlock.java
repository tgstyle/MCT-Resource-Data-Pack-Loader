package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlock extends Block implements IContentBlock, IVariantBlock {
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlock create(BlockDef def) { return BlockVariants.construct(def, ContentBlock::new); }

    protected ContentBlock(BlockDef def) {
        super(def.material, def.mapColor);
        this.def = def;
        this.variant = BlockVariants.property();
        ContentSetup.apply(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultSlipperiness(def.slipperiness);
        setDefaultState(this.blockState.getBaseState().withProperty(this.variant, def.at(0).name));
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return BlockVariants.container(this); }

    @Override public BlockDef getDef() { return def; }

    @Override public boolean canSustainPlant(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing direction, @Nonnull IPlantable plantable) { return BlockVariants.sustains(def, world, pos, direction, plantable) || super.canSustainPlant(state, world, pos, direction, plantable); }

    private BlockDef def() { return def == null ? BlockVariants.def() : def; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override public PropertyVariant getVariantProperty() { return variant; }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { BlockVariants.subBlocks(this, def, list); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(variant, def.at(meta).name); }

    @Override public int getMetaFromState(IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return getMetaFromState(state); }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return def.at(getMetaFromState(state)).light; }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(getMetaFromState(state)).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) { return def.at(getMetaFromState(state)).hardness; }

    @Override public float getExplosionResistance(World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) { return ContentSetup.resistanceOf(def, def.at(getMetaFromState(world.getBlockState(pos)))); }

    @Override public int getExpDrop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, int fortune) { return VariantDrops.experience(def, RANDOM); }

    @Override public boolean canSilkHarvest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player) { return def.silkHarvest; }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (def.at(getMetaFromState(state)).drops.isEmpty() || def.opensWith != null) { super.getDrops(drops, world, pos, state, fortune); }
        else { VariantDrops.roll(this, drops, world, state, def.at(getMetaFromState(state)), fortune); }
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (def.opensWith == null) { return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ); }
        ItemStack held = player.getHeldItem(hand);
        Item key = ForgeRegistries.ITEMS.getValue(def.opensWith);
        BlockVariant value = def.at(getMetaFromState(state));
        if (key == null || held.getItem() != key) {
            if (!world.isRemote) { player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + "." + value.name + ".locked"), true); }
            return true;
        }
        if (world.isRemote) { return true; }
        SoundEvent opening = def.openSound.isEmpty() ? null : SoundEvent.REGISTRY.getObject(new ResourceLocation(def.openSound));
        if (opening == null) { opening = getSoundType(state, world, pos, player).getBreakSound(); }
        world.playSound(null, pos, opening, SoundCategory.BLOCKS, 1.0F, 0.9F + world.rand.nextFloat() * 0.2F);
        NonNullList<ItemStack> loot = NonNullList.create();
        VariantDrops.roll(this, loot, world, state, value, 0);
        for (ItemStack stack : loot) { spawnAsEntity(world, pos, stack); }
        VariantDrops.spawnEntities(world, pos, value);
        if (!player.capabilities.isCreativeMode) { held.shrink(1); }
        world.playEvent(2001, pos, Block.getStateId(state));
        world.setBlockToAir(pos);
        return true;
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote && def.opensWith == null) { VariantDrops.spawnEntities(world, pos, def.at(getMetaFromState(state))); }
        super.breakBlock(world, pos, state);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def().renderLayer; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return def().opaque; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return def().fullCube; }

    @Override public int getLightOpacity(@Nonnull IBlockState state) { return def().lightOpacity; }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) { return BlockVariants.bounds(def(), super.getBoundingBox(state, source, pos)); }
}
