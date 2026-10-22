package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.content.def.RaidSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
                ids(owner, entry.getValue(), start);
                continue;
            }
            float amount = ContentSpawnSwitch.amount(owner, "raids." + entry.getKey(), entry.getValue());
            if (amount < 0.0F) { continue; }
            if (kind == 0) {
                if (amount < 1.0F || amount != Math.floor(amount) || amount > Integer.MAX_VALUE) { ContentLog.LOGGER.error("{} gives raids.every as {}, which is not a whole number above 0, ignoring it", owner, amount); }
                else { every = (int) amount; }
            }
            else if (amount > ContentGrowSwitch.MOST || kind == 2 && amount == 0.0F) { ContentLog.LOGGER.error("{} gives raids.{} as {}, which is {}, ignoring it", owner, entry.getKey(), amount, kind == 2 ? "not above 0 and at most " + ContentGrowSwitch.MOST : "above " + ContentGrowSwitch.MOST); }
            else if (kind == 2) { waves = amount; }
            else { points = amount; }
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

    private static void ids(String owner, JsonElement value, List<ResourceLocation> into) {
        List<JsonElement> given = new ArrayList<>();
        if (value.isJsonArray()) { value.getAsJsonArray().forEach(given::add); }
        else { given.add(value); }
        for (JsonElement one : given) {
            ResourceLocation id = one.isJsonPrimitive() && one.getAsJsonPrimitive().isString() && !one.getAsString().isBlank() ? ResourceLocation.tryParse(one.getAsString().trim()) : null;
            if (id == null) { ContentLog.LOGGER.error("{} gives raids.start as {}, which is not a raid id or a list of them, ignoring it", owner, one); }
            else { into.add(id); }
        }
    }

    public static RaidSwitchDef at(ServerLevel level, BlockPos pos) { return ContentEnvironments.raids(level, pos); }

    @Nullable public static RaidDef due(ServerLevel level, ServerPlayer player, int elapsed) {
        RaidSwitchDef def = at(level, player.blockPosition());
        if (def.every() <= 0) { return null; }
        CompoundTag clock = PlayerPersisted.section(player, CLOCK);
        String dimension = level.dimension().location().toString();
        int now = Math.min(def.every(), clock.getInt(dimension) + elapsed);
        clock.putInt(dimension, now);
        if (now < def.every()) { return null; }
        List<RaidDef> known = new ArrayList<>();
        for (ResourceLocation id : def.start()) {
            RaidDef raid = ContentRaids.def(id);
            if (raid != null) { known.add(raid); }
        }
        return known.isEmpty() ? null : known.get(player.getRandom().nextInt(known.size()));
    }

    public static void reset(ServerLevel level, ServerPlayer player) { PlayerPersisted.section(player, CLOCK).putInt(level.dimension().location().toString(), 0); }

    public static void started(ServerLevel level, BlockPos center, int reach) {
        double near = (double) reach * reach;
        for (ServerPlayer player : level.players()) {
            if (center.distToLowCornerSqr(player.getX(), player.getY(), player.getZ()) < near) { reset(level, player); }
        }
    }

    static void check(Collection<RaidSwitchDef> defs) {
        for (RaidSwitchDef def : defs) {
            for (ResourceLocation id : def.start()) {
                if (ContentRaids.def(id) == null) { ContentLog.LOGGER.error("{} names the raid {} in raids.start, which no raid file provides, so its clock never starts it", def.owner(), id); }
            }
        }
    }
}
