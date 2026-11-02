package mctmods.resourcedatapackloader.common.blocks.fluid;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

abstract class FluidTypeBase extends FluidType {
    private final boolean water;

    FluidTypeBase(Properties properties, boolean water) {
        super(properties);
        this.water = water;
    }

    static void waterLike(Properties properties) { properties.isWaterLike(true); }

    @Override public boolean move(@Nonnull LivingEntity entity, @Nonnull Vec3 movementVector, double gravity) {
        if (water) {
            return false;
        }
        entity.travelInWater(movementVector, gravity, entity.getDeltaMovement().y <= 0.0D, entity.getY());
        return true;
    }
}
