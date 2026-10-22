package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVillager.class) public interface IEntityVillager {
    @Accessor("careerId") int rdpl$career();

    @Accessor("careerId") void rdpl$setCareer(int career);

    @Accessor("careerLevel") int rdpl$careerLevel();
}
