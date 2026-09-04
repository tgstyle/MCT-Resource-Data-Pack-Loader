package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.RenderGlobal;
import javax.annotation.Nullable;

@Mixin(RenderGlobal.class) public abstract class MixinRenderGlobalSeamSky {
    @Unique private static final double RDPL_NEAR = 32.0D;
    @Shadow private WorldClient world;
    @Shadow @Final private Minecraft mc;

    @Inject(method = "renderSky(FI)V", at = @At("RETURN")) private void rdpl$seamSky(float partialTicks, int pass, CallbackInfo ci) {
        if (world == null) { return; }
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) { return; }
        int dimension = world.provider.getDimension();
        Integer under = ContentSeams.below(dimension);
        Integer over = ContentSeams.above(dimension);
        if (under == null && over == null) { return; }
        int floor = GenHeights.floor(world, 0);
        double eyeY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks + viewer.getEyeHeight();
        if (under != null) { rdpl$plane(under, floor - 1 - eyeY, partialTicks); }
        if (over != null) { rdpl$plane(over, RubicWorldControl.generatedCeiling(world) + 1 - eyeY, partialTicks); }
    }

    @Unique private boolean rdpl$near(double height) { return Math.abs(height) <= RDPL_NEAR; }

    @Unique private void rdpl$plane(int dimension, double height, float partialTicks) {
        if (!rdpl$near(height)) { return; }
        Vec3d tint = rdpl$skyOf(dimension, partialTicks);
        double extent = Math.max(64, mc.gameSettings.renderDistanceChunks * 16);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableFog();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.color((float) tint.x, (float) tint.y, (float) tint.z, 1.0F);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(-extent, height, -extent).endVertex();
        buffer.pos(-extent, height, extent).endVertex();
        buffer.pos(extent, height, extent).endVertex();
        buffer.pos(extent, height, -extent).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Unique @Nullable private static WorldServer rdpl$loaded(int dimension) { return DimensionManager.getWorld(dimension); }

    @Unique private Vec3d rdpl$skyOf(int dimension, float partialTicks) {
        WorldServer target = rdpl$loaded(dimension);
        if (target != null) { return target.provider.getFogColor(target.getCelestialAngle(partialTicks), partialTicks); }
        DimensionType type = rdpl$typeOf(dimension);
        if (type == DimensionType.NETHER) { return new Vec3d(0.2D, 0.03D, 0.03D); }
        if (type == DimensionType.THE_END) { return new Vec3d(0.06D, 0.06D, 0.09D); }
        return new Vec3d(0.5D, 0.66D, 1.0D);
    }

    @Unique @Nullable private static DimensionType rdpl$typeOf(int dimension) {
        try { return DimensionManager.getProviderType(dimension); }
        catch (IllegalArgumentException unknown) { return null; }
    }
}
