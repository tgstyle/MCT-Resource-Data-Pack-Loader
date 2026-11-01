package mctmods.resourcedatapackloader.mixin.client.resources;

import mctmods.resourcedatapackloader.client.util.PackInjector;

import java.util.List;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SimpleReloadableResourceManager.class) public abstract class MixinSimpleReloadableResourceManager {
    @ModifyVariable(method = "reloadResources(Ljava/util/List;)V", at = @At("HEAD"), argsOnly = true) private List<IResourcePack> rdpl$insertPacks(List<IResourcePack> list) { return PackInjector.insert(list); }
}
