package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ClaimWorldProtection {
    private ClaimWorldProtection() {}

    @SubscribeEvent public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!ClaimSettings.any() || !ClaimSettings.protectExplosions() || event.getWorld().isRemote) { return; }
        event.getAffectedBlocks().removeIf(pos -> ClaimGuard.covers(event.getWorld(), pos) != 0);
    }

    @SubscribeEvent public static void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity != null && ClaimGuard.refusesGriefing(entity)) { event.setResult(Event.Result.DENY); }
    }

    @SubscribeEvent public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!ClaimSettings.any() || ClaimSettings.fluidsOpen() || event.getWorld().isRemote) { return; }
        if (!ClaimGuard.sameOwnerAcross(event.getWorld(), event.getLiquidPos(), event.getPos())) { event.setNewState(event.getOriginalState()); }
    }
}
