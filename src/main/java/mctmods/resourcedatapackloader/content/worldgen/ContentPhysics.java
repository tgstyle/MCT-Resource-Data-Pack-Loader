package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class ContentPhysics {
    private static final double VANILLA_TERMINAL = 3.92D;
    private static final ContentDimensionSetting<Double> GRAVITY = scale("worldGravity", Config.worldgen::worldGravity);
    private static final ContentDimensionSetting<Double> FALL_DAMAGE = scale("worldFallDamage", Config.worldgen::worldFallDamage);
    private static final ContentDimensionSetting<Double> JUMP = scale("worldJumpStrength", Config.worldgen::worldJumpStrength);
    private static final ContentDimensionSetting<Double> TERMINAL = scale("worldTerminalVelocity", Config.worldgen::worldTerminalVelocity);

    private ContentPhysics() {}

    public static double gravity(Level level) { return factor(GRAVITY, level, ContentWeather.traits(level).gravity()); }

    public static double arrowGravity(Level level) {
        double own = ContentWeather.traits(level).arrowGravity();
        return own > 0.0D ? own : gravity(level);
    }

    public static boolean openAir(LivingEntity living) { return !living.isInWater() && !living.isInLava() && !living.isFallFlying(); }

    public static void onFall(LivingFallEvent event) {
        Level level = event.getEntity().level();
        double factor = factor(FALL_DAMAGE, level, ContentWeather.traits(level).fallDamage());
        if (factor != 1.0D) { event.setDamageMultiplier((float) (event.getDamageMultiplier() * factor)); }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        double factor = factor(JUMP, event.getEntity().level(), -1.0D);
        if (factor == 1.0D) { return; }
        Vec3 motion = event.getEntity().getDeltaMovement();
        event.getEntity().setDeltaMovement(motion.x, motion.y * factor, motion.z);
    }

    public static void tick(LivingEntity falling) {
        double factor = factor(TERMINAL, falling.level(), -1.0D);
        if (factor == 1.0D || falling.isFallFlying()) { return; }
        double cap = -VANILLA_TERMINAL * factor;
        Vec3 motion = falling.getDeltaMovement();
        if (motion.y < cap) { falling.setDeltaMovement(motion.x, cap, motion.z); }
    }

    private static ContentDimensionSetting<Double> scale(String key, Supplier<List<String>> configured) { return new ContentDimensionSetting<>(key, configured, ContentPhysics::positive, "which is not a number above zero"); }

    private static double factor(ContentDimensionSetting<Double> scale, Level level, double own) {
        Double found = scale.at(level.dimension().identifier().toString(), own > 0.0D ? own : null);
        return found == null ? 1.0D : found;
    }

    @Nullable private static Double positive(String value) {
        try {
            double found = Double.parseDouble(value);
            return found > 0.0D ? found : null;
        }
        catch (NumberFormatException wrong) { return null; }
    }
}
