package mctmods.resourcedatapackloader.mixin.world;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IMinMaxHeight;

import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockAccess.class) public interface IBlockAccessMinMaxHeight extends IMinMaxHeight {}
