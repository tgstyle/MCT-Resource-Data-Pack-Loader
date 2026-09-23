package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.FluidDef;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import javax.annotation.Nonnull;

public class ContentFluidType extends FluidType {
    private static final double LAVA_MOTION = 0.0023333333333333335D;
    private final FluidDef def;

    public ContentFluidType(FluidDef def) {
        super(properties(def));
        this.def = def;
    }

    private static Properties properties(FluidDef def) {
        Properties properties = Properties.create()
                .descriptionId("fluid." + def.key().getNamespace() + "." + def.name())
                .lightLevel(def.luminosity())
                .density(def.gaseous() ? -Math.abs(def.density()) : def.density())
                .temperature(def.temperature())
                .viscosity(def.viscosity());
        if (def.waterMaterial()) { return properties.fallDistanceModifier(0.0F).canExtinguish(true).supportsBoating(true).canHydrate(true); }
        properties.canSwim(false).canDrown(false).canPushEntity(false);
        if (!def.lavaMaterial()) { return properties; }
        return properties.pathType(PathType.LAVA).adjacentPathType(null).motionScale(LAVA_MOTION)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
    }

    @Override public boolean isVaporizedOnPlacement(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull FluidStack stack) { return def.waterMaterial() && level.dimensionType().ultraWarm(); }
}
