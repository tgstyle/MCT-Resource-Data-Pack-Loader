package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;

public final class ContentFluidType extends FluidTypeBase {
    private static final double LAVA_MOTION = 0.0023333333333333335D;
    private final FluidDef def;

    public ContentFluidType(FluidDef def) {
        super(properties(def), def.waterMaterial());
        this.def = def;
    }

    private static Properties properties(FluidDef def) {
        Properties properties = Properties.create()
                .descriptionId("fluid." + def.key().getNamespace() + "." + def.name())
                .lightLevel(def.luminosity())
                .density(def.gaseous() ? -Math.abs(def.density()) : def.density())
                .temperature(def.temperature())
                .viscosity(def.viscosity());
        if (def.waterMaterial()) { properties.fallDistanceModifier(0.0F).canExtinguish(true).supportsBoating(true).canHydrate(true); waterLike(properties); }
        else {
            properties.canSwim(false).canDrown(false).canPushEntity(false);
            if (def.lavaMaterial()) {
                properties.pathType(PathType.LAVA).adjacentPathType(null).motionScale(LAVA_MOTION)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
            }
        }
        return keyed(properties, def.behavior().creatures());
    }

    private static Properties keyed(Properties properties, FluidBehaviorDef.Creatures keys) {
        Boolean swim = keys.swim();
        Boolean drown = keys.drown();
        Float push = keys.push();
        if (swim != null) {
            properties.canSwim(swim);
            if (swim) { properties.canExtinguish(true).fallDistanceModifier(0.0F); }
        }
        if (drown != null) { properties.canDrown(drown); }
        if (push != null) { properties.canPushEntity(true).motionScale(push); }
        return properties;
    }

    @Override public boolean isVaporizedOnPlacement(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull FluidStack stack) { return def.waterMaterial() && level.environmentAttributes().getDimensionValue(EnvironmentAttributes.WATER_EVAPORATES); }
}
