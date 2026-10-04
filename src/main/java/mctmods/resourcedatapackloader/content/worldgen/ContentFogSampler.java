package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nullable;

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

    @Nullable public static SkyLookDef look(@Nullable World world) { return world != null && world.provider instanceof ContentWorldProvider ? ((ContentWorldProvider) world.provider).look() : null; }

    public static Vec3d color(Vec3d fallback) { return current == null ? fallback : current; }

    public static Vec3d fluidFog(IBlockState state, Vec3d vanilla) {
        SkyLookDef look = look(Minecraft.getMinecraft().world);
        if (look == null) { return vanilla; }
        Block block = state.getBlock();
        if (look.waterFogColor != SkyLookDef.UNSET && (block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
            double breath = vanilla.x - WATER_RED;
            return rgb(look.waterFogColor).add(breath, breath, breath);
        }
        if (look.lavaFogColor != SkyLookDef.UNSET && (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)) { return rgb(look.lavaFogColor); }
        return vanilla;
    }

    private static Vec3d rgb(int color) { return new Vec3d((color >> 16 & 255) / 255.0D, (color >> 8 & 255) / 255.0D, (color & 255) / 255.0D); }

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        SkyLookDef look = look(mc.world);
        Entity camera = mc.getRenderViewEntity();
        if (look == null || !look.sampleFog || camera == null) {
            target = null;
            current = null;
            return;
        }
        if (target == null || mc.world.getTotalWorldTime() % INTERVAL == 0L) { target = sample(mc.world, camera, look.fogGroundWeight); }
        current = current == null ? target : current.add(target.subtract(current).scale(EASE));
    }

    private static Vec3d sample(World world, Entity camera, float weight) {
        int x = MathHelper.floor(camera.posX);
        int z = MathHelper.floor(camera.posZ);
        double red = 0.0D;
        double green = 0.0D;
        double blue = 0.0D;
        int count = 0;
        for (int dx = -REACH; dx <= REACH; dx += STEP) {
            for (int dz = -REACH; dz <= REACH; dz += STEP) {
                BlockPos top = world.getHeight(new BlockPos(x + dx, 0, z + dz)).down();
                MapColor map = world.getBlockState(top).getMapColor(world, top);
                if (map == MapColor.AIR) { continue; }
                red += (map.colorValue >> 16 & 255) / 255.0D;
                green += (map.colorValue >> 8 & 255) / 255.0D;
                blue += (map.colorValue & 255) / 255.0D;
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
        SkyLookDef look = look(Minecraft.getMinecraft().world);
        if (look == null || look.fogDensity <= 0.0F || event.getState().getMaterial().isLiquid()) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) { return; }
        float far = event.getFarPlaneDistance();
        float end = far + (NEAR - far) * look.fogDensity;
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(event.getFogMode() == -1 ? 0.0F : end * START);
        GlStateManager.setFogEnd(end);
    }
}
