package mctmods.resourcedatapackloader.mixin.rdpl.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.world.World;

@Mixin(EntityBoat.class) public abstract class MixinEntityBoat extends Entity {
    public MixinEntityBoat(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "checkInWater", constant = @Constant(doubleValue = Double.MIN_VALUE)) private double waterLevelMinValue(double orig) {
        return Double.NEGATIVE_INFINITY;
    }
}
