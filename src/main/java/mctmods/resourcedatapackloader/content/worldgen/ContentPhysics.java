package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.DimensionValues;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentPhysics {
    private static final double VANILLA_TERMINAL = 3.92D;
    private static final UUID GRAVITY_ID = UUID.fromString("6f2b1c0e-3d7a-4b8e-9c21-5e0f7a1d2b34");
    private static final Scale GRAVITY = new Scale("worldGravity");
    private static final Scale FALL_DAMAGE = new Scale("worldFallDamage");
    private static final Scale JUMP = new Scale("worldJumpStrength");
    private static final Scale TERMINAL = new Scale("worldTerminalVelocity");

    private ContentPhysics() {}

    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof LivingEntity living)) { return; }
        keepGravity(living);
    }

    private static void keepGravity(LivingEntity living) {
        AttributeInstance gravity = living.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravity == null) { return; }
        double wanted = GRAVITY.factor(living.level()) - 1.0D;
        AttributeModifier held = gravity.getModifier(GRAVITY_ID);
        if ((held == null ? 0.0D : held.getAmount()) == wanted) { return; }
        gravity.removeModifier(GRAVITY_ID);
        if (wanted != 0.0D) { gravity.addTransientModifier(new AttributeModifier(GRAVITY_ID, "rdpl:worldGravity", wanted, AttributeModifier.Operation.MULTIPLY_TOTAL)); }
    }

    public static boolean openAir(LivingEntity living) { return !living.isInWater() && !living.isInLava() && !living.isFallFlying(); }

    public static double openAirGravity(LivingEntity living, AttributeInstance gravity) {
        double value = gravity.getValue();
        if (openAir(living)) { return value; }
        AttributeModifier held = gravity.getModifier(GRAVITY_ID);
        return held == null ? value : value / (1.0D + held.getAmount());
    }

    public static double gravity(Level level) { return GRAVITY.factor(level); }

    public static double scaledFall(Entity entity, double vanilla) {
        double factor = gravity(entity.level());
        return factor == 1.0D ? vanilla : vanilla * factor;
    }

    public static float scaledFall(Entity entity, float vanilla) {
        double factor = gravity(entity.level());
        return factor == 1.0D ? vanilla : (float) (vanilla * factor);
    }

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
        if (!falling.level().isClientSide()) { keepGravity(falling); }
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
