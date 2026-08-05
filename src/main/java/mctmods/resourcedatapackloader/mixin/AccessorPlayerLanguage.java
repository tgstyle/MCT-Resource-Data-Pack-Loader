package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerMP.class)
public interface AccessorPlayerLanguage {
    @Accessor("language") String rdpl$language();
}
