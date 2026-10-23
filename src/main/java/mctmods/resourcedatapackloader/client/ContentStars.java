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
    private final boolean dynamic;
    @Nullable private VertexBuffer[] groups;
    @Nullable private VertexBuffer[] movingGroups;
    private float[][] stars;
    private boolean[] moving;
    private int built;
    private int step = Integer.MIN_VALUE;

    public ContentStars(int count, float size, SkyOccluder[] occluders) {
        this.count = count;
        this.size = size;
        this.occluders = occluders;
        boolean fixed = false;
        for (SkyOccluder body : occluders) { fixed |= body.fixed(); }
        dynamic = fixed;
    }

    public static boolean keepsVanilla(@Nullable ClientLevel level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look == null || !look.tintsStars();
    }

    public void draw(ClientLevel level, float brightness, Matrix4f pose, Matrix4f projection, float partialTick, float turn) {
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
        VertexBuffer[] swept = movingGroups;
        if (swept != null) {
            int now = Math.round(turn * 1000.0F);
            if (step != now) { retime(swept, now); }
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
            if (swept != null) {
                VertexBuffer sweptBuffer = swept[group];
                sweptBuffer.bind();
                sweptBuffer.drawWithShader(pose, projection, shader);
            }
        }
        VertexBuffer.unbind();
    }

    private void release() {
        if (groups == null) { return; }
        for (VertexBuffer buffer : groups) {
            if (buffer != null) { buffer.close(); }
        }
        groups = null;
        if (movingGroups != null) {
            for (VertexBuffer buffer : movingGroups) { buffer.close(); }
            movingGroups = null;
        }
        step = Integer.MIN_VALUE;
    }

    private void retime(VertexBuffer[] swept, int now) {
        step = now;
        for (SkyOccluder body : occluders) {
            if (body.fixed()) { body.turn(now / 1000.0F); }
        }
        RenderSystem.setShader(GameRenderer::getPositionShader);
        for (int group = 0; group < GROUPS; ++group) {
            swept[group].bind();
            swept[group].upload(mesh(group, true));
        }
        VertexBuffer.unbind();
    }

    private VertexBuffer[] build() {
        stars = new float[count][];
        moving = new boolean[count];
        RandomSource random = RandomSource.create(10842L);
        float spread = size * 2.0F / 3.0F;
        for (int star = 0; star < count; ++star) {
            float[] quad = star(random, spread);
            if (quad == null || hidden(quad, false)) { continue; }
            stars[star] = quad;
            moving[star] = sweeps(quad);
        }
        if (dynamic) { movingGroups = IntStream.range(0, GROUPS).mapToObj(group -> new VertexBuffer(VertexBuffer.Usage.STATIC)).toArray(VertexBuffer[]::new); }
        RenderSystem.setShader(GameRenderer::getPositionShader);
        return IntStream.range(0, GROUPS).mapToObj(group -> ContentSkyRenderer.upload(mesh(group, false))).toArray(VertexBuffer[]::new);
    }

    private BufferBuilder.RenderedBuffer mesh(int group, boolean sweeping) {
        BufferBuilder buffer = SkyMesh.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for (int star = group; star < count; star += GROUPS) {
            float[] quad = stars[star];
            if (quad == null || moving[star] != sweeping || sweeping && hidden(quad, true)) { continue; }
            for (int corner = 0; corner < 12; corner += 3) { buffer.vertex(quad[corner], quad[corner + 1], quad[corner + 2]).endVertex(); }
        }
        return buffer.end();
    }

    private boolean hidden(float[] star, boolean fixed) {
        for (SkyOccluder body : occluders) {
            if (body.fixed() == fixed && body.covers(star[12] * SIZE, star[13] * SIZE, star[14] * SIZE, star[15])) { return true; }
        }
        return false;
    }

    private boolean sweeps(float[] star) {
        for (SkyOccluder body : occluders) {
            if (body.fixed() && body.sweeps(star[12], star[15])) { return true; }
        }
        return false;
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
        float[] quad = new float[16];
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
        quad[12] = (float) x;
        quad[13] = (float) y;
        quad[14] = (float) z;
        quad[15] = (float) (scale * 1.5D);
        return quad;
    }
}
