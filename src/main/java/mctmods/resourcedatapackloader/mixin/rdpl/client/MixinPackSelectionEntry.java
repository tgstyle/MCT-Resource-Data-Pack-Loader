package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(targets = "net.minecraft.client.gui.screens.packs.PackSelectionModel$EntryBase") public abstract class MixinPackSelectionEntry {
    @Shadow @Final private Pack pack;
    @Shadow protected abstract List<Pack> getSelfList();
    @Shadow protected abstract List<Pack> getOtherList();

    @Inject(method = "toggleSelection", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V")) private void rdpl$seatLanded(CallbackInfo ci) {
        if (!((PackSelectionModel.Entry) this).isSelected()) { PackFinder.seatShown(getOtherList()); }
    }

    @Inject(method = "canMoveUp", at = @At("RETURN"), cancellable = true) private void rdpl$stayAboveBase(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && (PackFinder.crossesBase(getSelfList(), pack, -1) || PackFinder.fixedBeside(getSelfList(), pack, -1))) { cir.setReturnValue(false); }
    }

    @Inject(method = "canMoveDown", at = @At("RETURN"), cancellable = true) private void rdpl$stayBelowBase(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && (PackFinder.crossesBase(getSelfList(), pack, 1) || PackFinder.fixedBeside(getSelfList(), pack, 1))) { cir.setReturnValue(false); }
    }
}
