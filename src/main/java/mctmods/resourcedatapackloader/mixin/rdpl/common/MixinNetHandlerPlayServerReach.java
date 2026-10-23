package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.item.ContentWeapons;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetHandlerPlayServer.class) public abstract class MixinNetHandlerPlayServerReach {
    @Shadow public EntityPlayerMP player;

    @ModifyConstant(method = "processUseEntity", constant = @Constant(doubleValue = 36.0D)) private double rdpl$seenReach(double vanilla) { return ContentWeapons.reachSqr(player, vanilla, 3.0D); }

    @ModifyConstant(method = "processUseEntity", constant = @Constant(doubleValue = 9.0D)) private double rdpl$blindReach(double vanilla) { return ContentWeapons.reachSqr(player, vanilla, 0.0D); }
}
