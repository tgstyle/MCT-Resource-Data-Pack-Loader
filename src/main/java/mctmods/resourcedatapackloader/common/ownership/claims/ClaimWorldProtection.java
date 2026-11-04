package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

public final class ClaimWorldProtection {
    private ClaimWorldProtection() {}

    @SubscribeEvent public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!ClaimSettings.any() || !ClaimSettings.protectExplosions() || event.getLevel().isClientSide()) { return; }
        event.getAffectedBlocks().removeIf(pos -> ClaimGuard.covers(event.getLevel(), pos) != 0);
    }

    @SubscribeEvent public static void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (ClaimGuard.refusesGriefing(entity)) { event.setCanGrief(false); }
    }

    @SubscribeEvent public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!ClaimSettings.any() || ClaimSettings.fluidsOpen() || !(event.getLevel() instanceof Level level) || level.isClientSide()) { return; }
        if (!ClaimGuard.sameOwnerAcross(level, event.getLiquidPos(), event.getPos())) { event.setNewState(event.getOriginalState()); }
    }

    @SubscribeEvent public static void onPiston(PistonEvent.Pre event) {
        if (!(event.getLevel() instanceof Level level)) { return; }
        PistonStructureResolver helper = event.getStructureHelper();
        if (helper == null || !helper.resolve()) { return; }
        BlockPos first = event.getPos().relative(event.getDirection(), helper.getPushDirection() == event.getDirection() ? 1 : 2);
        if (!ClaimGuard.pistonMayMove(level, event.getPos(), first, helper.getPushDirection(), helper.getToPush(), helper.getToDestroy())) { event.setCanceled(true); }
    }
}
