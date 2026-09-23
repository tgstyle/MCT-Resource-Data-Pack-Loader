package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ContentCaveAmbience {
    private static final int EVERY = 10;
    private static final int REACH = 16;
    private static final int POSITIONS_PER_TICK = 667;
    private static final int MOST_PARTICLES = 200;
    private static final Map<String, Optional<Holder<SoundEvent>>> SOUNDS = new HashMap<>();
    private static final Map<String, Optional<SimpleParticleType>> PARTICLES = new HashMap<>();
    private static final Set<String> TOLD = new HashSet<>();
    private static final Map<String, String> WHERE = new HashMap<>();

    private ContentCaveAmbience() {}

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level) || player.tickCount % EVERY != 0 || player.isSpectator()) { return; }
        BlockPos eyes = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        CaveRegionDef region = ContentCaveRegions.regionAt(level, eyes);
        String now = region == null ? "" : region.key().toString();
        if (ContentLog.LOGGER.debugEnabled() && !now.equals(WHERE.put(player.getGameProfile().getName(), now))) { ContentLog.LOGGER.debug("{} at {}, {}, {} is {}", player.getGameProfile().getName(), eyes.getX(), eyes.getY(), eyes.getZ(), region == null ? "in no cave region" : "in cave region " + now + (region.hasAmbience() ? " with ambience" : " without ambience")); }
        if (region == null || !region.hasAmbience()) { return; }
        RandomSource random = level.getRandom();
        if (!region.ambientSound().isEmpty() && random.nextFloat() < 1.0F - (float) Math.pow(1.0F - region.soundChance(), EVERY)) { sound(player, region, random); }
        if (!region.particle().isEmpty() && region.particleChance() > 0.0F) { particles(player, level, region, random); }
    }

    private static void sound(ServerPlayer player, CaveRegionDef region, RandomSource random) {
        Holder<SoundEvent> sound = SOUNDS.computeIfAbsent(region.ambientSound(), name -> Optional.ofNullable(Registered.holder(BuiltInRegistries.SOUND_EVENT, ResourceLocation.tryParse(name)))).orElse(null);
        if (sound == null) {
            if (TOLD.add(region.key() + " sound")) { ContentLog.LOGGER.error("Cave region {} names the ambient sound {}, which nothing registers, so the region stays quiet", region.key(), region.ambientSound()); }
            return;
        }
        player.connection.send(new ClientboundSoundPacket(sound, SoundSource.AMBIENT, player.getX(), player.getEyeY(), player.getZ(), 1.0F, 0.8F + random.nextFloat() * 0.4F, random.nextLong()));
    }

    private static void particles(ServerPlayer player, ServerLevel level, CaveRegionDef region, RandomSource random) {
        SimpleParticleType type = PARTICLES.computeIfAbsent(region.particle(), ContentCaveAmbience::particle).orElse(null);
        if (type == null) {
            if (TOLD.add(region.key() + " particle")) { ContentLog.LOGGER.error("Cave region {} names the particle {}, which is not a particle the game shows without settings of its own, so the region shows none", region.key(), region.particle()); }
            return;
        }
        int tries = Mth.clamp(Math.round(POSITIONS_PER_TICK * region.particleChance() * EVERY), 1, MOST_PARTICLES);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = 0; i < tries; i++) {
            at.set(player.getX() + random.nextInt(REACH) - random.nextInt(REACH), player.getY() + random.nextInt(REACH) - random.nextInt(REACH), player.getZ() + random.nextInt(REACH) - random.nextInt(REACH));
            if (!level.isLoaded(at) || !level.isEmptyBlock(at) || ContentCaveRegions.regionAt(level, at) != region) { continue; }
            level.sendParticles(player, type, false, at.getX() + random.nextDouble(), at.getY() + random.nextDouble(), at.getZ() + random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static Optional<SimpleParticleType> particle(String name) {
        ParticleType<?> found = Registered.find(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.tryParse(name));
        return found instanceof SimpleParticleType simple ? Optional.of(simple) : Optional.empty();
    }
}
