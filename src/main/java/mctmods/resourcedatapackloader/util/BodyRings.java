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

        Ring(ResourceLocation texture, float size) {
            this.texture = texture;
            this.size = size;
        }
    }

    public static void register(CelestialDef def) {
        if (def.ring == null) { return; }
        Ring ring = new Ring(def.ring, def.ringSize);
        RINGS.put(name(def.name), ring);
        RINGS.put(name(def.key.getPath()), ring);
    }

    @Nullable public static Ring find(String name) { return RINGS.get(name(name)); }

    private static String name(String name) { return name.trim().toLowerCase(Locale.ROOT); }
}
