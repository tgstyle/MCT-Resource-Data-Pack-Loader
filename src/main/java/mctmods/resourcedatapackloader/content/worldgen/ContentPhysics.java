package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class ContentPhysics {
    private static final double VANILLA_TERMINAL = 3.92;
    private static final ContentDimensionSetting<Double> GRAVITY = scale("worldGravity", () -> Config.worldgen.worldGravity);
    private static final ContentDimensionSetting<Double> FALL_DAMAGE = scale("worldFallDamage", () -> Config.worldgen.worldFallDamage);
    private static final ContentDimensionSetting<Double> JUMP = scale("worldJumpStrength", () -> Config.worldgen.worldJumpStrength);
    private static final ContentDimensionSetting<Double> TERMINAL = scale("worldTerminalVelocity", () -> Config.worldgen.worldTerminalVelocity);

    private ContentPhysics() {}

    public static boolean enabled() {
        if (GRAVITY.asked().length > 0 || FALL_DAMAGE.asked().length > 0 || JUMP.asked().length > 0 || TERMINAL.asked().length > 0) { return true; }
        for (DimensionDef def : ContentDimensions.all().values()) { if (def.traits.fallDamage > 0.0D) { return true; } }
        return false;
    }

    public static double gravity(World world, double base) { return base * gravityFactor(world.provider.getDimension()); }

    public static double arrowGravity(World world, double base) { return base * arrowGravityFactor(world.provider.getDimension()); }

    public static double gravityFactor(int dimension) {
        DimensionTraitsDef traits = traits(dimension);
        return factor(GRAVITY, dimension, traits == null ? -1.0D : traits.gravity);
    }

    public static double arrowGravityFactor(int dimension) {
        DimensionTraitsDef traits = traits(dimension);
        if (traits != null && traits.arrowGravity > 0.0D) { return traits.arrowGravity; }
        return gravityFactor(dimension);
    }

    public static double fallDamageFactor(int dimension) {
        DimensionTraitsDef traits = traits(dimension);
        return factor(FALL_DAMAGE, dimension, traits == null ? -1.0D : traits.fallDamage);
    }

    @Nullable private static DimensionTraitsDef traits(int dimension) {
        DimensionDef def = ContentDimensions.byId(dimension);
        return def == null ? null : def.traits;
    }

    @SubscribeEvent public static void onFall(LivingFallEvent event) {
        WorldProvider provider = event.getEntity().world.provider;
        if (provider instanceof ContentWorldProvider && ((ContentWorldProvider) provider).galacticraftPhysics()) { return; }
        double factor = fallDamageFactor(provider.getDimension());
        if (factor != 1.0) { event.setDamageMultiplier((float) (event.getDamageMultiplier() * factor)); }
    }

    @SubscribeEvent public static void onJump(LivingEvent.LivingJumpEvent event) {
        double factor = factor(JUMP, event.getEntity().world.provider.getDimension(), -1.0D);
        if (factor != 1.0) { event.getEntity().motionY *= factor; }
    }

    @SubscribeEvent public static void onTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase falling = event.getEntityLiving();
        double factor = factor(TERMINAL, falling.world.provider.getDimension(), -1.0D);
        if (factor == 1.0 || falling.isElytraFlying()) { return; }
        double cap = -VANILLA_TERMINAL * factor;
        if (falling.motionY < cap) { falling.motionY = cap; }
    }

    private static ContentDimensionSetting<Double> scale(String key, Supplier<String[]> configured) { return new ContentDimensionSetting<>(key, configured, ContentPhysics::positive, "which is not a number above zero"); }

    private static double factor(ContentDimensionSetting<Double> scale, int dimension, double own) {
        Double found = scale.at(dimension, own > 0.0D ? own : null);
        return found == null ? 1.0D : found;
    }

    @Nullable private static Double positive(String value) {
        try {
            double found = Double.parseDouble(value);
            return found > 0 ? found : null;
        }
        catch (NumberFormatException wrong) { return null; }
    }
}
