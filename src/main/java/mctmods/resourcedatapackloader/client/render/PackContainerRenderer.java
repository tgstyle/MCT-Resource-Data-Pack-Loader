package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.block.ContentBlockContainer;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PackContainerRenderer extends TileEntitySpecialRenderer<TileEntityPackContainer> {
    private static final ResourceLocation CHEST = new ResourceLocation("textures/entity/chest/normal.png");
    private final ModelChest model = new ModelChest();

    @Override public void render(@Nonnull TileEntityPackContainer tile, double x, double y, double z, float partial, int broken, float alpha) {
        ContainerDef held = held(tile);
        if (held == null || !held.chestModel) { return; }
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        if (broken >= 0) { bindTexture(DESTROY_STAGES[broken]); }
        else { bindTexture(held.chestTexture == null ? CHEST : held.chestTexture); }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(turned(tile), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float lid = tile.lid(partial);
        model.chestLid.rotateAngleX = -((1.0F - (1.0F - lid) * (1.0F - lid) * (1.0F - lid)) * ((float) Math.PI / 2F));
        model.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Nullable private static ContainerDef held(TileEntityPackContainer tile) {
        Block block = tile.getWorld().getBlockState(tile.getPos()).getBlock();
        return block instanceof ContentBlockContainer ? ((ContentBlockContainer) block).container() : null;
    }

    private static float turned(TileEntityPackContainer tile) {
        switch (tile.facing()) {
            case NORTH: return 180.0F;
            case WEST: return 90.0F;
            case EAST: return 270.0F;
            default: return 0.0F;
        }
    }
}
