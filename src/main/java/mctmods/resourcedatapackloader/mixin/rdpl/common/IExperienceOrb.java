package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrb.class) public interface IExperienceOrb {
    @Accessor("age") int rdpl$getAge();

    @Accessor("age") void rdpl$setAge(int age);
}
