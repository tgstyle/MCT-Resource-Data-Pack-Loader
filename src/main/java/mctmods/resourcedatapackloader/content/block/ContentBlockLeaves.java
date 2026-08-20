package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockLeaves extends BlockLeaves implements IContentBlock {
    public static final int MAX_VARIANTS = 4;
    public static final Set<String> HIDDEN = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(CHECK_DECAY.getName(), DECAYABLE.getName())));
    private static final ThreadLocal<PropertyVariant> PENDING = new ThreadLocal<>();
    private static final ThreadLocal<BlockDef> CONSTRUCTING = new ThreadLocal<>();
    private static final int VARIANT_MASK = 3;
    private static final int DECAYABLE_BIT = 4;
    private static final int CHECK_DECAY_BIT = 8;
    private static final int FORTUNE_BONUS = 2;
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlockLeaves create(BlockDef def) {
        CONSTRUCTING.set(def);
        PENDING.set(new PropertyVariant(ContentSetup.names(def)));
        try { return new ContentBlockLeaves(def, PENDING.get()); }
        finally {
            PENDING.remove();
            CONSTRUCTING.remove();
        }
    }

    protected ContentBlockLeaves(BlockDef def, PropertyVariant property) {
        this.def = def;
        this.variant = property;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
        setGraphicsLevel(!def.opaque);
        setDefaultState(this.blockState.getBaseState()
                .withProperty(property, def.at(0).name)
                .withProperty(DECAYABLE, Boolean.TRUE)
                .withProperty(CHECK_DECAY, Boolean.TRUE));
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, PENDING.get(), CHECK_DECAY, DECAYABLE); }

    @Override public BlockDef getDef() { return def; }

    public PropertyVariant getVariantProperty() { return variant; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override @Nonnull public BlockPlanks.EnumType getWoodType(int meta) { return BlockPlanks.EnumType.OAK; }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        for (BlockVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(variant, def.at(meta & VARIANT_MASK).name)
                .withProperty(DECAYABLE, (meta & DECAYABLE_BIT) == 0)
                .withProperty(CHECK_DECAY, (meta & CHECK_DECAY_BIT) > 0);
    }

    @Override public int getMetaFromState(IBlockState state) {
        int meta = ContentSetup.metaOf(def, state.getValue(variant));
        if (!state.getValue(DECAYABLE)) { meta |= DECAYABLE_BIT; }
        if (state.getValue(CHECK_DECAY)) { meta |= CHECK_DECAY_BIT; }
        return meta;
    }

    @Override public int damageDropped(@Nonnull IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override @Nonnull public List<ItemStack> onSheared(@Nonnull ItemStack item, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this, 1, damageDropped(world.getBlockState(pos))));
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        ItemStack sapling = ContentStacks.parse(def.registryName, def.leafSapling, 1);
        if (sapling.isEmpty()) { return; }
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        int percent = Math.min(100, def.leafSaplingChance + fortune * FORTUNE_BONUS);
        if (1 + rand.nextInt(100) <= percent) { drops.add(sapling); }
    }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return def.at(damageDropped(state)).light; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) { return def.at(damageDropped(state)).hardness; }

    @Override public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def.flammability; }

    @Override public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def.fireSpread; }

    @Override public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def.flammability > 0; }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return def().opaque; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return def().fullCube; }

    private BlockDef def() { return def == null ? CONSTRUCTING.get() : def; }

    @Override @SideOnly(Side.CLIENT) public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        if (def().opaque && world.getBlockState(pos.offset(side)).getBlock() == this) { return false; }
        return super.shouldSideBeRendered(state, world, pos, side);
    }
}
