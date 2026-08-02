package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.PathNavigate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLiving.class)
public interface AccessorEntityLivingNavigator {
    @Accessor("navigator") void rdpl$setNavigator(PathNavigate navigator);

    @Accessor("moveHelper") void rdpl$setMoveHelper(EntityMoveHelper helper);
}
