package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.gate.ContentGates;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(value = WorldUtil.class, remap = false) public class MixinWorldUtilGates {
    @Inject(method = "getPossibleDimensionsForSpaceshipTier(ILnet/minecraft/entity/player/EntityPlayerMP;)Ljava/util/List;", at = @At("RETURN")) private static void rdpl$gatedPlanets(int tier, EntityPlayerMP playerBase, CallbackInfoReturnable<List<Integer>> cir) {
        if (playerBase == null) { return; }
        cir.getReturnValue().removeIf(dimension -> ContentGates.barsDimension(playerBase, dimension));
    }
}
