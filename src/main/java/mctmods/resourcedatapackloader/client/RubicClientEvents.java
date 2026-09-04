package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.interfaces.IRubicPlayerList;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IGuiOptionsRowList;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IGuiScreen;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IGuiVideoSettings;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import javax.annotation.Nonnull;

import java.util.List;

public class RubicClientEvents {
    @SubscribeEvent public void onWorldClientTickEvent(TickEvent.ClientTickEvent evt) {
        IRubicWorldInternal world = (IRubicWorldInternal) FMLClientHandler.instance().getWorldClient();
        if (world == null || Minecraft.getMinecraft().isGamePaused()) { return; }
        if (evt.phase == TickEvent.Phase.END && world.rdpl$isRubicWorld()) { world.rdpl$tickRubicWorld(); }
    }

    @SubscribeEvent public void onServerTick(TickEvent.ServerTickEvent event) {
        IRubicPlayerList playerList = ((IRubicPlayerList) FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList());
        int prevDist = playerList.getVerticalViewDistance();
        int newDist = Config.client.verticalCubeLoadDistance;
        if (prevDist != newDist) {
            Rubic.LOGGER.info("Changing vertical view distance to {}, from {}", newDist, prevDist);
            playerList.setVerticalViewDistance(newDist);
        }
    }

    @SubscribeEvent public void initGuiEvent(InitGuiEvent.Post event) {
        GuiScreen currentGui = event.getGui();
        if (currentGui instanceof GuiVideoSettings) {
            GuiVideoSettings gvs = (GuiVideoSettings) currentGui;
            if (!FMLClientHandler.instance().hasOptifine()) {
                IGuiOptionsRowList gowl = (IGuiOptionsRowList) ((IGuiVideoSettings) gvs).getOptionsRowList();
                GuiOptionsRowList.Row row = this.createRow(gvs.width);
                gowl.getOptions().add(1, row);
            }
            else {
                int idx = 3;
                int btnSpacing = 20;
                ((IGuiScreen) gvs).getButtonList()
                        .add(idx, new VertViewDistanceSlider(gvs.width / 2 - 155 + 160, gvs.height / 6 + btnSpacing * (idx / 2) - 12));
                List<GuiButton> buttons = ((IGuiScreen) gvs).getButtonList();
                for (int i = 0; i < buttons.size() - 4; i++) {
                    GuiButton btn = buttons.get(i);
                    int x = gvs.width / 2 - 155 + i % 2 * 160;
                    int y = gvs.height / 6 + 21 * (i / 2) - 12;
                    btn.x = x;
                    btn.y = y;
                }
                for (int i = buttons.size() - 4; i < buttons.size() - 1; i++) {
                    GuiButton btn = buttons.get(i);
                    int newBtnWidth = 150 * 2 / 3;
                    int minX = gvs.width / 2 - 155;
                    int maxX = gvs.width / 2 - 155 + 160 + btn.width;
                    int minXCenter = minX + newBtnWidth / 2;
                    int maxXCenter = maxX - newBtnWidth / 2;
                    int x = minXCenter + (i % 3) * (maxXCenter - minXCenter) / 2 - newBtnWidth / 2;
                    int y = gvs.height / 6 + 21 * (buttons.size() - 4) / 2 - 12;
                    btn.x = x;
                    btn.y = y;
                    btn.width = newBtnWidth;
                }
            }
        }
    }

    private GuiOptionsRowList.Row createRow(int width) {
        VertViewDistanceSlider slider = new VertViewDistanceSlider(width / 2 - 155 + 160, 0);
        GuiButton spacer = new GuiButton(101, 0, 0, 0, 0, "");
        spacer.visible = false;
        return new GuiOptionsRowList.Row(slider, spacer);
    }

    private static class VertViewDistanceSlider extends GuiButton {
        private final int MAX_VIEW_DIST = Rubic.hasOptifine() ? 64 : 32;
        private float sliderValue;
        public boolean dragging;

        public VertViewDistanceSlider(int x, int y) {
            super(100, x, y, 150, 20, "");
            this.sliderValue = MathUtil.unlerp(Config.client.verticalCubeLoadDistance, 2, MAX_VIEW_DIST);
            this.displayString = this.createDisplayString();
        }

        @Override protected int getHoverState(boolean mouseOver) { return 0; }

        private void slideTo(int mouseX) {
            this.sliderValue = MathHelper.clamp((float) (mouseX - (this.x + 4)) / (float) (this.width - 8), 0.0F, 1.0F);
            setVerticalViewDistance(Math.round(MathUtil.lerp(this.sliderValue, 2, MAX_VIEW_DIST)));
            this.sliderValue = MathUtil.unlerp(Config.client.verticalCubeLoadDistance, 2, MAX_VIEW_DIST);
            this.displayString = this.createDisplayString();
        }

        @Override protected void mouseDragged(@Nonnull Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                if (this.dragging) { slideTo(mouseX); }
                mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)), this.y, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
            }
        }

        @Override public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                slideTo(mouseX);
                this.dragging = true;
                return true;
            }
            else { return false; }
        }

        private String createDisplayString() { return I18n.format("rdpl.gui.verticaldistance", Config.client.verticalCubeLoadDistance); }

        private static void setVerticalViewDistance(int value) {
            Config.client.verticalCubeLoadDistance = value;
            ConfigManager.sync(MCTMixin.MIXIN_ID, Type.INSTANCE);
        }

        @Override public void mouseReleased(int mouseX, int mouseY) { this.dragging = false; }
    }
}
