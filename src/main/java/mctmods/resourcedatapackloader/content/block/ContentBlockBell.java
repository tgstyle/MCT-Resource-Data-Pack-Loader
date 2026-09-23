package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BellDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackBell;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockBell extends Block implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    public static final int RING_EVENT = 1;
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<Attachment> ATTACHMENT = PropertyEnum.create("attachment", Attachment.class);
    private static final float HIGHEST_HIT = 0.8124F;
    private static final AxisAlignedBB NORTH_SOUTH_FLOOR = box(0, 0, 4, 16, 16, 12);
    private static final AxisAlignedBB EAST_WEST_FLOOR = box(4, 0, 0, 12, 16, 16);
    private static final AxisAlignedBB BELL_TOP = box(5, 6, 5, 11, 13, 11);
    private static final AxisAlignedBB BELL_BOTTOM = box(4, 4, 4, 12, 6, 12);
    private static final List<AxisAlignedBB> NORTH_SOUTH_BETWEEN = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(7, 13, 0, 9, 15, 16));
    private static final List<AxisAlignedBB> EAST_WEST_BETWEEN = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(0, 13, 7, 16, 15, 9));
    private static final List<AxisAlignedBB> TO_WEST = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(0, 13, 7, 13, 15, 9));
    private static final List<AxisAlignedBB> TO_EAST = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(3, 13, 7, 16, 15, 9));
    private static final List<AxisAlignedBB> TO_NORTH = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(7, 13, 0, 9, 15, 13));
    private static final List<AxisAlignedBB> TO_SOUTH = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(7, 13, 3, 9, 15, 16));
    private static final List<AxisAlignedBB> CEILING = Arrays.asList(BELL_BOTTOM, BELL_TOP, box(7, 13, 7, 9, 16, 9));
    private static boolean armed;
    private final BlockDef def;
    private final BellDef bell;

    public enum Attachment implements IStringSerializable {
        FLOOR, CEILING, SINGLE_WALL, DOUBLE_WALL;

        @Override @Nonnull public String getName() { return name().toLowerCase(Locale.ROOT); }
    }

    public ContentBlockBell(BlockDef def) {
        super(def.material, def.mapColor);
        this.def = def;
        this.bell = def.bell == null ? BellDef.PLAIN : def.bell;
        BlockVariant variant = def.at(0);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + variant.name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ATTACHMENT, Attachment.FLOOR));
        if (!armed) {
            MinecraftForge.EVENT_BUS.register(ContentBlockBell.class);
            armed = true;
        }
    }

    private static AxisAlignedBB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    public boolean swings() { return bell.swing; }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FACING, ATTACHMENT); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(ATTACHMENT, Attachment.values()[meta >> 2 & 3]);
    }

    @Override public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex() | state.getValue(ATTACHMENT).ordinal() << 2; }

    @Override @Nonnull public IBlockState withRotation(@Nonnull IBlockState state, Rotation rotation) { return state.withProperty(FACING, rotation.rotate(state.getValue(FACING))); }

    @Override @Nonnull public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirror) { return state.withRotation(mirror.toRotation(state.getValue(FACING))); }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return true; }

    @Override @Nullable public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) { return new TileEntityPackBell(); }

    @Override public boolean eventReceived(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, int id, int value) {
        super.eventReceived(state, world, pos, id, value);
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, value);
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return hit(world, pos, state, facing, hitY);
    }

    @SubscribeEvent public static void onImpact(ProjectileImpactEvent event) {
        RayTraceResult ray = event.getRayTraceResult();
        World world = event.getEntity().world;
        if (world.isRemote || ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) { return; }
        BlockPos pos = ray.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof ContentBlockBell)) { return; }
        ((ContentBlockBell) state.getBlock()).hit(world, pos, state, ray.sideHit, (float) (ray.hitVec.y - pos.getY()));
    }

    private boolean hit(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitY) {
        if (!properHit(state, side, hitY)) { return false; }
        ring(world, pos, side);
        return true;
    }

    private static boolean properHit(IBlockState state, EnumFacing side, float hitY) {
        if (side.getAxis() == EnumFacing.Axis.Y || hitY > HIGHEST_HIT) { return false; }
        EnumFacing.Axis along = state.getValue(FACING).getAxis();
        switch (state.getValue(ATTACHMENT)) {
            case FLOOR: return along == side.getAxis();
            case SINGLE_WALL:
            case DOUBLE_WALL: return along != side.getAxis();
            default: return true;
        }
    }

    public void ring(World world, BlockPos pos, @Nullable EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (world.isRemote || !(tile instanceof TileEntityPackBell)) { return; }
        ((TileEntityPackBell) tile).hit(side == null ? world.getBlockState(pos).getValue(FACING) : side);
        play(world, pos, bell.sound, 2.0F);
    }

    public void resonate(World world, BlockPos pos) { play(world, pos, bell.resonateSound, 1.0F); }

    private static void play(World world, BlockPos pos, String name, float volume) {
        if (name.isEmpty()) { return; }
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(name));
        if (sound != null) { world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, 1.0F); }
    }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block from, @Nonnull BlockPos fromPos) {
        if (world.isRemote) { return; }
        IBlockState shaped = reshaped(world, pos, state, fromPos);
        if (shaped == null) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return;
        }
        if (shaped != state) { world.setBlockState(pos, shaped, 3); }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityPackBell)) { return; }
        TileEntityPackBell held = (TileEntityPackBell) tile;
        boolean powered = world.isBlockPowered(pos);
        if (powered == held.powered()) { return; }
        if (powered) { ring(world, pos, null); }
        held.power(powered);
    }

    @Nullable private static IBlockState reshaped(World world, BlockPos pos, IBlockState state, BlockPos fromPos) {
        EnumFacing toward = toward(pos, fromPos);
        if (toward == null) { return state; }
        Attachment attachment = state.getValue(ATTACHMENT);
        EnumFacing facing = state.getValue(FACING);
        if (toward == held(state) && !stays(world, pos, state) && attachment != Attachment.DOUBLE_WALL) { return null; }
        if (toward.getAxis() != facing.getAxis()) { return state; }
        BlockPos near = pos.offset(toward);
        if (attachment == Attachment.DOUBLE_WALL && !sturdy(world, near, toward.getOpposite())) { return state.withProperty(ATTACHMENT, Attachment.SINGLE_WALL).withProperty(FACING, toward.getOpposite()); }
        if (attachment == Attachment.SINGLE_WALL && toward == facing.getOpposite() && sturdy(world, near, facing)) { return state.withProperty(ATTACHMENT, Attachment.DOUBLE_WALL); }
        return state;
    }

    @Nullable private static EnumFacing toward(BlockPos pos, BlockPos other) {
        for (EnumFacing side : EnumFacing.values()) {
            if (pos.offset(side).equals(other)) { return side; }
        }
        return null;
    }

    private static EnumFacing held(IBlockState state) {
        switch (state.getValue(ATTACHMENT)) {
            case FLOOR: return EnumFacing.DOWN;
            case CEILING: return EnumFacing.UP;
            default: return state.getValue(FACING);
        }
    }

    private static boolean stays(IBlockAccess world, BlockPos pos, IBlockState state) {
        EnumFacing side = held(state);
        if (side != EnumFacing.UP) { return sturdy(world, pos.offset(side), side.getOpposite()); }
        BlockPos above = pos.up();
        BlockFaceShape shape = world.getBlockState(above).getBlockFaceShape(world, above, EnumFacing.DOWN);
        return shape == BlockFaceShape.SOLID || shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG || shape == BlockFaceShape.CENTER_SMALL;
    }

    private static boolean sturdy(IBlockAccess world, BlockPos pos, EnumFacing face) { return world.getBlockState(pos).getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID; }

    @Nullable private IBlockState placement(World world, BlockPos pos, EnumFacing side, EnumFacing looking) {
        if (side.getAxis() == EnumFacing.Axis.Y) {
            IBlockState upright = getDefaultState().withProperty(ATTACHMENT, side == EnumFacing.DOWN ? Attachment.CEILING : Attachment.FLOOR).withProperty(FACING, looking);
            return stays(world, pos, upright) ? upright : null;
        }
        boolean between = side.getAxis() == EnumFacing.Axis.X && sturdy(world, pos.west(), EnumFacing.EAST) && sturdy(world, pos.east(), EnumFacing.WEST)
                || side.getAxis() == EnumFacing.Axis.Z && sturdy(world, pos.north(), EnumFacing.SOUTH) && sturdy(world, pos.south(), EnumFacing.NORTH);
        IBlockState hung = getDefaultState().withProperty(FACING, side.getOpposite()).withProperty(ATTACHMENT, between ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
        if (stays(world, pos, hung)) { return hung; }
        hung = hung.withProperty(ATTACHMENT, sturdy(world, pos.down(), EnumFacing.UP) ? Attachment.FLOOR : Attachment.CEILING);
        return stays(world, pos, hung) ? hung : null;
    }

    @Override public boolean canPlaceBlockOnSide(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return super.canPlaceBlockOnSide(world, pos, side) && placement(world, pos, side, EnumFacing.NORTH) != null;
    }

    @Override @Nonnull public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
        IBlockState placed = placement(world, pos, facing, placer.getHorizontalFacing());
        return placed == null ? getDefaultState() : placed;
    }

    private static List<AxisAlignedBB> parts(IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        boolean northSouth = facing.getAxis() == EnumFacing.Axis.Z;
        switch (state.getValue(ATTACHMENT)) {
            case FLOOR: return Collections.singletonList(northSouth ? NORTH_SOUTH_FLOOR : EAST_WEST_FLOOR);
            case CEILING: return CEILING;
            case DOUBLE_WALL: return northSouth ? NORTH_SOUTH_BETWEEN : EAST_WEST_BETWEEN;
            default:
                switch (facing) {
                    case NORTH: return TO_NORTH;
                    case SOUTH: return TO_SOUTH;
                    case EAST: return TO_EAST;
                    default: return TO_WEST;
                }
        }
    }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        AxisAlignedBB whole = null;
        for (AxisAlignedBB part : parts(state)) { whole = whole == null ? part : whole.union(part); }
        return whole == null ? FULL_BLOCK_AABB : whole;
    }

    @Override public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> boxes, @Nullable Entity entity, boolean actualState) {
        for (AxisAlignedBB part : parts(state)) { addCollisionBoxToList(pos, entityBox, boxes, part); }
    }

    @Override @Nullable public RayTraceResult collisionRayTrace(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        RayTraceResult nearest = null;
        double best = Double.MAX_VALUE;
        for (AxisAlignedBB part : parts(state)) {
            RayTraceResult found = rayTrace(pos, start, end, part);
            if (found == null) { continue; }
            double distance = found.hitVec.squareDistanceTo(start);
            if (distance < best) {
                best = distance;
                nearest = found;
            }
        }
        return nearest;
    }

    @Override @Nonnull public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return BlockFaceShape.UNDEFINED; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return false; }

    @Override public int getLightOpacity(@Nonnull IBlockState state) { return 0; }

    @Override public boolean getUseNeighborBrightness(@Nonnull IBlockState state) { return true; }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
