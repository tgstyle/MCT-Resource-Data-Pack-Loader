package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ContentBlock extends Block implements IContentBlock {
    private static final ThreadLocal<BlockDef> CONSTRUCTING = new ThreadLocal<>();
    private static final ThreadLocal<PropertyVariant> PROPERTY = new ThreadLocal<>();
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlock create(BlockDef def) {
        beginConstruction(def);
        try { return new ContentBlock(def); }
        finally { endConstruction(); }
    }

    protected static void beginConstruction(BlockDef def) { CONSTRUCTING.set(def); }

    protected static void endConstruction() {
        CONSTRUCTING.remove();
        PROPERTY.remove();
    }

    protected ContentBlock(BlockDef def) {
        super(def.material, def.mapColor);
        this.def = def;
        this.variant = PROPERTY.get();
        ContentSetup.apply(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultSlipperiness(def.slipperiness);
        setDefaultState(this.blockState.getBaseState().withProperty(this.variant, def.at(0).name));
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() {
        PropertyVariant property = new PropertyVariant(ContentSetup.names(CONSTRUCTING.get()));
        PROPERTY.set(property);
        return new BlockStateContainer(this, property);
    }

    @Override public BlockDef getDef() { return def; }

    @Override public boolean canSustainPlant(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing direction, @Nonnull IPlantable plantable) {
        if (def.plantTypes.isEmpty()) { return super.canSustainPlant(state, world, pos, direction, plantable); }
        if (def.plantTypes.contains(plantable.getPlantType(world, pos.offset(direction)).name().toLowerCase(Locale.ROOT))) { return true; }

        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    private BlockDef def() { return def == null ? CONSTRUCTING.get() : def; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    public PropertyVariant getVariantProperty() { return variant; }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        for (BlockVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(variant, def.at(meta).name);
    }

    @Override public int getMetaFromState(IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return getMetaFromState(state); }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return def.at(getMetaFromState(state)).light;
    }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(getMetaFromState(state)).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        return def.at(getMetaFromState(state)).hardness;
    }

    @Override public float getExplosionResistance(World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        return def.at(getMetaFromState(state)).resistance / def.explosionResistanceDivisor;
    }

    @Override public int getExpDrop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, int fortune) {
        if (def.expDropMax <= def.expDropMin) { return Math.max(0, def.expDropMin); }
        return def.expDropMin + RANDOM.nextInt(def.expDropMax - def.expDropMin + 1);
    }

    @Override public boolean canSilkHarvest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull net.minecraft.entity.player.EntityPlayer player) {
        return def.silkHarvest;
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        BlockVariant value = def.at(getMetaFromState(state));
        if (value.drops.isEmpty()) {
            super.getDrops(drops, world, pos, state, fortune);
            return;
        }

        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        int count = quantityDropped(state, fortune, rand);

        for (int i = 0; i < count; i++) {
            int roll = 1 + rand.nextInt(100);
            for (DropDef drop : value.drops) {
                if (drop.getResolved() == null) { continue; }
                ItemStack stack = new ItemStack(drop.getResolved(), drop.amount, drop.meta);
                if (stack.isEmpty()) { continue; }
                if (drop.guaranteed) { drops.add(stack.copy()); }
                if (roll <= drop.chanceFor(fortune)) { drops.add(stack.copy()); }
            }
        }
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def().renderLayer; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return def().opaque; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return def().fullCube; }

    @Override public int getLightOpacity(@Nonnull IBlockState state) { return def().lightOpacity; }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        BlockDef current = def();
        return current.bounds == null ? super.getBoundingBox(state, source, pos) : current.bounds;
    }

    @Override public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def().flammability; }

    @Override public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def().fireSpread; }

    @Override public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def().flammability > 0; }
}
