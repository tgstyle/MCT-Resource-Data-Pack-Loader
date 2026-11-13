package mctmods.resourcedatapackloader.common.util.dimension;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class BodyRings {
    public static final float DEFAULT_SIZE = 2.5F;
    private static final Map<String, Ring> RINGS = new HashMap<>();

    private BodyRings() {}

    public enum Front {
        BOTTOM, TOP, LEFT, RIGHT;

        @Nullable public static Front parse(String value) {
            for (Front front : values()) {
                if (front.name().equalsIgnoreCase(value.trim())) { return front; }
            }
            return null;
        }

        public float[] half(boolean inFront) {
            boolean low = (this == TOP || this == LEFT) == inFront;
            float from = low ? 0.0F : 0.5F;
            float to = low ? 0.5F : 1.0F;
            return this == TOP || this == BOTTOM ? new float[] {0.0F, from, 1.0F, to} : new float[] {from, 0.0F, to, 1.0F};
        }
    }

    public static final class Ring {
        public final ResourceLocation texture;
        public final float size;
        public final Front front;
        private final boolean override;

        Ring(ResourceLocation texture, float size, Front front, boolean override) {
            this.texture = texture;
            this.size = size;
            this.front = front;
            this.override = override;
        }
    }

    public static void register(CelestialDef def) {
        if (def.ring == null) { return; }
        Ring ring = new Ring(def.ring, def.ringSize, def.ringFront, false);
        RINGS.put(name(def.name), ring);
        RINGS.put(name(def.key.getPath()), ring);
    }

    public static void register(String name, ResourceLocation texture, float size, Front front) { RINGS.put(name(name), new Ring(texture, size, front, true)); }

    public static boolean overrides(String name) {
        Ring ring = RINGS.get(name(name));
        return ring != null && ring.override;
    }

    @Nullable public static Ring find(String name) { return RINGS.get(name(name)); }

    private static String name(String name) { return name.trim().toLowerCase(Locale.ROOT); }
}
