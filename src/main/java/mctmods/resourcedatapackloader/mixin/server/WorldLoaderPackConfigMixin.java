package mctmods.resourcedatapackloader.mixin.server;

import mctmods.resourcedatapackloader.common.pack.loading.PackFinder;

import java.util.List;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = WorldLoader.PackConfig.class, priority = 1100) public abstract class WorldLoaderPackConfigMixin {
    @ModifyVariable(method = "createResourceManager", at = @At("STORE")) @SuppressWarnings("ModifyVariableMayUseName") private CloseableResourceManager rdpl$beforeOwn(CloseableResourceManager closeableresourcemanager) {
        if (!(closeableresourcemanager instanceof MultiPackResourceManager packs)) { return closeableresourcemanager; }
        List<PackResources> opened = packs.listPacks().toList();
        List<PackResources> ordered = PackFinder.beforeOwn(opened);
        return ordered == opened ? closeableresourcemanager : new MultiPackResourceManager(PackType.SERVER_DATA, ordered);
    }
}
