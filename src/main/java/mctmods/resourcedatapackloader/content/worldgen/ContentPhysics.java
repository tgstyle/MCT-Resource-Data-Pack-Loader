package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import javax.annotation.Nullable;

public final class ContentPhysics {
    private ContentPhysics() {}

    private static final double VANILLA_TERMINAL = 3.92;
    private static final Scale GRAVITY = new Scale("worldGravity");
    private static final Scale FALL_DAMAGE = new Scale("worldFallDamage");
    private static final Scale JUMP = new Scale("worldJumpStrength");
    private static final Scale TERMINAL = new Scale("worldTerminalVelocity");

    public static boolean enabled() {
        if (GRAVITY.asked().length > 0 || FALL_DAMAGE.asked().length > 0 || JUMP.asked().length > 0 || TERMINAL.asked().length > 0) { return true; }
        for (DimensionDef def : ContentDimensions.all().values()) {
            if (def.traits.fallDamage > 0.0D) { return true; }
        }
        return false;
    }

    public static double gravity(World world, double base) { return base * gravityFactor(world.provider.getDimension()); }

    public static double arrowGravity(World world, double base) { return base * arrowGravityFactor(world.provider.getDimension()); }

    public static double gravityFactor(int dimension) {
        DimensionTraitsDef traits = traits(dimension);
        return GRAVITY.factorFor(dimension, traits == null ? -1.0D : traits.gravity);
    }

    public static double arrowGravityFactor(int dimension) {
        DimensionTraitsDef traits = traits(dimension);
        if (traits != null && traits.arrowGravity > 0.0D) { return traits.arrowGravity; }
        return gravityFactor(dimension);
    }

    public static double fallDamageFactor(int dimension) {
        DimensionTraitsDef traits = traits(dimension);
        return FALL_DAMAGE.factorFor(dimension, traits == null ? -1.0D : traits.fallDamage);
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
        double factor = JUMP.factorFor(event.getEntity().world.provider.getDimension(), -1.0D);
        if (factor != 1.0) { event.getEntity().motionY *= factor; }
    }

    @SubscribeEvent public static void onTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase falling = event.getEntityLiving();
        double factor = TERMINAL.factorFor(falling.world.provider.getDimension(), -1.0D);
        if (factor == 1.0 || falling.isElytraFlying()) { return; }
        double cap = -VANILLA_TERMINAL * factor;
        if (falling.motionY < cap) { falling.motionY = cap; }
    }

    private static final class Scale {
        private final String key;
        private final TemplateMemo<String[]> memo = new TemplateMemo<>();
        private String[] raw;
        private double everywhere = 1.0;
        private Int2DoubleOpenHashMap byDimension = new Int2DoubleOpenHashMap();

        Scale(String key) { this.key = key; }

        String[] asked() { return memo.get(this::read); }

        private String[] read() {
            switch (key) {
                case "worldGravity": return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldGravity);
                case "worldFallDamage": return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldFallDamage);
                case "worldJumpStrength": return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldJumpStrength);
                default: return ContentControl.list(ContentControl.TERRAIN, key, Config.worldgen.worldTerminalVelocity);
            }
        }

        double factorFor(int dimension, double own) {
            double fallback = own > 0.0D ? own : 1.0D;
            if (ContentControl.off(ContentControl.TERRAIN)) { return fallback; }
            String[] asked = asked();
            if (asked.length == 0) { return fallback; }
            if (asked != raw) {
                double bare = 1.0;
                Int2DoubleOpenHashMap scoped = new Int2DoubleOpenHashMap();
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
            if (byDimension.containsKey(dimension)) { return byDimension.get(dimension); }
            return own > 0.0D ? own : everywhere;
        }
    }
}
