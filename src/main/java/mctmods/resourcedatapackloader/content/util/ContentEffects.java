package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;

public final class ContentEffects {
    private ContentEffects() {}

    @Nullable public static MobEffectInstance parse(Object context, @Nullable String value) {
        if (value == null || value.trim().isEmpty()) { return null; }
        String[] parts = value.split(",");
        if (parts.length < 3) {
            ContentLog.LOGGER.error("Potion '{}' for {} needs id, duration and amplifier", value, context);
            return null;
        }
        ResourceLocation key = ResourceLocation.tryParse(parts[0].trim());
        MobEffect effect = key == null ? null : ForgeRegistries.MOB_EFFECTS.getValue(key);
        if (effect == null) {
            ContentLog.LOGGER.error("Unknown potion '{}' for {}", parts[0].trim(), context);
            return null;
        }
        try {
            int duration = Integer.parseInt(parts[1].trim());
            int amplifier = Integer.parseInt(parts[2].trim());
            boolean ambient = parts.length > 3 && Boolean.parseBoolean(parts[3].trim());
            return new MobEffectInstance(effect, duration, amplifier, ambient, true);
        }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Potion '{}' for {} has a bad number", value, context);
            return null;
        }
    }

    public static MobEffectInstance copy(MobEffectInstance effect) { return new MobEffectInstance(effect); }
}
