package mctmods.resourcedatapackloader.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;

public final class DoorPairs {
    private static final int FLAG_QUIET = 10;
    private static final int WOOD_OPEN = 1006;
    private static final int WOOD_CLOSE = 1012;

    private DoorPairs() {}

    public static boolean isDoor(BlockState state) {
        return state.getBlock() instanceof DoorBlock || state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.hasProperty(BlockStateProperties.DOOR_HINGE) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && state.hasProperty(BlockStateProperties.OPEN);
    }

    public static boolean isLower(BlockState state) { return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER; }

    public static boolean isOpen(BlockState state) { return state.getValue(BlockStateProperties.OPEN); }

    public static boolean isPowered(BlockState state) { return state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED); }

    private static boolean handClass(BlockState state) { return !(state.getBlock() instanceof DoorBlock door) || door.type().canOpenByHand(); }

    public static BlockPos partnerPos(BlockState lower, BlockPos pos) {
        Direction facing = lower.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return pos.relative(lower.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
    }

    public static boolean mirrors(BlockState door, BlockState partner) {
        return door.getValue(BlockStateProperties.DOOR_HINGE) != partner.getValue(BlockStateProperties.DOOR_HINGE) && door.getValue(BlockStateProperties.HORIZONTAL_FACING) == partner.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static boolean sameClass(BlockState door, BlockState partner) { return handClass(door) == handClass(partner); }

    public static void move(ServerLevel level, BlockPos lower, BlockState state, boolean open) {
        if (state.getBlock() instanceof DoorBlock door) {
            door.setOpen(null, level, state, lower, open);
            return;
        }
        level.setBlock(lower, state.setValue(BlockStateProperties.OPEN, open), FLAG_QUIET);
        BlockPos upper = lower.above();
        BlockState top = level.getBlockState(upper);
        if (isDoor(top)) { level.setBlock(upper, top.setValue(BlockStateProperties.OPEN, open), FLAG_QUIET); }
        level.levelEvent(null, open ? WOOD_OPEN : WOOD_CLOSE, lower, 0);
        level.gameEvent(null, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, lower);
    }
}
