package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record FluidDef(ResourceLocation key, String name, int color, ResourceLocation still, ResourceLocation flowing, int temperature, int density, int viscosity, int luminosity, boolean gaseous, boolean bucket,
                       boolean createBlock, String material, String creativeTab, int flammability, int fireSpread, int quantaPerBlock, List<String> potions, List<String> requires) {
    public ResourceLocation id() { return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), name); }
}
