package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Template.class)
public abstract class MixinTemplate {
    @Redirect(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    private void rdpl$onlyTellLoaded(World world, BlockPos pos, Block block, boolean skipDrops) {
        if (!ContentCascade.loaded(world, pos, 1)) { return; }

        world.notifyNeighborsRespectDebug(pos, block, skipDrops);
    }
}
