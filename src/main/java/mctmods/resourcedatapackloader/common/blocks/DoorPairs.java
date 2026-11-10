package mctmods.resourcedatapackloader.common.blocks;

import com.google.common.base.Optional;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DoorPairs {
    private static final String HALF = "half";
    private static final String HINGE = "hinge";
    private static final String FACING = "facing";
    private static final String OPEN = "open";
    private static final String POWERED = "powered";
    private static final String TRUE = "true";
    private static final int FLAG_QUIET = 10;
    private static final Map<Block, Boolean> DOORS = new ConcurrentHashMap<>();

    private DoorPairs() {}

    @Nullable private static IProperty<?> property(IBlockState state, String name) { return state.getBlock().getBlockState().getProperty(name); }

    private static <T extends Comparable<T>> String valueName(IBlockState state, IProperty<T> property) { return property.getName(state.getValue(property)); }

    @Nullable private static String value(IBlockState state, String name) {
        IProperty<?> property = property(state, name);
        return property == null ? null : valueName(state, property);
    }

    private static <T extends Comparable<T>> Set<String> names(IProperty<T> property) {
        Set<String> names = new HashSet<>();
        for (T allowed : property.getAllowedValues()) { names.add(property.getName(allowed)); }
        return names;
    }

    private static boolean offers(@Nullable IProperty<?> property, String first, String second) {
        if (property == null) { return false; }
        Set<String> names = names(property);
        return names.contains(first) && names.contains(second);
    }

    private static boolean horizontal(@Nullable IProperty<?> property) {
        if (property == null || property.getValueClass() != EnumFacing.class) { return false; }
        for (Object allowed : property.getAllowedValues()) {
            if (!((EnumFacing) allowed).getAxis().isHorizontal()) { return false; }
        }
        return true;
    }

    private static boolean probe(IBlockState state) {
        IProperty<?> open = property(state, OPEN);
        return open != null && open.getValueClass() == Boolean.class && horizontal(property(state, FACING)) && offers(property(state, HALF), "lower", "upper") && offers(property(state, HINGE), "left", "right");
    }

    public static boolean isDoor(IBlockState state) { return DOORS.computeIfAbsent(state.getBlock(), block -> probe(block.getDefaultState())); }

    public static boolean isLower(IBlockState state) { return "lower".equals(value(state, HALF)); }

    public static boolean isOpen(IBlockState state) { return TRUE.equals(value(state, OPEN)); }

    public static boolean isPowered(IBlockState state) { return TRUE.equals(value(state, POWERED)); }

    private static boolean ironClass(IBlockState state) { return state.getMaterial() == Material.IRON; }

    public static IBlockState lowerState(World world, BlockPos lower) {
        IBlockState state = world.getBlockState(lower);
        return state.getActualState(world, lower);
    }

    @Nullable public static BlockPos partnerPos(IBlockState lower, BlockPos pos) {
        EnumFacing facing = EnumFacing.byName(String.valueOf(value(lower, FACING)));
        if (facing == null) { return null; }
        return pos.offset("left".equals(value(lower, HINGE)) ? facing.rotateY() : facing.rotateYCCW());
    }

    public static boolean mirrors(IBlockState door, IBlockState partner) {
        return !String.valueOf(value(door, HINGE)).equals(value(partner, HINGE)) && String.valueOf(value(door, FACING)).equals(value(partner, FACING));
    }

    public static boolean sameClass(IBlockState door, IBlockState partner) { return ironClass(door) == ironClass(partner); }

    public static void move(World world, BlockPos lower, IBlockState state, boolean open) {
        Block block = state.getBlock();
        if (block instanceof BlockDoor) {
            ((BlockDoor) block).toggleDoor(world, lower, open);
            return;
        }
        IProperty<?> property = property(state, OPEN);
        if (property == null) { return; }
        world.setBlockState(lower, with(world.getBlockState(lower), property, open), FLAG_QUIET);
        world.markBlockRangeForRenderUpdate(lower, lower);
        boolean iron = ironClass(state);
        world.playEvent(null, open ? iron ? 1005 : 1006 : iron ? 1011 : 1012, lower, 0);
    }

    private static <T extends Comparable<T>> IBlockState with(IBlockState state, IProperty<T> property, boolean open) {
        Optional<T> parsed = property.parseValue(String.valueOf(open));
        return parsed.isPresent() ? state.withProperty(property, parsed.get()) : state;
    }
}
