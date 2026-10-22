package mctmods.resourcedatapackloader.client;

import static mctmods.resourcedatapackloader.client.SkyMesh.SIZE;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.Rgb;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.SharedConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public final class ContentStars {
    public static final ContentStars VANILLA = new ContentStars(1500, 0.15F, new SkyOccluder[0]);
    private static final int GROUPS = 8;
    private static final float[] SPEED = {2.1F, 3.3F, 1.7F, 2.9F, 3.7F, 1.3F, 2.5F, 3.1F};
    private static final float PHASE = 2.4F;
    private final int count;
    private final float size;
    private final SkyOccluder[] occluders;
    @Nullable private VertexBuffer[] groups;
    private int built;

    public ContentStars(int count, float size, SkyOccluder[] occluders) {
        this.count = count;
        this.size = size;
        this.occluders = occluders;
    }

    public static boolean keepsVanilla(@Nullable ClientLevel level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look == null || !look.tintsStars();
    }

    public void draw(ClientLevel level, float brightness, Matrix4f pose, Matrix4f projection, float partialTick) {
        ShaderInstance shader = GameRenderer.getPositionShader();
        if (shader == null) { return; }
        if (built != SkyOccluder.current()) {
            release();
            built = SkyOccluder.current();
        }
        VertexBuffer[] meshes = groups;
        if (meshes == null) {
            meshes = build();
            groups = meshes;
        }
        SkyLookDef look = ContentFogSampler.look(level);
        int color = look == null || look.starColor() == SkyLookDef.UNSET ? 0xFFFFFF : look.starColor();
        float twinkle = look == null ? 0.0F : look.starTwinkle();
        float red = Rgb.red(color);
        float green = Rgb.green(color);
        float blue = Rgb.blue(color);
        float time = (level.getGameTime() % Level.TICKS_PER_DAY + partialTick) / SharedConstants.TICKS_PER_SECOND;
        for (int group = 0; group < GROUPS; ++group) {
            float shown = brightness * (1.0F - twinkle * (0.5F + 0.5F * Mth.sin(time * SPEED[group] + group * PHASE)));
            RenderSystem.setShaderColor(red * shown, green * shown, blue * shown, shown);
            VertexBuffer buffer = meshes[group];
            buffer.bind();
            buffer.drawWithShader(pose, projection, shader);
        }
        VertexBuffer.unbind();
    }

    private void release() {
        if (groups == null) { return; }
        for (VertexBuffer buffer : groups) {
            if (buffer != null) { buffer.close(); }
        }
        groups = null;
    }

    private VertexBuffer[] build() {
        float[][] quads = new float[count][];
        RandomSource random = RandomSource.create(10842L);
        float spread = size * 2.0F / 3.0F;
        for (int star = 0; star < count; ++star) { quads[star] = star(random, spread); }
        RenderSystem.setShader(GameRenderer::getPositionShader);
        return IntStream.range(0, GROUPS).mapToObj(group -> ContentSkyRenderer.upload(mesh(quads, group))).toArray(VertexBuffer[]::new);
    }

    private BufferBuilder.RenderedBuffer mesh(float[][] quads, int group) {
        BufferBuilder buffer = SkyMesh.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for (int star = group; star < count; star += GROUPS) {
            float[] quad = quads[star];
            if (quad == null) { continue; }
            for (int corner = 0; corner < 12; corner += 3) { buffer.vertex(quad[corner], quad[corner + 1], quad[corner + 2]).endVertex(); }
        }
        return buffer.end();
    }

    @Nullable private float[] star(RandomSource random, float spread) {
        double x = random.nextFloat() * 2.0F - 1.0F;
        double y = random.nextFloat() * 2.0F - 1.0F;
        double z = random.nextFloat() * 2.0F - 1.0F;
        double scale = size + random.nextFloat() * spread;
        double length = x * x + y * y + z * z;
        if (length >= 1.0D || length <= 0.01D) { return null; }
        length = 1.0D / Math.sqrt(length);
        x *= length;
        y *= length;
        z *= length;
        double yaw = Math.atan2(x, z);
        double yawSin = Math.sin(yaw);
        double yawCos = Math.cos(yaw);
        double pitch = Math.atan2(Math.sqrt(x * x + z * z), y);
        double pitchSin = Math.sin(pitch);
        double pitchCos = Math.cos(pitch);
        double spin = random.nextDouble() * Math.PI * 2.0D;
        double spinSin = Math.sin(spin);
        double spinCos = Math.cos(spin);
        float[] quad = new float[12];
        for (int corner = 0; corner < 4; ++corner) {
            double a = ((corner & 2) - 1) * scale;
            double b = ((corner + 1 & 2) - 1) * scale;
            double c = a * spinCos - b * spinSin;
            double d = b * spinCos + a * spinSin;
            double e = c * pitchSin;
            double f = -c * pitchCos;
            quad[corner * 3] = (float) (x * SIZE + f * yawSin - d * yawCos);
            quad[corner * 3 + 1] = (float) (y * SIZE + e);
            quad[corner * 3 + 2] = (float) (z * SIZE + d * yawSin + f * yawCos);
        }
        for (SkyOccluder body : occluders) {
            if (body.covers(x * SIZE, y * SIZE, z * SIZE, scale * 1.5D)) { return null; }
        }
        return quad;
    }
}
