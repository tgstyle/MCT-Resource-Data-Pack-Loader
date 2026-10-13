package mctmods.resourcedatapackloader.util.compat;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class GcNesting {
    private static final Map<CelestialBody, Moon> NEST = new IdentityHashMap<>();

    private GcNesting() {}

    static void nest(CelestialBody body, Moon parent) { NEST.put(body, parent); }

    public static CelestialBody center(@Nullable CelestialBody body, CelestialBody center) {
        Moon parent = NEST.get(body);
        return parent != null && center instanceof Planet ? parent : center;
    }

    public static float drawn(CelestialBody body, float alpha) { return alpha <= 0.0F && NEST.containsKey(body) ? -1.0F : alpha; }

    @Nullable public static CelestialBody seen(CelestialBody body, @Nullable CelestialBody selected) {
        Moon parent = NEST.get(body);
        if (parent == null) { return selected; }
        return parent == selected ? ((IChildBody) body).getParentPlanet() : null;
    }

    public static List<CelestialBody> siblings(@Nullable CelestialBody body, List<CelestialBody> list) {
        Moon parent = NEST.get(body);
        if (parent == null) { return unnest(list); }
        list.clear();
        for (Map.Entry<CelestialBody, Moon> entry : NEST.entrySet()) {
            if (entry.getValue() == parent) { list.add(entry.getKey()); }
        }
        list.add(parent);
        return list;
    }

    public static List<CelestialBody> unnest(List<CelestialBody> list) {
        list.removeIf(NEST::containsKey);
        return list;
    }
}
