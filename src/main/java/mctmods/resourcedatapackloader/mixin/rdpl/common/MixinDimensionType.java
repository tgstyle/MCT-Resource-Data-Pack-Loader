package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.IDayLength;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DimensionType.class) public abstract class MixinDimensionType implements IDayLength {
    @Unique private long rdpl$dayLength;

    @Override public void rdpl$dayLength(long ticks) { rdpl$dayLength = ticks; }

    @ModifyConstant(method = "timeOfDay(J)F", constant = @Constant(doubleValue = 24000.0D))
    private double rdpl$ownDayLength(double vanilla) { return rdpl$dayLength > 0L ? rdpl$dayLength : vanilla; }
}
