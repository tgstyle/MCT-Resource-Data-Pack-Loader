package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.content.def.RaidSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentRaidSwitch {
    private static final List<String> KEYS = List.of("every", "start", "waves", "points");
    private static final String CLOCK = "rdplRaidClock";

    private ContentRaidSwitch() {}

    @Nullable static RaidSwitchDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives raids that is not an object of every, start, waves and points keys, ignoring it", owner);
            return null;
        }
        int every = 0;
        List<Identifier> start = new ArrayList<>();
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
            Double value = ContentSpawnSwitch.number(entry.getValue());
            if (value == null) {
                ContentLog.LOGGER.error("{} gives raids.{} that is not a number, ignoring it", owner, entry.getKey());
                continue;
            }
            if (kind == 0) {
                if (value < 1.0D || value != Math.rint(value) || value > Integer.MAX_VALUE) { ContentLog.LOGGER.error("{} gives raids.every that is not a whole number above 0, ignoring it", owner); }
                else { every = value.intValue(); }
            }
            else if (kind == 2) {
                if (value <= 0.0D || value > ContentGrowSwitch.MOST) { ContentLog.LOGGER.error("{} gives raids.waves that is not a number above 0 and up to 100, ignoring it", owner); }
                else { waves = value.floatValue(); }
            }
            else if (value < 0.0D || value > ContentGrowSwitch.MOST) { ContentLog.LOGGER.error("{} gives raids.points that is not a number from 0 to 100, ignoring it", owner); }
            else { points = value.floatValue(); }
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

    private static List<Identifier> ids(String owner, JsonElement value) {
        List<Identifier> found = new ArrayList<>();
        if (value.isJsonArray()) {
            for (JsonElement one : value.getAsJsonArray()) { id(owner, one, found); }
        }
        else { id(owner, value, found); }
        return found;
    }

    private static void id(String owner, JsonElement value, List<Identifier> into) {
        Identifier id = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && !value.getAsString().isBlank() ? Identifier.tryParse(value.getAsString().trim()) : null;
        if (id == null) { ContentLog.LOGGER.error("{} gives raids.start as {}, which is not a raid id or a list of them, ignoring it", owner, value); }
        else { into.add(id); }
    }

    public static RaidSwitchDef at(ServerLevel level, BlockPos pos) { return ContentEnvironments.raids(level, pos); }

    @Nullable public static RaidDef due(ServerLevel level, ServerPlayer player, int elapsed) {
        RaidSwitchDef def = at(level, player.blockPosition());
        if (def.every <= 0) { return null; }
        CompoundTag clock = PlayerPersisted.section(player, CLOCK);
        String dimension = level.dimension().identifier().toString();
        int now = Math.min(def.every, clock.getIntOr(dimension, 0) + elapsed);
        clock.putInt(dimension, now);
        if (now < def.every) { return null; }
        List<RaidDef> known = new ArrayList<>();
        for (Identifier id : def.start) {
            RaidDef raid = ContentRaids.def(id);
            if (raid != null) { known.add(raid); }
        }
        return known.isEmpty() ? null : known.get(player.getRandom().nextInt(known.size()));
    }

    public static void reset(ServerLevel level, ServerPlayer player) { PlayerPersisted.section(player, CLOCK).putInt(level.dimension().identifier().toString(), 0); }

    public static void started(ServerLevel level, BlockPos center, int reach) {
        double near = (double) reach * reach;
        for (ServerPlayer player : level.players()) {
            if (center.distToLowCornerSqr(player.getX(), player.getY(), player.getZ()) < near) { reset(level, player); }
        }
    }

    static void check(Collection<RaidSwitchDef> defs) {
        for (RaidSwitchDef def : defs) {
            for (Identifier id : def.start) {
                if (ContentRaids.def(id) == null) { ContentLog.LOGGER.error("{} names the raid {} in raids.start, which no raid file provides, so its clock never starts it", def.owner, id); }
            }
        }
    }
}
