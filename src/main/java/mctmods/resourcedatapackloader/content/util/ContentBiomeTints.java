package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;import java.util.function.BiFunction;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentBiomeTints {
    private static final int WHITE = 0xFFFFFF;
    private static final String FOLIAGE = "foliage";
    private static final String GRASS = "grass";
    private static final String WATER = "water";

    private ContentBiomeTints() {}

    public static boolean biomeDriven(String tint) {
        String value = mode(tint);
        return FOLIAGE.equals(value) || "biome".equals(value) || GRASS.equals(value) || WATER.equals(value);
    }

    public static String mode(String tint) { return Settings.lower(tint); }

    public static int fixed(String tint, Object context) {
        String value = mode(tint);
        if (biomeDriven(value)) { return ContentTypes.NO_COLOR; }
        if ("none".equals(value)) { return WHITE; }
        return ContentTypes.color(tint, context.toString());
    }

    public static int biome(String tint, @Nullable IBlockAccess world, @Nullable BlockPos pos) {
        String value = mode(tint);
        if (GRASS.equals(value)) { return grass(world, pos); }
        if (WATER.equals(value)) { return water(world, pos); }
        return foliage(world, pos);
    }

    public static int grass(@Nullable IBlockAccess world, @Nullable BlockPos pos) { return tint(world, pos, GRASS, BiomeColorHelper::getGrassColorAtPos); }

    public static int water(@Nullable IBlockAccess world, @Nullable BlockPos pos) { return tint(world, pos, WATER, BiomeColorHelper::getWaterColorAtPos); }

    public static int foliage(@Nullable IBlockAccess world, @Nullable BlockPos pos) { return tint(world, pos, FOLIAGE, BiomeColorHelper::getFoliageColorAtPos); }

    private static int tint(@Nullable IBlockAccess world, @Nullable BlockPos pos, String what, BiFunction<IBlockAccess, BlockPos, Integer> lookup) {
        if (world == null || pos == null) { return WHITE; }
        try { return lookup.apply(world, pos); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.debug("Could not read the {} color at {}", what, pos, ex);
            return WHITE;
        }
    }
}
