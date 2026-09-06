package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class) public interface IMob {
    @Accessor("navigation") void rdpl$setNavigation(PathNavigation navigation);

    @Accessor("moveControl") void rdpl$setMoveControl(MoveControl control);
}
