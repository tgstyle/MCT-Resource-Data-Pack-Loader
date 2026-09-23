package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentProspect;
import mctmods.resourcedatapackloader.loot.BlockDrops;
import mctmods.resourcedatapackloader.util.FaceHiding;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(Block.class) public abstract class MixinBlock {
    @Redirect(method = "shouldRenderFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;supportsExternalFaceHiding()Z", remap = false)) private static boolean rdpl$cachedFaceHiding(BlockState state) {
        return ((FaceHiding) state).rdpl$faceHiding();
    }

    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"), cancellable = true) private static void rdpl$prospectSample(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!ContentProspect.withholdsDrops(entity)) { return; }
        cir.setReturnValue(BlockDrops.prospected(state, level, pos, entity, tool));
    }
}
