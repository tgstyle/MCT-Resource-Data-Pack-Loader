package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.biome.BiomeTaiga;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeTaiga.class) public interface IBiomeTaiga { @Accessor("type") BiomeTaiga.Type rdpl$type(); }
