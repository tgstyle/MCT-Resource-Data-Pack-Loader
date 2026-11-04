package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

public final class ClaimWorldProtection {
    private ClaimWorldProtection() {}

    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!ClaimSettings.any() || !ClaimSettings.protectExplosions() || event.getLevel().isClientSide()) { return; }
        event.getAffectedBlocks().removeIf(pos -> ClaimGuard.covers(event.getLevel(), pos) != 0);
    }

    public static void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (ClaimGuard.refusesGriefing(entity)) { event.setCanGrief(false); }
    }

    public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!ClaimSettings.any() || ClaimSettings.fluidsOpen() || !(event.getLevel() instanceof Level level) || level.isClientSide()) { return; }
        if (!ClaimGuard.sameOwnerAcross(level, event.getLiquidPos(), event.getPos())) { event.setNewState(event.getState()); }
    }

    public static void onPiston(PistonEvent.Pre event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide() || !ClaimSettings.any()) { return; }
        PistonStructureResolver resolver = event.getStructureHelper();
        if (resolver == null || !resolver.resolve()) { return; }
        BlockPos piston = event.getPos();
        Direction direction = event.getDirection();
        BlockPos first = event.getPistonMoveType().isExtend ? piston.relative(direction) : piston.relative(direction, 2);
        if (!ClaimGuard.pistonMayMove(level, piston, first, resolver.getPushDirection(), resolver.getToPush(), resolver.getToDestroy())) { event.setCanceled(true); }
    }
}
