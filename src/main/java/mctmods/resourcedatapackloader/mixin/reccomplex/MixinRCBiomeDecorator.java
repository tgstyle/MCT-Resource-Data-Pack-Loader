package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;

import ivorius.reccomplex.world.gen.feature.decoration.RCBiomeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;
import java.util.Random;

@SuppressWarnings("unused") @Mixin(value = RCBiomeDecorator.class, remap = false) public abstract class MixinRCBiomeDecorator {
    @Inject(method = "decorate(Lnet/minecraft/world/WorldServer;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Livorius/reccomplex/world/gen/feature/decoration/RCBiomeDecorator$DecorationType;)Lnet/minecraftforge/fml/common/eventhandler/Event$Result;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void rdpl$leaveDecorationAlone(WorldServer worldIn, Random random, BlockPos chunkPos, RCBiomeDecorator.DecorationType type, CallbackInfoReturnable<Event.Result> cir) {
        if (ContentStructures.blocks(worldIn, "reccomplex")) { cir.setReturnValue(Event.Result.DEFAULT); }
    }

    @Inject(method = "decorate(Lnet/minecraft/world/WorldServer;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Livorius/reccomplex/world/gen/feature/decoration/RCBiomeDecorator$DecorationType;I)I",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void rdpl$leaveDecorationAmountAlone(WorldServer worldIn, Random random, BlockPos chunkPos, RCBiomeDecorator.DecorationType type, int amount, CallbackInfoReturnable<Integer> cir) {
        if (ContentStructures.blocks(worldIn, "reccomplex")) { cir.setReturnValue(amount); }
    }
}
