package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.Colors;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import java.util.Random;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentStars {
    public static final ContentStars VANILLA = new ContentStars(1500, 0.15F, new SkyOccluder[0]);
    private static final double RADIUS = 100.0D;
    private static final int GROUPS = 8;
    private static final float[] SPEED = {2.1F, 3.3F, 1.7F, 2.9F, 3.7F, 1.3F, 2.5F, 3.1F};
    private static final float PHASE = 2.4F;
    private final int count;
    private final float size;
    private final SkyOccluder[] occluders;
    private final boolean dynamic;
    private int lists = -1;
    private int movingLists = -1;
    private int built;
    private int step = Integer.MIN_VALUE;
    private double[][] stars;
    private boolean[] moving;

    public ContentStars(int count, float size, SkyOccluder[] occluders) {
        this.count = count;
        this.size = size;
        this.occluders = occluders;
        boolean fixed = false;
        for (SkyOccluder body : occluders) { fixed |= body.fixed(); }
        dynamic = fixed;
    }

    public static boolean keepsVanilla(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        return look == null || !look.tintsStars();
    }

    public void draw(World world, float brightness, float partialTicks, float turn) {
        if (lists < 0 || built != SkyOccluder.current()) {
            release();
            build();
        }
        int now = Math.round(turn * 1000.0F);
        if (dynamic && step != now) { retime(now); }
        SkyLookDef look = ContentFogSampler.look(world);
        int color = look == null || look.starColor == SkyLookDef.UNSET ? 0xFFFFFF : look.starColor;
        float twinkle = look == null ? 0.0F : look.starTwinkle;
        float red = Colors.red(color) / 255.0F;
        float green = Colors.green(color) / 255.0F;
        float blue = Colors.blue(color) / 255.0F;
        float time = (world.getTotalWorldTime() % DimensionTraitsDef.VANILLA_DAY + partialTicks) / 20.0F;
        for (int group = 0; group < GROUPS; ++group) {
            float shown = brightness * (1.0F - twinkle * (0.5F + 0.5F * MathHelper.sin(time * SPEED[group] + group * PHASE)));
            GlStateManager.color(red * shown, green * shown, blue * shown, shown);
            GlStateManager.callList(lists + group);
            if (dynamic) { GlStateManager.callList(movingLists + group); }
        }
    }

    void release() {
        if (lists < 0) { return; }
        GLAllocation.deleteDisplayLists(lists, GROUPS);
        lists = -1;
        if (movingLists >= 0) {
            GLAllocation.deleteDisplayLists(movingLists, GROUPS);
            movingLists = -1;
        }
        step = Integer.MIN_VALUE;
    }

    private void retime(int now) {
        step = now;
        for (SkyOccluder body : occluders) {
            if (body.fixed()) { body.turn(now / 1000.0F); }
        }
        compile(movingLists, true);
    }

    private void build() {
        built = SkyOccluder.current();
        Random random = new Random(10842L);
        float spread = size * 2.0F / 3.0F;
        stars = new double[count][];
        moving = new boolean[count];
        for (int star = 0; star < count; ++star) {
            double[] quad = star(random, spread);
            if (quad == null || hidden(quad, false)) { continue; }
            stars[star] = quad;
            moving[star] = sweeps(quad);
        }
        lists = GLAllocation.generateDisplayLists(GROUPS);
        compile(lists, false);
        if (dynamic) { movingLists = GLAllocation.generateDisplayLists(GROUPS); }
    }

    private void compile(int base, boolean sweeping) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (int group = 0; group < GROUPS; ++group) {
            GlStateManager.glNewList(base + group, GL11.GL_COMPILE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            for (int star = group; star < count; star += GROUPS) {
                double[] quad = stars[star];
                if (quad == null || moving[star] != sweeping || sweeping && hidden(quad, true)) { continue; }
                for (int corner = 0; corner < 12; corner += 3) { buffer.pos(quad[corner], quad[corner + 1], quad[corner + 2]).endVertex(); }
            }
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private boolean hidden(double[] star, boolean fixed) {
        for (SkyOccluder body : occluders) {
            if (body.fixed() == fixed && body.covers(star[12] * RADIUS, star[13] * RADIUS, star[14] * RADIUS, star[15])) { return true; }
        }
        return false;
    }

    private boolean sweeps(double[] star) {
        for (SkyOccluder body : occluders) {
            if (body.fixed() && body.sweeps(star[12], star[15])) { return true; }
        }
        return false;
    }

    @Nullable private double[] star(Random random, float spread) {
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
        double[] quad = new double[16];
        for (int corner = 0; corner < 4; ++corner) {
            double a = ((corner & 2) - 1) * scale;
            double b = ((corner + 1 & 2) - 1) * scale;
            double c = a * spinCos - b * spinSin;
            double d = b * spinCos + a * spinSin;
            double e = c * pitchSin;
            double f = -c * pitchCos;
            quad[corner * 3] = x * RADIUS + f * yawSin - d * yawCos;
            quad[corner * 3 + 1] = y * RADIUS + e;
            quad[corner * 3 + 2] = z * RADIUS + d * yawSin + f * yawCos;
        }
        quad[12] = x;
        quad[13] = y;
        quad[14] = z;
        quad[15] = scale * 1.5D;
        return quad;
    }
}
