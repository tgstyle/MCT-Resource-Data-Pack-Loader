package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;

public class EntityStorageScreen extends AbstractContainerScreen<EntityStorageMenu> {
    private static final int TROUGH = 0xFF373737;
    private static final int FLUID = 0xFF3F76E4;
    private static final int ENERGY = 0xFFC03030;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int HEADER = 17;
    private static final int CELL = ContentContainerMenu.SLOT;
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
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partial);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(@Nonnull GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        ContentContainerScreen.frame(graphics, leftPos, topPos, imageWidth, def.bands(), def.columns());
        int band = def.rows();
        if (def.fluidCapacity() > 0) { gauge(graphics, band++, menu.fluidAmount(), def.fluidCapacity(), FLUID); }
        if (def.energyCapacity() > 0) { gauge(graphics, band, menu.energy(), def.energyCapacity(), ENERGY); }
    }

    @Override protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int band = def.rows();
        if (def.fluidCapacity() > 0) {
            Fluid held = menu.fluid();
            Component name = held == Fluids.EMPTY ? Component.translatable("rdpl.storage.empty") : held.getFluidType().getDescription();
            label(graphics, band++, Component.translatable("rdpl.storage.fluid", name, menu.fluidAmount(), def.fluidCapacity()));
        }
        if (def.energyCapacity() > 0) { label(graphics, band, Component.translatable("rdpl.storage.energy", menu.energy(), def.energyCapacity())); }
    }

    private void gauge(GuiGraphics graphics, int band, int held, int most, int color) {
        int cells = def.columns() * CELL;
        int left = leftPos + (imageWidth - cells) / 2;
        int top = topPos + HEADER + band * CELL;
        graphics.fill(left, top, left + cells, top + CELL, TROUGH);
        graphics.fill(left + 1, top + 1, left + 1 + (int) ((long) (cells - 2) * Math.min(held, most) / most), top + CELL - 1, color);
    }

    private void label(GuiGraphics graphics, int band, Component text) { graphics.drawCenteredString(font, text, imageWidth / 2, HEADER + band * CELL + 5, TEXT); }
}
