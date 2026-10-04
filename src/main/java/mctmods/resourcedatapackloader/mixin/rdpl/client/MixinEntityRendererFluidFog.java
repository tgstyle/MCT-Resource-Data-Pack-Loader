package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentFogSampler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class) public abstract class MixinEntityRendererFluidFog {
    @Redirect(method = "updateFogColor(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getFogColor(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;", remap = false))
    private Vec3d rdpl$fluidFog(Block block, World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) { return ContentFogSampler.fluidFog(state, block.getFogColor(world, pos, state, entity, originalColor, partialTicks)); }
}
