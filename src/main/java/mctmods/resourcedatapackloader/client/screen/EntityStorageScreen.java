package mctmods.resourcedatapackloader.client.screen;

import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
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
        super(menu, inventory, title, ContentContainerMenu.width(EntityStorageMenu.shape(menu.def())), ContentContainerMenu.height(EntityStorageMenu.shape(menu.def())));
        this.def = menu.def();
    }

    @Override public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        super.extractBackground(graphics, mouseX, mouseY, partial);
        ContentContainerScreen.frame(graphics, leftPos, topPos, imageWidth, def.bands(), StorageDef.PER_ROW, def.slotRows());
        int band = def.slotRows();
        if (def.fluidCapacity() > 0) {
            int top = trough(graphics, band++);
            int fill = fill(menu.fluidAmount(), def.fluidCapacity());
            Fluid held = menu.fluid();
            if (held != Fluids.EMPTY && fill > 0) { fluid(graphics, held, leftPos + EDGE + 1, top, fill); }
        }
        if (def.energyCapacity() > 0) {
            int top = trough(graphics, band);
            graphics.fill(leftPos + EDGE + 1, top, leftPos + EDGE + 1 + fill(menu.energy(), def.energyCapacity()), top + FILL_TALL, ENERGY);
        }
    }

    @Override protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        int band = def.slotRows();
        if (def.fluidCapacity() > 0) {
            Fluid held = menu.fluid();
            Component name = held == Fluids.EMPTY ? Component.translatable("rdpl.storage.empty") : held.getFluidType().getDescription();
            label(graphics, band++, Component.translatable("rdpl.storage.fluid", name, menu.fluidAmount(), def.fluidCapacity()));
        }
        if (def.energyCapacity() > 0) { label(graphics, band, Component.translatable("rdpl.storage.energy", menu.energy(), def.energyCapacity())); }
    }

    private int trough(GuiGraphicsExtractor graphics, int band) {
        int top = topPos + HEADER + band * CELL;
        graphics.fill(leftPos + EDGE, top + 1, leftPos + imageWidth - EDGE, top + CELL - 1, TROUGH);
        return top + 2;
    }

    private int fill(int held, int most) { return (int) ((long) (imageWidth - EDGE - EDGE - 2) * Math.min(held, most) / most); }

    private static void fluid(GuiGraphicsExtractor graphics, Fluid held, int x, int y, int wide) {
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(held.defaultFluidState());
        TextureAtlasSprite sprite = model.stillMaterial().sprite();
        FluidTintSource tint = model.fluidTintSource();
        int color = tint == null ? -1 : tint.colorAsStack(new FluidStack(held, 1));
        graphics.enableScissor(x, y, x + wide, y + FILL_TALL);
        for (int across = 0; across < wide; across += TILE) { graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + across, y, TILE, TILE, color); }
        graphics.disableScissor();
    }

    private void label(GuiGraphicsExtractor graphics, int band, Component text) { graphics.centeredText(font, text, imageWidth / 2, HEADER + band * CELL + 5, TEXT); }
}
