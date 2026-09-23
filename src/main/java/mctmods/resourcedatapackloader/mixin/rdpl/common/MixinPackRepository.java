package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.pack.PackFinder;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Collection;
import java.util.List;

@Mixin(PackRepository.class) public abstract class MixinPackRepository {
    @WrapMethod(method = "rebuildSelected")
    private List<Pack> rdpl$seatPacks(Collection<String> ids, Operation<List<Pack>> original) { return PackFinder.seat(original.call(ids)); }
}
