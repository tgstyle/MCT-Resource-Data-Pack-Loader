package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.gates.ContentGates;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.StringJoiner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class AdAstraGates {
    private AdAstraGates() {}

    public static String hidden(ServerPlayer player, String disabled) {
        if (ContentGates.idle()) { return disabled; }
        StringJoiner planets = new StringJoiner(",");
        if (!disabled.isBlank()) { planets.add(disabled); }
        for (ResourceLocation dimension : ContentGates.guarded()) {
            if (ContentGates.barsDimension(player, dimension)) { planets.add(dimension.toString()); }
        }
        return planets.toString();
    }

    public static boolean hides(ServerPlayer player, ResourceLocation dimension) { return !ContentGates.idle() && ContentGates.barsDimension(player, dimension); }

    public static boolean refused(ServerPlayer player, ResourceKey<Level> dimension) {
        for (GateDef def : ContentGates.forDimension(dimension.location())) {
            if (ContentGates.admits(player, def)) { continue; }
            ContentLog.LOGGER.debug("Gate {} kept {} from landing on {}", def.key(), player.getName().getString(), def.dimension());
            ContentGates.refuse(player, def);
            return true;
        }
        return false;
    }
}
