package mctmods.resourcedatapackloader.mixin.server.level;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityTicks;
import mctmods.resourcedatapackloader.common.environment.ContentTimeWindows;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.environment.ContentWeatherCycle;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RuleSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentSpawnChunks;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class) public abstract class ServerLevelMixin {
    @Shadow @Final private ServerLevelData serverLevelData;
    @Unique private final ContentWeatherCycle rdpl$weatherCycle = new ContentWeatherCycle();

    @Redirect(method = "advanceWeatherCycle()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z")) private boolean rdpl$ownWeather(DimensionType type) {
        ServerLevel level = (ServerLevel) (Object) this;
        int forced = ContentRuleSwitch.weather(level);
        if (forced != RuleSwitchDef.UNSET) {
            ContentWeatherCycle.force(level, forced);
            return false;
        }
        DimensionTraitsDef traits = ContentWeather.traits(level);
        DimensionTraitsDef.Cycle cycle = traits.cycle();
        TimeWindowDef window = ContentTimeWindows.active(level, ContentDimensions.def(level));
        if (!traits.precipitation()) {
            ContentWeatherCycle.clear(level);
            return false;
        }
        if (window != null && window.holdsDry()) {
            ContentWeatherCycle.dry(level);
            return false;
        }
        if (window != null && window.gives(TimeWindowDef.ALWAYS_RAIN) ? window.alwaysRain() : traits.alwaysRain()) { return ContentWeatherCycle.pour(level, serverLevelData, type.hasSkyLight()); }
        int steps = ContentRuleSwitch.speed(level);
        if (cycle != null) {
            rdpl$weatherCycle.tick(level, cycle, steps);
            return false;
        }
        ContentWeatherCycle.hurry(level, serverLevelData, steps);
        return type.hasSkyLight();
    }

    @Redirect(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")) private boolean rdpl$lightningWhereAllowed(ServerLevel level) {
        DimensionTraitsDef traits = ContentWeather.traits(level);
        return traits.precipitation() && traits.lightning() && level.isThundering();
    }

    @Redirect(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")) private boolean rdpl$iceWhereItPrecipitates(Biome biome, LevelReader level, BlockPos pos) { return ContentWeather.traits(level).precipitation() && biome.shouldFreeze(level, pos); }

    @Redirect(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V")) private void rdpl$rainFillBelowCeiling(Block block, BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (ContentWeather.above(level, pos.getY())) { return; }
        Biome.Precipitation falls = ContentWeather.fallsAs(level, level.getBiome(pos.above()).value(), precipitation);
        if (falls == Biome.Precipitation.SNOW && !ContentWeather.traits(level).snow()) { return; }
        block.handlePrecipitation(state, level, pos, falls);
    }

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true) private void rdpl$standStillWhileLandIsMade(Entity p_entity, CallbackInfo ci) { if (ContentPregen.busy() || ContentEntityTicks.frozen(p_entity)) { ci.cancel(); } }

    @Redirect(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V")) private void rdpl$slowDistantProjectile(Entity entity) {
        if (entity instanceof Projectile && ContentEntityTicks.slowedNow(entity)) { return; }
        entity.tick();
    }

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true) private void rdpl$holdWeatherWhileLandIsMade(CallbackInfo ci) { if (ContentPregen.busyIn((ServerLevel) (Object) this)) { ci.cancel(); } }

    @Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WritableLevelData;getDayTime()J")) private long rdpl$advanceFromLockedTime(WritableLevelData data) {
        long locked = ContentTerrain.lockedTime((ServerLevel) (Object) this);
        return locked >= 0 ? locked : data.getDayTime();
    }

    @Redirect(method = "setDefaultSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;removeRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V")) private <T> void rdpl$spawnTicketReleased(ServerChunkCache chunks, TicketType<T> type, ChunkPos pos, int distance, T value) { if (ContentSpawnChunks.leftToTheGame()) { chunks.removeRegionTicket(type, pos, distance, value); } }

    @Redirect(method = "setDefaultSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V")) private <T> void rdpl$spawnTicketHeld(ServerChunkCache chunks, TicketType<T> type, ChunkPos pos, int distance, T value) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (ContentSpawnChunks.leftToTheGame()) { chunks.addRegionTicket(type, pos, distance, value); }
        else if (level.dimension() == Level.OVERWORLD) { ContentSpawnChunks.hold(level, pos); }
    }

    @Redirect(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V")) private void rdpl$ownBlast(ServerGamePacketListenerImpl connection, Packet<?> packet, Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
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
