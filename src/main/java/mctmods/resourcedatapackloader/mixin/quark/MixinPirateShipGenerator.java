package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.world.world.PirateShipGenerator;
import java.util.Random;

@Mixin(value = PirateShipGenerator.class, remap = false)
public abstract class MixinPirateShipGenerator {
    @Unique private static final int SAFE_OFFSET = 8;
    @Unique private static final int SHIP_REACH = 24;

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0), remap = false)
    private int rdpl$offsetX(Random random, int bound) { return random.nextInt(bound) + SAFE_OFFSET; }

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 1), remap = false)
    private int rdpl$offsetZ(Random random, int bound) { return random.nextInt(bound) + SAFE_OFFSET; }

    @Inject(method = "generateShipAt", at = @At("HEAD"), cancellable = true, remap = false)
    private static void rdpl$requireLoaded(WorldServer world, Random random, BlockPos pos, CallbackInfo ci) {
        if (!ContentCascade.loaded(world, pos, SHIP_REACH)) { ci.cancel(); }
    }
}
