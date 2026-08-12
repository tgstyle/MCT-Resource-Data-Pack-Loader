package mctmods.resourcedatapackloader.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureVillagePieces.Village.class)
public interface AccessorVillagePiece {
    @Accessor("structureType") void rdpl$setStructureType(int type);

    @Accessor("averageGroundLvl") int rdpl$averageGround();

    @Invoker("getBiomeSpecificBlockState") IBlockState rdpl$biomeBlock(IBlockState state);
}
