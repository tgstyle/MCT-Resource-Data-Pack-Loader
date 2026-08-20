package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class) public interface IBiomeName { @Accessor("biomeName") String rdpl$biomeName(); }
