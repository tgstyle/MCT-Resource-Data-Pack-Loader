package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.client.util.ContentDimensionRulesClient;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserSkyLook;
import mctmods.resourcedatapackloader.core.util.math.Colors;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentFogSampler {
    private static final int REACH = 16;
    private static final int STEP = 4;
    private static final int INTERVAL = 20;
    private static final double EASE = 0.1D;
    private static final float NEAR = 8.0F;
    private static final float START = 0.75F;
    private static final double WATER_RED = 0.02F;
    @Nullable private static Vec3d target;
    @Nullable private static Vec3d current;

    private ContentFogSampler() {}

    @Nullable public static SkyLookDef look(@Nullable World world) {
        DimensionDef def = ContentDimensions.of(world);
        return def == null ? null : def.look;
    }

    public static Vec3d color(Vec3d fallback) { return current == null ? fallback : current; }

    public static Vec3d fluidFog(IBlockState state, Vec3d vanilla) {
        SkyLookDef look = look(Minecraft.getMinecraft().world);
        if (look == null) { return vanilla; }
        Block block = state.getBlock();
        if (look.waterFogColor != SkyLookDef.UNSET && (block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
            double breath = vanilla.x - WATER_RED;
            return Colors.vec(look.waterFogColor).add(breath, breath, breath);
        }
        if (look.lavaFogColor != SkyLookDef.UNSET && (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)) { return Colors.vec(look.lavaFogColor); }
        return vanilla;
    }

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        ContentWindowLook.tick(mc.world);
        DimensionDef def = ContentDimensions.of(mc.world);
        SkyLookDef look = def == null ? null : def.look;
        Entity camera = mc.getRenderViewEntity();
        if (def == null || camera == null || !(look != null && look.sampleFog || ContentWindowLook.samples(def))) {
            target = null;
            current = null;
            return;
        }
        if (target == null || mc.world.getTotalWorldTime() % INTERVAL == 0L) { target = sample(mc.world, camera, look == null ? ContentParserSkyLook.GROUND_WEIGHT : look.fogGroundWeight); }
        current = current == null ? target : current.add(target.subtract(current).scale(EASE));
    }

    private static Vec3d sample(World world, Entity camera, float weight) {
        BlockPos feet = new BlockPos(camera);
        int x = feet.getX();
        int z = feet.getZ();
        double red = 0.0D;
        double green = 0.0D;
        double blue = 0.0D;
        int count = 0;
        for (int dx = -REACH; dx <= REACH; dx += STEP) {
            for (int dz = -REACH; dz <= REACH; dz += STEP) {
                BlockPos top = world.getHeight(new BlockPos(x + dx, 0, z + dz)).down();
                MapColor map = world.getBlockState(top).getMapColor(world, top);
                if (map == MapColor.AIR) { continue; }
                red += Colors.redD(map.colorValue);
                green += Colors.greenD(map.colorValue);
                blue += Colors.blueD(map.colorValue);
                count++;
            }
        }
        Vec3d sky = world.getSkyColor(camera, 1.0F);
        if (count == 0) { return sky; }
        double light = world.getSunBrightness(1.0F);
        Vec3d ground = new Vec3d(red / count * light, green / count * light, blue / count * light);
        return sky.scale(1.0D - weight).add(ground.scale(weight));
    }

    @SubscribeEvent public static void onFog(EntityViewRenderEvent.RenderFogEvent event) {
        World world = Minecraft.getMinecraft().world;
        SkyLookDef look = look(world);
        float density = ContentWindowLook.fogDensity(world, look == null ? 0.0F : look.fogDensity);
        if (density <= 0.0F || event.getState().getMaterial().isLiquid()) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) { return; }
        float far = event.getFarPlaneDistance();
        float end = MathUtil.lerp(density, far, NEAR);
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(event.getFogMode() == -1 ? 0.0F : end * START);
        GlStateManager.setFogEnd(end);
    }

    @SubscribeEvent public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        Vec3d tinted = ContentDimensionRulesClient.vision(Minecraft.getMinecraft().world, (float) event.getRenderPartialTicks(), new Vec3d(event.getRed(), event.getGreen(), event.getBlue()));
        event.setRed((float) tinted.x);
        event.setGreen((float) tinted.y);
        event.setBlue((float) tinted.z);
    }
}
