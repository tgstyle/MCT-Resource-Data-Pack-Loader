package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(Minecraft.class) public abstract class MixinMinecraft {
    @ModifyVariable(method = {"<init>", "reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;"}, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;"), name = "list")
    private List<PackResources> rdpl$beforeOwn(List<PackResources> list) { return PackFinder.beforeOwn(list); }
}
