package mctmods.resourcedatapackloader.common.pack.definitions.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record FireworkDef(boolean replaceUse, String mode, String on, int flight, boolean consume, boolean dispense, List<Explosion> explosions, List<Burst> bursts) {
    public static final String PATTERN_POINTS = "points";
    public static final int MIN_DELAY = 0;
    public static final int MAX_DELAY = 40;
    public static final float MIN_SPREAD = 0.0F;
    public static final float MAX_SPREAD = 1.0F;

    public static final String MODE_LAUNCH = "launch";
    public static final String MODE_BURST = "burst";
    public static final String ON_BLOCK = "block";
    public static final String ON_AIR = "air";
    public static final String ON_BOTH = "both";
    public static final String PATTERN_SPHERE = "sphere";
    public static final String PATTERN_RING = "ring";
    public static final String PATTERN_FOUNTAIN = "fountain";
    public static final String SHAPE_SMALL_BALL = "small_ball";
    public static final String SHAPE_LARGE_BALL = "large_ball";
    public static final String SHAPE_STAR = "star";
    public static final String SHAPE_CREEPER = "creeper";
    public static final String SHAPE_BURST = "burst";
    public static final int MIN_FLIGHT = 0;
    public static final int MAX_FLIGHT = 3;
    public static final int DEFAULT_FLIGHT = 1;
    public static final int MIN_COUNT = 1;
    public static final int MAX_COUNT = 200;
    public static final int DEFAULT_COUNT = 40;
    public static final float DEFAULT_SPEED = 0.3F;

    public boolean onBlock() { return !ON_AIR.equals(on); }

    public boolean onAir() { return !ON_BLOCK.equals(on); }

    public record Explosion(String shape, int[] colors, int[] fade, boolean trail, boolean twinkle) {}

    public record Burst(ResourceLocation particle, int count, float speed, String pattern, @Nullable ResourceLocation sound, int delay, float spread, List<float[]> points) {}
}
