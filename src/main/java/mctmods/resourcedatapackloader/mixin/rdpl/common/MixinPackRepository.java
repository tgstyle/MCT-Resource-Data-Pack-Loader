package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(PackRepository.class) public abstract class MixinPackRepository {
    @Shadow private List<Pack> selected;

    @Inject(method = {"reload", "setSelected"}, at = @At("TAIL"))
    private void rdpl$seatPacks(CallbackInfo ci) { selected = PackFinder.seat(selected); }
}
