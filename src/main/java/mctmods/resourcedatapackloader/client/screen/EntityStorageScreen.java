package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class EntityStorageScreen extends AbstractContainerScreen<EntityStorageMenu> {
    private static final int TROUGH = 0xFF373737;
    private static final int ENERGY = 0xFFC03030;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int HEADER = 17;
    private static final int CELL = ContentContainerMenu.SLOT;
    private static final int EDGE = ContentContainerScreen.EDGE;
    private static final int TILE = 16;
    private static final int FILL_TALL = CELL - 4;
    private final StorageDef def;

    public EntityStorageScreen(EntityStorageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.def = menu.def();
        ContainerDef shape = EntityStorageMenu.shape(def);
        this.imageWidth = ContentContainerMenu.width(shape);
        this.imageHeight = ContentContainerMenu.height(shape);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        renderBackground(graphics, mouseX, mouseY, partial);
        super.render(graphics, mouseX, mouseY, partial);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(@Nonnull GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        ContentContainerScreen.frame(graphics, leftPos, topPos, imageWidth, def.bands(), StorageDef.PER_ROW, def.slotRows());
        int band = def.slotRows();
        if (def.fluidCapacity() > 0) {
            int top = trough(graphics, band++);
            int fill = fill(menu.fluidAmount(), def.fluidCapacity());
            Fluid held = menu.fluid();
            if (held != Fluids.EMPTY && fill > 0) { fluid(graphics, new FluidStack(held, 1), leftPos + EDGE + 1, top, fill); }
        }
        if (def.energyCapacity() > 0) {
            int top = trough(graphics, band);
            graphics.fill(leftPos + EDGE + 1, top, leftPos + EDGE + 1 + fill(menu.energy(), def.energyCapacity()), top + FILL_TALL, ENERGY);
        }
    }

    @Override protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int band = def.slotRows();
        if (def.fluidCapacity() > 0) {
            Fluid held = menu.fluid();
            Component name = held == Fluids.EMPTY ? Component.translatable("rdpl.storage.empty") : held.getFluidType().getDescription();
            label(graphics, band++, Component.translatable("rdpl.storage.fluid", name, menu.fluidAmount(), def.fluidCapacity()));
        }
        if (def.energyCapacity() > 0) { label(graphics, band, Component.translatable("rdpl.storage.energy", menu.energy(), def.energyCapacity())); }
    }

    private int trough(GuiGraphics graphics, int band) {
        int top = topPos + HEADER + band * CELL;
        graphics.fill(leftPos + EDGE, top + 1, leftPos + imageWidth - EDGE, top + CELL - 1, TROUGH);
        return top + 2;
    }

    private int fill(int held, int most) { return (int) ((long) (imageWidth - EDGE - EDGE - 2) * Math.min(held, most) / most); }

    private static void fluid(GuiGraphics graphics, FluidStack held, int x, int y, int wide) {
        IClientFluidTypeExtensions client = IClientFluidTypeExtensions.of(held.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(client.getStillTexture(held));
        int color = client.getTintColor(held);
        graphics.enableScissor(x, y, x + wide, y + FILL_TALL);
        for (int across = 0; across < wide; across += TILE) {
            graphics.blit(x + across, y, 0, TILE, TILE, sprite, (color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
        }
        graphics.disableScissor();
    }

    private void label(GuiGraphics graphics, int band, Component text) { graphics.drawCenteredString(font, text, imageWidth / 2, HEADER + band * CELL + 5, TEXT); }
}
