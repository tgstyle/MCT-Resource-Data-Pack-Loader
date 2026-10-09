package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityLockable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("target") @Mixin(TileEntityBeacon.class) public abstract class MixinTileEntityBeaconBetterFps extends TileEntityLockable {
    @Dynamic @ModifyConstant(method = "updateLevels(III)V", constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO), remap = false, require = 0) private int rdpl$updateLevelsYValue(int orig) { return GenHeights.floor(world, orig); }
}
