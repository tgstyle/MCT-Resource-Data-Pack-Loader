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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public final class ContentDimensionAmbience {
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
        if (vanilla == null || vanilla.particle == null && vanilla.tint == null) { return biome.getAmbientParticle(); }
        return Optional.ofNullable(vanilla.particle);
    }

    public static void particles(ClientLevel level, BlockPos at) {
        Vanilla vanilla = vanilla(level);
        Tint tint = vanilla == null ? null : vanilla.tint;
        RainDef rain = rain(level);
        if (tint == null && rain == null || level.getBlockState(at).isCollisionShapeFullBlock(level, at)) { return; }
        if (tint != null && level.random.nextFloat() <= tint.chance) { add(level, rain, tint.type, at, tint.color); }
        if (rain == null) { return; }
        Optional<AmbientParticleSettings> settings = settings(level.getBiome(at).value(), level);
        if (settings.isPresent() && settings.get().canSpawn(level.random)) { add(level, rain, settings.get().getOptions(), at, Vec3.ZERO); }
    }

    private static void add(ClientLevel level, @Nullable RainDef rain, ParticleOptions options, BlockPos at, Vec3 speed) {
        double x = at.getX() + level.random.nextDouble();
        double y = at.getY() + level.random.nextDouble();
        double z = at.getZ() + level.random.nextDouble();
        if (rain == null) { level.addParticle(options, x, y, z, speed.x, speed.y, speed.z); }
        else { WindGust.spawn(level, rain, options, x, y, z, speed.x, speed.y, speed.z); }
    }

    @Nullable private static RainDef rain(@Nullable Level level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        return def == null ? null : def.traits().rain();
    }

    @Nullable private static Holder<SoundEvent> sound(@Nullable ResourceLocation name) {
        if (name == null) { return null; }
        return ForgeRegistries.SOUND_EVENTS.getHolder(name).orElseGet(() -> Holder.direct(SoundEvent.createVariableRangeEvent(name)));
    }

    private static boolean tints(@Nullable ParticleType<?> type) { return type == ParticleTypes.ENTITY_EFFECT || type == ParticleTypes.AMBIENT_ENTITY_EFFECT; }

    @Nullable private static ParticleOptions options(ResourceLocation dimension, AmbienceDef ambience, @Nullable ParticleType<?> type) {
        if (type == ParticleTypes.DUST) { return ambience.particleColor() == AmbienceDef.NO_COLOR ? DustParticleOptions.REDSTONE : new DustParticleOptions(Vec3.fromRGB24(ambience.particleColor()).toVector3f(), 1.0F); }
        if (type instanceof SimpleParticleType simple) { return simple; }
        ContentLog.LOGGER.error("Dimension {} names the ambience particle {}, which is not a particle the game shows without settings of its own, so it shows none", dimension, ambience.particle());
        return null;
    }

    private record Tint(SimpleParticleType type, float chance, Vec3 color) {}

    private static final class Vanilla {
        @Nullable private final Music music;
        @Nullable private final Holder<SoundEvent> loop;
        @Nullable private final AmbientAdditionsSettings additions;
        @Nullable private final AmbientParticleSettings particle;
        @Nullable private final Tint tint;

        private Vanilla(ResourceLocation dimension, AmbienceDef ambience) {
            Holder<SoundEvent> track = sound(ambience.music());
            music = track == null ? null : new Music(track, ambience.musicMinDelay(), ambience.musicMaxDelay(), true);
            loop = sound(ambience.loopSound());
            Holder<SoundEvent> addition = ambience.sounds() ? sound(ambience.ambientSound()) : null;
            additions = addition == null ? null : new AmbientAdditionsSettings(addition, ambience.soundChance());
            ParticleType<?> type = ambience.shows() ? Registered.find(ForgeRegistries.PARTICLE_TYPES, ambience.particle()) : null;
            if (tints(type)) {
                tint = new Tint((SimpleParticleType) type, ambience.particleChance(), ambience.particleColor() == AmbienceDef.NO_COLOR ? Vec3.ZERO : Vec3.fromRGB24(ambience.particleColor()));
                particle = null;
                return;
            }
            tint = null;
            ParticleOptions options = ambience.shows() ? options(dimension, ambience, type) : null;
            particle = options == null ? null : new AmbientParticleSettings(options, ambience.particleChance());
        }
    }
}
