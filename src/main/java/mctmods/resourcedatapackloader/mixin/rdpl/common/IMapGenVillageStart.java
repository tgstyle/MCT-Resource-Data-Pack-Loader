package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapGenVillage.Start.class) public interface IMapGenVillageStart { @Accessor("hasMoreThanTwoComponents") void rdpl$setSizeable(boolean sizeable); }
