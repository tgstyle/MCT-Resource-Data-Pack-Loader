package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class) public interface IItemEntity {
    @Accessor("age") int rdpl$getAge();

    @Accessor("age") void rdpl$setAge(int age);
}
