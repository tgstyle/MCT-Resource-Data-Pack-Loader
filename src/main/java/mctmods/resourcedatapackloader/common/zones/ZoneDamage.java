package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class ZoneDamage {
    private ZoneDamage() {}

    public static void onAttack(LivingAttackEvent event) { guard(event, event.getEntity(), event.getSource()); }

    public static void onHurt(LivingHurtEvent event) { guard(event, event.getEntity(), event.getSource()); }

    private static void guard(Event event, LivingEntity victim, DamageSource source) {
        if (ZoneRules.none(victim.level()) || ZoneRules.noDamage() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        Entity attacker = source.getEntity();
        Player by = attacker instanceof Player player ? player : null;
        ZoneDef zone = ZoneRules.refusingDamage(victim, attacker, by);
        if (zone == null) { return; }
        event.setCanceled(true);
        if (by != null) { ZoneGuard.tell(by, zone); }
    }
}
