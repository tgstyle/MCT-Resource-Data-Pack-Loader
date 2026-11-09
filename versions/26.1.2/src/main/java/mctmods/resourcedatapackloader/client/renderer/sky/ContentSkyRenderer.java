package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyFlip;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyPoseDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ContentSkyRenderer {
    private static final Identifier[] MOONS = Stream.of("full_moon", "waning_gibbous", "third_quarter", "waning_crescent", "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous").map(name -> Identifier.withDefaultNamespace("textures/environment/celestial/moon/" + name + ".png")).toArray(Identifier[]::new);
    private static final Map<DimensionTraitsDef.Sky, ContentSkyRenderer> RENDERERS = new IdentityHashMap<>();
    private static final int PHASES = 8;
    private static final float[] UNTINTED = {1.0F, 1.0F, 1.0F};
    private static final GpuBuffer[] QUADS = new GpuBuffer[SkyFlip.values().length];
    private static final GpuBuffer[] PHASE_QUADS = new GpuBuffer[SkyFlip.values().length];
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    private final DimensionTraitsDef.Sky sky;
    private final ContentStars stars;
    @Nullable private final Identifier moonTexture;
    private final float moonSize;
    private final float moonTilt;
    private final SkyPoseDef moonPose;
    private final float[] offsets;

    private ContentSkyRenderer(DimensionTraitsDef.Sky sky, @Nullable DimensionTraitsDef.Moon moon) {
        this.sky = sky;
        this.moonTexture = moon == null ? null : moon.texture();
        this.moonSize = moon == null ? DimensionTraitsDef.VANILLA_MOON : moon.size();
        this.moonTilt = moon == null ? 0.0F : moon.tilt();
        this.moonPose = moon == null ? SkyPoseDef.NONE : moon.pose();
        SkyOccluder[] bodies = occluders();
        this.offsets = new float[bodies.length];
        this.stars = new ContentStars(sky.starCount(), sky.starSize(), bodies);
    }

    @Nullable public static ContentSkyRenderer of(@Nullable ClientLevel level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        DimensionTraitsDef.Sky sky = def == null ? null : def.traits().sky();
        return sky == null ? null : RENDERERS.computeIfAbsent(sky, held -> new ContentSkyRenderer(held, def.traits().moon()));
    }

    private SkyOccluder[] occluders() {
        List<SkyOccluder> bodies = new ArrayList<>();
        if (sky.sunSize() > 0.0F) { bodies.add(new SkyOccluder((float) Math.toRadians(sky.sunTilt()), sky.sunSize(), sky.sunPose(), false, false, new Identifier[] {sky.sunTexture()}, 1, 1)); }
        if (sky.bodies() == null) {
            if (moonSize > 0.0F) { bodies.add(moonTexture == null ? new SkyOccluder((float) Math.toRadians(moonTilt), moonSize, moonPose, true, false, MOONS, 1, 1) : new SkyOccluder((float) Math.toRadians(moonTilt), moonSize, moonPose, true, false, new Identifier[] {moonTexture}, 4, 2)); }
        }
        else {
            for (DimensionTraitsDef.Body body : sky.bodies()) { bodies.add(new SkyOccluder((float) Math.toRadians(body.tilt()), body.size(), body.pose(), false, !body.followsTime(), new Identifier[] {body.texture()}, 1, 1).gated(body.gate())); }
        }
        return bodies.toArray(new SkyOccluder[0]);
    }

    private float[] angles(float sunAngle, float moonAngle, float starAngle) {
        int at = 0;
        if (sky.sunSize() > 0.0F) { offsets[at++] = quantized(sunAngle - starAngle); }
        if (sky.bodies() == null) {
            if (moonSize > 0.0F) { offsets[at] = quantized(moonAngle - starAngle); }
        }
        else {
            for (DimensionTraitsDef.Body body : sky.bodies()) { offsets[at++] = body.followsTime() ? quantized(sunAngle - starAngle + (float) Math.toRadians(body.angle())) : quantized((float) Math.toRadians(body.angle()) - Math.round(starAngle * 1.0E3F) / 1.0E3F); }
        }
        return offsets;
    }

    private static float quantized(float angle) { return Math.round(Mth.positiveModulo(angle, Mth.TWO_PI) * 1.0E4F) / 1.0E4F; }

    public void render(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, int phase, float rainBrightness, float starBrightness, Consumer<PoseStack> moon) {
        poseStack.pushPose();
        double ticks = ContentSkyShapes.ticks();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(sunAngle));
        poseStack.mulPose(Axis.ZP.rotationDegrees(sky.sunTilt()));
        poseStack.mulPose(Axis.YP.rotationDegrees(sky.sunPose().degrees(ticks)));
        if (sky.sunSize() > 0.0F) { sun(poseStack, rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level)); }
        poseStack.popPose();
        if (sky.bodies() == null) {
            if (moonSize > 0.0F) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotation(moonAngle));
                poseStack.mulPose(Axis.ZP.rotationDegrees(moonTilt));
                poseStack.mulPose(Axis.YP.rotationDegrees(moonPose.degrees(ticks)));
                if (moonTexture != null) { phase(poseStack, moonTexture, phase, rainBrightness); }
                else if (moonPose.flip() != SkyFlip.NONE) { quad(poseStack, MOONS[Math.floorMod(phase, PHASES)], moonSize, rainBrightness * ContentFogSampler.moon(Minecraft.getInstance().level), quads(moonPose.flip().inverse())); }
                else {
                    poseStack.scale(moonSize / DimensionTraitsDef.VANILLA_MOON, 1.0F, moonSize / DimensionTraitsDef.VANILLA_MOON);
                    moon.accept(poseStack);
                }
                poseStack.popPose();
            }
        }
        else {
            for (DimensionTraitsDef.Body body : sky.bodies()) {
                if (ContentSkyGates.hides(body.gate())) { continue; }
                poseStack.pushPose();
                if (body.followsTime()) { poseStack.mulPose(Axis.XP.rotation(sunAngle)); }
                poseStack.mulPose(Axis.XP.rotationDegrees(body.angle()));
                poseStack.mulPose(Axis.ZP.rotationDegrees(body.tilt()));
                poseStack.mulPose(Axis.YP.rotationDegrees(body.pose().degrees(ticks)));
                quad(poseStack, body.texture(), body.size(), rainBrightness * ContentFogSampler.moon(Minecraft.getInstance().level), quads(body.pose().flip()));
                poseStack.popPose();
            }
        }
        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation(starAngle));
            stars.hide(angles(sunAngle, moonAngle, starAngle));
            stars.draw(poseStack, starBrightness);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void sun(PoseStack poseStack, float rainBrightness) {
        GpuBuffer vertices = quads(sky.sunPose().flip());
        float[] tint = ContentSkyExtras.underSun(poseStack, sky.sunSize(), rainBrightness, Minecraft.getInstance().getTextureManager().getTexture(sky.sunTexture()), vertices);
        draw(poseStack, sky.sunTexture(), sky.sunSize(), tint == null ? UNTINTED : tint, rainBrightness, vertices, 0);
    }

    private void quad(PoseStack poseStack, Identifier texture, float size, float rainBrightness, GpuBuffer vertices) { draw(poseStack, texture, size, UNTINTED, rainBrightness, vertices, 0); }

    private void phase(PoseStack poseStack, Identifier texture, int phase, float rainBrightness) { draw(poseStack, texture, moonSize, UNTINTED, rainBrightness, phaseQuads(moonPose.flip()), Math.floorMod(phase, PHASES) * 4); }

    @SuppressWarnings("resource") private static GpuBuffer quads(SkyFlip flip) {
        if (QUADS[flip.ordinal()] == null) { QUADS[flip.ordinal()] = corners(flip); }
        return QUADS[flip.ordinal()];
    }

    @SuppressWarnings("resource") private static GpuBuffer phaseQuads(SkyFlip flip) {
        if (PHASE_QUADS[flip.ordinal()] == null) { PHASE_QUADS[flip.ordinal()] = phases(flip); }
        return PHASE_QUADS[flip.ordinal()];
    }

    private void draw(PoseStack poseStack, Identifier texture, float size, float[] tint, float rainBrightness, GpuBuffer vertices, int baseVertex) {
        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (color == null) { return; }
        AbstractTexture image = Minecraft.getInstance().getTextureManager().getTexture(texture);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        modelView.translate(0.0F, ContentSkyShapes.SIZE, 0.0F);
        modelView.scale(size, 1.0F, size);
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(tint[0], tint[1], tint[2], rainBrightness), new Vector3f(), new Matrix4f());
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL sky body", color, OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(RenderPipelines.CELESTIAL);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler0", image.getTextureView(), image.getSampler());
            pass.setVertexBuffer(0, vertices);
            pass.setIndexBuffer(quadIndices.getBuffer(6), quadIndices.type());
            pass.drawIndexed(baseVertex, 0, 6, 1);
        }
        modelView.popMatrix();
    }

    private static GpuBuffer phases(SkyFlip flip) {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(PHASES * 4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (int phase = 0; phase < PHASES; phase++) {
                float u0 = (phase % 4) * 0.25F;
                float v0 = phase < 4 ? 0.0F : 0.5F;
                float left = flip.horizontal() ? u0 : u0 + 0.25F;
                float right = flip.horizontal() ? u0 + 0.25F : u0;
                float top = flip.vertical() ? v0 : v0 + 0.5F;
                float bottom = flip.vertical() ? v0 + 0.5F : v0;
                buffer.addVertex(-1.0F, 0.0F, -1.0F).setUv(left, top);
                buffer.addVertex(1.0F, 0.0F, -1.0F).setUv(right, top);
                buffer.addVertex(1.0F, 0.0F, 1.0F).setUv(right, bottom);
                buffer.addVertex(-1.0F, 0.0F, 1.0F).setUv(left, bottom);
            }
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL moon phases", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }

    private static GpuBuffer corners(SkyFlip flip) {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            float left = flip.horizontal() ? 1.0F : 0.0F;
            float right = 1.0F - left;
            float top = flip.vertical() ? 1.0F : 0.0F;
            float bottom = 1.0F - top;
            buffer.addVertex(-1.0F, 0.0F, -1.0F).setUv(left, top);
            buffer.addVertex(1.0F, 0.0F, -1.0F).setUv(right, top);
            buffer.addVertex(1.0F, 0.0F, 1.0F).setUv(right, bottom);
            buffer.addVertex(-1.0F, 0.0F, 1.0F).setUv(left, bottom);
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL sky body", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }
}
