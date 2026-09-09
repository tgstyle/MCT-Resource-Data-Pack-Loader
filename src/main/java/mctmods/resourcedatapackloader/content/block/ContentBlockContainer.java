package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockContainer extends ContentBlock {
    public static final int MAX_VARIANTS = BlockDef.MAX_META;
    private static final ContainerDef PLAIN = new ContainerDef(3, 9, "", false, null, null, 0, 0, "");
    private final ContainerDef container;

    public static ContentBlockContainer create(BlockDef def) {
        beginConstruction(def);
        try { return new ContentBlockContainer(def); }
        finally { endConstruction(); }
    }

    protected ContentBlockContainer(BlockDef def) {
        super(def);
        this.container = def.container == null ? PLAIN : def.container;
    }

    public ContainerDef container() { return held(); }

    private ContainerDef held() {
        if (container != null) { return container; }
        BlockDef during = BlockVariants.def();
        return during == null || during.container == null ? PLAIN : during.container;
    }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return true; }

    @Override @Nullable public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        TileEntityPackContainer tile = new TileEntityPackContainer();
        tile.shape(held(), getTranslationKey() + "." + getDef().at(getMetaFromState(state)).name + ".name");
        if (!world.isRemote && !held().lootTable.isEmpty()) { tile.loot(new ResourceLocation(held().lootTable), world.rand.nextLong()); }
        return tile;
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) { return true; }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityPackContainer)) { return false; }
        player.openGui(ResourceDataPackLoader.INSTANCE, PackGuiHandler.CONTAINER, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull net.minecraft.entity.EntityLivingBase placer, @Nonnull net.minecraft.item.ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityPackContainer)) { return; }
        ((TileEntityPackContainer) tile).unloot();
        ((TileEntityPackContainer) tile).face(EnumFacing.byHorizontalIndex(
                net.minecraft.util.math.MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3).getOpposite());
        tile.markDirty();
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPackContainer) { net.minecraft.inventory.InventoryHelper.dropInventoryItems(world, pos, (TileEntityPackContainer) tile); }
        super.breakBlock(world, pos, state);
    }

    @Override @Nonnull public net.minecraft.util.EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return held().chestModel ? net.minecraft.util.EnumBlockRenderType.ENTITYBLOCK_ANIMATED : super.getRenderType(state);
    }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return !held().chestModel && super.isOpaqueCube(state); }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return !held().chestModel && super.isFullCube(state); }

    @Override public int getLightOpacity(@Nonnull IBlockState state) { return held().chestModel ? 0 : super.getLightOpacity(state); }

    @Override public boolean getUseNeighborBrightness(@Nonnull IBlockState state) { return held().chestModel || super.getUseNeighborBrightness(state); }

    @Override public boolean hasComparatorInputOverride(@Nonnull IBlockState state) { return true; }

    @Override public int getComparatorInputOverride(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityPackContainer ? Container.calcRedstoneFromInventory((TileEntityPackContainer) tile) : 0;
    }

    @Override public boolean eventReceived(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, int id, int value) {
        super.eventReceived(state, world, pos, id, value);
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, value);
    }
}
