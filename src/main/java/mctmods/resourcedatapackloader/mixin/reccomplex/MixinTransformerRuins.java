package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerRuins;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(value = TransformerRuins.class, remap = false) public abstract class MixinTransformerRuins {
    @Unique private static boolean rdpl$told;

    @Inject(method = "decayBlock", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$leaveLandAlone(World world, Random random, IBlockState state, BlockPos pos, StructureBoundingBox boundingBox, CallbackInfo ci) {
        if (world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null) { return; }
        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("A ruin was being weathered into land that has not been made yet. Making it there and then would have made everything around it in turn, so that part is left unweathered");
        }
        ci.cancel();
    }
}
