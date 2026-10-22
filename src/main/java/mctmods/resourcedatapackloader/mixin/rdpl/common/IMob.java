package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class) public interface IMob {
    @Accessor("navigation") void rdpl$setNavigation(PathNavigation navigation);

    @Accessor("moveControl") void rdpl$setMoveControl(MoveControl control);

    @Invoker("getEquipmentDropChance") float rdpl$dropChance(EquipmentSlot slot);
}
