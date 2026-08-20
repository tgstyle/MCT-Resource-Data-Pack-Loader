package mctmods.resourcedatapackloader.mixin.paperfixes;

import mctmods.resourcedatapackloader.content.compat.PaperPathCache;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathFinder.class) public abstract class MixinPathFinderCache {
    @Inject(method = "findPath(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/entity/EntityLiving;DDDF)Lnet/minecraft/pathfinding/Path;", at = @At("HEAD"))
    private void rdpl$openPaperCache(IBlockAccess worldIn, EntityLiving entitylivingIn, double x, double y, double z, float maxDistance, CallbackInfoReturnable<Path> cir) { PaperPathCache.open(); }

    @Inject(method = "findPath(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/entity/EntityLiving;DDDF)Lnet/minecraft/pathfinding/Path;", at = @At("RETURN"))
    private void rdpl$closePaperCache(IBlockAccess worldIn, EntityLiving entitylivingIn, double x, double y, double z, float maxDistance, CallbackInfoReturnable<Path> cir) { PaperPathCache.close(); }
}
