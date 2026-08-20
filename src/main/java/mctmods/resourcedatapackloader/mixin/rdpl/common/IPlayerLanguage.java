package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerMP.class) public interface IPlayerLanguage { @Accessor("language") String rdpl$language(); }
