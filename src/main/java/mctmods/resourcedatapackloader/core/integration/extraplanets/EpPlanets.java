package mctmods.resourcedatapackloader.core.integration.extraplanets;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.ExtraPlanetsDef;

import com.mjr.extraplanets.Config;
import com.mjr.extraplanets.entities.landers.EntityGeneralLander;
import com.mjr.extraplanets.entities.landers.EntityJupiterLander;
import com.mjr.extraplanets.entities.landers.EntityMercuryLander;
import com.mjr.extraplanets.entities.landers.EntityNeptuneLander;
import com.mjr.extraplanets.entities.landers.EntitySaturnLander;
import com.mjr.extraplanets.entities.landers.EntityUranusLander;
import java.util.Map;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class EpPlanets {
    private EpPlanets() {}

    public static float temperature(ExtraPlanetsDef ep, boolean daytime, float base) {
        if (!Config.THERMAL_PADDINGS) { return base; }
        return FMLCommonHandler.instance().getEffectiveSide().isServer() && daytime ? ep.temperatureDay : ep.temperatureNight;
    }

    public static void radiation(Map<String, Integer> levels) {
        for (Map.Entry<String, Integer> level : levels.entrySet()) {
            if (level.getValue() > 0) { Config.OTHER_ADDON_PLANET_MOON_RAD_VALUES_LIST.put(level.getKey(), level.getValue()); }
            else { Config.OTHER_ADDON_PLANET_MOON_RAD_VALUES_LIST.remove(level.getKey()); }
        }
    }

    public static EntityLanderBase lander(String kind, EntityPlayerMP player) {
        switch (kind) {
            case "jupiter": return new EntityJupiterLander(player);
            case "saturn": return new EntitySaturnLander(player);
            case "mercury": return new EntityMercuryLander(player);
            case "neptune": return new EntityNeptuneLander(player);
            case "uranus": return new EntityUranusLander(player);
            default: return new EntityGeneralLander(player);
        }
    }
}
