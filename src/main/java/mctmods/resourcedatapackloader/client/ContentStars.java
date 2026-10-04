package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public final class ContentStars {
    public static final ContentStars VANILLA = new ContentStars(1500, 0.15F, true);
    private static final int GROUPS = 8;
    private static final float[] SPEED = {2.1F, 3.3F, 1.7F, 2.9F, 3.7F, 1.3F, 2.5F, 3.1F};
    private static final float PHASE = 2.4F;
    private final int count;
    private final float size;
    private final boolean vanilla;
    @Nullable private VertexBuffer[] groups;

    public ContentStars(int count, float size) { this(count, size, false); }

    private ContentStars(int count, float size, boolean vanilla) {
        this.count = count;
        this.size = size;
        this.vanilla = vanilla;
    }

    public static boolean replacesVanilla(@Nullable ClientLevel level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look != null && look.tintsStars();
    }

    public void draw(ClientLevel level, float brightness, Matrix4f pose, Matrix4f projection, float partialTick) {
        ShaderInstance shader = GameRenderer.getPositionShader();
        if (shader == null) { return; }
        if (groups == null) { groups = build(); }
        SkyLookDef look = ContentFogSampler.look(level);
        int color = look == null || look.starColor() == SkyLookDef.UNSET ? 0xFFFFFF : look.starColor();
        float twinkle = look == null ? 0.0F : look.starTwinkle();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float time = (level.getGameTime() % 24000L + partialTick) / 20.0F;
        for (int group = 0; group < GROUPS; ++group) {
            if (groups[group] == null) { continue; }
            float shown = brightness * (1.0F - twinkle * (0.5F + 0.5F * Mth.sin(time * SPEED[group] + group * PHASE)));
            RenderSystem.setShaderColor(red * shown, green * shown, blue * shown, shown);
            groups[group].bind();
            groups[group].drawWithShader(pose, projection, shader);
        }
        VertexBuffer.unbind();
    }

    private VertexBuffer[] build() {
        float[][] quads = new float[count][];
        RandomSource random = RandomSource.create(10842L);
        float spread = size * 2.0F / 3.0F;
        for (int star = 0; star < count; ++star) { quads[star] = vanilla ? vanillaStar(random) : star(random, spread); }
        RenderSystem.setShader(GameRenderer::getPositionShader);
        return IntStream.range(0, GROUPS).mapToObj(group -> {
            MeshData mesh = mesh(quads, group);
            return mesh == null ? null : ContentSkyRenderer.upload(mesh);
        }).toArray(VertexBuffer[]::new);
    }

    @Nullable private MeshData mesh(float[][] quads, int group) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for (int star = group; star < count; star += GROUPS) {
            float[] quad = quads[star];
            if (quad == null) { continue; }
            for (int corner = 0; corner < 12; corner += 3) { buffer.addVertex(quad[corner], quad[corner + 1], quad[corner + 2]); }
        }
        return buffer.build();
    }

    @Nullable private static float[] vanillaStar(RandomSource random) {
        float x = random.nextFloat() * 2.0F - 1.0F;
        float y = random.nextFloat() * 2.0F - 1.0F;
        float z = random.nextFloat() * 2.0F - 1.0F;
        float scale = 0.15F + random.nextFloat() * 0.1F;
        float length = Mth.lengthSquared(x, y, z);
        if (length <= 0.010000001F || length >= 1.0F) { return null; }
        Vector3f center = new Vector3f(x, y, z).normalize(100.0F);
        float spin = (float) (random.nextDouble() * (float) Math.PI * 2.0D);
        Quaternionf rotation = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), center).rotateZ(spin);
        float[] quad = new float[12];
        float[][] corners = {{scale, -scale}, {scale, scale}, {-scale, scale}, {-scale, -scale}};
        for (int corner = 0; corner < 4; ++corner) {
            center.add(new Vector3f(corners[corner][0], corners[corner][1], 0.0F).rotate(rotation));
            quad[corner * 3] = center.x;
            quad[corner * 3 + 1] = center.y;
            quad[corner * 3 + 2] = center.z;
        }
        return quad;
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
            quad[corner * 3] = (float) (x * 100.0D + f * yawSin - d * yawCos);
            quad[corner * 3 + 1] = (float) (y * 100.0D + e);
            quad[corner * 3 + 2] = (float) (z * 100.0D + d * yawSin + f * yawCos);
        }
        return quad;
    }
}
