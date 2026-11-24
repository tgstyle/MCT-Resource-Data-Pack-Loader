package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;

import net.minecraft.world.World;

public final class GenHeights {
    public static final int WORLD_TOP = 255;

    private GenHeights() {}

    public static boolean rubic(World world) { return world instanceof IRubicWorld && ((IRubicWorld) world).rdpl$isRubicWorld(); }

    public static int rubicOr(World world, int rubicValue, int orig) { return rubic(world) ? rubicValue : orig; }

    public static boolean within(int y, int min, int max) { return y >= min && y <= max; }

    public static int floor(World world, int above) { return rubic(world) ? ((IRubicWorld) world).rdpl$getMinHeight() + above : above; }

    public static int ceiling(World world, int fallback) { return rubic(world) ? ((IRubicWorld) world).rdpl$getMaxHeight() : fallback; }

    public static int ceilingOffset(World world, int orig) { return rubic(world) ? ((IRubicWorld) world).rdpl$getMaxHeight() + orig - CompatHandler.VANILLA_HEIGHT : orig; }

    public static int topFilledCubeY(IColumn column) {
        int top = Integer.MIN_VALUE;
        for (ICube cube : column.getLoadedCubes()) { if (!cube.isEmpty() && cube.getY() > top) { top = cube.getY(); } }
        return top;
    }
}
