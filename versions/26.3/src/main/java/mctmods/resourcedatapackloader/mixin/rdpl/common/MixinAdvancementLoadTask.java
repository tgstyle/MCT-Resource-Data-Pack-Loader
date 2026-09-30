package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.recipe.RecipeAdvancements;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ResourceManagerRegistryLoadTask.class) public abstract class MixinAdvancementLoadTask {
    @WrapOperation(method = "load(Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Map<Identifier, Resource>> rdpl$advancements(Supplier<Map<Identifier, Resource>> supplier, Executor executor, Operation<CompletableFuture<Map<Identifier, Resource>>> original, @Local(argsOnly = true) RegistryOps.RegistryInfoLookup context) {
        CompletableFuture<Map<Identifier, Resource>> listed = original.call(supplier, executor);
        if (!Registries.ADVANCEMENT.equals(((IRegistryLoadTask) this).rdpl$registryKey())) { return listed; }
        return RecipeAdvancements.pruned(context, listed);
    }
}
