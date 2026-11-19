package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.scripts.RdplApi;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.text.RomanNumerals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public final class ContentEffects {
    private ContentEffects() {}

    public static void cooldown(LivingEntity entity, Item item, int ticks) { if (ticks > 0 && entity instanceof Player player) { player.getCooldowns().addCooldown(item, ticks); } }

    public static void used(Player player, Item item, ItemDef def, InteractionHand hand) {
        if (!def.runs().isEmpty() && player instanceof ServerPlayer server) { Functions.runAs(server, def.runs(), "The item " + def.key()); }
        if (!scripted(player, def, hand)) { cooldown(player, item, def.cooldown()); }
    }

    private static boolean scripted(Player player, ItemDef def, InteractionHand hand) {
        if (def.script() == null || !(player.level() instanceof ServerLevel level)) { return false; }
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("rdpl", new RdplApi(level, player));
        bindings.put("item", player.getItemInHand(hand));
        bindings.put("hand", hand.name());
        return "cancel".equals(ScriptEngine.run(def.script(), bindings));
    }

    @Nullable public static Holder<MobEffect> find(@Nullable String name) {
        if (name == null) { return null; }
        return Registered.holder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.tryParse(Settings.lower(name)));
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

    public static void tooltip(@Nullable MobEffectInstance effect, List<Component> tooltip) {
        if (effect == null || !effect.getEffect().value().isBeneficial()) { return; }
        String level = RomanNumerals.of(effect.getAmplifier());
        MutableComponent name = Component.translatable(effect.getDescriptionId());
        tooltip.add((level.isEmpty() ? name : name.append(" " + level)).withStyle(ChatFormatting.GREEN));
    }
}
