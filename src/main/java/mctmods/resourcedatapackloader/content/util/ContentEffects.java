package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.RomanNumerals;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentEffects {
    private ContentEffects() {}

    public static void cooldown(LivingEntity entity, Item item, int ticks) { if (ticks > 0 && entity instanceof Player player) { player.getCooldowns().addCooldown(item, ticks); } }

    @Nullable public static MobEffect find(@Nullable String name) {
        if (name == null) { return null; }
        return Registered.find(ForgeRegistries.MOB_EFFECTS, ResourceLocation.tryParse(Settings.lower(name)));
    }

    @Nullable public static MobEffectInstance parse(Object context, @Nullable String value) {
        if (value == null || value.trim().isEmpty()) { return null; }
        String[] parts = value.split(",");
        if (parts.length < 3) {
            ContentLog.LOGGER.error("Potion '{}' for {} needs id, duration and amplifier", value, context);
            return null;
        }
        MobEffect effect = find(parts[0]);
        if (effect == null) {
            ContentLog.LOGGER.error("Unknown potion '{}' for {}", parts[0].trim(), context);
            return null;
        }
        try {
            int duration = Integer.parseInt(parts[1].trim());
            int amplifier = Integer.parseInt(parts[2].trim());
            boolean ambient = parts.length > 3 && Boolean.parseBoolean(parts[3].trim());
            return new MobEffectInstance(effect, duration, amplifier, ambient, false);
        }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Potion '{}' for {} has a bad number", value, context);
            return null;
        }
    }

    public static MobEffectInstance copy(MobEffectInstance effect) { return new MobEffectInstance(effect); }

    public static void tooltip(@Nullable MobEffectInstance effect, List<Component> tooltip) {
        if (effect == null || !effect.getEffect().isBeneficial()) { return; }
        String level = RomanNumerals.of(effect.getAmplifier());
        MutableComponent name = Component.translatable(effect.getDescriptionId());
        tooltip.add((level.isEmpty() ? name : name.append(" " + level)).withStyle(ChatFormatting.GREEN));
    }
}
