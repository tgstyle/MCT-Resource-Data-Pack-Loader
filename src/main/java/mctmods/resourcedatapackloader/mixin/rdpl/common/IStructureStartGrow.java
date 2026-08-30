package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureStart.class) public interface IStructureStartGrow { @Invoker("updateBoundingBox") void rdpl$updateBoundingBox(); }
