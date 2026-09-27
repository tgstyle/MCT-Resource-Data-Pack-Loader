package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLevel.class) public interface IServerLevel {
    @Accessor("dragonFight") void rdpl$setDragonFight(EnderDragonFight dragonFight);
}
