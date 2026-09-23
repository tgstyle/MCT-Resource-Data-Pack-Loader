package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLevel.class) public interface IServerLevel {
    @Accessor("dragonFight") void rdpl$setDragonFight(EndDragonFight dragonFight);
}
