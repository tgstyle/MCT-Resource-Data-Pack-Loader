package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.util.ContentEffects;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

final class WeightTiers {
    private static final UUID HEAVY = UUID.fromString("b7c2e914-5d38-4a6f-8e01-3f9a7d2c6b58");
    private static final int REFRESH = 40;
    private static final int LENGTH = 60;

    private WeightTiers() {}

    static int pick(WeightDef settings, int total, int limit) {
        if (limit <= 0) { return -1; }
        int reached = -1;
        List<WeightDef.Tier> tiers = settings.tiers;
        for (int index = 0; index < tiers.size(); index++) {
            if (total > tiers.get(index).above() * limit) { reached = index; }
        }
        return reached;
    }

    static void sync(Player player, PlayerWeights.Entry entry, WeightDef settings, int tier) {
        WeightDef.Tier now = tier < 0 ? null : settings.tiers.get(tier);
        entry.stop = now != null && !now.sprint();
        entry.exhaustion = now == null ? 0.0D : now.exhaustion() / 20.0D;
        entry.heavy = now != null && (entry.stop || entry.exhaustion > 0.0D || !now.effects().isEmpty());
        speed(player, now == null ? 0.0D : now.speed());
        if (tier != entry.tier && now != null && !now.message().isEmpty()) { player.displayClientMessage(Component.translatable(now.message()), true); }
        entry.tier = tier;
    }

    static void clear(Player player) { speed(player, 0.0D); }

    static void hold(Player player, PlayerWeights.Entry entry) {
        if (entry.stop) { player.setSprinting(false); }
        if (entry.exhaustion > 0.0D) { player.causeFoodExhaustion((float) entry.exhaustion); }
        if (player.tickCount % REFRESH != 0) { return; }
        for (PotionEffectDef effect : ContentWeights.settings().tiers.get(entry.tier).effects()) {
            MobEffect potion = ContentEffects.find(effect.potion());
            if (potion != null) { player.addEffect(new MobEffectInstance(potion, LENGTH, effect.amplifier(), effect.ambient(), false)); }
        }
    }

    static boolean jumps(WeightDef settings, int tier) { return tier < 0 || settings.tiers.get(tier).jump(); }

    private static void speed(Player player, double change) {
        AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) { return; }
        AttributeModifier held = attribute.getModifier(HEAVY);
        if (held != null && held.getAmount() == change) { return; }
        if (held != null) { attribute.removeModifier(HEAVY); }
        if (change != 0.0D) { attribute.addTransientModifier(new AttributeModifier(HEAVY, "rdpl weight", change, AttributeModifier.Operation.MULTIPLY_TOTAL)); }
    }
}
