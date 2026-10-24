package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.content.tile.TilePortway;
import mctmods.resourcedatapackloader.content.util.PortwayGlobal;
import mctmods.resourcedatapackloader.content.util.PortwayTeam;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockPortway extends ContentBlock {
    public static final int MAX_VARIANTS = 1;
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<BlockDoor.EnumDoorHalf> HALF = BlockDoor.HALF;

    public static ContentBlockPortway create(BlockDef def) { return BlockVariants.construct(def, ContentBlockPortway::new); }

    protected ContentBlockPortway(BlockDef def) {
        super(def);
        IBlockState state = getDefaultState();
        if (settings().facing) { state = state.withProperty(FACING, EnumFacing.NORTH); }
        if (settings().tall) { state = state.withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER); }
        setDefaultState(state);
    }

    private static PortwayDef shape(BlockDef def) { return def.portway == null ? PortwayDef.PLAIN : def.portway; }

    public PortwayDef settings() { return shape(getDef()); }

    public boolean upper(IBlockState state) { return settings().tall && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER; }

    @Override @Nonnull protected BlockStateContainer createBlockState() {
        PortwayDef shape = shape(BlockVariants.def());
        List<IProperty<?>> properties = new ArrayList<>();
        properties.add(BlockVariants.property());
        if (shape.facing) { properties.add(FACING); }
        if (shape.tall) { properties.add(HALF); }
        return new BlockStateContainer(this, properties.toArray(new IProperty<?>[0]));
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        if (settings().facing) { state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)); }
        if (settings().tall) { state = state.withProperty(HALF, (meta & 4) != 0 ? BlockDoor.EnumDoorHalf.UPPER : BlockDoor.EnumDoorHalf.LOWER); }
        return state;
    }

    @Override public int getMetaFromState(IBlockState state) {
        int meta = settings().facing ? state.getValue(FACING).getHorizontalIndex() : 0;
        return upper(state) ? meta | 4 : meta;
    }

    @Override public int damageDropped(@Nonnull IBlockState state) { return 0; }

    @Override @Nonnull public IBlockState withRotation(@Nonnull IBlockState state, @Nonnull Rotation rotation) { return settings().facing ? state.withProperty(FACING, rotation.rotate(state.getValue(FACING))) : state; }

    @Override @Nonnull public IBlockState withMirror(@Nonnull IBlockState state, @Nonnull Mirror mirror) { return settings().facing ? state.withRotation(mirror.toRotation(state.getValue(FACING))) : state; }

    @Override @Nonnull public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
        IBlockState state = getDefaultState();
        return settings().facing ? state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()) : state;
    }

    @Override public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && (!settings().tall || !world.isOutsideBuildHeight(pos.up()) && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up()));
    }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return !upper(state); }

    @Override @Nullable public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) { return new TilePortway(); }

    @Override public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (settings().tall) { world.setBlockState(pos.up(), state.withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER), 3); }
        TileEntity tile = world.getTileEntity(pos);
        if (world.isRemote || !(tile instanceof TilePortway)) { return; }
        if (stack.hasDisplayName()) { ((TilePortway) tile).rename(stack.getDisplayName()); }
        Portways.register(world, pos);
    }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block from, @Nonnull BlockPos fromPos) {
        if (!settings().tall || world.isRemote) { return; }
        IBlockState other = world.getBlockState(upper(state) ? pos.down() : pos.up());
        if (other.getBlock() == this && other.getValue(HALF) != state.getValue(HALF)) { return; }
        if (!upper(state)) {
            TileEntity tile = world.getTileEntity(pos);
            drop(world, pos, state, tile instanceof TilePortway ? ((TilePortway) tile).named() : "");
        }
        world.setBlockToAir(pos);
    }

    @Override public void onBlockHarvested(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player) {
        if (upper(state) && player.capabilities.isCreativeMode && world.getBlockState(pos.down()).getBlock() == this) { world.setBlockToAir(pos.down()); }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (!upper(state)) { super.getDrops(drops, world, pos, state, fortune); }
    }

    @Override public void harvestBlock(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity tile, @Nonnull ItemStack tool) {
        String name = tile instanceof TilePortway ? ((TilePortway) tile).named() : "";
        if (name.isEmpty() || upper(state) || !getDef().at(0).drops.isEmpty()) {
            super.harvestBlock(world, player, pos, state, tile, tool);
            return;
        }
        StatBase stat = StatList.getBlockStats(this);
        if (stat != null) { player.addStat(stat); }
        player.addExhaustion(0.005F);
        drop(world, pos, state, name);
    }

    private void drop(World world, BlockPos pos, IBlockState state, String name) {
        if (world.isRemote || !world.getGameRules().getBoolean("doTileDrops")) { return; }
        if (name.isEmpty() || !getDef().at(0).drops.isEmpty()) {
            dropBlockAsItem(world, pos, state, 0);
            return;
        }
        ItemStack named = new ItemStack(this);
        named.setStackDisplayName(name);
        spawnAsEntity(world, pos, named);
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) {
            PortwayGlobal.remove(world.getMinecraftServer(), world.provider.getDimension(), pos);
            PortwayTeam.remove(world.getMinecraftServer(), world.provider.getDimension(), pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) { return true; }
        BlockPos base = upper(state) ? pos.down() : pos;
        if (PortwayTravel.turnsBack(player, world, settings(), String.valueOf(getRegistryName()))) { return true; }
        Portways.touch(player, world, base, settings());
        player.openGui(ResourceDataPackLoader.INSTANCE, PackGuiHandler.PORTWAY, world, base.getX(), base.getY(), base.getZ());
        return true;
    }
}
