package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.world.World;

public final class GenHeights {
    private GenHeights() {}

    public static boolean rubic(World world) { return world instanceof IRubicWorld && ((IRubicWorld) world).rdpl$isRubicWorld(); }

    public static int floor(World world, int above) { return rubic(world) ? ((IRubicWorld) world).rdpl$getMinHeight() + above : above; }

    public static int ceiling(World world, int fallback) { return rubic(world) ? ((IRubicWorld) world).rdpl$getMaxHeight() : fallback; }
}
