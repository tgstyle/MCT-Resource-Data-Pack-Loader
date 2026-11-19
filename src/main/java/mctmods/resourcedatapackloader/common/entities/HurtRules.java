package mctmods.resourcedatapackloader.common.entities;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.HurtRuleDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class HurtRules {
    private static final Map<String, List<HurtRuleDef>> BY_VICTIM = new HashMap<>();
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

    public static void onAttack(LivingAttackEvent event) {
        if (BY_VICTIM.isEmpty() && BY_KIND.isEmpty()) { return; }
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (victim.level().isClientSide() || attacker == null || victim instanceof Player || attacker instanceof Player) { return; }
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(victim.getType());
        List<HurtRuleDef> byId = key == null ? null : BY_VICTIM.get(key.toString());
        if (byId != null) { apply(byId, event, victim, attacker); }
        if (!BY_KIND.isEmpty()) { apply(BY_KIND, event, victim, attacker); }
    }

    private static void apply(List<HurtRuleDef> rules, LivingAttackEvent event, LivingEntity victim, Entity attacker) {
        for (HurtRuleDef rule : rules) {
            if (!MobStats.targets(rule.victims(), victim) || !MobStats.targets(rule.attackers(), attacker)) { continue; }
            if (rule.cancel()) { event.setCanceled(true); }
            if (!rule.runs().isEmpty()) { Functions.runAs(victim, rule.runs(), "A hurt rule"); }
        }
    }

    @Nullable private static List<String> kinds(ResourceLocation key, JsonObject json, String member) {
        JsonElement value = json.get(member);
        if (value == null) { return null; }
        if (value.isJsonPrimitive()) {
            JsonArray single = new JsonArray();
            single.add(value);
            value = single;
        }
        List<String> found = MobStats.entitiesOrTags("Hurt rule file " + key, member, value);
        return found == null || found.isEmpty() ? null : found;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        List<String> attackers = kinds(key, json, "attacker");
        List<String> victims = kinds(key, json, "victim");
        if (attackers == null || victims == null) {
            ContentLog.LOGGER.error("Hurt rule file {} needs both an attacker and a victim, ignoring it", key);
            return;
        }
        boolean cancel = Json.getBoolean(json, "cancel", false);
        String runs = Json.getString(json, "runs", "").trim();
        if (!cancel && runs.isEmpty()) {
            ContentLog.LOGGER.error("Hurt rule file {} neither cancels nor runs a function, ignoring it", key);
            return;
        }
        HurtRuleDef rule = new HurtRuleDef(attackers, victims, cancel, runs);
        if (victims.stream().anyMatch(victim -> victim.startsWith("#"))) {
            BY_KIND.add(rule);
            return;
        }
        for (String victim : victims) { BY_VICTIM.computeIfAbsent(victim, id -> new ArrayList<>()).add(rule); }
    }
}
