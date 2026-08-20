package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldProviderEnd.class) public interface IWorldProviderEnd { @Accessor("dragonFightManager") void rdpl$setDragonFightManager(DragonFightManager fight); }
