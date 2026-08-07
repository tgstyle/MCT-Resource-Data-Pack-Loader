package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureVillagePieces.Village.class)
public interface AccessorVillagePiece {
    @Accessor("structureType") void rdpl$setStructureType(int type);
}
