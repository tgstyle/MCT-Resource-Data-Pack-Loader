package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.util.WindGust;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import javax.annotation.Nullable;

public final class ContentWeatherRenderer {
    private static final ResourceLocation RAIN = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
    private static final ResourceLocation SNOW = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");
    private final float[] sizeX = new float[1024];
    private final float[] sizeZ = new float[1024];
    private final float red;
    private final float green;
    private final float blue;
    private final float snowRed;
    private final float snowGreen;
    private final float snowBlue;
    private final RainDef rain;
    private final boolean upward;
    private double slantX;
    private double slantZ;
    @Nullable private BufferBuilder buffer;
    @Nullable private Biome.Precipitation drawing;

    public ContentWeatherRenderer(RainDef rain) {
        for (int row = 0; row < 32; row++) {
            for (int column = 0; column < 32; column++) {
                float dx = column - 16;
                float dz = row - 16;
                float length = Mth.sqrt(dx * dx + dz * dz);
                sizeX[row << 5 | column] = -dz / length;
                sizeZ[row << 5 | column] = dx / length;
            }
        }
        red = (rain.color() >> 16 & 255) / 255.0F;
        green = (rain.color() >> 8 & 255) / 255.0F;
        blue = (rain.color() & 255) / 255.0F;
        snowRed = (rain.snowColor() >> 16 & 255) / 255.0F;
        snowGreen = (rain.snowColor() >> 8 & 255) / 255.0F;
        snowBlue = (rain.snowColor() & 255) / 255.0F;
        this.rain = rain;
        upward = rain.angle() > 90.0F;
    }

    public void render(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        float strength = level.getRainLevel(partialTick);
        if (strength <= 0.0F) { return; }
        double time = level.getGameTime() + partialTick;
        double slope = WindGust.slope(WindGust.angle(rain, time)) * (upward ? 1.0D : -1.0D);
        double heading = Math.toRadians(WindGust.heading(rain, time));
        slantX = -Math.sin(heading) * slope;
        slantZ = Math.cos(heading) * slope;
        lightTexture.turnOnLightLayer();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        int radius = Minecraft.useFancyGraphics() ? 10 : 5;
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.setShader(GameRenderer::getParticleShader);
        int blockX = Mth.floor(camX);
        int blockY = Mth.floor(camY);
        int blockZ = Mth.floor(camZ);
        drawing = null;
        buffer = null;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int z = blockZ - radius; z <= blockZ + radius; z++) {
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                column(level, pos, x, z, blockX, blockY, blockZ, radius, ticks, partialTick, strength, camX, camY, camZ);
            }
        }
        if (buffer != null) { BufferUploader.drawWithShader(buffer.buildOrThrow()); }
        buffer = null;
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();
    }

    private void column(ClientLevel level, BlockPos.MutableBlockPos pos, int x, int z, int blockX, int blockY, int blockZ, int radius, int ticks, float partialTick, float strength, double camX, double camY, double camZ) {
        pos.set(x, camY, z);
        Biome biome = level.getBiome(pos).value();
        if (!biome.hasPrecipitation()) { return; }
        int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        int bottom = Math.max(blockY - radius, ground);
        int top = Math.max(blockY + radius, ground);
        if (bottom == top) { return; }
        int lightY = Math.max(ground, blockY);
        int seed = x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761;
        RandomSource random = RandomSource.create(seed);
        pos.set(x, bottom, z);
        Biome.Precipitation precipitation = biome.getPrecipitationAt(pos);
        if (precipitation == Biome.Precipitation.NONE) { return; }
        BufferBuilder builder = begin(precipitation);
        int index = (z - blockZ + 16) * 32 + x - blockX + 16;
        double halfX = sizeX[index] * 0.5D;
        double halfZ = sizeZ[index] * 0.5D;
        double fromX = x + 0.5D - camX;
        double fromZ = z + 0.5D - camZ;
        float distance = (float) Math.sqrt(fromX * fromX + fromZ * fromZ) / radius;
        pos.set(x, lightY, z);
        int light = LevelRenderer.getLightColor(level, pos);
        float u = 0.0F;
        float scroll;
        float alpha;
        float r;
        float g;
        float b;
        if (precipitation == Biome.Precipitation.RAIN) {
            int wrapped = ticks & 131071;
            int offset = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
            scroll = -(wrapped + offset + partialTick) / 32.0F * (3.0F + random.nextFloat()) % 32.0F;
            alpha = ((1.0F - distance * distance) * 0.5F + 0.5F) * strength;
            r = red;
            g = green;
            b = blue;
        }
        else {
            float time = ticks + partialTick;
            float drift = -((ticks & 511) + partialTick) / 512.0F;
            u = (float) (random.nextDouble() + time * 0.01D * (float) random.nextGaussian());
            scroll = drift + (float) (random.nextDouble() + time * (float) random.nextGaussian() * 0.001D);
            alpha = ((1.0F - distance * distance) * 0.3F + 0.5F) * strength;
            light = ((light >> 16 & 65535) * 3 + 240) / 4 << 16 | ((light & 65535) * 3 + 240) / 4;
            r = snowRed;
            g = snowGreen;
            b = snowBlue;
        }
        if (upward) { scroll = -scroll; }
        double shiftTop = top - ground;
        double shiftBottom = bottom - ground;
        builder.addVertex((float) (x - camX - halfX + 0.5D + slantX * shiftTop), (float) (top - camY), (float) (z - camZ - halfZ + 0.5D + slantZ * shiftTop)).setUv(u, bottom * 0.25F + scroll).setColor(r, g, b, alpha).setLight(light);
        builder.addVertex((float) (x - camX + halfX + 0.5D + slantX * shiftTop), (float) (top - camY), (float) (z - camZ + halfZ + 0.5D + slantZ * shiftTop)).setUv(u + 1.0F, bottom * 0.25F + scroll).setColor(r, g, b, alpha).setLight(light);
        builder.addVertex((float) (x - camX + halfX + 0.5D + slantX * shiftBottom), (float) (bottom - camY), (float) (z - camZ + halfZ + 0.5D + slantZ * shiftBottom)).setUv(u + 1.0F, top * 0.25F + scroll).setColor(r, g, b, alpha).setLight(light);
        builder.addVertex((float) (x - camX - halfX + 0.5D + slantX * shiftBottom), (float) (bottom - camY), (float) (z - camZ - halfZ + 0.5D + slantZ * shiftBottom)).setUv(u, top * 0.25F + scroll).setColor(r, g, b, alpha).setLight(light);
    }

    private BufferBuilder begin(Biome.Precipitation precipitation) {
        if (drawing == precipitation && buffer != null) { return buffer; }
        if (buffer != null) { BufferUploader.drawWithShader(buffer.buildOrThrow()); }
        drawing = precipitation;
        RenderSystem.setShaderTexture(0, precipitation == Biome.Precipitation.RAIN ? RAIN : SNOW);
        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        return buffer;
    }
}
