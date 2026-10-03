package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.gui.ContainerEntityStorage;
import mctmods.resourcedatapackloader.content.gui.ContainerPack;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryBasic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import javax.annotation.Nullable;

public class GuiEntityStorage extends GuiPackContainer {
    private static final int TROUGH = 0xFF373737;
    private static final int ENERGY = 0xFFC03030;
    private static final int TEXT = 0xFFFFFF;
    private static final int TILE = 16;
    private static final int FILL_TALL = ContainerPack.SLOT - 4;
    private final ContainerEntityStorage storage;

    public GuiEntityStorage(ContainerEntityStorage storage, Entity entity) {
        super(storage, new InventoryBasic(entity.getName(), true, 1), storage.bands(), StorageDef.PER_ROW, null);
        this.storage = storage;
        limitSlots(storage.def.slots());
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partial, mouseX, mouseY);
        int band = storage.slotRows();
        if (storage.def.fluidCapacity > 0) {
            int top = trough(band++);
            int fill = fill(storage.fluidAmount(), storage.def.fluidCapacity);
            FluidStack held = held();
            if (held != null && fill > 0) { fluid(held, guiLeft + EDGE + 1, top, fill); }
        }
        if (storage.def.energyCapacity > 0) {
            int top = trough(band);
            drawRect(guiLeft + EDGE + 1, top, guiLeft + EDGE + 1 + fill(storage.energy(), storage.def.energyCapacity), top + FILL_TALL, ENERGY);
        }
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int band = storage.slotRows();
        if (storage.def.fluidCapacity > 0) {
            FluidStack held = held();
            String name = held == null ? I18n.format("rdpl.storage.empty") : held.getLocalizedName();
            label(band++, I18n.format("rdpl.storage.fluid", name, storage.fluidAmount(), storage.def.fluidCapacity));
        }
        if (storage.def.energyCapacity > 0) { label(band, I18n.format("rdpl.storage.energy", storage.energy(), storage.def.energyCapacity)); }
    }

    @Nullable private FluidStack held() {
        Fluid fluid = storage.fluid() == null ? null : FluidRegistry.getFluid(storage.fluid());
        return fluid == null ? null : new FluidStack(fluid, 1);
    }

    private int trough(int band) {
        int top = guiTop + ContainerPack.HEADER + band * ContainerPack.SLOT;
        drawRect(guiLeft + EDGE, top + 1, guiLeft + xSize - EDGE, top + ContainerPack.SLOT - 1, TROUGH);
        return top + 2;
    }

    private int fill(int held, int most) { return (int) ((long) (xSize - EDGE - EDGE - 2) * Math.min(held, most) / most); }

    private void fluid(FluidStack held, int x, int y, int wide) {
        TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(held.getFluid().getStill(held).toString());
        int color = held.getFluid().getColor(held);
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        for (int across = 0; across < wide; across += TILE) {
            int step = Math.min(TILE, wide - across);
            double right = sprite.getInterpolatedU(step);
            double bottom = sprite.getInterpolatedV(FILL_TALL);
            buffer.pos(x + across, y + FILL_TALL, zLevel).tex(sprite.getMinU(), bottom).endVertex();
            buffer.pos(x + across + step, y + FILL_TALL, zLevel).tex(right, bottom).endVertex();
            buffer.pos(x + across + step, y, zLevel).tex(right, sprite.getMinV()).endVertex();
            buffer.pos(x + across, y, zLevel).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        }
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    private void label(int band, String text) { drawCenteredString(fontRenderer, text, xSize / 2, ContainerPack.HEADER + band * ContainerPack.SLOT + 5, TEXT); }
}
