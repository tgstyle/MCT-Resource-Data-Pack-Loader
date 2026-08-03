package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldProviderEnd.class)
public interface AccessorWorldProviderEnd {
    @Accessor("dragonFightManager") void rdpl$setDragonFightManager(DragonFightManager fight);
}
