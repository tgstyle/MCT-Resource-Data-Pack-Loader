package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.client.util.CameraPos;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public final class CubeChunkBorder {
    private static final int SIZE = 16;
    private static final float YELLOW_RED = 1.0F;
    private static final float YELLOW_GREEN = 1.0F;
    private static final float YELLOW_BLUE = 0.0F;
    private static final float BLUE_RED = 0.25F;
    private static final float BLUE_GREEN = 0.25F;
    private static final float BLUE_BLUE = 1.0F;

    private CubeChunkBorder() {}

    public static void render(EntityPlayer player, Tessellator tessellator, float partialTicks) {
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        Vec3d at = CameraPos.viewAt(player, partialTicks);
        double playerX = at.x;
        double playerY = at.y;
        double playerZ = at.z;
        double yOffset = (Math.round(playerY / 16)) * 16 - 128;
        double minY = (0.0D - playerY) + yOffset;
        double maxY = (256.0D - playerY) + yOffset;
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        double chunkX = (double) (player.chunkCoordX << 4) - playerX;
        double chunkZ = (double) (player.chunkCoordZ << 4) - playerZ;
        GlStateManager.glLineWidth(1.0F);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = -16; i <= 32; i += 16) {
            for (int j = -16; j <= 32; j += 16) { column(bufferbuilder, chunkX + (double) i, minY, maxY, chunkZ + (double) j, 1.0F, 0.0F, 0.0F, 0.5F); }
        }
        for (int k = 2; k < SIZE; k += 2) {
            column(bufferbuilder, chunkX + (double) k, minY, maxY, chunkZ, YELLOW_RED, YELLOW_GREEN, YELLOW_BLUE, 1.0F);
            column(bufferbuilder, chunkX + (double) k, minY, maxY, chunkZ + SIZE, YELLOW_RED, YELLOW_GREEN, YELLOW_BLUE, 1.0F);
        }
        for (int l = 2; l < SIZE; l += 2) {
            column(bufferbuilder, chunkX, minY, maxY, chunkZ + (double) l, YELLOW_RED, YELLOW_GREEN, YELLOW_BLUE, 1.0F);
            column(bufferbuilder, chunkX + SIZE, minY, maxY, chunkZ + (double) l, YELLOW_RED, YELLOW_GREEN, YELLOW_BLUE, 1.0F);
        }
        for (int i1 = (int) yOffset; i1 <= 256 + (int) yOffset; i1 += 2) { ring(bufferbuilder, chunkX, (double) i1 - playerY, chunkZ, YELLOW_RED, YELLOW_GREEN, YELLOW_BLUE); }
        tessellator.draw();
        GlStateManager.glLineWidth(2.0F);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int j1 = 0; j1 <= SIZE; j1 += SIZE) {
            for (int l1 = 0; l1 <= SIZE; l1 += SIZE) { column(bufferbuilder, chunkX + (double) j1, minY, maxY, chunkZ + (double) l1, BLUE_RED, BLUE_GREEN, BLUE_BLUE, 1.0F); }
        }
        for (int k1 = (int) yOffset; k1 <= 256 + (int) yOffset; k1 += SIZE) { ring(bufferbuilder, chunkX, (double) k1 - playerY, chunkZ, BLUE_RED, BLUE_GREEN, BLUE_BLUE); }
        tessellator.draw();
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z, float red, float green, float blue, float alpha) { buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex(); }

    private static void column(BufferBuilder buffer, double x, double minY, double maxY, double z, float red, float green, float blue, float alpha) {
        vertex(buffer, x, minY, z, red, green, blue, 0.0F);
        vertex(buffer, x, minY, z, red, green, blue, alpha);
        vertex(buffer, x, maxY, z, red, green, blue, alpha);
        vertex(buffer, x, maxY, z, red, green, blue, 0.0F);
    }

    private static void ring(BufferBuilder buffer, double chunkX, double y, double chunkZ, float red, float green, float blue) {
        vertex(buffer, chunkX, y, chunkZ, red, green, blue, 0.0F);
        vertex(buffer, chunkX, y, chunkZ, red, green, blue, 1.0F);
        vertex(buffer, chunkX, y, chunkZ + SIZE, red, green, blue, 1.0F);
        vertex(buffer, chunkX + SIZE, y, chunkZ + SIZE, red, green, blue, 1.0F);
        vertex(buffer, chunkX + SIZE, y, chunkZ, red, green, blue, 1.0F);
        vertex(buffer, chunkX, y, chunkZ, red, green, blue, 1.0F);
        vertex(buffer, chunkX, y, chunkZ, red, green, blue, 0.0F);
    }
}
