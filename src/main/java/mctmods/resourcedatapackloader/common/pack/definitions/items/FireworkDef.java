package mctmods.resourcedatapackloader.common.pack.definitions.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class FireworkDef {
    public static final String MODE_LAUNCH = "launch";
    public static final String MODE_BURST = "burst";
    public static final String ON_BLOCK = "block";
    public static final String ON_AIR = "air";
    public static final String ON_BOTH = "both";
    public static final String PATTERN_SPHERE = "sphere";
    public static final String PATTERN_RING = "ring";
    public static final String PATTERN_FOUNTAIN = "fountain";
    public static final String PATTERN_POINTS = "points";
    public static final int MAX_DELAY = 40;
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
    public final boolean replaceUse;
    public final String mode;
    public final String on;
    public final int flight;
    public final boolean consume;
    public final boolean dispense;
    public final List<Explosion> explosions;
    public final List<Burst> bursts;

    public FireworkDef(boolean replaceUse, String mode, String on, int flight, boolean consume, boolean dispense, List<Explosion> explosions, List<Burst> bursts) {
        this.dispense = dispense;
        this.replaceUse = replaceUse;
        this.mode = mode;
        this.on = on;
        this.flight = flight;
        this.consume = consume;
        this.explosions = explosions;
        this.bursts = bursts;
    }

    public boolean onBlock() { return !ON_AIR.equals(on); }

    public boolean onAir() { return !ON_BLOCK.equals(on); }

    public static final class Explosion {
        public final String shape;
        public final int[] colors;
        public final int[] fade;
        public final boolean trail;
        public final boolean twinkle;

        public Explosion(String shape, int[] colors, int[] fade, boolean trail, boolean twinkle) {
            this.shape = shape;
            this.colors = colors;
            this.fade = fade;
            this.trail = trail;
            this.twinkle = twinkle;
        }
    }

    public static final class Burst {
        public final ResourceLocation particle;
        public final int count;
        public final float speed;
        public final String pattern;
        @Nullable public final ResourceLocation sound;
        public final int delay;
        public final float spread;
        public final List<float[]> points;

        public Burst(ResourceLocation particle, int count, float speed, String pattern, @Nullable ResourceLocation sound, int delay, float spread, List<float[]> points) {
            this.delay = delay;
            this.spread = spread;
            this.points = points;
            this.particle = particle;
            this.count = count;
            this.speed = speed;
            this.pattern = pattern;
            this.sound = sound;
        }
    }
}
