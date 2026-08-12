package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.mixin.AccessorVillagePiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.reccomplex.world.gen.feature.villages.GenericVillagePiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GenericVillagePiece.class, remap = false)
public abstract class MixinGenericVillagePiece {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureBoundingBox;offset(III)V"))
    private void rdpl$notBelowItsPlot(StructureBoundingBox box, int x, int y, int z) {
        box.offset(x, y, z);
        if (!ContentBeard.wanted()) { return; }

        int floor = ((AccessorVillagePiece) this).rdpl$averageGround() - 1;
        if (box.minY >= floor) { return; }

        int lifted = floor - box.minY;
        box.offset(0, lifted, 0);
        ContentLog.LOGGER.debug("A village plot from Recurrent Complex settled {} block(s) below the ground its plot was given, deeper than a footing course, so it is lifted to sit on it", lifted);
    }
}
