package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ASMEventHandler.class) public interface IASMEventHandler { @Accessor(remap = false) ModContainer getOwner(); }
