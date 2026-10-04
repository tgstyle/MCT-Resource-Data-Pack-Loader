package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.RainDef;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public final class ContentWeatherShape {
    private static final float MAX_LEAN = 75.0F;
    private final float[] sizeX = new float[1024];
    private final float[] sizeZ = new float[1024];
    private final int color;
    private final int snowColor;
    private final float slantX;
    private final float slantZ;
    private final boolean upward;

    public ContentWeatherShape(RainDef rain) {
        for (int row = 0; row < 32; row++) {
            for (int column = 0; column < 32; column++) {
                float dx = column - 16;
                float dz = row - 16;
                float length = Mth.length(dx, dz);
                sizeX[row << 5 | column] = -dz / length;
                sizeZ[row << 5 | column] = dx / length;
            }
        }
        color = rain.color();
        snowColor = rain.snowColor();
        upward = rain.angle() > 90.0F;
        float lean = Math.min(Math.min(rain.angle(), 180.0F - rain.angle()), MAX_LEAN);
        double slope = Math.tan(Math.toRadians(lean)) * (upward ? 1.0D : -1.0D);
        double heading = Math.toRadians(rain.heading());
        slantX = (float) (-Math.sin(heading) * slope);
        slantZ = (float) (Math.cos(heading) * slope);
    }

    public void build(VertexConsumer builder, ClientLevel level, List<WeatherEffectRenderer.ColumnInstance> columns, Vec3 camera, boolean snow, int radius, float intensity) {
        float radiusSq = radius * radius;
        float maxAlpha = snow ? 0.8F : 1.0F;
        int rgb = snow ? snowColor : color;
        int cameraX = Mth.floor(camera.x);
        int cameraZ = Mth.floor(camera.z);
        for (WeatherEffectRenderer.ColumnInstance column : columns) {
            float relativeX = (float) (column.x() + 0.5D - camera.x);
            float relativeZ = (float) (column.z() + 0.5D - camera.z);
            float alpha = Mth.lerp(Math.min((relativeX * relativeX + relativeZ * relativeZ) / radiusSq, 1.0F), maxAlpha, 0.5F) * intensity;
            int tint = ARGB.color(alpha, rgb);
            int index = (column.z() - cameraZ + 16) * 32 + column.x() - cameraX + 16;
            float halfX = sizeX[index] / 2.0F;
            float halfZ = sizeZ[index] / 2.0F;
            int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING, column.x(), column.z());
            float shiftTop = column.topY() - ground;
            float shiftBottom = column.bottomY() - ground;
            float top = (float) (column.topY() - camera.y);
            float bottom = (float) (column.bottomY() - camera.y);
            float scroll = upward ? -column.vOffset() : column.vOffset();
            float v0 = column.bottomY() * 0.25F + scroll;
            float v1 = column.topY() * 0.25F + scroll;
            float u0 = column.uOffset();
            float u1 = column.uOffset() + 1.0F;
            builder.addVertex(relativeX - halfX + slantX * shiftTop, top, relativeZ - halfZ + slantZ * shiftTop).setUv(u0, v0).setColor(tint).setLight(column.lightCoords());
            builder.addVertex(relativeX + halfX + slantX * shiftTop, top, relativeZ + halfZ + slantZ * shiftTop).setUv(u1, v0).setColor(tint).setLight(column.lightCoords());
            builder.addVertex(relativeX + halfX + slantX * shiftBottom, bottom, relativeZ + halfZ + slantZ * shiftBottom).setUv(u1, v1).setColor(tint).setLight(column.lightCoords());
            builder.addVertex(relativeX - halfX + slantX * shiftBottom, bottom, relativeZ - halfZ + slantZ * shiftBottom).setUv(u0, v1).setColor(tint).setLight(column.lightCoords());
        }
    }
}
