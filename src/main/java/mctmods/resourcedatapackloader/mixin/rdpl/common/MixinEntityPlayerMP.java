package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityPlayerMP.class) public abstract class MixinEntityPlayerMP {
    @ModifyConstant(method = "<init>", constant = @Constant(doubleValue = 255)) private double rdpl$unstuckCeiling(double orig, MinecraftServer server, WorldServer worldIn, GameProfile profile, PlayerInteractionManager interactionManager) {
        if (!((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return orig; }
        EntityPlayerMP self = (EntityPlayerMP) (Object) this;
        if (!worldIn.isBlockLoaded(new BlockPos(self))) { return Double.NEGATIVE_INFINITY; }
        return ((IRubicWorld) worldIn).rdpl$getMaxHeight() - 1;
    }
}
