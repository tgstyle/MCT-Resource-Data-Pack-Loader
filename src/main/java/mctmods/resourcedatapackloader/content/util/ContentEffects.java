package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.RomanNumerals;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentEffects {
    private ContentEffects() {}

    public static void cooldown(LivingEntity entity, ItemStack stack, int ticks) { if (ticks > 0 && entity instanceof Player player) { player.getCooldowns().addCooldown(stack, ticks); } }

    @Nullable public static Holder<MobEffect> find(@Nullable String name) {
        if (name == null) { return null; }
        return Registered.holder(BuiltInRegistries.MOB_EFFECT, Identifier.tryParse(Settings.lower(name)));
    }

    @Nullable public static MobEffectInstance parse(Object context, @Nullable String value) {
        if (value == null || value.trim().isEmpty()) { return null; }
        String[] parts = value.split(",");
        if (parts.length < 3) {
            ContentLog.LOGGER.error("Potion '{}' for {} needs id, duration and amplifier", value, context);
            return null;
        }
        Holder<MobEffect> effect = find(parts[0]);
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

    public static void tooltip(@Nullable MobEffectInstance effect, Consumer<Component> tooltip) {
        if (effect == null || !effect.getEffect().value().isBeneficial()) { return; }
        String level = RomanNumerals.of(effect.getAmplifier());
        MutableComponent name = Component.translatable(effect.getDescriptionId());
        tooltip.accept((level.isEmpty() ? name : name.append(" " + level)).withStyle(ChatFormatting.GREEN));
    }
}
