package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityItem.class)
public interface AccessorEntityItem {
    @Accessor("age") int rdpl$getAge();

    @Accessor("age") void rdpl$setAge(int age);
}
