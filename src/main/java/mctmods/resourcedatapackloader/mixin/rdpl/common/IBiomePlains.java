package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.biome.BiomePlains;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomePlains.class) public interface IBiomePlains { @Accessor("sunflowers") boolean rdpl$sunflowers(); }
