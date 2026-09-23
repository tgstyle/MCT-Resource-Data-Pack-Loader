package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(WorldGenLiquids.class) public abstract class MixinWorldGenLiquids {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$notIntoACut(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        StructureBoundingBox near = new StructureBoundingBox(position.getX() - 1, 0, position.getZ() - 1, position.getX() + 1, 0, position.getZ() + 1);
        List<RailPiece> lines = BeardRails.railways(worldIn, near);
        List<StructureComponent> tunnels = BeardRoadsTunnels.tunnels(worldIn, near);
        if (lines.isEmpty() && tunnels.isEmpty()) { return; }
        boolean met = BeardRails.runsInto(worldIn, lines, position.getX(), position.getY(), position.getZ()) || BeardRoadsTunnels.runsIntoTunnel(tunnels, position.getX(), position.getY(), position.getZ());
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
            BlockPos beside = position.offset(side);
            met |= BeardRails.runsInto(worldIn, lines, beside.getX(), beside.getY(), beside.getZ()) || BeardRoadsTunnels.runsIntoTunnel(tunnels, beside.getX(), beside.getY(), beside.getZ());
        }
        if (!met) { return; }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A spring at {}, {}, {} would have run into a cut, bore or tunnel, so it is left out", position.getX(), position.getY(), position.getZ()); }
        cir.setReturnValue(false);
    }
}
