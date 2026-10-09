package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBed.class) public abstract class MixinBlockBedDimensionOverlay {
    @WrapOperation(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canSleepAt(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/WorldProvider$WorldSleepResult;", remap = false)) private WorldProvider.WorldSleepResult rdpl$existingBeds(WorldProvider provider, EntityPlayer player, BlockPos pos, Operation<WorldProvider.WorldSleepResult> original) {
        if (ContentDimensionRules.existingBedsExplode(provider)) { return WorldProvider.WorldSleepResult.BED_EXPLODES; }
        return original.call(provider, player, pos);
    }
}
