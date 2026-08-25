package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.biome.BiomeSnow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeSnow.class) public interface IBiomeSnow { @Accessor("superIcy") boolean rdpl$superIcy(); }
