package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.CelestialDef;

import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class BodyRings {
    public static final float DEFAULT_SIZE = 2.5F;
    private static final Map<String, Ring> RINGS = new HashMap<>();

    private BodyRings() {}

    public static final class Ring {
        public final ResourceLocation texture;
        public final float size;
        private final boolean override;

        Ring(ResourceLocation texture, float size, boolean override) {
            this.texture = texture;
            this.size = size;
            this.override = override;
        }
    }

    public static void register(CelestialDef def) {
        if (def.ring == null) { return; }
        Ring ring = new Ring(def.ring, def.ringSize, false);
        RINGS.put(name(def.name), ring);
        RINGS.put(name(def.key.getPath()), ring);
    }

    public static void register(String name, ResourceLocation texture, float size) { RINGS.put(name(name), new Ring(texture, size, true)); }

    public static boolean overrides(String name) {
        Ring ring = RINGS.get(name(name));
        return ring != null && ring.override;
    }

    @Nullable public static Ring find(String name) { return RINGS.get(name(name)); }

    private static String name(String name) { return name.trim().toLowerCase(Locale.ROOT); }
}
