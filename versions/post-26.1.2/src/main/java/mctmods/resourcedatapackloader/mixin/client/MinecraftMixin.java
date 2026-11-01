package mctmods.resourcedatapackloader.mixin.client;

import mctmods.resourcedatapackloader.client.util.PackListClient;
import mctmods.resourcedatapackloader.common.pack.loading.PackFinder;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class) public abstract class MinecraftMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature") @Definition(id = "openAllSelected", method = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;") @Expression("? = ?.openAllSelected()") @ModifyVariable(method = { "<init>(Lnet/minecraft/client/main/GameConfig;)V", "reloadResourcePacks(ZLnet/minecraft/client/GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;" }, at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER)) private List<PackResources> rdpl$beforeOwn(List<PackResources> list) { return PackFinder.beforeOwn(list); }

    @Inject(method = "clearResourcePacksOnError", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;save()V")) private void rdpl$fallBack(CallbackInfo ci) { PackListClient.failed((Minecraft) (Object) this); }

    @Inject(method = "abortResourcePackRecovery()V", at = @At("HEAD"), cancellable = true) private void rdpl$dropAfterFailedRecovery(CallbackInfo ci) {
        if (PackListClient.dropAfterFailedRecovery((Minecraft) (Object) this)) { ci.cancel(); }
    }

    @Redirect(method = "rollbackResourcePacks", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z")) private boolean rdpl$clearableOrAlwaysOn(Stream<Pack> packs, Predicate<? super Pack> clearable) { return packs.anyMatch(clearable) || PackListClient.holdsAlwaysOn(); }
}
