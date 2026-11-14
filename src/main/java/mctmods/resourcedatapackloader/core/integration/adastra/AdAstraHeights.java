package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.AdAstraDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.vehicles.Lander;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class AdAstraHeights {
    private static final int VANILLA_TOP = 320;
    private static final int BAR_FLOOR = 100;
    private static final int LANDER_HOLD = 10;

    private AdAstraHeights() {}

    public static int exit(Level level) {
        AdAstraDef body = body(level);
        return body != null && body.exitHeight() > 0 ? body.exitHeight() : standard(level);
    }

    public static int landing(Level level) {
        AdAstraDef body = body(level);
        return body != null && body.landingHeight() > 0 ? body.landingHeight() : standard(level);
    }

    public static void orbitFall(Entity entity, ServerLevel target) {
        if (entity.getY() == AdAstraConfig.atmosphereLeave && PlanetApi.API.isSpace(entity.level()) && !PlanetApi.API.isSpace(target)) { entity.setPos(entity.getX(), landing(target), entity.getZ()); }
    }

    public static boolean holdsLander(LivingEntity entity) { return entity instanceof Player && entity.getVehicle() instanceof Lander lander && lander.getY() > landing(lander.level()) - LANDER_HOLD; }

    @Nullable public static Rocket climbing(Entity entity) {
        if (entity instanceof Rocket rocket) { return rocket; }
        return entity.getVehicle() instanceof Rocket ridden && !ridden.hideRider() ? ridden : null;
    }

    public static double barHeight(Level level, double y) {
        int exit = exit(level);
        int leave = AdAstraConfig.atmosphereLeave;
        if (exit == leave || exit <= BAR_FLOOR) { return y; }
        return BAR_FLOOR + (y - BAR_FLOOR) * (leave - BAR_FLOOR) / (exit - BAR_FLOOR);
    }

    private static int standard(Level level) { return Math.max(AdAstraConfig.atmosphereLeave, level.getMaxBuildHeight() + AdAstraConfig.atmosphereLeave - VANILLA_TOP); }

    @Nullable public static AdAstraDef body(Level level) {
        DimensionDef def = ContentDimensions.base(level);
        return def == null ? null : def.adastra();
    }
}
