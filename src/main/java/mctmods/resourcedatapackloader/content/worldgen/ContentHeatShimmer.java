package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IShaderGroup;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import java.io.IOException;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentHeatShimmer {
    private static final ResourceLocation SHADER = new ResourceLocation("resourcedatapackloader", "shaders/post/heat.json");
    private static final String DEPTH = "DepthSampler";
    private static final float EASE = 0.05F;
    private static final float GONE = 0.002F;
    private static final float NIGHT = 0.2F;
    private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);
    private static float strength;
    @Nullable private static SkyLookDef.Heat heat;
    @Nullable private static ShaderGroup group;
    private static boolean failed;
    private static int width;
    private static int height;
    private static int depthTexture = -1;
    private static int depthWidth;
    private static int depthHeight;

    private ContentHeatShimmer() {}

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        SkyLookDef look = ContentFogSampler.look(mc.world);
        float target = target(mc, look);
        strength = look == null || look.heat == null ? 0.0F : strength + (target - strength) * EASE;
        if (target <= 0.0F && strength < GONE) { strength = 0.0F; }
        if (strength <= 0.0F) {
            close();
            return;
        }
        heat = look.heat;
        ShaderGroup own = open(mc);
        if (own != null) { set(own, "Strength", strength); }
    }

    @SubscribeEvent public static void onRenderLast(RenderWorldLastEvent event) {
        ShaderGroup own = group;
        SkyLookDef.Heat look = heat;
        if (own == null || look == null) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.displayWidth != width || mc.displayHeight != height) {
            width = mc.displayWidth;
            height = mc.displayHeight;
            own.createBindFramebuffers(width, height);
        }
        set(own, "Mode", look.followsWorld ? 1.0F : 0.0F);
        if (look.followsWorld) { followWorld(own, mc, look); }
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        own.render(event.getPartialTicks());
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableDepth();
    }

    @Nullable private static ShaderGroup open(Minecraft mc) {
        if (group != null || failed || !OpenGlHelper.shadersSupported) { return group; }
        try {
            group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), SHADER);
            width = 0;
            height = 0;
        }
        catch (IOException | JsonSyntaxException e) {
            failed = true;
            ContentLog.LOGGER.error("The heat shimmer shader {} could not be loaded ({}), so no shimmer is drawn", SHADER, e.getMessage());
        }
        return group;
    }

    private static void followWorld(ShaderGroup own, Minecraft mc, SkyLookDef.Heat look) {
        Framebuffer main = mc.getFramebuffer();
        copyDepth(main.framebufferTextureWidth, main.framebufferTextureHeight);
        GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        set(own, "StartDistance", look.startDistance);
        set(own, "EndDistance", Math.max(mc.gameSettings.renderDistanceChunks * 16.0F, look.startDistance + 1.0F));
        set(own, "DepthBias", PROJECTION.get(10) - 1.0F);
        set(own, "DepthLinear", PROJECTION.get(14));
        for (Shader shader : ((IShaderGroup) own).getListShaders()) { shader.getShaderManager().addSamplerTexture(DEPTH, depthTexture); }
    }

    private static void copyDepth(int w, int h) {
        if (depthTexture < 0) { depthTexture = TextureUtil.glGenTextures(); }
        GlStateManager.bindTexture(depthTexture);
        if (w != depthWidth || h != depthHeight) {
            depthWidth = w;
            depthHeight = h;
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, w, h, 0);
        }
        else { GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, w, h); }
        GlStateManager.bindTexture(0);
    }

    private static void set(ShaderGroup own, String name, float value) {
        for (Shader shader : ((IShaderGroup) own).getListShaders()) {
            ShaderUniform uniform = shader.getShaderManager().getShaderUniform(name);
            if (uniform != null) { uniform.set(value); }
        }
    }

    private static void close() {
        heat = null;
        if (depthTexture >= 0) {
            TextureUtil.deleteTexture(depthTexture);
            depthTexture = -1;
            depthWidth = 0;
            depthHeight = 0;
        }
        if (group == null) { return; }
        group.deleteShaderGroup();
        group = null;
    }

    private static float target(Minecraft mc, @Nullable SkyLookDef look) {
        Entity camera = mc.getRenderViewEntity();
        if (look == null || look.heat == null || camera == null || mc.world == null || camera.isInWater()) { return 0.0F; }
        if (mc.world.getBiome(new BlockPos(camera)).getDefaultTemperature() < look.heat.minTemperature) { return 0.0F; }
        if (!look.heat.dayOnly) { return look.heat.strength; }
        return look.heat.strength * Math.max(0.0F, (mc.world.getSunBrightness(1.0F) - NIGHT) / (1.0F - NIGHT));
    }
}
