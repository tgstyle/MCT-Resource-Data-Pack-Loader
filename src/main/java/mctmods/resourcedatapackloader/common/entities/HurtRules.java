package mctmods.resourcedatapackloader.common.entities;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.HurtRuleDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class HurtRules {
    private static final List<String> FILE_KEYS = List.of("attacker", "victim", "cancel", "runs");
    private static final Map<Identifier, List<HurtRuleDef>> BY_VICTIM = new HashMap<>();
    private static final List<HurtRuleDef> BY_KIND = new ArrayList<>();

    private HurtRules() {}

    public static void load() {
        BY_VICTIM.clear();
        BY_KIND.clear();
        Json.eachObject(PackManager.HURT_RULES, "hurt rule file", HurtRules::read);
        int count = BY_KIND.size();
        for (List<HurtRuleDef> rules : BY_VICTIM.values()) { count += rules.size(); }
        if (count > 0) { Summary.info("entities.hurt", "Loaded " + count + " hurt rule entr(ies)"); }
    }

    public static void onAttack(LivingIncomingDamageEvent event) {
        if (BY_VICTIM.isEmpty() && BY_KIND.isEmpty()) { return; }
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (victim.level().isClientSide() || attacker == null || victim instanceof Player || attacker instanceof Player) { return; }
        List<HurtRuleDef> byId = BY_VICTIM.get(BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()));
        if (byId != null) { apply(byId, event, victim, attacker); }
        if (!BY_KIND.isEmpty()) { apply(BY_KIND, event, victim, attacker); }
    }

    private static void apply(List<HurtRuleDef> rules, LivingIncomingDamageEvent event, LivingEntity victim, Entity attacker) {
        for (HurtRuleDef rule : rules) {
            if (!rule.victims().matches(victim) || !rule.attackers().matches(attacker)) { continue; }
            if (rule.cancel()) { event.setCanceled(true); }
            if (!rule.runs().isEmpty()) { Functions.runAs(victim, rule.runs(), "A hurt rule"); }
        }
    }

    @Nullable private static HurtRuleDef.Who who(Identifier key, JsonObject json, String member) {
        JsonElement value = json.get(member);
        if (value == null) { return null; }
        JsonArray entries = new JsonArray();
        if (value.isJsonArray()) { entries = value.getAsJsonArray(); }
        else { entries.add(value); }
        JsonArray listed = new JsonArray();
        List<TagKey<EntityType<?>>> tags = new ArrayList<>();
        for (JsonElement entry : entries) {
            String text = entry.isJsonPrimitive() ? Settings.lower(entry.getAsString()) : "";
            Identifier tag = text.startsWith("#") && text.contains(":") ? Identifier.tryParse(text.substring(1)) : null;
            if (tag == null) {
                listed.add(entry);
                continue;
            }
            TagKey<EntityType<?>> entityTag = TagKey.create(Registries.ENTITY_TYPE, tag);
            tags.add(entityTag);
        }
        List<Identifier> ids = new ArrayList<>();
        List<String> kinds = new ArrayList<>();
        if (!MobStats.entities("Hurt rule file " + key, member, listed, ids, kinds) || ids.isEmpty() && kinds.isEmpty() && tags.isEmpty()) { return null; }
        return new HurtRuleDef.Who(ids, kinds, tags);
    }

    private static void read(Identifier key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        HurtRuleDef.Who attackers = who(key, json, "attacker");
        HurtRuleDef.Who victims = who(key, json, "victim");
        if (attackers == null || victims == null) {
            ContentLog.LOGGER.error("Hurt rule file {} needs both an attacker and a victim, ignoring it", key);
            return;
        }
        boolean cancel = Json.getBoolean(json, "cancel", false);
        String runs = Json.getString(json, "runs", "");
        if (!cancel && runs.isEmpty()) {
            ContentLog.LOGGER.error("Hurt rule file {} neither cancels nor runs a function, ignoring it", key);
            return;
        }
        HurtRuleDef rule = new HurtRuleDef(attackers, victims, cancel, runs);
        if (!victims.listsOnlyIds()) {
            BY_KIND.add(rule);
            return;
        }
        for (Identifier victim : victims.ids()) { BY_VICTIM.computeIfAbsent(victim, _ -> new ArrayList<>()).add(rule); }
    }
}
