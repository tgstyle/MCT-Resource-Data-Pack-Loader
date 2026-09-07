package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.DimensionValues;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentPhysics {
    private static final double VANILLA_TERMINAL = 3.92D;
    private static final Scale GRAVITY = new Scale("worldGravity");
    private static final Scale FALL_DAMAGE = new Scale("worldFallDamage");
    private static final Scale JUMP = new Scale("worldJumpStrength");
    private static final Scale TERMINAL = new Scale("worldTerminalVelocity");

    private ContentPhysics() {}

    public static double gravity(Level level) { return GRAVITY.factor(level); }

    public static void onFall(LivingFallEvent event) {
        double factor = FALL_DAMAGE.factor(event.getEntity().level());
        if (factor != 1.0D) { event.setDamageMultiplier((float) (event.getDamageMultiplier() * factor)); }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        double factor = JUMP.factor(event.getEntity().level());
        if (factor == 1.0D) { return; }
        Vec3 motion = event.getEntity().getDeltaMovement();
        event.getEntity().setDeltaMovement(motion.x, motion.y * factor, motion.z);
    }

    public static void tick(LivingEntity falling) {
        double factor = TERMINAL.factor(falling.level());
        if (factor == 1.0D || falling.isFallFlying()) { return; }
        double cap = -VANILLA_TERMINAL * factor;
        Vec3 motion = falling.getDeltaMovement();
        if (motion.y < cap) { falling.setDeltaMovement(motion.x, cap, motion.z); }
    }

    private static final class Scale {
        private final String key;
        private final DimensionValues<Double> values;

        Scale(String key) {
            this.key = key;
            this.values = new DimensionValues<>(key, Scale::positive, "which is not a number above zero");
        }

        private List<String> asked() {
            return switch (key) {
                case "worldGravity" -> ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldGravity());
                case "worldFallDamage" -> ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldFallDamage());
                case "worldJumpStrength" -> ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldJumpStrength());
                default -> ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldTerminalVelocity());
            };
        }

        double factor(Level level) {
            if (ContentControl.off(ContentControl.TERRAIN)) { return 1.0D; }
            Double found = values.at(level.dimension().location().toString(), asked());
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
}
