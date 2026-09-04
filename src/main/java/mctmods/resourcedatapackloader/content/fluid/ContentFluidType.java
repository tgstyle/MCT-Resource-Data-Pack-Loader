package mctmods.resourcedatapackloader.content.fluid;

import mctmods.resourcedatapackloader.content.def.FluidDef;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class ContentFluidType extends FluidType {
    private static final int OPAQUE = 0xFF000000;
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

    @Override public void initializeClient(@Nonnull Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override @Nonnull public ResourceLocation getStillTexture() { return def.still(); }

            @Override @Nonnull public ResourceLocation getFlowingTexture() { return def.flowing(); }

            @Override public int getTintColor() { return def.color() | OPAQUE; }
        });
    }
}
