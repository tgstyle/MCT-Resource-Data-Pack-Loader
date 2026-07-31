package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.FluidDef;

import net.minecraftforge.fluids.Fluid;

public class ContentFluid extends Fluid {
    private final int color;

    public ContentFluid(FluidDef def) {
        super(def.name, def.still, def.flowing);
        this.color = def.color >>> 24 == 0 ? def.color | 0xFF000000 : def.color;
        setTemperature(def.temperature);
        setDensity(def.density);
        setViscosity(def.viscosity);
        setLuminosity(def.luminosity);
        setGaseous(def.gaseous);
    }

    @Override public int getColor() { return color; }
}
