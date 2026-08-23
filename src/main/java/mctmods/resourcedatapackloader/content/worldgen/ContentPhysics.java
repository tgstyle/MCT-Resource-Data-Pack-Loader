package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ContentPhysics {
    private ContentPhysics() {}

    private static final double VANILLA_TERMINAL = 3.92;
    private static final Scale GRAVITY = new Scale("worldGravity");
    private static final Scale FALL_DAMAGE = new Scale("worldFallDamage");
    private static final Scale JUMP = new Scale("worldJumpStrength");
    private static final Scale TERMINAL = new Scale("worldTerminalVelocity");

    public static boolean enabled() {
        return GRAVITY.asked().length > 0 || FALL_DAMAGE.asked().length > 0 || JUMP.asked().length > 0 || TERMINAL.asked().length > 0;
    }

    public static double gravity(World world, double base) { return base * GRAVITY.factorFor(world.provider.getDimension()); }

    @SubscribeEvent public static void onFall(LivingFallEvent event) {
        double factor = FALL_DAMAGE.factorFor(event.getEntity().world.provider.getDimension());
        if (factor != 1.0) { event.setDamageMultiplier((float) (event.getDamageMultiplier() * factor)); }
    }

    @SubscribeEvent public static void onJump(LivingEvent.LivingJumpEvent event) {
        double factor = JUMP.factorFor(event.getEntity().world.provider.getDimension());
        if (factor != 1.0) { event.getEntity().motionY *= factor; }
    }

    @SubscribeEvent public static void onTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase falling = event.getEntityLiving();
        double factor = TERMINAL.factorFor(falling.world.provider.getDimension());
        if (factor == 1.0 || falling.isElytraFlying()) { return; }
        double cap = -VANILLA_TERMINAL * factor;
        if (falling.motionY < cap) { falling.motionY = cap; }
    }

    private static final class Scale {
        private final String key;
        private String[] raw;
        private double everywhere = 1.0;
        private Map<Integer, Double> byDimension = new HashMap<>();

        Scale(String key) { this.key = key; }

        String[] asked() {
            switch (key) {
                case "worldGravity": return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldGravity);
                case "worldFallDamage": return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldFallDamage);
                case "worldJumpStrength": return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldJumpStrength);
                default: return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldTerminalVelocity);
            }
        }

        double factorFor(int dimension) {
            if (ContentControl.off(ContentControl.TERRAIN)) { return 1.0; }
            String[] asked = asked();
            if (asked.length == 0) { return 1.0; }
            if (!Arrays.equals(asked, raw)) {
                double bare = 1.0;
                Map<Integer, Double> scoped = new HashMap<>();
                for (String entry : asked) {
                    String line = entry.trim();
                    int split = line.indexOf('=');
                    String value = split < 0 ? line : line.substring(split + 1).trim();
                    double found;
                    try { found = Double.parseDouble(value); }
                    catch (NumberFormatException wrong) {
                        ContentLog.LOGGER.error("{} names '{}', which is not a number, ignoring it", key, line);
                        continue;
                    }
                    if (found <= 0) {
                        ContentLog.LOGGER.error("{} names '{}', which is not above zero, ignoring it", key, line);
                        continue;
                    }
                    if (split < 0) { bare = found; }
                    else {
                        try { scoped.put(Integer.parseInt(line.substring(0, split).trim()), found); }
                        catch (NumberFormatException wrong) { ContentLog.LOGGER.error("{} names '{}', whose dimension is not a whole number, ignoring it", key, line); }
                    }
                }
                everywhere = bare;
                byDimension = scoped;
                raw = asked;
            }
            Double found = byDimension.get(dimension);
            return found != null ? found : everywhere;
        }
    }
}
