package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Collection;
import java.util.List;

@Mixin(PackRepository.class) public abstract class MixinPackRepository {
    @Inject(method = "rebuildSelected", at = @At("RETURN"), cancellable = true)
    private void rdpl$seatPacks(Collection<String> ids, CallbackInfoReturnable<List<Pack>> cir) { cir.setReturnValue(PackFinder.seat(cir.getReturnValue())); }
}
