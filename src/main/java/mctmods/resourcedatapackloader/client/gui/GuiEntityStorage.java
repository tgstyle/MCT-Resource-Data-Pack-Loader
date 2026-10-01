package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.content.gui.ContainerEntityStorage;
import mctmods.resourcedatapackloader.content.gui.ContainerPack;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryBasic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiEntityStorage extends GuiPackContainer {
    private static final int TROUGH = 0xFF373737;
    private static final int FLUID = 0xFF3F76E4;
    private static final int ENERGY = 0xFFC03030;
    private static final int TEXT = 0xFFFFFF;
    private final ContainerEntityStorage storage;

    public GuiEntityStorage(ContainerEntityStorage storage, Entity entity) {
        super(storage, new InventoryBasic(entity.getName(), true, 1), storage.bands(), storage.def.columns, null);
        this.storage = storage;
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partial, mouseX, mouseY);
        int band = storage.def.rows;
        if (storage.def.fluidCapacity > 0) { gauge(band++, storage.fluidAmount(), storage.def.fluidCapacity, FLUID); }
        if (storage.def.energyCapacity > 0) { gauge(band, storage.energy(), storage.def.energyCapacity, ENERGY); }
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int band = storage.def.rows;
        if (storage.def.fluidCapacity > 0) {
            Fluid held = storage.fluid() == null ? null : FluidRegistry.getFluid(storage.fluid());
            String name = held == null ? I18n.format("rdpl.storage.empty") : new FluidStack(held, 1).getLocalizedName();
            label(band++, I18n.format("rdpl.storage.fluid", name, storage.fluidAmount(), storage.def.fluidCapacity));
        }
        if (storage.def.energyCapacity > 0) { label(band, I18n.format("rdpl.storage.energy", storage.energy(), storage.def.energyCapacity)); }
    }

    private void gauge(int band, int held, int most, int color) {
        int cells = storage.def.columns * ContainerPack.SLOT;
        int left = guiLeft + (xSize - cells) / 2;
        int top = guiTop + ContainerPack.HEADER + band * ContainerPack.SLOT;
        drawRect(left, top, left + cells, top + ContainerPack.SLOT, TROUGH);
        drawRect(left + 1, top + 1, left + 1 + (int) ((long) (cells - 2) * Math.min(held, most) / most), top + ContainerPack.SLOT - 1, color);
    }

    private void label(int band, String text) { drawCenteredString(fontRenderer, text, xSize / 2, ContainerPack.HEADER + band * ContainerPack.SLOT + 5, TEXT); }
}
