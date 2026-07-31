package mctmods.resourcedatapackloader.mixin.vanillaportals;

import mctmods.resourcedatapackloader.content.gate.VanillaPortalLink;

import net.minecraft.entity.Entity;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.Teleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Teleporter.class)
public abstract class MixinTeleporter {
    @Redirect(method = "placeInExistingPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;setPlayerLocation(DDDFF)V"))
    private void rdpl$placePlayer(NetHandlerPlayServer connection, double x, double y, double z, float yaw, float pitch) {
        double[] stored = VanillaPortalLink.stored(connection.player);
        if (stored.length != 3) {
            connection.setPlayerLocation(x, y, z, yaw, pitch);
            return;
        }
        connection.setPlayerLocation(stored[0], stored[1], stored[2], connection.player.rotationYaw, connection.player.rotationPitch);
    }

    @Redirect(method = "placeInExistingPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setLocationAndAngles(DDDFF)V"))
    private void rdpl$placeEntity(Entity entity, double x, double y, double z, float yaw, float pitch) {
        double[] stored = VanillaPortalLink.stored(entity);
        if (stored.length != 3) {
            entity.setLocationAndAngles(x, y, z, yaw, pitch);
            return;
        }
        entity.setLocationAndAngles(stored[0], stored[1], stored[2], entity.rotationYaw, entity.rotationPitch);
    }
}
