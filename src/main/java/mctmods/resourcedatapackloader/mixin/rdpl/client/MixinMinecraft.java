package mctmods.resourcedatapackloader.mixin.rdpl.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(Minecraft.class) public abstract class MixinMinecraft {
    @Definition(id = "openAllSelected", method = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;")
    @Expression("? = ?.openAllSelected()")
    @ModifyVariable(method = { "<init>", "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;" }, at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private List<PackResources> rdpl$beforeOwn(List<PackResources> opened) { return PackFinder.beforeOwn(opened); }
}
