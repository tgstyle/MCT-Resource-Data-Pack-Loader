package mctmods.resourcedatapackloader.mixin.world.level.levelgen.structure.templatesystem;

import mctmods.resourcedatapackloader.common.worldgen.feature.tree.TreeRecorder;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTemplate.class) public abstract class StructureTemplateMixin {
    @Inject(method = "updateShapeAtEdge(Lnet/minecraft/world/level/LevelAccessor;ILnet/minecraft/world/phys/shapes/DiscreteVoxelShape;III)V", at = @At("HEAD"), cancellable = true) private static void rdpl$heldEdge(LevelAccessor level, int updateMode, DiscreteVoxelShape shape, int startX, int startY, int startZ, CallbackInfo ci) {
        if (TreeRecorder.heldEdge(level, updateMode, shape, startX, startY, startZ)) { ci.cancel(); }
    }
}
