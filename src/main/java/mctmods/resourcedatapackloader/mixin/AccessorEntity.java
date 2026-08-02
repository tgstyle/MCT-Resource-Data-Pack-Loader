package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Accessor("isImmuneToFire") void rdpl$setImmuneToFire(boolean immune);

    @Invoker("setSize") void rdpl$setSize(float width, float height);
}
