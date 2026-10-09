package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.client.render.RingQuads;
import mctmods.resourcedatapackloader.util.BodyRings;

import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT) public final class GcRings {
    private static boolean hooked;

    private GcRings() {}

    static void hook() {
        if (hooked) { return; }
        hooked = true;
        MinecraftForge.EVENT_BUS.register(new GcRings());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) public void behind(CelestialBodyRenderEvent.Pre event) { draw(event.celestialBody, false); }

    @SubscribeEvent(priority = EventPriority.LOWEST) public void inFront(CelestialBodyRenderEvent.Post event) { draw(event.celestialBody, true); }

    private static void draw(CelestialBody body, boolean front) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (!(screen instanceof GuiCelestialSelection)) { return; }
        BodyRings.Ring ring = BodyRings.find(body.getName());
        if (ring == null) { return; }
        GuiCelestialSelection map = (GuiCelestialSelection) screen;
        float alpha = map.getAlpha(body);
        if (alpha <= 0.0F) { return; }
        float width = map.getWidthForCelestialBody(body) * ring.size;
        float height = width / RingQuads.aspect(ring);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        if (front) { map.drawTexturedModalRect(-width / 2.0F, 0.0F, width, height / 2.0F, 0.0F, 0.5F, 1.0F, 0.5F, false, false, 1.0F, 1.0F); }
        else { map.drawTexturedModalRect(-width / 2.0F, -height / 2.0F, width, height / 2.0F, 0.0F, 0.0F, 1.0F, 0.5F, false, false, 1.0F, 1.0F); }
    }
}
