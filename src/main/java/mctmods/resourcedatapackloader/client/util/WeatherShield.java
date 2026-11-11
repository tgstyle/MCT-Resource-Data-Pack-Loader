package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndex;
import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndexes;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public final class WeatherShield {
    private static final int SPLASH_REACH = 10;
    private static final ShieldIndex.Near NEAR = new ShieldIndex.Near();
    private static int shutHeight;

    private WeatherShield() {}

    public static void fill(LevelReader level, Vec3 camera) {
        int radius = Math.max(Minecraft.getInstance().options.weatherRadius().get(), SPLASH_REACH);
        shutHeight = Mth.floor(camera.y) + radius + 1;
        ShieldIndexes.near(level, Mth.floor(camera.x), Mth.floor(camera.z), radius, NEAR);
    }

    public static boolean shut(int x, int z) { return NEAR.shut(x, z); }

    public static int shutHeight() { return shutHeight; }
}
