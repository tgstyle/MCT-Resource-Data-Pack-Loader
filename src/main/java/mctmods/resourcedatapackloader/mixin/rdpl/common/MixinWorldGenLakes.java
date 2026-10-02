package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenLakes.class) public class MixinWorldGenLakes {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$spareRoads(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn.isRemote || !ContentBeard.wanted()) { return; }
        StructureBoundingBox box = ContentStructureSearch.villageRoadIn(worldIn, position.getX() - 8, position.getY() - 16, position.getZ() - 8, position.getX() + 7, position.getY() + 20, position.getZ() + 7);
        if (box == null) { return; }
        ContentLog.LOGGER.debug("A lake at {}, {}, {} would flood the road at {}, {}, so it is not made", position.getX(), position.getY(), position.getZ(), box.minX, box.minZ);
        cir.setReturnValue(false);
    }
}
