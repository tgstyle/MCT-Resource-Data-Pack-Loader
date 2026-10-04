package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.WindGust;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public final class ContentDimensionAmbience {
    private static final int OPAQUE = 0xFF000000;
    private static final Map<AmbienceDef, Vanilla> VANILLA = new IdentityHashMap<>();

    private ContentDimensionAmbience() {}

    @Nullable private static Vanilla vanilla(@Nullable Level level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        AmbienceDef ambience = def == null ? null : def.traits().ambience();
        return ambience == null ? null : VANILLA.computeIfAbsent(ambience, held -> new Vanilla(def.key(), held));
    }

    @Nullable public static Music music(@Nullable Level level) {
        Vanilla vanilla = vanilla(level);
        return vanilla == null ? null : vanilla.music;
    }

    public static Optional<Holder<SoundEvent>> loop(Biome biome, @Nullable Level level) {
        Vanilla vanilla = vanilla(level);
        return vanilla == null || vanilla.loop == null ? biome.getAmbientLoop() : Optional.of(vanilla.loop);
    }

    public static Optional<AmbientAdditionsSettings> additions(Biome biome, @Nullable Level level) {
        Vanilla vanilla = vanilla(level);
        return vanilla == null || vanilla.additions == null ? biome.getAmbientAdditions() : Optional.of(vanilla.additions);
    }

    public static Optional<AmbientParticleSettings> particle(Biome biome, @Nullable Level level) { return rain(level) == null ? settings(biome, level) : Optional.empty(); }

    private static Optional<AmbientParticleSettings> settings(Biome biome, @Nullable Level level) {
        Vanilla vanilla = vanilla(level);
        return vanilla == null || vanilla.particle == null ? biome.getAmbientParticle() : Optional.of(vanilla.particle);
    }

    public static void particles(ClientLevel level, BlockPos at) {
        RainDef rain = rain(level);
        if (rain == null || level.getBlockState(at).isCollisionShapeFullBlock(level, at)) { return; }
        Optional<AmbientParticleSettings> settings = settings(level.getBiome(at).value(), level);
        if (settings.isEmpty() || !settings.get().canSpawn(level.random)) { return; }
        WindGust.spawn(level, rain, settings.get().getOptions(), at.getX() + level.random.nextDouble(), at.getY() + level.random.nextDouble(), at.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
    }

    @Nullable private static RainDef rain(@Nullable Level level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        return def == null ? null : def.traits().rain();
    }

    @Nullable private static Holder<SoundEvent> sound(@Nullable ResourceLocation name) {
        if (name == null) { return null; }
        Holder<SoundEvent> registered = Registered.holder(BuiltInRegistries.SOUND_EVENT, name);
        return registered == null ? Holder.direct(SoundEvent.createVariableRangeEvent(name)) : registered;
    }

    @Nullable private static ParticleOptions options(ResourceLocation dimension, AmbienceDef ambience) {
        ParticleType<?> type = Registered.find(BuiltInRegistries.PARTICLE_TYPE, ambience.particle());
        boolean colored = ambience.particleColor() != AmbienceDef.NO_COLOR;
        if (type == ParticleTypes.DUST) { return colored ? new DustParticleOptions(Vec3.fromRGB24(ambience.particleColor()).toVector3f(), 1.0F) : DustParticleOptions.REDSTONE; }
        if (type == ParticleTypes.ENTITY_EFFECT) { return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, OPAQUE | (colored ? ambience.particleColor() : 0)); }
        if (type instanceof SimpleParticleType simple) { return simple; }
        ContentLog.LOGGER.error("Dimension {} names the ambience particle {}, which is not a particle the game shows without settings of its own, so it shows none", dimension, ambience.particle());
        return null;
    }

    private static final class Vanilla {
        @Nullable private final Music music;
        @Nullable private final Holder<SoundEvent> loop;
        @Nullable private final AmbientAdditionsSettings additions;
        @Nullable private final AmbientParticleSettings particle;

        private Vanilla(ResourceLocation dimension, AmbienceDef ambience) {
            Holder<SoundEvent> track = sound(ambience.music());
            music = track == null ? null : new Music(track, ambience.musicMinDelay(), ambience.musicMaxDelay(), true);
            loop = sound(ambience.loopSound());
            Holder<SoundEvent> addition = ambience.sounds() ? sound(ambience.ambientSound()) : null;
            additions = addition == null ? null : new AmbientAdditionsSettings(addition, ambience.soundChance());
            ParticleOptions options = ambience.shows() ? options(dimension, ambience) : null;
            particle = options == null ? null : new AmbientParticleSettings(options, ambience.particleChance());
        }
    }
}
