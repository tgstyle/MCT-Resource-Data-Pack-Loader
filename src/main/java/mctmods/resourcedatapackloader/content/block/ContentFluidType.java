package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.FluidDef;

import net.neoforged.neoforge.fluids.FluidType;

public class ContentFluidType extends FluidType {
    private final FluidDef def;

    public ContentFluidType(FluidDef def) {
        super(Properties.create()
                .descriptionId("fluid." + def.key().getNamespace() + "." + def.name())
                .lightLevel(def.luminosity())
                .density(def.gaseous() ? -Math.abs(def.density()) : def.density())
                .temperature(def.temperature())
                .viscosity(def.viscosity())
                .canDrown(!def.gaseous())
                .canSwim(!def.gaseous()));
        this.def = def;
    }

    public FluidDef getDef() { return def; }
}
