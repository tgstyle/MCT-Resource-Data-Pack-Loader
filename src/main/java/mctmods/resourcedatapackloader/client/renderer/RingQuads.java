package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.client.util.Draw;
import mctmods.resourcedatapackloader.common.util.dimension.BodyRings;

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
        float[] half = ring.front.half(front);
        float left = -across + half[0] * 2.0F * across;
        float right = -across + half[2] * 2.0F * across;
        float near = -down + half[1] * 2.0F * down;
        float far = -down + half[3] * 2.0F * down;
        BufferBuilder buffer = Draw.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(left, 100.0D, near).tex(half[0], half[1]).endVertex();
        buffer.pos(right, 100.0D, near).tex(half[2], half[1]).endVertex();
        buffer.pos(right, 100.0D, far).tex(half[2], half[3]).endVertex();
        buffer.pos(left, 100.0D, far).tex(half[0], half[3]).endVertex();
        Tessellator.getInstance().draw();
    }
}
