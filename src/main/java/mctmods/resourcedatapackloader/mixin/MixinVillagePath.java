package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(StructureVillagePieces.Path.class)
public abstract class MixinVillagePath extends StructureVillagePieces.Village {
    @SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) @Inject(method = "addComponentParts", at = @At("HEAD"), cancellable = true)
    private void rdpl$grade(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentBeard.wanted()) { return; }

        ContentBeard.pave(this, worldIn, structureBoundingBoxIn,
                getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState()),
                getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState()),
                getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState()));
        cir.setReturnValue(true);
    }
}
