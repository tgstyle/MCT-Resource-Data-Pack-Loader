package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;

@Mixin(StructureMineshaftPieces.Corridor.class) public abstract class MixinMineshaftCorridor {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;setEntityId(Lnet/minecraft/util/ResourceLocation;)V"))
    private void rdpl$spawner(MobSpawnerBaseLogic logic, ResourceLocation id) {
        logic.setEntityId(ContentStructurePlacement.spawner(ContentStructurePlacement.MINESHAFTS, id, new Random()));
    }
}
