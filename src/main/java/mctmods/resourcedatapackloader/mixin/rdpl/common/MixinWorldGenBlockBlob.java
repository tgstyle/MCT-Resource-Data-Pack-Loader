package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenBlockBlob.class) public class MixinWorldGenBlockBlob {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$spareRoads(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn.isRemote || !ContentBeard.wanted()) { return; }
        StructureBoundingBox box = ContentStructureSearch.villageRoadIn(worldIn, position.getX() - 3, position.getY() - 28, position.getZ() - 3, position.getX() + 1, position.getY() + 14, position.getZ() + 1);
        if (box == null) { return; }
        ContentLog.LOGGER.debug("A boulder at {}, {}, {} would stand on the road at {}, {}, so it is not made", position.getX(), position.getY(), position.getZ(), box.minX, box.minZ);
        cir.setReturnValue(false);
    }
}
