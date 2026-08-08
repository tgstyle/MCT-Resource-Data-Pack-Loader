package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.pack.PackInjector;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinSimpleReloadableResourceManager {
    @ModifyVariable(method = "reloadResources(Ljava/util/List;)V", at = @At("HEAD"), argsOnly = true)
    private List<IResourcePack> rdpl$insertPacks(List<IResourcePack> list) { return PackInjector.insert(list); }
}
