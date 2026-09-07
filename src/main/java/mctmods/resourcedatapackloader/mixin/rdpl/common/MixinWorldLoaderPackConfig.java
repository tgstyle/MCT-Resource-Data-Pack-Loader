package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(value = WorldLoader.PackConfig.class, priority = 900) public abstract class MixinWorldLoaderPackConfig {
    @ModifyVariable(method = "createResourceManager", at = @At("STORE"), name = "closeableresourcemanager")
    private CloseableResourceManager rdpl$beforeOwn(CloseableResourceManager closeableresourcemanager) {
        if (!(closeableresourcemanager instanceof MultiPackResourceManager packs)) { return closeableresourcemanager; }
        List<PackResources> opened = packs.listPacks().toList();
        List<PackResources> ordered = PackFinder.beforeOwn(opened);
        return ordered == opened ? closeableresourcemanager : new MultiPackResourceManager(PackType.SERVER_DATA, ordered);
    }
}
