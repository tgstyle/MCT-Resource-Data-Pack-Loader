package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ZoneDamage {
    private ZoneDamage() {}

    @SubscribeEvent public static void onAttack(LivingAttackEvent event) { guard(event, event.getEntityLiving(), event.getSource()); }

    @SubscribeEvent public static void onHurt(LivingHurtEvent event) { guard(event, event.getEntityLiving(), event.getSource()); }

    private static void guard(Event event, EntityLivingBase victim, DamageSource source) {
        if (ZoneRules.none(victim.world) || ZoneRules.noDamage() || source.canHarmInCreative()) { return; }
        Entity attacker = source.getTrueSource();
        EntityPlayer by = attacker instanceof EntityPlayer ? (EntityPlayer) attacker : null;
        ZoneDef zone = ZoneRules.refusingDamage(victim, attacker, by);
        if (zone == null) { return; }
        event.setCanceled(true);
        if (by != null) { ZoneGuard.tell(by, zone); }
    }
}
