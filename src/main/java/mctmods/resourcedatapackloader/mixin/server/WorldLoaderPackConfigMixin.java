package mctmods.resourcedatapackloader.mixin.server;

import mctmods.resourcedatapackloader.common.pack.loading.PackFinder;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import java.util.List;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldLoader.PackConfig.class) public abstract class WorldLoaderPackConfigMixin {
    @Definition(id = "openAllSelected", method = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;") @Expression("? = ?.openAllSelected()") @ModifyVariable(method = "createResourceManager()Lcom/mojang/datafixers/util/Pair;", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), name = "openedPacks") private List<PackResources> rdpl$beforeOwn(List<PackResources> openedPacks) { return PackFinder.beforeOwn(openedPacks); }
}
