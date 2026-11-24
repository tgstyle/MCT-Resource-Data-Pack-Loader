package mctmods.resourcedatapackloader.client.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentPotionClient {
    private ContentPotionClient() {}

    public static void draw(ResourceLocation texture, int x, int y, float alpha) {
        Draw.blit(texture, x, y, 18, 18, alpha);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
