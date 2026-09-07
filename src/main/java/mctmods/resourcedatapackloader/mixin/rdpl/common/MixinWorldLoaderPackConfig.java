package mctmods.resourcedatapackloader.mixin.rdpl.common;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import mctmods.resourcedatapackloader.pack.PackFinder;

import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(WorldLoader.PackConfig.class) public abstract class MixinWorldLoaderPackConfig {
    @Definition(id = "openAllSelected", method = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;")
    @Expression("? = ?.openAllSelected()")
    @ModifyVariable(method = "createResourceManager", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private List<PackResources> rdpl$beforeOwn(List<PackResources> opened) { return PackFinder.beforeOwn(opened); }
}
