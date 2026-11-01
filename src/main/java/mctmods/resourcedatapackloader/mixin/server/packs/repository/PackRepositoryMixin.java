package mctmods.resourcedatapackloader.mixin.server.packs.repository;

import mctmods.resourcedatapackloader.common.pack.loading.PackFinder;
import mctmods.resourcedatapackloader.common.pack.loading.PackListPacks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PackRepository.class) public abstract class PackRepositoryMixin {
    @Shadow @Final private Set<RepositorySource> sources;

    @WrapMethod(method = "rebuildSelected") private List<Pack> rdpl$seatPacks(Collection<String> selectedNames, Operation<List<Pack>> original) { return PackFinder.seat(original.call(selectedNames), PackFinder.servesClient(sources)); }

    @Inject(method = "discoverAvailable()Ljava/util/Map;", at = @At("RETURN"), cancellable = true) private void rdpl$packList(CallbackInfoReturnable<Map<String, Pack>> cir) {
        if (PackFinder.servesClient(sources)) { cir.setReturnValue(PackListPacks.apply(cir.getReturnValue())); }
    }
}
