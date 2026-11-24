package mctmods.resourcedatapackloader.mixin.integration.quark;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentCascade;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.world.feature.Archaeologist;
import vazkii.quark.world.world.ArchaeologistHouseGenerator;

@Mixin(value = ArchaeologistHouseGenerator.class, remap = false) public abstract class MixinArchaeologistHouseGenerator {
    @Unique private static final int rdpl$PLACE_OFFSET = 7;

    @Inject(method = "generateHouse", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$requireLoaded(WorldServer world, BlockPos pos, EnumFacing face, CallbackInfo ci) {
        int reach = rdpl$PLACE_OFFSET + ContentCascade.templateReach(world, Archaeologist.HOUSE_STRUCTURE);
        if (!ContentCascade.loaded(world, pos, reach)) { ci.cancel(); }
    }
}
