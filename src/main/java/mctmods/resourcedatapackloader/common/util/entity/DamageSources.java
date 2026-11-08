package mctmods.resourcedatapackloader.common.util.entity;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.DamageSource;

public final class DamageSources {
    private static final Map<String, DamageSource> SOURCES = new ConcurrentHashMap<>();

    private DamageSources() {}

    public static DamageSource source(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "lava": return DamageSource.LAVA;
            case "in_fire": return DamageSource.IN_FIRE;
            case "on_fire": return DamageSource.ON_FIRE;
            case "drown": return DamageSource.DROWN;
            case "magic": return DamageSource.MAGIC;
            case "wither": return DamageSource.WITHER;
            case "hot_floor": return DamageSource.HOT_FLOOR;
            case "generic": return DamageSource.GENERIC;
            case "fireworks": return DamageSource.FIREWORKS;
            default: return SOURCES.computeIfAbsent(name, DamageSource::new);
        }
    }
}
