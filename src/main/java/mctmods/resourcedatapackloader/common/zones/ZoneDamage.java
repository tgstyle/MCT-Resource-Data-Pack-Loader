package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class ZoneDamage {
    private ZoneDamage() {}

    @SubscribeEvent public static void onIncoming(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();
        if (ZoneRules.none(victim.level()) || ZoneRules.noDamage() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        Entity attacker = source.getEntity();
        Player by = attacker instanceof Player player ? player : null;
        ZoneDef zone = ZoneRules.refusingDamage(victim, attacker, by);
        if (zone == null) { return; }
        event.setCanceled(true);
        if (by != null) { ZoneGuard.tell(by, zone); }
    }
}
