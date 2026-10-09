package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.util.BodyRings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT) public final class RingQuads {
    private RingQuads() {}

    public static float aspect(BodyRings.Ring ring) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ring.texture);
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        return width <= 0 || height <= 0 ? 1.0F : (float) width / height;
    }

    public static void sky(BodyRings.Ring ring, float bodySize, boolean front) {
        float across = bodySize * ring.size;
        float down = across / aspect(ring);
        float near = front ? 0.0F : -down;
        float far = front ? down : 0.0F;
        float top = front ? 0.5F : 0.0F;
        float bottom = front ? 1.0F : 0.5F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-across, 100.0D, near).tex(0.0D, top).endVertex();
        buffer.pos(across, 100.0D, near).tex(1.0D, top).endVertex();
        buffer.pos(across, 100.0D, far).tex(1.0D, bottom).endVertex();
        buffer.pos(-across, 100.0D, far).tex(0.0D, bottom).endVertex();
        tessellator.draw();
    }
}
