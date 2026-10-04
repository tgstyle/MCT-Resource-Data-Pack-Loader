package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

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
    public static final ContentStars VANILLA = new ContentStars(1500, 0.15F);
    private static final int GROUPS = 8;
    private static final float[] SPEED = {2.1F, 3.3F, 1.7F, 2.9F, 3.7F, 1.3F, 2.5F, 3.1F};
    private static final float PHASE = 2.4F;
    private final int count;
    private final float size;
    private int lists = -1;

    public ContentStars(int count, float size) {
        this.count = count;
        this.size = size;
    }

    public static boolean replacesVanilla(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        return look != null && look.tintsStars();
    }

    public void draw(World world, float brightness, float partialTicks) {
        if (lists < 0) { build(); }
        SkyLookDef look = ContentFogSampler.look(world);
        int color = look == null || look.starColor == SkyLookDef.UNSET ? 0xFFFFFF : look.starColor;
        float twinkle = look == null ? 0.0F : look.starTwinkle;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float time = (world.getTotalWorldTime() % 24000L + partialTicks) / 20.0F;
        for (int group = 0; group < GROUPS; ++group) {
            float shown = brightness * (1.0F - twinkle * (0.5F + 0.5F * MathHelper.sin(time * SPEED[group] + group * PHASE)));
            GlStateManager.color(red * shown, green * shown, blue * shown, shown);
            GlStateManager.callList(lists + group);
        }
    }

    private void build() {
        lists = GLAllocation.generateDisplayLists(GROUPS);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Random random = new Random(10842L);
        float spread = size * 2.0F / 3.0F;
        double[][] corners = new double[count][];
        for (int star = 0; star < count; ++star) { corners[star] = star(random, spread); }
        for (int group = 0; group < GROUPS; ++group) {
            GlStateManager.glNewList(lists + group, GL11.GL_COMPILE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            for (int star = group; star < count; star += GROUPS) {
                double[] quad = corners[star];
                if (quad == null) { continue; }
                for (int corner = 0; corner < 12; corner += 3) { buffer.pos(quad[corner], quad[corner + 1], quad[corner + 2]).endVertex(); }
            }
            tessellator.draw();
            GlStateManager.glEndList();
        }
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
        double[] quad = new double[12];
        for (int corner = 0; corner < 4; ++corner) {
            double a = ((corner & 2) - 1) * scale;
            double b = ((corner + 1 & 2) - 1) * scale;
            double c = a * spinCos - b * spinSin;
            double d = b * spinCos + a * spinSin;
            double e = c * pitchSin;
            double f = -c * pitchCos;
            quad[corner * 3] = x * 100.0D + f * yawSin - d * yawCos;
            quad[corner * 3 + 1] = y * 100.0D + e;
            quad[corner * 3 + 2] = z * 100.0D + d * yawSin + f * yawCos;
        }
        return quad;
    }
}
