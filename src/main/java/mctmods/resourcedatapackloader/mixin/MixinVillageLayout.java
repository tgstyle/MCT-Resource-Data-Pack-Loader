package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(StructureVillagePieces.class)
public abstract class MixinVillageLayout {
    @Inject(method = "generateAndAddComponent", at = @At("HEAD"))
    private static void rdpl$beginBuilding(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) { ContentBeard.layingBuilding(true); }

    @Inject(method = "generateAndAddComponent", at = @At("RETURN"))
    private static void rdpl$endBuilding(StructureVillagePieces.Start start, List<StructureComponent> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType, CallbackInfoReturnable<StructureComponent> cir) { ContentBeard.layingBuilding(false); }

    @Inject(method = "generateAndAddRoadPiece", at = @At("HEAD"))
    private static void rdpl$beginRoad(StructureVillagePieces.Start start, List<StructureComponent> p_176069_1_, Random rand, int p_176069_3_, int p_176069_4_, int p_176069_5_, EnumFacing facing, int p_176069_7_, CallbackInfoReturnable<StructureComponent> cir) { ContentBeard.layingRoad(true); }

    @Inject(method = "generateAndAddRoadPiece", at = @At("RETURN"))
    private static void rdpl$endRoad(StructureVillagePieces.Start start, List<StructureComponent> p_176069_1_, Random rand, int p_176069_3_, int p_176069_4_, int p_176069_5_, EnumFacing facing, int p_176069_7_, CallbackInfoReturnable<StructureComponent> cir) { ContentBeard.layingRoad(false); }
}
