package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructureVillagePieces.Church.class) public abstract class MixinVillageChurch {
    @ModifyVariable(method = "addComponentParts", at = @At("STORE"), name = "iblockstate") private IBlockState rdpl$churchWalls(IBlockState iblockstate) {
        if (iblockstate == null || iblockstate.getBlock() != Blocks.COBBLESTONE) { return iblockstate; }

        return ((IVillagePiece) this).rdpl$biomeBlock(iblockstate);
    }
}
