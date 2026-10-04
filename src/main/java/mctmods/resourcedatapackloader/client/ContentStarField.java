package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import javax.annotation.Nullable;

public final class ContentStarField {
    public static final int GROUPS = 8;
    public static final int VANILLA_COUNT = 1500;
    public static final float VANILLA_SIZE = 0.15F;
    private static final float[] SPEED = {2.1F, 3.3F, 1.7F, 2.9F, 3.7F, 1.3F, 2.5F, 3.1F};
    private static final float PHASE = 2.4F;

    private ContentStarField() {}

    public static boolean replacesVanilla(@Nullable Level level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look != null && look.tintsStars();
    }

    public static Vector4f[] colors(@Nullable Level level, float brightness) {
        SkyLookDef look = ContentFogSampler.look(level);
        int color = look == null || look.starColor() == SkyLookDef.UNSET ? 0xFFFFFF : look.starColor();
        float twinkle = look == null ? 0.0F : look.starTwinkle();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float time = level == null ? 0.0F : (level.getGameTime() % 24000L + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)) / 20.0F;
        Vector4f[] colors = new Vector4f[GROUPS];
        for (int group = 0; group < GROUPS; ++group) {
            float shown = brightness * (1.0F - twinkle * (0.5F + 0.5F * Mth.sin(time * SPEED[group] + group * PHASE)));
            colors[group] = new Vector4f(red * shown, green * shown, blue * shown, shown);
        }
        return colors;
    }

    public static float[][] groups(int count, float smallest) {
        float[][] stars = new float[Math.max(count, 0)][];
        RandomSource random = RandomSource.createThreadLocalInstance(10842L);
        float spread = smallest * 2.0F / 3.0F;
        for (int star = 0; star < stars.length; ++star) { stars[star] = star(random, smallest, spread); }
        float[][] groups = new float[GROUPS][];
        for (int group = 0; group < GROUPS; ++group) {
            int shown = 0;
            for (int star = group; star < stars.length; star += GROUPS) {
                if (stars[star] != null) { ++shown; }
            }
            float[] corners = new float[shown * 12];
            int at = 0;
            for (int star = group; star < stars.length; star += GROUPS) {
                if (stars[star] == null) { continue; }
                System.arraycopy(stars[star], 0, corners, at, 12);
                at += 12;
            }
            groups[group] = corners;
        }
        return groups;
    }

    @Nullable private static float[] star(RandomSource random, float smallest, float spread) {
        float x = random.nextFloat() * 2.0F - 1.0F;
        float y = random.nextFloat() * 2.0F - 1.0F;
        float z = random.nextFloat() * 2.0F - 1.0F;
        float size = smallest + random.nextFloat() * spread;
        float length = Mth.lengthSquared(x, y, z);
        if (length >= 1.0F || length <= 0.010000001F) { return null; }
        Vector3f center = new Vector3f(x, y, z).normalize(100.0F);
        float spin = (float) (random.nextDouble() * (float) Math.PI * 2.0D);
        Matrix3f turn = new Matrix3f().rotateTowards(new Vector3f(center).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-spin);
        float[] corners = new float[12];
        float[][] offsets = {{size, -size}, {size, size}, {-size, size}, {-size, -size}};
        for (int corner = 0; corner < 4; ++corner) {
            Vector3f point = new Vector3f(offsets[corner][0], offsets[corner][1], 0.0F).mul(turn).add(center);
            corners[corner * 3] = point.x;
            corners[corner * 3 + 1] = point.y;
            corners[corner * 3 + 2] = point.z;
        }
        return corners;
    }
}
