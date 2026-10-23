package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentSeasonTint {
    private static final int WHITE = 0xFFFFFF;
    private static final int GRASS = 0;
    private static final int FOLIAGE = 1;
    private static final int[] NONE = {WHITE, WHITE};
    private static final Map<Integer, Map<Biome, int[]>> CACHE = new ConcurrentHashMap<>();
    @Nullable private static volatile Boolean found;
    private static volatile boolean dirty;
    @Nullable private static List<Object> last;

    private ContentSeasonTint() {}

    static boolean wanted() {
        Boolean known = found;
        if (known == null) {
            known = compute();
            found = known;
        }
        return known;
    }

    private static boolean compute() {
        for (DimensionDef def : ContentDimensions.all().values()) {
            if (def.look != null && def.look.tintsPlants()) { return true; }
        }
        for (SkyLookDef look : ContentEnvironments.looks()) {
            if (look.tintsPlants()) { return true; }
        }
        return false;
    }

    public static void changed() { dirty = true; }

    public static int grass(Biome biome, int color) { return tinted(biome, GRASS, color); }

    public static int foliage(Biome biome, int color) { return tinted(biome, FOLIAGE, color); }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onGrass(BiomeEvent.GetGrassColor event) { event.setNewColor(grass(event.getBiome(), event.getNewColor())); }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onFoliage(BiomeEvent.GetFoliageColor event) { event.setNewColor(foliage(event.getBiome(), event.getNewColor())); }

    @SubscribeEvent public static void registerColors(ColorHandlerEvent.Block event) {
        if (wanted()) { event.getBlockColors().registerBlockColorHandler(ContentSeasonTint::leaf, Blocks.LEAVES); }
    }

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !dirty) { return; }
        dirty = false;
        World world = Minecraft.getMinecraft().world;
        if (world == null) { return; }
        CACHE.clear();
        if (!wanted() && !ContentSnowTint.wanted()) { return; }
        int dimension = world.provider.getDimension();
        List<Object> now = new ArrayList<>();
        now.add(dimension);
        now.addAll(ContentEnvironments.tints(dimension));
        List<Object> before = last == null ? Collections.singletonList(dimension) : last;
        last = now;
        if (!now.equals(before)) { Minecraft.getMinecraft().renderGlobal.loadRenderers(); }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        CACHE.clear();
        last = null;
        dirty = false;
    }

    private static int leaf(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
        BlockPlanks.EnumType type = state.getValue(BlockOldLeaf.VARIANT);
        if (type == BlockPlanks.EnumType.SPRUCE) { return fixed(ColorizerFoliage.getFoliageColorPine(), world, pos); }
        if (type == BlockPlanks.EnumType.BIRCH) { return fixed(ColorizerFoliage.getFoliageColorBirch(), world, pos); }
        return world != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(world, pos) : ColorizerFoliage.getFoliageColorBasic();
    }

    private static int fixed(int constant, @Nullable IBlockAccess world, @Nullable BlockPos pos) {
        if (world == null || pos == null) { return constant; }
        return multiply(constant, ContentSnowTint.average(world, pos, biome -> tints(biome)[FOLIAGE]));
    }

    private static int tinted(Biome biome, int which, int color) {
        if (!wanted()) { return color; }
        int tint = tints(biome)[which];
        return tint == WHITE ? color : multiply(color, tint);
    }

    private static int multiply(int color, int tint) {
        int red = (color >> 16 & 255) * (tint >> 16 & 255) / 255;
        int green = (color >> 8 & 255) * (tint >> 8 & 255) / 255;
        int blue = (color & 255) * (tint & 255) / 255;
        return red << 16 | green << 8 | blue;
    }

    private static int[] tints(Biome biome) {
        World world = Minecraft.getMinecraft().world;
        if (world == null) { return NONE; }
        int dimension = world.provider.getDimension();
        return CACHE.computeIfAbsent(dimension, ignored -> new ConcurrentHashMap<>()).computeIfAbsent(biome, key -> look(dimension, key));
    }

    private static int[] look(int dimension, Biome biome) {
        DimensionDef def = ContentDimensions.byId(dimension, biome);
        SkyLookDef look = def == null ? null : def.look;
        if (look == null) { return NONE; }
        return new int[]{look.grassTint == SkyLookDef.UNSET ? WHITE : look.grassTint, look.foliageTint == SkyLookDef.UNSET ? WHITE : look.foliageTint};
    }
}
