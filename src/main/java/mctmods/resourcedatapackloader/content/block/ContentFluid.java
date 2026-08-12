package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.content.util.TintFactory;

import net.minecraftforge.fluids.Fluid;

public class ContentFluid extends Fluid {
    private final int color;

    public ContentFluid(FluidDef def) {
        super(def.name, def.still, def.flowing);
        this.color = TintFactory.opaque(def.color);
        setTemperature(def.temperature);
        setDensity(def.density);
        setViscosity(def.viscosity);
        setLuminosity(def.luminosity);
        setGaseous(def.gaseous);
    }

    @Override public int getColor() { return color; }
}
