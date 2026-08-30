package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructureVillagePieces.class) public abstract class MixinStructureVillagePieces {
    @Inject(method = "getStructureVillageWeightedPieceList", at = @At("RETURN")) private static void rdpl$filterPieces(Random random, int size, CallbackInfoReturnable<List<StructureVillagePieces.PieceWeight>> cir) {
        List<StructureVillagePieces.PieceWeight> list = cir.getReturnValue();
        if (list == null || !ContentVillages.filtering()) { return; }

        list.removeIf(weight -> {
            if (!ContentVillages.blocked(weight.villagePieceClass)) { return false; }
            ContentLog.LOGGER.debug("Village piece {} is left out of the layout by the pack's piece list", weight.villagePieceClass.getSimpleName());
            return true;
        });
    }

    @Inject(method = "generateAndAddComponent", at = @At("HEAD"), cancellable = true) private static void rdpl$plotCap(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        int most = ContentVillages.plotsMost();
        if (most > 0 && ContentVillages.plots(structureComponents) >= most) { cir.setReturnValue(null); }
    }

    @Inject(method = "generateAndAddRoadPiece", at = @At("HEAD"), cancellable = true) private static void rdpl$roadCap(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        int most = ContentVillages.plotsMost();
        if (most > 0 && ContentVillages.plots(structureComponents) >= most) { cir.setReturnValue(null); }
    }

    @ModifyVariable(method = "generateAndAddComponent", at = @At("HEAD"), ordinal = 0, argsOnly = true) private static int rdpl$standBackX(int placed, StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) { return ContentBeard.wanted() && facing != null ? placed + facing.getXOffset() : placed; }

    @ModifyVariable(method = "generateAndAddComponent", at = @At("HEAD"), ordinal = 2, argsOnly = true) private static int rdpl$standBackZ(int placed, StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) { return ContentBeard.wanted() && facing != null ? placed + facing.getZOffset() : placed; }

    @Inject(method = "generateAndAddComponent", at = @At("HEAD")) private static void rdpl$beginBuilding(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        ContentBeard.laying(structureComponents);
        ContentBeard.layingBuilding(true);
    }

    @Inject(method = "generateAndAddComponent", at = @At("RETURN"), cancellable = true) private static void rdpl$sparePlaza(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        StructureComponent placed = cir.getReturnValue();
        if (placed == null || placed instanceof StructureVillagePieces.Path) { return; }
        if (!ContentBeard.wanted() || start == null) { return; }
        StructureBoundingBox box = placed.getBoundingBox();
        int reach = ContentBeard.plazaReach() + 1;
        List<StructureBoundingBox> wells = BeardPlots.wellBoxes(structureComponents);
        if (wells.isEmpty()) { wells.add(start.getBoundingBox()); }
        for (StructureBoundingBox well : wells) {
            if (box.minX > well.maxX + reach || box.maxX < well.minX - reach || box.minZ > well.maxZ + reach || box.maxZ < well.minZ - reach) { continue; }
            ContentLog.LOGGER.debug("{} at {}, {} stands within {} of the well at {}, {}, closer than the plaza paving plus its verge, so it is not built", placed.getClass().getSimpleName(), box.minX, box.minZ, reach, well.minX, well.minZ);
            structureComponents.remove(placed);
            cir.setReturnValue(null);
            return;
        }
    }

    @SuppressWarnings("ConstantConditions") @Inject(method = "generateAndAddComponent", at = @At("RETURN"), cancellable = true) private static void rdpl$flatterFooting(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        StructureComponent placed = cir.getReturnValue();
        if (placed == null || placed instanceof StructureVillagePieces.Path) { return; }
        if (!ContentBeard.wanted() || start == null || facing == null) { return; }
        StructureBoundingBox box = placed.getBoundingBox();
        structureComponents.remove(placed);
        int sink = ContentBeard.footingSink(placed);
        int misfit = ContentBeard.footingMisfit(box, structureComponents, sink);
        if (misfit == 0) {
            structureComponents.add(placed);
            return;
        }
        int alongX = facing.getAxis() == EnumFacing.Axis.X ? 0 : 1;
        StructureBoundingBox well = start.getBoundingBox();
        int wellward = (alongX == 1 ? (well.minX + well.maxX) / 2 - (box.minX + box.maxX) / 2 : (well.minZ + well.maxZ) / 2 - (box.minZ + box.maxZ) / 2) >= 0 ? 1 : -1;
        int reach = ContentBeard.plazaReach() + 1;
        int bestMisfit = misfit;
        int bestSlide = 0;
        for (int step : new int[] { 2, 4, 6, 8, 10, 12, -2, -4, -6, -8, -10, -12 }) {
            int slide = step * wellward;
            StructureBoundingBox tried = new StructureBoundingBox(box);
            tried.offset(alongX * slide, 0, (1 - alongX) * slide);
            if (!(tried.minX > well.maxX + reach || tried.maxX < well.minX - reach || tried.minZ > well.maxZ + reach || tried.maxZ < well.minZ - reach)) { continue; }
            if (StructureComponent.findIntersecting(structureComponents, tried) != null) { continue; }
            int triedMisfit = ContentBeard.footingMisfit(tried, structureComponents, sink);
            if (triedMisfit < bestMisfit) {
                bestMisfit = triedMisfit;
                bestSlide = slide;
                if (bestMisfit == 0) { break; }
            }
        }
        if (bestMisfit == Integer.MAX_VALUE) {
            ContentLog.LOGGER.debug("{} at {}, {} would stand on an apron deeper than {} block(s) and found no better fit within 12 along its road, so it is not built", placed.getClass().getSimpleName(), box.minX, box.minZ, 2 + sink);
            cir.setReturnValue(null);
            return;
        }
        structureComponents.add(placed);
        if (bestSlide != 0) {
            box.offset(alongX * bestSlide, 0, (1 - alongX) * bestSlide);
            ContentLog.LOGGER.debug("{} at {}, {} slid {} along its road to a better fit, {} block(s) of apron in total instead of {}", placed.getClass().getSimpleName(), box.minX, box.minZ, bestSlide, bestMisfit, misfit == Integer.MAX_VALUE ? "too deep" : String.valueOf(misfit));
        }
    }

    @Inject(method = "generateAndAddComponent", at = @At("RETURN")) private static void rdpl$endBuilding(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        ContentBeard.layingBuilding(false);
        ContentBeard.laying(null);
    }
}
