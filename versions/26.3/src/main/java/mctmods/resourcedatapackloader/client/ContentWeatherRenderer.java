package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.util.RainSplash;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.CustomWeatherEffectRenderer;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentWeatherRenderer implements CustomWeatherEffectRenderer {
    private static final Identifier RAIN = Identifier.withDefaultNamespace("textures/environment/rain.png");
    private static final Identifier SNOW = Identifier.withDefaultNamespace("textures/environment/snow.png");
    private final RainDef rain;
    private final ContentWeatherShape shape;
    @Nullable private GpuBuffer vertexBuffer;
    private boolean ready;

    public ContentWeatherRenderer(RainDef rain) {
        this.rain = rain;
        this.shape = new ContentWeatherShape(rain);
    }

    @Override public void prepare(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull Vec3 camera) {
        Minecraft mc = Minecraft.getInstance();
        int columnCount = weather.rainColumns.size() + weather.snowColumns.size();
        ready = columnCount != 0 && mc.level != null && !ContentDimensionEffects.cameraAboveCeiling();
        if (!ready) { return; }
        int indexCount;
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(columnCount * DefaultVertexFormat.PARTICLE.getVertexSize() * 4)) {
            BufferBuilder builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.PARTICLE);
            shape.build(builder, mc.level, weather.rainColumns, camera, false, weather.radius, weather.intensity);
            shape.build(builder, mc.level, weather.snowColumns, camera, true, weather.radius, weather.intensity);
            try (MeshData mesh = builder.buildOrThrow()) {
                upload(mesh.vertexBuffer());
                indexCount = mesh.drawState().indexCount();
            }
        }
        RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS).requestIndexCount(indexCount);
    }

    @Override public boolean renderSnowAndRain(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull Vec3 camera, @Nonnull RenderPass pass) {
        render(weather, pass, RenderPipelines.WEATHER);
        return true;
    }

    @Override public boolean renderSnowAndRainOit(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull Vec3 camera, @Nonnull OitStage stage, @Nonnull RenderPass pass) {
        render(weather, pass, RenderPipelines.OIT_WEATHER.getPipeline(stage));
        return true;
    }

    @Override public boolean tickRain(@Nonnull ClientLevel level, long ticks, @Nonnull Camera camera) {
        RainSplash.tick(level, rain, ticks, camera);
        return true;
    }

    private void render(WeatherRenderState weather, RenderPass pass, RenderPipeline pipeline) {
        if (!ready || vertexBuffer == null) { return; }
        Minecraft mc = Minecraft.getInstance();
        int rainCount = weather.rainColumns.size();
        pass.pushDebugGroup(() -> "RDPL Weather");
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
        RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
        pass.setPipeline(RenderSystem.getCompiledPipeline(pipeline));
        RenderSystem.bindDefaultUniforms(pass);
        pass.setUniform("DynamicTransforms", transforms);
        pass.setUniform("Sampler2", mc.gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
        pass.setIndexBuffer(indices.getBuffer(), indices.type());
        pass.setVertexBuffer(0, vertexBuffer.slice());
        draw(pass, mc.getTextureManager().getTexture(RAIN), 0, rainCount);
        draw(pass, mc.getTextureManager().getTexture(SNOW), rainCount, weather.snowColumns.size());
        pass.popDebugGroup();
    }

    private void upload(ByteBuffer buffer) {
        GpuDevice device = RenderSystem.getDevice();
        if (vertexBuffer == null || vertexBuffer.size() < buffer.remaining()) {
            if (vertexBuffer != null) { vertexBuffer.close(); }
            vertexBuffer = device.createBuffer(() -> "RDPL Weather Vertex Buffer", 40, buffer.remaining());
        }
        device.createCommandEncoder().writeToBuffer(vertexBuffer.slice(), buffer);
    }

    private static void draw(RenderPass pass, AbstractTexture texture, int start, int count) {
        pass.setUniform("Sampler0", texture.getTextureView(), texture.getSampler());
        pass.drawIndexed(count * 6, 1, start * 6, 0, 0);
    }
}
