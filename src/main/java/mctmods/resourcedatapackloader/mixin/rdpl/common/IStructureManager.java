package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureManager.class) public interface IStructureManager {
    @Accessor("level") LevelAccessor rdpl$level();
}
