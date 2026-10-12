package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.client.render.RingQuads;
import mctmods.resourcedatapackloader.util.BodyRings;

import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

@SideOnly(Side.CLIENT) public final class GcRings {
    private static final int SEGMENTS = 90;
    private static final float STEP = (float) (Math.PI * 2.0D / SEGMENTS);
    private static final float PLANET_SCALE = 75.0F;
    private static final float MOON_SCALE = 0.6F;
    private static boolean hooked;

    private GcRings() {}

    static void hook() {
        if (hooked) { return; }
        hooked = true;
        MinecraftForge.EVENT_BUS.register(new GcRings());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) public void behind(CelestialBodyRenderEvent.Pre event) { draw(event.celestialBody, false); }

    @SubscribeEvent(priority = EventPriority.LOWEST) public void inFront(CelestialBodyRenderEvent.Post event) { draw(event.celestialBody, true); }

    @SubscribeEvent(priority = EventPriority.HIGH) public void belt(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre event) {
        CelestialBody body = event.celestialBody;
        float width = GcCelestial.beltWidth(body);
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (width <= 0.0F || !(screen instanceof GuiCelestialSelection)) { return; }
        event.setCanceled(true);
        float alpha = ((GuiCelestialSelection) screen).getAlpha(body);
        float scale = body instanceof Planet ? PLANET_SCALE : MOON_SCALE;
        float distance = body.getRelativeDistanceFromCenter().unScaledDistance;
        float inner = scale * Math.max(0.0F, distance - width / 2.0F);
        float outer = scale * (distance + width / 2.0F);
        Vector3f offset = event.parentOffset;
        GL11.glTranslatef(offset.x, offset.y, offset.z);
        GL11.glColor4f(body.getRingColorR(), body.getRingColorG(), body.getRingColorB(), alpha / 2.0F);
        circle(inner);
        circle(outer);
        GL11.glColor4f(body.getRingColorR(), body.getRingColorG(), body.getRingColorB(), alpha / 10.0F);
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= SEGMENTS; i++) {
            float cos = MathHelper.cos(i * STEP);
            float sin = MathHelper.sin(i * STEP);
            GL11.glVertex2f(outer * cos, outer * sin);
            GL11.glVertex2f(inner * cos, inner * sin);
        }
        GL11.glEnd();
        GL11.glTranslatef(-offset.x, -offset.y, -offset.z);
    }

    private static void circle(float radius) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < SEGMENTS; i++) { GL11.glVertex2f(radius * MathHelper.cos(i * STEP), radius * MathHelper.sin(i * STEP)); }
        GL11.glEnd();
    }

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
