package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.compat.ContentCurios;
import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentWorn {
    private static final int POLL = 20;
    private static final int LENGTH = 40;
    private static Map<Item, Worn> worn;
    private static List<Entry> everything;

    private ContentWorn() {}

    private record Entry(Holder<Attribute> attribute, Identifier id, AttributeModifier modifier) {}

    private record Effect(Holder<MobEffect> effect, PotionEffectDef def) {}

    private record Worn(List<Effect> effects, List<Entry> entries) {}

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.tickCount % POLL != 0 || ContentCurios.missing()) { return; }
        if (worn == null) { build(); }
        if (worn.isEmpty()) { return; }
        Set<Identifier> wanted = new HashSet<>();
        for (ItemStack stack : ContentCurios.worn(player, held -> worn.containsKey(held.getItem()))) {
            Worn found = worn.get(stack.getItem());
            for (Effect effect : found.effects()) { refresh(player, effect); }
            for (Entry entry : found.entries()) {
                wanted.add(entry.id());
                AttributeInstance instance = player.getAttribute(entry.attribute());
                if (instance != null && instance.getModifier(entry.id()) == null) { instance.addTransientModifier(entry.modifier()); }
            }
        }
        for (Entry entry : everything) {
            if (wanted.contains(entry.id())) { continue; }
            AttributeInstance instance = player.getAttribute(entry.attribute());
            if (instance != null && instance.getModifier(entry.id()) != null) { instance.removeModifier(entry.id()); }
        }
    }

    private static void refresh(Player player, Effect effect) {
        MobEffectInstance active = player.getEffect(effect.effect());
        if (active != null && active.getDuration() > POLL) { return; }
        player.addEffect(new MobEffectInstance(effect.effect(), LENGTH, effect.def().amplifier(), effect.def().ambient(), effect.def().showParticles()));
    }

    private static void build() {
        worn = new HashMap<>();
        everything = new ArrayList<>();
        Set<String> told = new HashSet<>();
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            ItemDef def = entry.def();
            if (def == null || (def.wornEffects().isEmpty() && def.wornAttributes().isEmpty())) { continue; }
            if (!def.slotted()) {
                if (told.add(def.key().toString())) { ContentLog.LOGGER.info("{} has worn effects or attributes but no curioSlot, so they are ignored", def.key()); }
                continue;
            }
            List<Effect> effects = new ArrayList<>();
            List<Entry> entries = new ArrayList<>();
            for (PotionEffectDef asked : def.wornEffects()) {
                Holder<MobEffect> effect = ContentEffects.find(asked.potion());
                if (effect == null) { ContentLog.LOGGER.error("Worn effect '{}' on {} is not a potion effect, skipping it", asked.potion(), entry.id()); }
                else { effects.add(new Effect(effect, asked)); }
            }
            for (int index = 0; index < def.wornAttributes().size(); index++) {
                AttributeDef asked = def.wornAttributes().get(index);
                Holder<Attribute> attribute = ContentAttributes.find(asked.attribute(), entry.id());
                Identifier id = Identifier.tryBuild(entry.id().getNamespace(), entry.id().getPath() + "/worn/" + index);
                if (attribute == null || id == null) { continue; }
                Entry made = new Entry(attribute, id, new AttributeModifier(id, asked.amount(), AttributeModifier.Operation.BY_ID.apply(asked.operation())));
                entries.add(made);
                everything.add(made);
            }
            worn.put(entry.item(), new Worn(effects, entries));
        }
    }
}
