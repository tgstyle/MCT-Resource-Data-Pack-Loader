package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.compat.LineClientCompat;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IGameRenderer;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IPostChain;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fc;
import java.util.Arrays;
import javax.annotation.Nullable;

public final class ContentHeatShimmer {
    private static final Identifier EFFECT = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "heat");
    private static final String UNIFORM = "HeatConfig";
    private static final float EASE = 0.05F;
    private static final float GONE = 0.002F;
    private static final float RAIN_SHADE = 0.3125F;
    private static float strength;
    private static float[] written = new float[0];
    @Nullable private static PostChain chain;
    private static boolean failed;

    private ContentHeatShimmer() {}

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        SkyLookDef look = ContentFogSampler.look(mc.level);
        float target = target(mc, look);
        strength = look == null || look.heat() == null ? 0.0F : strength + (target - strength) * EASE;
        if (target <= 0.0F && strength < GONE) { strength = 0.0F; }
    }

    public static void onAfterLevel(RenderLevelStageEvent.AfterLevel event) {
        Minecraft mc = Minecraft.getInstance();
        SkyLookDef look = ContentFogSampler.look(mc.level);
        SkyLookDef.Heat heat = look == null ? null : look.heat();
        if (heat == null) { strength = 0.0F; }
        if (strength <= 0.0F || failed) { return; }
        PostChain own = mc.getShaderManager().getPostChain(EFFECT, LevelTargetBundle.MAIN_TARGETS);
        if (own == null) {
            failed = true;
            ContentLog.LOGGER.error("The heat shimmer effect {} could not be loaded, so no shimmer is drawn", EFFECT);
            return;
        }
        float[] values = values(mc, heat, event.getLevelRenderState().cameraRenderState.projectionMatrix);
        if (own != chain || !Arrays.equals(values, written)) {
            for (PostPass pass : ((IPostChain) own).rdpl$getPasses()) { LineClientCompat.writeUniform(pass, UNIFORM, values); }
            chain = own;
            written = values;
        }
        RenderTarget target = ClientCompat.mainTarget(mc);
        FrameGraphBuilder frame = new FrameGraphBuilder();
        own.addToFrame(frame, target.width, target.height, PostChain.TargetBundle.of(PostChain.MAIN_TARGET_ID, frame.importExternal("main", target)));
        frame.execute(((IGameRenderer) mc.gameRenderer).rdpl$getResourcePool());
    }

    private static float[] values(Minecraft mc, SkyLookDef.Heat heat, Matrix4fc projection) {
        boolean zeroToOne = ClientCompat.depthZeroToOne();
        float scale = zeroToOne ? 1.0F : 2.0F;
        float bias = projection.m22() - (zeroToOne ? 0.0F : 1.0F);
        float linear = projection.m32();
        float sky = linear / (scale + bias) > linear / bias ? 1.0F : 0.0F;
        float end = Math.max(mc.options.getEffectiveRenderDistance() * 16.0F, heat.startDistance() + 1.0F);
        return new float[] {strength, heat.followsWorld() ? 1.0F : 0.0F, heat.startDistance(), end, scale, bias, linear, sky};
    }

    private static float target(Minecraft mc, @Nullable SkyLookDef look) {
        Entity camera = mc.getCameraEntity();
        ClientLevel level = mc.level;
        if (look == null || look.heat() == null || camera == null || level == null || camera.isInWater()) { return 0.0F; }
        SkyLookDef.Heat heat = look.heat();
        if (level.getBiome(camera.blockPosition()).value().getBaseTemperature() < heat.minTemperature()) { return 0.0F; }
        if (!heat.dayOnly()) { return heat.strength(); }
        float sun = Mth.clamp(Mth.cos(level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, camera.position()) * Mth.DEG_TO_RAD) * 2.0F + 0.2F, 0.0F, 1.0F);
        return heat.strength() * sun * (1.0F - level.getRainLevel(1.0F) * RAIN_SHADE) * (1.0F - level.getThunderLevel(1.0F) * RAIN_SHADE);
    }
}
