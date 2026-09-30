package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.recipe.RecipeAdvancements;
import mctmods.resourcedatapackloader.recipe.RecipeFiles;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ResourceManagerRegistryLoadTask.class) public abstract class MixinRecipeLoadTask {
    @Shadow @Final private ResourceManager resourceManager;
    @Shadow @Final private List<Registry.PendingTags<?>> pendingTags;

    @WrapOperation(method = "load(Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Map<Identifier, Resource>> rdpl$recipes(Supplier<Map<Identifier, Resource>> supplier, Executor executor, Operation<CompletableFuture<Map<Identifier, Resource>>> original, @Local(argsOnly = true) RegistryOps.RegistryInfoLookup context) {
        if (!Registries.RECIPE.equals(((IRegistryLoadTask) this).rdpl$registryKey())) { return original.call(supplier, executor); }
        Supplier<Map<Identifier, Resource>> filtered = () -> {
            try { return RecipeFiles.filter(supplier.get(), resourceManager, context, pendingTags); }
            finally { RecipeAdvancements.publish(context, Set.of()); }
        };
        return original.call(filtered, executor);
    }
}
