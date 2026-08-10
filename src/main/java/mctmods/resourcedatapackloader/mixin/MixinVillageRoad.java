package mctmods.resourcedatapackloader.mixin;

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

@Mixin(StructureVillagePieces.Path.class)
public abstract class MixinVillageRoad {
    @Inject(method = "findPieceBox", at = @At("RETURN"), cancellable = true)
    private static void rdpl$backOff(StructureVillagePieces.Start start, List<StructureComponent> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing, CallbackInfoReturnable<StructureBoundingBox> cir) {
        StructureBoundingBox box = cir.getReturnValue();
        if (box == null || facing == null || !ContentBeard.wanted()) { return; }

        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        List<StructureComponent> held = ContentBeard.laid();
        int kept;
        ContentBeard.laying(p_175848_1_);
        try { kept = ContentBeard.roadReach(box, facing); }
        finally { ContentBeard.laying(held); }
        if (kept >= rows) { return; }

        if (kept < 7) {
            ContentLog.LOGGER.debug("A road from {}, {} facing {} cannot be graded to a walkable slope, so it is not laid", p_175848_3_, p_175848_5_, facing);
            cir.setReturnValue(null);
            return;
        }

        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        if (alongX && step > 0) { box.maxX = box.minX + kept - 1; }
        else if (alongX) { box.minX = box.maxX - kept + 1; }
        else if (step > 0) { box.maxZ = box.minZ + kept - 1; }
        else { box.minZ = box.maxZ - kept + 1; }

        ContentLog.LOGGER.debug("A road from {}, {} facing {} backs off from {} to {} block(s) to keep a walkable slope", p_175848_3_, p_175848_5_, facing, rows, kept);
    }
}
