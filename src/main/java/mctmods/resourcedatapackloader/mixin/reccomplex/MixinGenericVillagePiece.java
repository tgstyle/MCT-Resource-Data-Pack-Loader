package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.mixin.AccessorVillagePiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.reccomplex.world.gen.feature.villages.GenericVillagePiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GenericVillagePiece.class, remap = false)
public abstract class MixinGenericVillagePiece {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureBoundingBox;offset(III)V"))
    private void rdpl$notBelowItsPlot(StructureBoundingBox box, int x, int y, int z) {
        box.offset(x, y, z);
        if (!ContentBeard.wanted()) { return; }

        int floor = ((AccessorVillagePiece) this).rdpl$averageGround() + ContentBeard.plotSeat((StructureComponent) (Object) this);
        if (box.minY == floor) { return; }

        int moved = floor - box.minY;
        box.offset(0, moved, 0);
        ContentLog.LOGGER.debug("A village plot from Recurrent Complex would have settled {} block(s) away from its seat, so it is moved to put its own floor level with the road", moved);
    }
}
