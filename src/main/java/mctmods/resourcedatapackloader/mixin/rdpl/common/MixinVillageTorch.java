package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(StructureVillagePieces.Torch.class) public abstract class MixinVillageTorch {
    @Inject(method = "findPieceBox", at = @At("HEAD"), cancellable = true) private static void rdpl$ownLamps(StructureVillagePieces.Start start, List<StructureComponent> p_175856_1_, Random rand, int p_175856_3_, int p_175856_4_, int p_175856_5_, EnumFacing facing, CallbackInfoReturnable<StructureBoundingBox> cir) {
        if (!ContentBeard.wanted()) { return; }

        ContentLog.LOGGER.debug("A vanilla torch piece is left out of the layout at {}, {}, since the roads carry their own lamp posts", p_175856_3_, p_175856_5_);
        cir.setReturnValue(null);
    }
}
