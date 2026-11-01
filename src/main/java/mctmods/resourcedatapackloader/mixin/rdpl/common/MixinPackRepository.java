package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.pack.PackFinder;
import mctmods.resourcedatapackloader.pack.PackListPacks;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(PackRepository.class) public abstract class MixinPackRepository {
    @Shadow private List<Pack> selected;
    @Shadow @Final private Set<RepositorySource> sources;

    @Inject(method = {"reload", "setSelected"}, at = @At("TAIL")) private void rdpl$seatPacks(CallbackInfo ci) { selected = PackFinder.seat(selected, PackFinder.servesClient(sources)); }

    @Inject(method = "discoverAvailable()Ljava/util/Map;", at = @At("RETURN"), cancellable = true) private void rdpl$packList(CallbackInfoReturnable<Map<String, Pack>> cir) {
        if (PackFinder.servesClient(sources)) { cir.setReturnValue(PackListPacks.apply(cir.getReturnValue())); }
    }
}
