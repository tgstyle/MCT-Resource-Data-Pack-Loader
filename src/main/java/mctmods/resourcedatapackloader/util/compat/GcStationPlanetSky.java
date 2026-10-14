package mctmods.resourcedatapackloader.util.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT) final class GcStationPlanetSky extends IRenderHandler {
    private final IRenderHandler inner;
    private final ResourceLocation planet;

    GcStationPlanetSky(IRenderHandler inner, ResourceLocation planet) {
        this.inner = inner;
        this.planet = planet;
    }

    @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
        inner.render(partialTicks, world, mc);
        float height = 400.0F + (float) mc.player.posY / 2.0F;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableFog();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -height / 10, 0.0F);
        float scale = Math.max(100 * (0.3F - height / 10000.0F), 0.2F);
        GlStateManager.scale(scale, 0.0F, scale);
        GlStateManager.translate(0.0F, -height, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        mc.renderEngine.bindTexture(planet);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 0.5F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-1.0D, 0, 1.0D).tex(0.0D, 1.0D).endVertex();
        buffer.pos(1.0D, 0, 1.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(1.0D, 0, -1.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(-1.0D, 0, -1.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
