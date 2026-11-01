package mctmods.resourcedatapackloader.mixin.client;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.client.util.PackListClient;
import mctmods.resourcedatapackloader.common.pack.loading.PackFinder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraftforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class) public abstract class MinecraftMixin {
    @ModifyVariable(method = {"<init>", "reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;"}, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;")) @SuppressWarnings("ModifyVariableMayUseName") private List<PackResources> rdpl$beforeOwn(List<PackResources> list) { return PackFinder.beforeOwn(list); }

    @Inject(method = "clearResourcePacksOnError(Ljava/lang/Throwable;Lnet/minecraft/network/chat/Component;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;save()V")) private void rdpl$fallBack(CallbackInfo ci) { PackListClient.failed((Minecraft) (Object) this); }

    @Inject(method = "abortResourcePackRecovery()V", at = @At("HEAD"), cancellable = true) private void rdpl$dropAfterFailedRecovery(CallbackInfo ci) {
        if (PackListClient.dropAfterFailedRecovery((Minecraft) (Object) this)) { ci.cancel(); }
        else { rdpl$gameLoaded = true; }
    }

    @Redirect(method = "rollbackResourcePacks", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z")) private boolean rdpl$clearableOrAlwaysOn(Stream<Pack> packs, Predicate<? super Pack> clearable) { return packs.anyMatch(clearable) || PackListClient.holdsAlwaysOn(); }

    @Unique private boolean rdpl$gameLoaded;

    @Inject(method = "onGameLoadFinished", at = @At("HEAD")) private void rdpl$markGameLoaded(CallbackInfo ci) { rdpl$gameLoaded = true; }

    @Redirect(method = "clearResourcePacksOnError", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenRun(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;")) private CompletableFuture<Void> rdpl$finishFailedStart(CompletableFuture<Void> reload, Runnable action) {
        return reload.thenRun(() -> {
            if (rdpl$gameLoaded) { return; }
            rdpl$gameLoaded = true;
            if (!ClientModLoader.completeModLoading()) { Minecraft.getInstance().setScreen(new TitleScreen(true)); }
        }).thenRun(action);
    }

    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "getSituationalMusic", at = @At("RETURN"), cancellable = true) private void rdpl$dimensionMusic(CallbackInfoReturnable<Music> cir) {
        if (player == null || cir.getReturnValue() == Musics.CREDITS) { return; }
        Music music = ContentDimensionAmbience.music(player.level());
        if (music != null) { cir.setReturnValue(music); }
    }
}
