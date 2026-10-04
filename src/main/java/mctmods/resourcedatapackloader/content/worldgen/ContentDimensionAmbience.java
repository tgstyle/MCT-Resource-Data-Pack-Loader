package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public final class ContentDimensionAmbience {
    private static final int OPAQUE = 0xFF000000;
    private static final Map<AmbienceDef, Layers> LAYERS = new IdentityHashMap<>();

    private ContentDimensionAmbience() {}

    public static void layers(EnvironmentAttributeSystem.Builder builder, Level level) {
        DimensionDef def = ContentDimensions.def(level);
        AmbienceDef ambience = def == null ? null : def.traits().ambience();
        if (ambience == null) { return; }
        Layers layers = LAYERS.computeIfAbsent(ambience, held -> new Layers(def.key(), held));
        builder.addConstantLayer(layers.fixed);
        if (layers.loop != null || !layers.additions.isEmpty()) {
            builder.addConstantLayer(EnvironmentAttributes.AMBIENT_SOUNDS, base -> new AmbientSounds(layers.loop == null ? base.loop() : Optional.of(layers.loop), base.mood(), layers.additions.isEmpty() ? base.additions() : layers.additions));
        }
    }

    @Nullable private static Holder<SoundEvent> sound(@Nullable Identifier name) {
        if (name == null) { return null; }
        Holder<SoundEvent> registered = Registered.holder(BuiltInRegistries.SOUND_EVENT, name);
        return registered == null ? Holder.direct(SoundEvent.createVariableRangeEvent(name)) : registered;
    }

    @Nullable private static ParticleOptions options(Identifier dimension, AmbienceDef ambience) {
        ParticleType<?> type = Registered.find(BuiltInRegistries.PARTICLE_TYPE, ambience.particle());
        boolean colored = ambience.particleColor() != AmbienceDef.NO_COLOR;
        if (type == ParticleTypes.DUST) { return colored ? new DustParticleOptions(ambience.particleColor(), 1.0F) : DustParticleOptions.REDSTONE; }
        if (type == ParticleTypes.ENTITY_EFFECT) { return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, OPAQUE | (colored ? ambience.particleColor() : 0)); }
        if (type instanceof SimpleParticleType simple) { return simple; }
        ContentLog.LOGGER.error("Dimension {} names the ambience particle {}, which is not a particle the game shows without settings of its own, so it shows none", dimension, ambience.particle());
        return null;
    }

    private static final class Layers {
        private final EnvironmentAttributeMap fixed;
        @Nullable private final Holder<SoundEvent> loop;
        private final List<AmbientAdditionsSettings> additions;

        private Layers(Identifier dimension, AmbienceDef ambience) {
            EnvironmentAttributeMap.Builder map = EnvironmentAttributeMap.builder();
            Holder<SoundEvent> track = sound(ambience.music());
            if (track != null) { map.set(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(new Music(track, ambience.musicMinDelay(), ambience.musicMaxDelay(), true))); }
            ParticleOptions options = ambience.shows() ? options(dimension, ambience) : null;
            if (options != null) { map.set(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(options, ambience.particleChance())); }
            fixed = map.build();
            loop = sound(ambience.loopSound());
            Holder<SoundEvent> addition = ambience.sounds() ? sound(ambience.ambientSound()) : null;
            additions = addition == null ? List.of() : List.of(new AmbientAdditionsSettings(addition, ambience.soundChance()));
        }
    }
}
