package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public final class DamageSources {
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private DamageSources() {}

    public static DamageSource source(Level level, String name, String owner) {
        ResourceLocation id = ResourceLocation.tryParse(name.toLowerCase(Locale.ROOT));
        Holder<DamageType> held = id == null ? null : type(level, id);
        if (held != null) { return new DamageSource(held); }
        if (WARNED.add(owner + "|" + name)) { ContentLog.LOGGER.error("{} names the damage type '{}', which is not registered, so generic damage is used instead", owner, name); }
        return level.damageSources().generic();
    }

    @Nullable public static Holder<DamageType> type(Level level, ResourceLocation id) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(ResourceKey.create(Registries.DAMAGE_TYPE, id)).orElse(null);
    }
}
