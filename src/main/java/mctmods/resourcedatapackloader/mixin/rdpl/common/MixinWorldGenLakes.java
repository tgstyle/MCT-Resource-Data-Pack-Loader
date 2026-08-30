package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenLakes.class) public class MixinWorldGenLakes {
    @Unique private int rdpl$minY;

    @Inject(method = "generate", at = @At("HEAD")) private void onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) {
        this.rdpl$minY = Coords.getMinCubePopulationPos(position.getY());
    }

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$spareRoads(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn.isRemote || !ContentBeard.wanted()) { return; }
        int least = position.getX() - 8;
        int most = position.getX() + 7;
        int leastZ = position.getZ() - 8;
        int mostZ = position.getZ() + 7;
        for (StructureStart start : ContentStructureSearch.villageStarts(worldIn)) {
            if (!start.getBoundingBox().intersectsWith(least, leastZ, most, mostZ)) { continue; }
            for (StructureComponent piece : start.getComponents()) {
                if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                StructureBoundingBox box = piece.getBoundingBox();
                if (position.getY() + 8 < box.minY - 12 || position.getY() - 4 > box.maxY + 12) { continue; }
                if (box.intersectsWith(least, leastZ, most, mostZ)) {
                    ContentLog.LOGGER.debug("A lake at {}, {}, {} would flood the road at {}, {}, so it is not made", position.getX(), position.getY(), position.getZ(), box.minX, box.minZ);
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 5, ordinal = 0)) private int getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return rdpl$minY; }
        return orig;
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 4, ordinal = 0)) private int getMinWorldHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight() + orig;
    }
}
