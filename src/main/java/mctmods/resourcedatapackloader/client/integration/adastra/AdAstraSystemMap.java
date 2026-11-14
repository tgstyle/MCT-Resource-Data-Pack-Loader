package mctmods.resourcedatapackloader.client.integration.adastra;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;
import mctmods.resourcedatapackloader.core.integration.adastra.ContentAdAstra;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import earth.terrarium.adastra.api.client.events.AdAstraClientEvents;
import earth.terrarium.adastra.client.screens.PlanetsScreen;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public final class AdAstraSystemMap {
    private static final int SIDES = 75;
    private static final int UNIT = 30;
    private static final int ICON = 12;
    private static final int STAR_ICON = 16;
    private static final int BAND_ALPHA = 0x30000000;
    private static final int EDGE_ALPHA = 0x80000000;
    private static final float MOON_UNIT = 0.6F;
    private static final float MILLIS_PER_DEGREE = 100.0F;
    private static final Map<ResourceLocation, int[]> SIZES = new HashMap<>();
    private static final Set<ResourceLocation> UNREADABLE = new HashSet<>();

    private AdAstraSystemMap() {}

    public static void register() { AdAstraClientEvents.RenderSolarSystemEvent.register(AdAstraSystemMap::render); }

    private static void render(GuiGraphics graphics, @Nullable ResourceLocation id, int width, int height) {
        ContentAdAstra.PackSystem system = id == null ? null : ContentAdAstra.systems().get(id);
        if (system == null) { return; }
        float cx = width / 2.0F;
        float cy = height / 2.0F;
        List<CelestialDef> bodies = system.bodies();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        orbits(bodies, cx, cy);
        CelestialDef star = system.star();
        icon(graphics, star.icon(), cx, cy, Math.max(1, Math.round(STAR_ICON * star.relativeSize())));
        for (CelestialDef body : bodies) {
            if (body.isMoon() || body.isBelt()) { continue; }
            float[] at = place(body, cx, cy, UNIT * body.distance());
            body(graphics, body, at[0], at[1]);
            for (CelestialDef moon : bodies) {
                if (!moon.isMoon() || !body.named(moon.parent())) { continue; }
                float[] around = place(moon, at[0], at[1], moonRadius(body, moon));
                body(graphics, moon, around[0], around[1]);
            }
        }
    }

    private static void orbits(List<CelestialDef> bodies, float cx, float cy) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        for (CelestialDef body : bodies) {
            if (body.isBelt()) { belt(buffer, body, cx, cy); }
            else if (!body.isMoon()) { PlanetsScreen.drawCircle(buffer, cx, cy, UNIT * body.distance(), SIDES, body.ringColor()); }
            else { moonOrbit(buffer, body, bodies, cx, cy); }
        }
        MeshData mesh = buffer.build();
        if (mesh != null) { BufferUploader.drawWithShader(mesh); }
    }

    private static void belt(BufferBuilder buffer, CelestialDef body, float cx, float cy) {
        float inner = UNIT * Math.max(0.0F, body.distance() - body.beltWidth() / 2.0F);
        float outer = UNIT * (body.distance() + body.beltWidth() / 2.0F);
        int rgb = body.ringColor() & 0xFFFFFF;
        for (float radius = inner; radius <= outer; radius += 1.0F) { PlanetsScreen.drawCircle(buffer, cx, cy, radius, SIDES, BAND_ALPHA | rgb); }
        PlanetsScreen.drawCircle(buffer, cx, cy, inner, SIDES, EDGE_ALPHA | rgb);
        PlanetsScreen.drawCircle(buffer, cx, cy, outer, SIDES, EDGE_ALPHA | rgb);
    }

    private static void moonOrbit(BufferBuilder buffer, CelestialDef moon, List<CelestialDef> bodies, float cx, float cy) {
        for (CelestialDef planet : bodies) {
            if (planet.isMoon() || planet.isBelt() || !planet.named(moon.parent())) { continue; }
            float[] at = place(planet, cx, cy, UNIT * planet.distance());
            PlanetsScreen.drawCircle(buffer, at[0], at[1], moonRadius(planet, moon), SIDES, moon.ringColor());
            return;
        }
    }

    private static float moonRadius(CelestialDef planet, CelestialDef moon) { return ICON * planet.relativeSize() / 2.0F + MOON_UNIT * moon.distance(); }

    private static float[] place(CelestialDef body, float cx, float cy, float radius) {
        double turned = body.orbitTime() > 0.0F ? Math.toRadians(Util.getMillis() / MILLIS_PER_DEGREE / body.orbitTime()) : 0.0D;
        double angle = body.phaseShift() + turned;
        return new float[] {(float) (cx + radius * Math.cos(angle)), (float) (cy + radius * Math.sin(angle))};
    }

    private static void body(GuiGraphics graphics, CelestialDef body, float x, float y) {
        int size = Math.max(4, Math.round(ICON * body.relativeSize()));
        ring(graphics, body, x, y, size, false);
        icon(graphics, body.icon(), x, y, size);
        ring(graphics, body, x, y, size, true);
    }

    private static void icon(GuiGraphics graphics, ResourceLocation texture, float x, float y, int size) {
        int[] pixels = sizeOf(texture);
        if (pixels == null) { return; }
        graphics.blit(texture, Math.round(x - size / 2.0F), Math.round(y - size / 2.0F), size, size, 0.0F, 0.0F, pixels[0], pixels[1], pixels[0], pixels[1]);
    }

    private static void ring(GuiGraphics graphics, CelestialDef body, float x, float y, int size, boolean inFront) {
        ResourceLocation ring = body.ring();
        int[] pixels = ring == null ? null : sizeOf(ring);
        if (pixels == null) { return; }
        float across = size * body.ringSize();
        float down = across * pixels[1] / pixels[0];
        float[] half = body.ringFront().half(inFront);
        graphics.blit(ring, Math.round(x - across / 2.0F + half[0] * across), Math.round(y - down / 2.0F + half[1] * down), Math.round((half[2] - half[0]) * across), Math.round((half[3] - half[1]) * down),
                half[0] * pixels[0], half[1] * pixels[1], Math.round((half[2] - half[0]) * pixels[0]), Math.round((half[3] - half[1]) * pixels[1]), pixels[0], pixels[1]);
    }

    @Nullable private static int[] sizeOf(ResourceLocation texture) {
        int[] held = SIZES.get(texture);
        if (held != null || UNREADABLE.contains(texture)) { return held; }
        try (InputStream in = Minecraft.getInstance().getResourceManager().getResourceOrThrow(texture).open(); NativeImage image = NativeImage.read(in)) {
            held = new int[] {image.getWidth(), image.getHeight()};
            SIZES.put(texture, held);
        } catch (IOException | RuntimeException bad) {
            UNREADABLE.add(texture);
            ContentLog.LOGGER.error("Ad Astra map cannot read texture {}, so it is not drawn: {}", texture, bad.getMessage());
        }
        return held;
    }
}
