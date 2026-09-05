package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeManager.class) public interface IBiomeManager { @Accessor("biomeZoomSeed") long rdpl$getBiomeZoomSeed(); }
