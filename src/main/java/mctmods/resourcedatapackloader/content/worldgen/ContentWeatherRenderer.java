package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;

@SideOnly(Side.CLIENT) public final class ContentWeatherRenderer extends IRenderHandler {
    private static final ResourceLocation RAIN = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW = new ResourceLocation("textures/environment/snow.png");
    private static final float MAX_LEAN = 75.0F;
    private static final int NONE = -1;
    private static final int RAINING = 0;
    private static final int SNOWING = 1;
    private final float[] sizeX = new float[1024];
    private final float[] sizeZ = new float[1024];
    private final Random random = new Random();
    private final float red;
    private final float green;
    private final float blue;
    private final float snowRed;
    private final float snowGreen;
    private final float snowBlue;
    private final double slantX;
    private final double slantZ;
    private final boolean upward;
    private int drawing;

    public ContentWeatherRenderer(RainDef rain) {
        for (int row = 0; row < 32; row++) {
            for (int column = 0; column < 32; column++) {
                float dx = column - 16;
                float dz = row - 16;
                float length = MathHelper.sqrt(dx * dx + dz * dz);
                sizeX[row << 5 | column] = -dz / length;
                sizeZ[row << 5 | column] = dx / length;
            }
        }
        red = (rain.color >> 16 & 255) / 255.0F;
        green = (rain.color >> 8 & 255) / 255.0F;
        blue = (rain.color & 255) / 255.0F;
        snowRed = (rain.snowColor >> 16 & 255) / 255.0F;
        snowGreen = (rain.snowColor >> 8 & 255) / 255.0F;
        snowBlue = (rain.snowColor & 255) / 255.0F;
        upward = rain.angle > 90.0F;
        float lean = Math.min(Math.min(rain.angle, 180.0F - rain.angle), MAX_LEAN);
        double slope = Math.tan(Math.toRadians(lean)) * (upward ? 1.0D : -1.0D);
        double heading = Math.toRadians(rain.heading);
        slantX = -Math.sin(heading) * slope;
        slantZ = Math.cos(heading) * slope;
    }

    @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
        float strength = world.getRainStrength(partialTicks);
        Entity view = mc.getRenderViewEntity();
        if (strength <= 0.0F || view == null) { return; }
        mc.entityRenderer.enableLightmap();
        int ticks = ((IEntityRenderer) mc.entityRenderer).getRendererUpdateCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableCull();
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1F);
        double camX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        double camY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        double camZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
        int radius = mc.gameSettings.fancyGraphics ? 10 : 5;
        int blockX = MathHelper.floor(view.posX);
        int blockY = MathHelper.floor(view.posY);
        int blockZ = MathHelper.floor(view.posZ);
        drawing = NONE;
        buffer.setTranslation(-camX, -camY, -camZ);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int z = blockZ - radius; z <= blockZ + radius; z++) {
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                column(mc, world, view, pos, x, z, blockX, blockY, blockZ, MathHelper.floor(camY), radius, ticks, partialTicks, strength);
            }
        }
        if (drawing != NONE) { tessellator.draw(); }
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        mc.entityRenderer.disableLightmap();
    }

    private void column(Minecraft mc, WorldClient world, Entity view, BlockPos.MutableBlockPos pos, int x, int z, int blockX, int blockY, int blockZ, int eyeY, int radius, int ticks, float partialTicks, float strength) {
        pos.setPos(x, 0, z);
        Biome biome = world.getBiome(pos);
        if (!biome.canRain() && !biome.getEnableSnow()) { return; }
        int ground = world.getPrecipitationHeight(pos).getY();
        int bottom = Math.max(blockY - radius, ground);
        int top = Math.max(blockY + radius, ground);
        if (bottom == top) { return; }
        int lightY = Math.max(ground, eyeY);
        int seed = x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761;
        random.setSeed(seed);
        pos.setPos(x, bottom, z);
        boolean rain = world.getBiomeProvider().getTemperatureAtHeight(biome.getTemperature(pos), ground) >= 0.15F;
        begin(mc, rain ? RAINING : SNOWING);
        int index = (z - blockZ + 16) * 32 + x - blockX + 16;
        double halfX = sizeX[index] * 0.5D;
        double halfZ = sizeZ[index] * 0.5D;
        double fromX = x + 0.5F - view.posX;
        double fromZ = z + 0.5F - view.posZ;
        float distance = MathHelper.sqrt(fromX * fromX + fromZ * fromZ) / radius;
        pos.setPos(x, lightY, z);
        int light = world.getCombinedLight(pos, 0);
        double u = 0.0D;
        double scroll;
        float alpha;
        float r;
        float g;
        float b;
        if (rain) {
            scroll = -((double) (ticks + x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 31) + partialTicks) / 32.0D * (3.0D + random.nextDouble());
            alpha = ((1.0F - distance * distance) * 0.5F + 0.5F) * strength;
            r = red;
            g = green;
            b = blue;
        }
        else {
            float time = ticks + partialTicks;
            u = random.nextDouble() + time * 0.01D * (float) random.nextGaussian();
            scroll = -((ticks & 511) + partialTicks) / 512.0F + random.nextDouble() + time * (float) random.nextGaussian() * 0.001D;
            alpha = ((1.0F - distance * distance) * 0.3F + 0.5F) * strength;
            light = (light * 3 + 15728880) / 4;
            r = snowRed;
            g = snowGreen;
            b = snowBlue;
        }
        if (upward) { scroll = -scroll; }
        int sky = light >> 16 & 65535;
        int block = light & 65535;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        double shiftTop = top - ground;
        double shiftBottom = bottom - ground;
        buffer.pos(x - halfX + 0.5D + slantX * shiftTop, top, z - halfZ + 0.5D + slantZ * shiftTop).tex(u, bottom * 0.25D + scroll).color(r, g, b, alpha).lightmap(sky, block).endVertex();
        buffer.pos(x + halfX + 0.5D + slantX * shiftTop, top, z + halfZ + 0.5D + slantZ * shiftTop).tex(u + 1.0D, bottom * 0.25D + scroll).color(r, g, b, alpha).lightmap(sky, block).endVertex();
        buffer.pos(x + halfX + 0.5D + slantX * shiftBottom, bottom, z + halfZ + 0.5D + slantZ * shiftBottom).tex(u + 1.0D, top * 0.25D + scroll).color(r, g, b, alpha).lightmap(sky, block).endVertex();
        buffer.pos(x - halfX + 0.5D + slantX * shiftBottom, bottom, z - halfZ + 0.5D + slantZ * shiftBottom).tex(u, top * 0.25D + scroll).color(r, g, b, alpha).lightmap(sky, block).endVertex();
    }

    private void begin(Minecraft mc, int kind) {
        if (drawing == kind) { return; }
        if (drawing != NONE) { Tessellator.getInstance().draw(); }
        drawing = kind;
        mc.getTextureManager().bindTexture(kind == RAINING ? RAIN : SNOW);
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
}
