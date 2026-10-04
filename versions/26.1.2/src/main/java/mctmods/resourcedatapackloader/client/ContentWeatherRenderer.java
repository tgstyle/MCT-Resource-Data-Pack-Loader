package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.util.RainSplash;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.CustomWeatherEffectRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nonnull;

public final class ContentWeatherRenderer implements CustomWeatherEffectRenderer {
    private static final Identifier RAIN = Identifier.withDefaultNamespace("textures/environment/rain.png");
    private static final Identifier SNOW = Identifier.withDefaultNamespace("textures/environment/snow.png");
    private final RainDef rain;
    private final ContentWeatherShape shape;

    public ContentWeatherRenderer(RainDef rain) {
        this.rain = rain;
        this.shape = new ContentWeatherShape(rain);
    }

    @Override public boolean renderSnowAndRain(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull MultiBufferSource buffers, @Nonnull Vec3 camera) {
        Minecraft mc = Minecraft.getInstance();
        int rainCount = weather.rainColumns.size();
        int columnCount = rainCount + weather.snowColumns.size();
        RenderTarget target = OutputTarget.WEATHER_TARGET.getRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (columnCount == 0 || mc.level == null || color == null) { return true; }
        RenderPipeline pipeline = Minecraft.useShaderTransparency() ? RenderPipelines.WEATHER_DEPTH_WRITE : RenderPipelines.WEATHER_NO_DEPTH_WRITE;
        GpuBuffer vertexBuffer;
        GpuBuffer indexBuffer;
        VertexFormat.IndexType indexType;
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(columnCount * DefaultVertexFormat.PARTICLE.getVertexSize() * 4)) {
            BufferBuilder builder = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            shape.build(builder, mc.level, weather.rainColumns, camera, false, weather.radius, weather.intensity);
            shape.build(builder, mc.level, weather.snowColumns, camera, true, weather.radius, weather.intensity);
            try (MeshData mesh = builder.buildOrThrow()) {
                vertexBuffer = pipeline.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());
                RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(mesh.drawState().mode());
                indexBuffer = indices.getBuffer(mesh.drawState().indexCount());
                indexType = indices.type();
            }
        }
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL Weather", color, OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler2", mc.gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            pass.setIndexBuffer(indexBuffer, indexType);
            pass.setVertexBuffer(0, vertexBuffer);
            draw(pass, mc.getTextureManager().getTexture(RAIN), 0, rainCount);
            draw(pass, mc.getTextureManager().getTexture(SNOW), rainCount, weather.snowColumns.size());
        }
        return true;
    }

    @Override public boolean tickRain(@Nonnull ClientLevel level, int ticks, @Nonnull Camera camera) {
        RainSplash.tick(level, rain, ticks, camera);
        return true;
    }

    private static void draw(RenderPass pass, AbstractTexture texture, int start, int count) {
        pass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
        pass.drawIndexed(0, start * 6, count * 6, 1);
    }
}
