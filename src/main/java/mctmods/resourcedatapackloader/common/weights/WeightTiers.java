package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.util.ContentEffects;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

final class WeightTiers {
    private static final Identifier HEAVY = ResourceDataPackLoader.id("weight");
    private static final Identifier GROUNDED = ResourceDataPackLoader.id("weight_jump");
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
        modify(player.getAttribute(Attributes.MOVEMENT_SPEED), HEAVY, now == null ? 0.0D : now.speed());
        modify(player.getAttribute(Attributes.JUMP_STRENGTH), GROUNDED, now != null && !now.jump() ? -1.0D : 0.0D);
        if (tier != entry.tier && now != null && !now.message().isEmpty()) { player.sendOverlayMessage(Component.translatable(now.message())); }
        entry.tier = tier;
    }

    static void clear(Player player) {
        modify(player.getAttribute(Attributes.MOVEMENT_SPEED), HEAVY, 0.0D);
        modify(player.getAttribute(Attributes.JUMP_STRENGTH), GROUNDED, 0.0D);
    }

    static void hold(Player player, PlayerWeights.Entry entry) {
        if (entry.stop) { player.setSprinting(false); }
        if (entry.exhaustion > 0.0D) { player.causeFoodExhaustion((float) entry.exhaustion); }
        if (player.tickCount % REFRESH != 0) { return; }
        for (PotionEffectDef effect : ContentWeights.settings().tiers.get(entry.tier).effects()) {
            Holder<MobEffect> potion = ContentEffects.find(effect.potion());
            if (potion != null) { player.addEffect(new MobEffectInstance(potion, LENGTH, effect.amplifier(), effect.ambient(), false)); }
        }
    }

    private static void modify(AttributeInstance attribute, Identifier id, double change) {
        if (attribute == null) { return; }
        AttributeModifier held = attribute.getModifier(id);
        if (held != null && held.amount() == change) { return; }
        if (held != null) { attribute.removeModifier(id); }
        if (change != 0.0D) { attribute.addTransientModifier(new AttributeModifier(id, change, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); }
    }
}
