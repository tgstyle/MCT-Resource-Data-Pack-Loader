package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.block.ContentBlockBell;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackBell;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;

public class PackBellRenderer extends TileEntitySpecialRenderer<TileEntityPackBell> {
    private static final String BODY = "body";
    private static final float PIVOT_Y = 0.75F;
    private static final float DEGREES = 180.0F / (float) Math.PI;

    public static ModelResourceLocation body(ResourceLocation block) { return new ModelResourceLocation(block, BODY); }

    @Override public void render(@Nullable TileEntityPackBell tile, double x, double y, double z, float partial, int broken, float alpha) {
        if (tile == null || !tile.hasWorld()) { return; }
        Block block = tile.getWorld().getBlockState(tile.getPos()).getBlock();
        if (!(block instanceof ContentBlockBell) || !((ContentBlockBell) block).swings() || block.getRegistryName() == null) { return; }
        Minecraft minecraft = Minecraft.getMinecraft();
        ModelManager models = minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
        IBakedModel model = models.getModel(body(block.getRegistryName()));
        if (model == models.getMissingModel()) { return; }
        float tiltX = 0.0F;
        float tiltZ = 0.0F;
        if (tile.shaking()) {
            float time = tile.ticks() + partial;
            float swing = MathHelper.sin(time / (float) Math.PI) / (4.0F + time / 3.0F);
            switch (tile.clicked()) {
                case NORTH: tiltX = -swing; break;
                case SOUTH: tiltX = swing; break;
                case EAST: tiltZ = -swing; break;
                case WEST: tiltZ = swing; break;
                default: break;
            }
        }
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x + 0.5D, y + PIVOT_Y, z + 0.5D);
        GlStateManager.rotate(tiltZ * DEGREES, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(tiltX * DEGREES, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.5D, -PIVOT_Y, -0.5D);
        minecraft.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
