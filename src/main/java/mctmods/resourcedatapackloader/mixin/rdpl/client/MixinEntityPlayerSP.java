package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.core.MixinUtils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class) public class MixinEntityPlayerSP extends AbstractClientPlayer {
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) { super(worldIn, playerProfile); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.0D)) private double replaceEntityYForBlockPos(double value) { return this.posY; }

    @Redirect(method = "onUpdate",
            at = @At(target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/math/BlockPos;)Z", value = "INVOKE"))
    private boolean canEntityUpdate_isBlockLoadedRedirect(World world, BlockPos pos) { return MixinUtils.canTickPosition(world, pos); }
}
