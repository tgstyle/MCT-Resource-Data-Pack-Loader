package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.eventbus.api.Event;

public final class ClaimWorldProtection {
    private ClaimWorldProtection() {}

    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!ClaimSettings.any() || !ClaimSettings.protectExplosions() || event.getLevel().isClientSide) { return; }
        event.getAffectedBlocks().removeIf(pos -> ClaimGuard.covers(event.getLevel(), pos) != 0);
    }

    public static void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity != null && ClaimGuard.refusesGriefing(entity)) { event.setResult(Event.Result.DENY); }
    }

    public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!ClaimSettings.any() || ClaimSettings.fluidsOpen() || !(event.getLevel() instanceof Level level) || level.isClientSide) { return; }
        if (!ClaimGuard.sameOwnerAcross(level, event.getLiquidPos(), event.getPos())) { event.setNewState(event.getOriginalState()); }
    }

    public static void onPiston(PistonEvent.Pre event) {
        if (!ClaimSettings.any() || !ClaimSettings.protectPistons() || !(event.getLevel() instanceof Level level) || level.isClientSide) { return; }
        PistonStructureResolver resolver = event.getStructureHelper();
        if (resolver == null || !resolver.resolve()) { return; }
        BlockPos piston = event.getPos();
        boolean extend = event.getPistonMoveType().isExtend;
        Direction direction = extend ? event.getDirection() : event.getDirection().getOpposite();
        BlockPos first = extend ? event.getFaceOffsetPos() : piston.relative(event.getDirection(), 2);
        if (!ClaimGuard.pistonMayMove(level, piston, first, direction, resolver.getToPush(), resolver.getToDestroy())) { event.setCanceled(true); }
    }
}
