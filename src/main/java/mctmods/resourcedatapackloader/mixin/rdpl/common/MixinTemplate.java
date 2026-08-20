package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Template.class) public abstract class MixinTemplate {
    @Redirect(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    private void rdpl$notifyOnlyMadeGround(World world, BlockPos pos, Block blockType, boolean updateObservers) {
        if (IChunk.rdpl$getPopulating() == null) {
            world.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
            return;
        }
        if (world.getWorldInfo().getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) { return; }
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos beside = pos.offset(side);
            if (world.isAreaLoaded(beside, 1)) { world.neighborChanged(beside, blockType, pos); }
        }
    }
}
