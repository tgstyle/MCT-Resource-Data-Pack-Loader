package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.AdAstraDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserAdAstra;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserCelestial;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentAdAstra {
    public static final String MOD_ID = "ad_astra";
    private static final Map<ResourceLocation, PackSystem> SYSTEMS = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentAdAstra() {}

    public record PackSystem(CelestialDef star, List<CelestialDef> bodies) {}

    private record Placed(CelestialDef def, @Nullable ResourceLocation system) {}

    public static boolean active() { return PackRequirements.modLoaded(MOD_ID); }

    public static void install() {
        warnNotRockets();
        if (!active()) { return; }
        AdAstraGravity.register();
        ContentLog.LOGGER.info("Ad Astra is loaded, so a dimension can be a planet or moon with an adastra block");
    }

    private static void warnNotRockets() {
        for (EntityVariantDef def : ContentEntities.variants()) {
            if (def.rocket() == null) { continue; }
            EntityType<?> base = Registered.find(ForgeRegistries.ENTITY_TYPES, def.base());
            if (base == null) { continue; }
            if (!(active() && AdAstraRockets.rocket(base))) { ContentLog.LOGGER.error("Entity variant {} has an adastra block but {} is not an Ad Astra rocket, so the block is ignored", def.key(), def.base()); }
        }
    }

    public static Map<ResourceLocation, PackSystem> systems() {
        if (GENERATION.stale()) { load(); }
        return SYSTEMS;
    }

    private static void load() {
        SYSTEMS.clear();
        if (!active() || !Config.content.dimensions()) { return; }
        List<Placed> placed = new ArrayList<>();
        for (DimensionDef def : ContentDimensions.available()) {
            AdAstraDef body = def.adastra();
            if (body != null && body.map() != null) { placed.add(new Placed(body.map(), body.system())); }
        }
        Map<ResourceLocation, CelestialDef> stars = new LinkedHashMap<>();
        Json.eachFile(PackManager.CELESTIAL, "celestial body", (key, contents) -> {
            CelestialDef def = ContentParserCelestial.celestial(key, contents);
            if (def == null || !ContentRegistry.available(def.requires(), key)) { return; }
            if (CelestialDef.SYSTEM.equals(def.kind())) { stars.put(key, def); }
            else if (placed.stream().noneMatch(held -> held.def().key().equals(key))) { placed.add(new Placed(def, def.isMoon() ? null : ContentParserAdAstra.system(key, def.parent()))); }
        });
        for (Map.Entry<ResourceLocation, CelestialDef> star : stars.entrySet()) { SYSTEMS.put(star.getKey(), new PackSystem(star.getValue(), new ArrayList<>())); }
        for (Placed body : placed) {
            ResourceLocation system = body.system() != null ? body.system() : moonSystem(body.def().parent(), placed);
            PackSystem home = SYSTEMS.get(system);
            if (home != null) { home.bodies().add(body.def()); }
            else if (!AdAstraDef.SOLAR_SYSTEM.equals(system)) { ContentLog.LOGGER.warn("Celestial body {} belongs to star system {}, which no celestial file defines, so it is left off the map", body.def().key(), system); }
        }
        if (!SYSTEMS.isEmpty()) { Summary.info("adastraSystems", "Loaded " + SYSTEMS.size() + " pack star system(s) for the Ad Astra map"); }
    }

    private static ResourceLocation moonSystem(String parent, List<Placed> placed) {
        for (Placed planet : placed) {
            if (!planet.def().isMoon() && planet.system() != null && planet.def().named(parent)) { return planet.system(); }
        }
        return AdAstraDef.SOLAR_SYSTEM;
    }
}
