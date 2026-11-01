package mctmods.resourcedatapackloader.mixin.world.gen.structure;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;

import java.util.Random;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureMineshaftPieces.Corridor.class) public abstract class MixinMineshaftCorridor {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;setEntityId(Lnet/minecraft/util/ResourceLocation;)V")) private void rdpl$spawner(MobSpawnerBaseLogic logic, ResourceLocation id, World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) { logic.setEntityId(ContentStructurePlacement.spawner(ContentStructurePlacement.MINESHAFTS, id, randomIn)); }
}
