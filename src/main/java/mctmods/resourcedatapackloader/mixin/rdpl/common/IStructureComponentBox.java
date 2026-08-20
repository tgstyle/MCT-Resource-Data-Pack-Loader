package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureComponent.class) public interface IStructureComponentBox { @Accessor("boundingBox") StructureBoundingBox rdpl$box(); }
