package mctmods.resourcedatapackloader.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class CubeChunkBorder {
    private CubeChunkBorder() {}

    public static void render(EntityPlayer player, Tessellator tessellator, float partialTicks) {
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
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
            for (int j = -16; j <= 32; j += 16) {
                column(bufferbuilder, chunkX + (double) i, minY, maxY, chunkZ + (double) j, 1.0F, 0.0F, 0.0F, 0.5F);
            }
        }
        for (int k = 2; k < 16; k += 2) {
            column(bufferbuilder, chunkX + (double) k, minY, maxY, chunkZ, 1.0F, 1.0F, 0.0F, 1.0F);
            column(bufferbuilder, chunkX + (double) k, minY, maxY, chunkZ + 16.0D, 1.0F, 1.0F, 0.0F, 1.0F);
        }
        for (int l = 2; l < 16; l += 2) {
            column(bufferbuilder, chunkX, minY, maxY, chunkZ + (double) l, 1.0F, 1.0F, 0.0F, 1.0F);
            column(bufferbuilder, chunkX + 16.0D, minY, maxY, chunkZ + (double) l, 1.0F, 1.0F, 0.0F, 1.0F);
        }
        for (int i1 = (int) yOffset; i1 <= 256 + (int) yOffset; i1 += 2) {
            ring(bufferbuilder, chunkX, (double) i1 - playerY, chunkZ, 1.0F, 1.0F, 0.0F);
        }
        tessellator.draw();
        GlStateManager.glLineWidth(2.0F);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int j1 = 0; j1 <= 16; j1 += 16) {
            for (int l1 = 0; l1 <= 16; l1 += 16) {
                column(bufferbuilder, chunkX + (double) j1, minY, maxY, chunkZ + (double) l1, 0.25F, 0.25F, 1.0F, 1.0F);
            }
        }
        for (int k1 = (int) yOffset; k1 <= 256 + (int) yOffset; k1 += 16) {
            ring(bufferbuilder, chunkX, (double) k1 - playerY, chunkZ, 0.25F, 0.25F, 1.0F);
        }
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
        vertex(buffer, chunkX, y, chunkZ + 16.0D, red, green, blue, 1.0F);
        vertex(buffer, chunkX + 16.0D, y, chunkZ + 16.0D, red, green, blue, 1.0F);
        vertex(buffer, chunkX + 16.0D, y, chunkZ, red, green, blue, 1.0F);
        vertex(buffer, chunkX, y, chunkZ, red, green, blue, 1.0F);
        vertex(buffer, chunkX, y, chunkZ, red, green, blue, 0.0F);
    }
}
