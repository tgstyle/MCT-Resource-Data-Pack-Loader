package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.content.def.RaidSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import com.google.gson.JsonElement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentRaidSwitch {
    private static final List<String> KEYS = Arrays.asList("every", "start", "waves", "points");
    private static final String CLOCK = "rdplRaidClock";

    private ContentRaidSwitch() {}

    @Nullable static RaidSwitchDef parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives raids that is not an object of every, start, waves and points keys, ignoring it", owner);
            return null;
        }
        int every = 0;
        List<ResourceLocation> start = new ArrayList<>();
        float waves = 1.0F;
        float points = 1.0F;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            int kind = KEYS.indexOf(entry.getKey());
            if (kind < 0) {
                ContentLog.LOGGER.error("{} gives raids.{}, which is not a raids key, ignoring it", owner, entry.getKey());
                continue;
            }
            if (kind == 1) {
                start.addAll(ids(owner, entry.getValue()));
                continue;
            }
            Float value = ContentSpawnSwitch.number(owner, "raids." + entry.getKey(), entry.getValue(), kind == 0);
            if (value == null) { continue; }
            if (kind == 0) {
                if (value < 1.0F) { ContentLog.LOGGER.error("{} gives raids.every as {}, which is not above zero, ignoring it", owner, entry.getValue()); }
                else { every = value.intValue(); }
                continue;
            }
            if (value > ContentGrowSwitch.MOST) {
                ContentLog.LOGGER.error("{} gives raids.{} as {}, which is above {}, ignoring it", owner, entry.getKey(), value, (int) ContentGrowSwitch.MOST);
                continue;
            }
            if (kind == 2) {
                if (value <= 0.0F) { ContentLog.LOGGER.error("{} gives raids.waves as {}, which is not above zero, ignoring it", owner, entry.getValue()); }
                else { waves = value; }
            }
            else { points = value; }
        }
        if (every > 0 && start.isEmpty()) {
            ContentLog.LOGGER.error("{} gives raids.every without raids.start, so there is no clock", owner);
            every = 0;
        }
        else if (every == 0 && !start.isEmpty()) {
            ContentLog.LOGGER.error("{} gives raids.start without raids.every, so there is no clock", owner);
            start.clear();
        }
        return new RaidSwitchDef(owner, every, start, waves, points);
    }

    private static List<ResourceLocation> ids(String owner, JsonElement value) {
        List<ResourceLocation> found = new ArrayList<>();
        if (value.isJsonArray()) {
            for (JsonElement one : value.getAsJsonArray()) { id(owner, one, found); }
        }
        else { id(owner, value, found); }
        return found;
    }

    private static void id(String owner, JsonElement value, List<ResourceLocation> into) {
        String name = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? value.getAsString().trim() : "";
        if (name.isEmpty()) { ContentLog.LOGGER.error("{} gives raids.start as {}, which is not a raid id or a list of them, ignoring it", owner, value); }
        else { into.add(new ResourceLocation(name)); }
    }

    public static RaidSwitchDef at(WorldServer world, BlockPos pos) { return ContentEnvironments.raids(world, pos); }

    @Nullable public static RaidDef due(WorldServer world, EntityPlayer player, int elapsed) {
        RaidSwitchDef def = at(world, new BlockPos(player));
        if (def.every <= 0) { return null; }
        NBTTagCompound clock = PlayerPersisted.section(player, CLOCK);
        String dimension = String.valueOf(world.provider.getDimension());
        int now = Math.min(def.every, clock.getInteger(dimension) + elapsed);
        clock.setInteger(dimension, now);
        if (now < def.every) { return null; }
        List<RaidDef> known = new ArrayList<>();
        for (ResourceLocation id : def.start) {
            RaidDef raid = ContentRaids.def(id);
            if (raid != null) { known.add(raid); }
        }
        return known.isEmpty() ? null : known.get(player.getRNG().nextInt(known.size()));
    }

    public static void reset(WorldServer world, EntityPlayer player) { PlayerPersisted.section(player, CLOCK).setInteger(String.valueOf(world.provider.getDimension()), 0); }

    public static void started(WorldServer world, BlockPos center, int reach) {
        double near = (double) reach * reach;
        for (EntityPlayer player : world.playerEntities) {
            if (player.getDistanceSq(center) < near) { reset(world, player); }
        }
    }

    static void check(Collection<RaidSwitchDef> defs) {
        for (RaidSwitchDef def : defs) {
            for (ResourceLocation id : def.start) {
                if (ContentRaids.def(id) == null) { ContentLog.LOGGER.error("{} names the raid {} in raids.start, which no raid file provides, so its clock never starts it", def.owner, id); }
            }
        }
    }
}
