package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class ZoneDamage {
    private ZoneDamage() {}

    public static void onAttack(LivingIncomingDamageEvent event) {
        if (ZoneRules.noDamage() || ZoneRules.none(event.getEntity().level()) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        Entity attacker = event.getSource().getEntity();
        Player by = attacker instanceof Player player ? player : null;
        ZoneDef zone = ZoneRules.refusingDamage(event.getEntity(), attacker, by);
        if (zone == null) { return; }
        event.setCanceled(true);
        if (by instanceof ServerPlayer player) { ZoneGuard.tell(player, zone); }
    }
}
