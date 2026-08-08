package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.ContentHardness;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelSeed {
    @Redirect(method = "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getPositionRandom(Lnet/minecraft/util/math/Vec3i;)J"))
    private long rdpl$seedByGroup(Vec3i pos, IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, BufferBuilder buffer, boolean checkSides) {
        long fallback = MathHelper.getPositionRandom(pos);
        if (!ContentHardness.anyRolls()) { return fallback; }

        return ContentHardness.modelSeed(blockStateIn, pos, fallback);
    }
}
