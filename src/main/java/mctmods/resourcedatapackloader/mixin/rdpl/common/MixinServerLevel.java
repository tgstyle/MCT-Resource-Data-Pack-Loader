package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class) public abstract class MixinServerLevel {
    @Redirect(method = "setDefaultSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;removeRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"))
    private <T> void rdpl$spawnTicketReleased(ServerChunkCache chunks, TicketType<T> type, ChunkPos pos, int distance, T value) {
        if (ContentSpawnChunks.leftToTheGame()) { chunks.removeRegionTicket(type, pos, distance, value); }
    }

    @Redirect(method = "setDefaultSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"))
    private <T> void rdpl$spawnTicketHeld(ServerChunkCache chunks, TicketType<T> type, ChunkPos pos, int distance, T value) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (ContentSpawnChunks.leftToTheGame()) { chunks.addRegionTicket(type, pos, distance, value); }
        else if (level.dimension() == Level.OVERWORLD) { ContentSpawnChunks.hold(level, pos); }
    }

    @Redirect(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void rdpl$ownBlast(ServerGamePacketListenerImpl connection, Packet<?> packet, Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator,
                               double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        if (!(packet instanceof ClientboundExplodePacket blast) || ContentEntities.explodeSound(source) == null || RDPLNetwork.hush(connection.player, x, y, z)) {
            connection.send(packet);
            return;
        }
        Vec3 shove = new Vec3(blast.getKnockbackX(), blast.getKnockbackY(), blast.getKnockbackZ());
        if (shove.lengthSqr() > 0.0D) {
            connection.player.setDeltaMovement(connection.player.getDeltaMovement().add(shove));
            connection.send(new ClientboundSetEntityMotionPacket(connection.player));
        }
        connection.send(new ClientboundLevelParticlesPacket(radius < 2.0F ? ParticleTypes.EXPLOSION : ParticleTypes.EXPLOSION_EMITTER, true, x, y, z, 0.0F, 0.0F, 0.0F, 0.0F, 1));
    }
}
