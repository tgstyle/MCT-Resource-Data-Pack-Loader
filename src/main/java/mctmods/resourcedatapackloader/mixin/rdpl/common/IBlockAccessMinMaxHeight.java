package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;

import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockAccess.class) public interface IBlockAccessMinMaxHeight extends IMinMaxHeight {}
