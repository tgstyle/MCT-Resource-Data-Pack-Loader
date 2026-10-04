package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.util.RainSplash;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.CustomWeatherEffectRenderer;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentWeatherRenderer implements CustomWeatherEffectRenderer {
    private static final Identifier RAIN = Identifier.withDefaultNamespace("textures/environment/rain.png");
    private static final Identifier SNOW = Identifier.withDefaultNamespace("textures/environment/snow.png");
    private final RainDef rain;
    private final ContentWeatherShape shape;
    @Nullable private GpuBuffer vertexBuffer;

    public ContentWeatherRenderer(RainDef rain) {
        this.rain = rain;
        this.shape = new ContentWeatherShape(rain);
    }

    @Override public boolean renderSnowAndRain(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull Vec3 camera) {
        Minecraft mc = Minecraft.getInstance();
        int rainCount = weather.rainColumns.size();
        int columnCount = rainCount + weather.snowColumns.size();
        RenderTarget target = OutputTarget.WEATHER_TARGET.getRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (columnCount == 0 || mc.level == null || color == null) { return true; }
        RenderPipeline pipeline = mc.gameRenderer.gameRenderState().useShaderTransparency() ? RenderPipelines.WEATHER_DEPTH_WRITE : RenderPipelines.WEATHER_NO_DEPTH_WRITE;
        GpuBuffer indexBuffer;
        IndexType indexType;
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(columnCount * DefaultVertexFormat.PARTICLE.getVertexSize() * 4)) {
            BufferBuilder builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.PARTICLE);
            shape.build(builder, mc.level, weather.rainColumns, camera, false, weather.radius, weather.intensity);
            shape.build(builder, mc.level, weather.snowColumns, camera, true, weather.radius, weather.intensity);
            try (MeshData mesh = builder.buildOrThrow()) {
                upload(mesh.vertexBuffer());
                RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(mesh.drawState().primitiveTopology());
                indexBuffer = indices.getBuffer(mesh.drawState().indexCount());
                indexType = indices.type();
            }
        }
        if (vertexBuffer == null) { return true; }
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL Weather", color, Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler2", mc.gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            pass.setIndexBuffer(indexBuffer, indexType);
            pass.setVertexBuffer(0, vertexBuffer.slice());
            draw(pass, mc.getTextureManager().getTexture(RAIN), 0, rainCount);
            draw(pass, mc.getTextureManager().getTexture(SNOW), rainCount, weather.snowColumns.size());
        }
        return true;
    }

    @Override public boolean tickRain(@Nonnull ClientLevel level, long ticks, @Nonnull Camera camera) {
        RainSplash.tick(level, rain, ticks, camera);
        return true;
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
        pass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
        pass.drawIndexed(count * 6, 1, start * 6, 0, 0);
    }
}
