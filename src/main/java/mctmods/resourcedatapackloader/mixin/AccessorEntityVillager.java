package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVillager.class)
public interface AccessorEntityVillager {
    @Accessor("careerId") void rdpl$setCareer(int career);
}
