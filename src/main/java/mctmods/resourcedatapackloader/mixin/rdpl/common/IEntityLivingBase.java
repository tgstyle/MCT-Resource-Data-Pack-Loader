package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLivingBase.class) public interface IEntityLivingBase {
    @Accessor("recentlyHit") void rdpl$setRecentlyHit(int ticks);

    @Accessor("recentlyHit") int rdpl$getRecentlyHit();
}
