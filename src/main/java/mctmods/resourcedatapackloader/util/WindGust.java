package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.WindDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IParticle;

import net.minecraft.client.particle.Particle;
import javax.annotation.Nullable;

public final class WindGust {
    private static final float MAX_LEAN = 75.0F;
    private static final float LEVEL = 90.0F;
    private static final double LONGEST = 80.0D;
    private static final double DRIFT = 0.1D;
    private static final long MIX = 0x9E3779B97F4A7C15L;
    private static final long SWING = 0x632BE59BD9B4E019L;
    @Nullable private static RainDef carried;
    private static double carriedTime;

    private WindGust() {}

    public static float angle(RainDef rain, double time) {
        WindDef wind = rain.wind();
        if (wind == null) { return rain.angle(); }
        float push = (float) (wind.gust() * shape(wind, time, segment(wind, time)));
        return rain.angle() > LEVEL ? Math.max(LEVEL, rain.angle() - push) : Math.min(LEVEL, rain.angle() + push);
    }

    public static float heading(RainDef rain, double time) {
        WindDef wind = rain.wind();
        if (wind == null) { return rain.heading(); }
        long segment = segment(wind, time);
        return rain.heading() + (float) (wind.swing() * (unit(segment ^ SWING) * 2.0D - 1.0D) * shape(wind, time, segment));
    }

    public static double slope(float angle) { return Math.tan(Math.toRadians(Math.min(Math.min(angle, 180.0F - angle), MAX_LEAN))); }

    public static void carry(@Nullable RainDef rain, double time) {
        carried = rain;
        carriedTime = time;
    }

    public static void caught(@Nullable Particle particle) {
        RainDef rain = carried;
        if (particle == null || rain == null) { return; }
        double speed = slope(angle(rain, carriedTime)) * DRIFT;
        if (speed <= 0.0D) { return; }
        double heading = Math.toRadians(heading(rain, carriedTime));
        ((IParticle) particle).setXd(-Math.sin(heading) * speed);
        ((IParticle) particle).setZd(Math.cos(heading) * speed);
    }

    private static long segment(WindDef wind, double time) { return (long) Math.floor(time / mean(wind)); }

    private static double mean(WindDef wind) { return (wind.everyMin() + wind.everyMax()) * 0.5D; }

    private static double shape(WindDef wind, double time, long segment) {
        double phase = (time - segment * mean(wind) - unit(segment) * (wind.everyMax() - wind.everyMin()) * 0.5D) / Math.min(LONGEST, wind.everyMin());
        if (phase <= 0.0D || phase >= 1.0D) { return 0.0D; }
        double rise = Math.sin(Math.PI * phase);
        return rise * rise;
    }

    private static double unit(long seed) {
        long mixed = seed * MIX;
        mixed ^= mixed >>> 31;
        mixed *= 0xBF58476D1CE4E5B9L;
        mixed ^= mixed >>> 27;
        return (mixed >>> 11) * 0x1.0p-53;
    }
}
