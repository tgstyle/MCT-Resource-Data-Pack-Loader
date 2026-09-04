package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLayout;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IVillageBlock;
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

    @Inject(method = "generateAndAddRoadPiece", at = @At("HEAD"), cancellable = true) private static void rdpl$roadCap(StructureVillagePieces.Start start, List<StructureComponent> p_176069_1_, Random rand, int p_176069_3_, int p_176069_4_, int p_176069_5_, EnumFacing facing, int p_176069_7_, CallbackInfoReturnable<StructureComponent> cir) {
        if (CityGrowth.bulbLaying() || CityGrowth.alleyLaying() || CityLayout.laying()) {
            cir.setReturnValue(null);
            return;
        }
        int most = ContentVillages.plotsMost();
        if (most > 0 && ContentVillages.plots(p_176069_1_) >= most) { cir.setReturnValue(null); }
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
            if (!BeardPlots.nearWell(box, well, reach)) { continue; }
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
        if (!BeardLayout.flatterFooting(placed, start, structureComponents, facing)) { cir.setReturnValue(null); }
    }

    @Inject(method = "generateAndAddComponent", at = @At("RETURN")) private static void rdpl$endBuilding(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) {
        ContentBeard.layingBuilding(false);
        ContentBeard.laying(null);
        StructureComponent placed = cir.getReturnValue();
        if (placed instanceof IVillageBlock && start instanceof IVillageBlock) { ((IVillageBlock) placed).rdpl$block(((IVillageBlock) start).rdpl$block()); }
    }
}
