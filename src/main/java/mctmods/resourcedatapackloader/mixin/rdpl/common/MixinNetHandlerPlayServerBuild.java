package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class) public abstract class MixinNetHandlerPlayServerBuild {
    @Shadow public EntityPlayerMP player;

    @Redirect(method = "processTryUseItemOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getBuildLimit()I"))
    private int rdpl$buildCeiling(MinecraftServer server) {
        World world = player.world;
        return ((IRubicWorld) world).rdpl$isRubicWorld() ? ((IRubicWorld) world).rdpl$getMaxHeight() : server.getBuildLimit();
    }

    @Inject(method = "processTryUseItemOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;markPlayerActive()V", shift = At.Shift.AFTER), cancellable = true)
    private void rdpl$buildFloor(CPacketPlayerTryUseItemOnBlock packetIn, CallbackInfo ci) {
        World world = player.world;
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        int floor = ((IRubicWorld) world).rdpl$getMinHeight();
        BlockPos at = packetIn.getPos();
        if (at.getY() > floor || (packetIn.getDirection() != EnumFacing.DOWN && at.getY() >= floor)) { return; }
        TextComponentTranslation said = new TextComponentTranslation("rdpl.build.toolow", floor);
        said.getStyle().setColor(TextFormatting.RED);
        player.connection.sendPacket(new SPacketChat(said, ChatType.GAME_INFO));
        player.connection.sendPacket(new SPacketBlockChange(world, at));
        player.connection.sendPacket(new SPacketBlockChange(world, at.offset(packetIn.getDirection())));
        ci.cancel();
    }
}
