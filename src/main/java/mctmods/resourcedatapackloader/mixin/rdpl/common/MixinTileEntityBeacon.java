package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityLockable;
import java.util.List;

@Mixin(TileEntityBeacon.class) public abstract class MixinTileEntityBeacon extends TileEntityLockable {
    @Shadow private boolean isComplete;
    @Shadow @Final private List<TileEntityBeacon.BeamSegment> beamSegments;
    @Unique private int rdpl$scanTop = 256;

    @ModifyConstant(method = "updateSegmentColors", constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO)) private int updateSegmentColorsYValue(int orig) {
        return ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @Inject(method = "updateSegmentColors", at = @At("HEAD")) private void rdpl$figureScanTop(CallbackInfo ci) {
        IRubicWorld rubic = (IRubicWorld) world;
        if (!rubic.rdpl$isRubicWorld()) { rdpl$scanTop = 256; return; }
        rdpl$scanTop = Math.min(rubic.rdpl$getMaxHeight(), Math.max(rdpl$topStorageBlockY() + 1, pos.getY() + 1));
    }

    @ModifyConstant(method = "updateSegmentColors", constant = @Constant(intValue = 256)) private int rdpl$beamScanTop(int orig) { return rdpl$scanTop; }

    @Inject(method = "updateSegmentColors", at = @At("TAIL")) private void rdpl$extendBeam(CallbackInfo ci) {
        IRubicWorld rubic = (IRubicWorld) world;
        if (!rubic.rdpl$isRubicWorld() || !isComplete || beamSegments.isEmpty()) { return; }
        int extend = rubic.rdpl$getMaxHeight() - rdpl$scanTop;
        if (extend <= 0) { return; }
        IBeamSegment last = (IBeamSegment) beamSegments.get(beamSegments.size() - 1);
        last.rdpl$setHeight(last.rdpl$getHeight() + extend);
    }

    @Unique private int rdpl$topStorageBlockY() {
        IColumn column = (IColumn) world.getChunk(pos);
        int top = Integer.MIN_VALUE;
        for (ICube cube : column.getLoadedCubes()) {
            if (!cube.isEmpty() && cube.getY() > top) { top = cube.getY(); }
        }
        return top == Integer.MIN_VALUE ? Integer.MIN_VALUE : (top << 4) + 15;
    }
}
