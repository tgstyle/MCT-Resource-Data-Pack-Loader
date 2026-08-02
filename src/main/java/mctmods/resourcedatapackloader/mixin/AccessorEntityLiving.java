package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLiving.class)
public interface AccessorEntityLiving {
    @Accessor("deathLootTable") void rdpl$setDeathLootTable(ResourceLocation table);
}
