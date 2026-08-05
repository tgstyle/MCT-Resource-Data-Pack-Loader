package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureComponent.class)
public interface AccessorStructureComponentBox {
    @Accessor("boundingBox") StructureBoundingBox rdpl$box();
}
