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
                bufferbuilder.pos(chunkX + (double) i, minY, chunkZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
                bufferbuilder.pos(chunkX + (double) i, minY, chunkZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                bufferbuilder.pos(chunkX + (double) i, maxY, chunkZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                bufferbuilder.pos(chunkX + (double) i, maxY, chunkZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            }
        }
        for (int k = 2; k < 16; k += 2) {
            bufferbuilder.pos(chunkX + (double) k, minY, chunkZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, minY, chunkZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, maxY, chunkZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, maxY, chunkZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, minY, chunkZ + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, minY, chunkZ + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, maxY, chunkZ + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + (double) k, maxY, chunkZ + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }
        for (int l = 2; l < 16; l += 2) {
            bufferbuilder.pos(chunkX, minY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX, minY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, maxY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, maxY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, minY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, minY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, maxY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, maxY, chunkZ + (double) l).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }
        for (int i1 = (int) yOffset; i1 <= 256 + (int) yOffset; i1 += 2) {
            double d7 = (double) i1 - playerY;
            bufferbuilder.pos(chunkX, d7, chunkZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX, d7, chunkZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, d7, chunkZ + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, d7, chunkZ + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, d7, chunkZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, d7, chunkZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, d7, chunkZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }
        tessellator.draw();
        GlStateManager.glLineWidth(2.0F);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int j1 = 0; j1 <= 16; j1 += 16) {
            for (int l1 = 0; l1 <= 16; l1 += 16) {
                bufferbuilder.pos(chunkX + (double) j1, minY, chunkZ + (double) l1).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
                bufferbuilder.pos(chunkX + (double) j1, minY, chunkZ + (double) l1).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
                bufferbuilder.pos(chunkX + (double) j1, maxY, chunkZ + (double) l1).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
                bufferbuilder.pos(chunkX + (double) j1, maxY, chunkZ + (double) l1).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            }
        }
        for (int k1 = (int) yOffset; k1 <= 256 + (int) yOffset; k1 += 16) {
            double d8 = (double) k1 - playerY;
            bufferbuilder.pos(chunkX, d8, chunkZ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(chunkX, d8, chunkZ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, d8, chunkZ + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, d8, chunkZ + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX + 16.0D, d8, chunkZ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, d8, chunkZ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            bufferbuilder.pos(chunkX, d8, chunkZ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
        }
        tessellator.draw();
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
    }
}
