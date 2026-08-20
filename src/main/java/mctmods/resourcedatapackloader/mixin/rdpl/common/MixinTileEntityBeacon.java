package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityLockable;

@Mixin(TileEntityBeacon.class) public abstract class MixinTileEntityBeacon extends TileEntityLockable {
    @ModifyConstant(method = "updateSegmentColors", constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO)) private int updateSegmentColorsYValue(int orig) {
        return ((IRubicWorld) world).rdpl$getMinHeight();
    }
}
