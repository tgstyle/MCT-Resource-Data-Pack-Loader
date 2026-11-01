package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.tints.TintFactory;

import net.minecraftforge.fluids.Fluid;

public class ContentFluid extends Fluid {
    private final int tint;

    public ContentFluid(FluidDef def) {
        super(def.name, def.still, def.flowing);
        this.tint = TintFactory.opaque(def.color);
        setTemperature(def.temperature);
        setDensity(def.density);
        setViscosity(def.viscosity);
        setLuminosity(def.luminosity);
        setGaseous(def.gaseous);
    }

    @Override public int getColor() { return tint; }
}
