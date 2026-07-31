package mctmods.resourcedatapackloader.content.def;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class FluidDef {
    public final ResourceLocation registryName;
    public final String name;
    public final int color;
    public final ResourceLocation still;
    public final ResourceLocation flowing;
    public final int temperature;
    public final int density;
    public final int viscosity;
    public final int luminosity;
    public final boolean gaseous;
    public final boolean bucket;
    public final boolean createBlock;
    public final Material material;
    public final String creativeTab;
    public final int flammability;
    public final int fireSpread;
    public final int quantaPerBlock;
    public final List<String> potions;
    public final List<String> requires;
    @Nullable private net.minecraftforge.fluids.Fluid resolved;

    public FluidDef(ResourceLocation registryName, String name, int color, ResourceLocation still, ResourceLocation flowing, int temperature, int density, int viscosity, int luminosity, boolean gaseous, boolean bucket, boolean createBlock, Material material, String creativeTab, int flammability, int fireSpread, int quantaPerBlock, List<String> potions, List<String> requires) {
        this.registryName = registryName;
        this.name = name;
        this.color = color;
        this.still = still;
        this.flowing = flowing;
        this.temperature = temperature;
        this.density = density;
        this.viscosity = viscosity;
        this.luminosity = luminosity;
        this.gaseous = gaseous;
        this.bucket = bucket;
        this.createBlock = createBlock;
        this.material = material;
        this.creativeTab = creativeTab;
        this.flammability = flammability;
        this.fireSpread = fireSpread;
        this.quantaPerBlock = quantaPerBlock;
        this.potions = potions;
        this.requires = requires;
    }

    public void resolve(net.minecraftforge.fluids.Fluid fluid) { this.resolved = fluid; }

    @Nullable public net.minecraftforge.fluids.Fluid getResolved() { return resolved; }
}
