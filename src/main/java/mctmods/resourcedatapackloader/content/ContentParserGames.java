package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ItemGiveDef;
import mctmods.resourcedatapackloader.content.def.RoundResetDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.pack.port.Ids;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Team;
import net.minecraft.world.BossEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentParserGames {
    private ContentParserGames() {}

    private static List<String> names(JsonObject json, String field) {
        List<String> found = new ArrayList<>();
        for (String entry : Json.strings(json, field)) {
            String named = entry.trim();
            if (!named.isEmpty()) { found.add(named); }
        }
        return found;
    }

    @Nullable public static ScoreDef scoreFile(ResourceLocation key, String contents) {
        JsonObject json = ContentParser.GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Score file {} is empty, ignoring it", key);
            return null;
        }
        String name = GsonHelper.getAsString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Score file {} names the objective '{}', and an objective name is 1 to 16 characters, so it is left out", key, name);
            return null;
        }
        String wanted = GsonHelper.getAsString(json, "criterion", "dummy").trim();
        ObjectiveCriteria criterion = ObjectiveCriteria.byName(Ids.criterion(wanted)).orElse(null);
        if (criterion == null) {
            ContentLog.LOGGER.error("Objective {} scores on '{}', which is not a criterion the game knows, so it is left out. dummy, deathCount, playerKillCount, totalKillCount, health and any stat name are the ones there are", name, wanted);
            return null;
        }
        String slot = GsonHelper.getAsString(json, "display", "").trim();
        slot = "belowName".equals(slot) ? "below_name" : slot;
        if (!slot.isEmpty() && !displaySlotKnown(slot)) {
            ContentLog.LOGGER.error("Objective {} asks to be shown in '{}', which is not list, sidebar, belowName, below_name or sidebar.team.<color>, so it is not shown", name, slot);
            slot = "";
        }
        ObjectiveCriteria.RenderType render = json.has("render") ? ObjectiveCriteria.RenderType.byId(GsonHelper.getAsString(json, "render", "integer").trim()) : null;
        JsonObject results = GsonHelper.getAsJsonObject(json, "results", new JsonObject());
        JsonObject points = GsonHelper.getAsJsonObject(json, "points", new JsonObject());
        JsonObject ends = GsonHelper.getAsJsonObject(json, "ends", new JsonObject());
        JsonObject opens = GsonHelper.getAsJsonObject(json, "opens", new JsonObject());
        Map<String, Integer> kills = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(points, "kill", new JsonObject()).entrySet()) { kills.put(entry.getKey().trim(), entry.getValue().getAsInt()); }
        return new ScoreDef(name, GsonHelper.getAsString(json, "displayName", name), criterion, slot, render,
                GsonHelper.getAsBoolean(json, "teamTotals", true),
                GsonHelper.getAsBoolean(json, "individuals", false),
                Map.copyOf(kills),
                GsonHelper.getAsInt(points, "death", 0),
                GsonHelper.getAsInt(ends, "atScore", 0),
                GsonHelper.getAsInt(ends, "afterMinutes", 0),
                GsonHelper.getAsInt(ends, "afterRounds", 0),
                GsonHelper.getAsBoolean(results, "card", false),
                GsonHelper.getAsString(results, "title", name + " results"),
                GsonHelper.getAsString(results, "icon", "").trim(),
                GsonHelper.getAsString(results, "image", "").trim(),
                cardColor(results, name),
                Math.max(20, GsonHelper.getAsInt(results, "seconds", 8) * 20),
                GsonHelper.getAsBoolean(json, "carries", false),
                GsonHelper.getAsBoolean(ends, "resets", false),
                Math.max(0, GsonHelper.getAsInt(ends, "intermissionSeconds", 10)),
                GsonHelper.getAsString(json, "awardsTo", "").trim(),
                GsonHelper.getAsBoolean(ends, "locksTeams", true),
                GsonHelper.getAsInt(points, "ownKill", 0),
                GsonHelper.getAsString(ends, "intermissionSays", "Round cooldown {seconds}"),
                GsonHelper.getAsString(ends, "startsSays", "Round starting in {seconds}"),
                opensBy(opens, key),
                GsonHelper.getAsString(opens, "says", "Waiting for {leader} to start the round"),
                GsonHelper.getAsString(opens, "leaderSays", "Type /rdpl round start"),
                lobbyAt(key, opens),
                GsonHelper.getAsBoolean(opens, "lobbyJoins", false),
                GsonHelper.getAsString(opens, "joinsSays", "Round is in progress, you can join after it ends"),
                GsonHelper.getAsBoolean(ends, "lastStanding", false),
                GsonHelper.getAsString(ends, "outSays", "You are out until the round ends"),
                roundReset(key, GsonHelper.getAsJsonObject(json, "reset", new JsonObject())));
    }

    private static RoundResetDef roundReset(ResourceLocation key, JsonObject reset) {
        String lead = GsonHelper.getAsString(reset, "lead", RoundResetDef.NONE).trim();
        if (!RoundResetDef.NONE.equals(lead) && !RoundResetDef.NOW.equals(lead) && !RoundResetDef.VOTE.equals(lead)) {
            ContentLog.LOGGER.error("Score file {} lets the lead reset the round by '{}', which is not none, now or vote, so the lead cannot", key, lead);
            lead = RoundResetDef.NONE;
        }
        String players = GsonHelper.getAsString(reset, "players", RoundResetDef.NONE).trim();
        if (!RoundResetDef.NONE.equals(players) && !RoundResetDef.VOTE.equals(players)) {
            ContentLog.LOGGER.error("Score file {} lets players reset the round by '{}', which is not none or vote, so they cannot", key, players);
            players = RoundResetDef.NONE;
        }
        return new RoundResetDef(lead, RoundResetDef.VOTE.equals(players), Json.strings(reset, "teams"),
                Mth.clamp(GsonHelper.getAsInt(reset, "passPercent", 51), 1, 100),
                Math.max(5, GsonHelper.getAsInt(reset, "voteSeconds", 30)),
                Math.max(0, GsonHelper.getAsInt(reset, "cooldownSeconds", 60)),
                GsonHelper.getAsString(reset, "leadSays", "{player} reset the round"),
                GsonHelper.getAsString(reset, "voteSays", "{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds"),
                GsonHelper.getAsString(reset, "tallySays", "Reset the round? {yes} yes, {no} no, {seconds}"),
                GsonHelper.getAsString(reset, "passSays", "The vote passed, so the round is reset"),
                GsonHelper.getAsString(reset, "failSays", "The vote failed, so the round goes on"));
    }

    private static String opensBy(JsonObject opens, ResourceLocation key) {
        String asked = GsonHelper.getAsString(opens, "by", ScoreDef.AUTO).trim();
        if (ScoreDef.AUTO.equals(asked) || ScoreDef.LEADER.equals(asked)) { return asked; }
        ContentLog.LOGGER.error("Score file {} opens its round by '{}', which is not auto or leader, so it opens on its own", key, asked);
        return ScoreDef.AUTO;
    }

    @Nullable private static ScoreDef.Place lobbyAt(ResourceLocation key, JsonObject opens) {
        String asked = GsonHelper.getAsString(opens, "lobby", "").trim();
        ResourceKey<Level> dimension = Level.OVERWORLD;
        int comma = asked.indexOf(',');
        int colon = comma < 0 ? -1 : asked.lastIndexOf(':', comma);
        if (colon > 0) {
            String named = asked.substring(0, colon).trim();
            ResourceLocation id = "-1".equals(named) ? Level.NETHER.location() : "1".equals(named) ? Level.END.location() : "0".equals(named) ? Level.OVERWORLD.location() : ResourceLocation.tryParse(named);
            if (id == null) {
                ContentLog.LOGGER.error("Score file {} gives opens.lobby '{}', whose dimension is not a dimension id, so the lobby has no area of its own", key, asked);
                return null;
            }
            dimension = ResourceKey.create(Registries.DIMENSION, id);
            asked = asked.substring(colon + 1);
        }
        int[] at = point(key, asked, "Score file {} gives opens.lobby '{}', which is not three whole numbers x,y,z, so the lobby has no area of its own");
        return at == null ? null : new ScoreDef.Place(dimension, at[0], at[1], at[2]);
    }

    private static int cardColor(JsonObject results, String name) {
        String asked = GsonHelper.getAsString(results, "background", "").trim();
        return asked.isEmpty() ? 0x1E2630 : ContentParser.color(asked, name + " results background") & 0xFFFFFF;
    }

    @Nullable public static RaidDef raidFile(ResourceLocation key, String contents) {
        JsonObject json = ContentParser.GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Raid file {} is empty, ignoring it", key);
            return null;
        }
        String omen = GsonHelper.getAsString(json, "omen", "").trim();
        if (omen.isEmpty()) {
            ContentLog.LOGGER.error("Raid {} names no omen effect, and nothing else starts a raid, so it is left out", key);
            return null;
        }
        String colorName = GsonHelper.getAsString(json, "color", "red").trim().toUpperCase(Locale.ROOT);
        BossEvent.BossBarColor color = BossEvent.BossBarColor.RED;
        try { color = BossEvent.BossBarColor.valueOf(colorName); }
        catch (IllegalArgumentException notABarColor) { ContentLog.LOGGER.error("Raid {} asks for the bar color '{}', which is not pink, blue, red, green, yellow, purple or white, so it is red", key, colorName); }
        List<List<RaidDef.Group>> waves = new ArrayList<>();
        if (json.has("waves")) {
            for (JsonElement wave : GsonHelper.getAsJsonArray(json, "waves")) {
                if (!wave.isJsonArray()) {
                    ContentLog.LOGGER.error("Raid {} has a wave that is not a list of groups, skipping it", key);
                    continue;
                }
                List<RaidDef.Group> groups = new ArrayList<>();
                for (JsonElement group : wave.getAsJsonArray()) {
                    if (!group.isJsonObject() || GsonHelper.getAsString(group.getAsJsonObject(), "entity", "").trim().isEmpty()) {
                        ContentLog.LOGGER.error("Raid {} has a group in wave {} that names no entity, skipping it", key, waves.size() + 1);
                        continue;
                    }
                    groups.add(new RaidDef.Group(GsonHelper.getAsString(group.getAsJsonObject(), "entity", "").trim(), ContentParser.amount(group.getAsJsonObject(), "count", 1, 1)));
                }
                if (!groups.isEmpty()) { waves.add(List.copyOf(groups)); }
            }
        }
        if (waves.isEmpty()) {
            ContentLog.LOGGER.error("Raid {} has no wave with anyone in it, so it is left out", key);
            return null;
        }
        return new RaidDef(key, omen, GsonHelper.getAsString(json, "name", "Raid"), color, List.copyOf(waves),
                Math.max(1, GsonHelper.getAsInt(json, "waveDelay", 300)),
                Math.max(8, GsonHelper.getAsInt(json, "spawnDistance", 32)),
                Math.max(16, GsonHelper.getAsInt(json, "reach", 96)),
                Math.max(0, GsonHelper.getAsInt(json, "timeout", 48000)),
                GsonHelper.getAsString(json, "sound", "").trim(),
                GsonHelper.getAsString(json, "wins", "").trim(),
                GsonHelper.getAsString(json, "loses", "").trim(),
                Json.strings(json, "bell"));
    }

    @Nullable public static TeamDef teamFile(ResourceLocation key, String contents) {
        JsonObject json = ContentParser.GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Team file {} is empty, ignoring it", key);
            return null;
        }
        String name = GsonHelper.getAsString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Team file {} names the team '{}', and a team name is 1 to 16 characters, so the team is left out", key, name);
            return null;
        }
        ChatFormatting color = ChatFormatting.getByName(GsonHelper.getAsString(json, "color", "white").trim().toLowerCase(Locale.ROOT));
        if (color == null || !color.isColor()) {
            ContentLog.LOGGER.error("Team {} asks for the color '{}', which is not one of the sixteen text colors, so it is white", name, GsonHelper.getAsString(json, "color", ""));
            color = ChatFormatting.WHITE;
        }
        boolean friendlyFire = GsonHelper.getAsBoolean(json, "friendlyFire", false);
        return new TeamDef(name, GsonHelper.getAsString(json, "displayName", name), color,
                GsonHelper.getAsString(json, "prefix", ""), GsonHelper.getAsString(json, "suffix", ""),
                friendlyFire, GsonHelper.getAsBoolean(json, "mobFriendlyFire", friendlyFire),
                GsonHelper.getAsBoolean(json, "seeFriendlyInvisibles", true),
                visible(json, "nameTags", key), visible(json, "deathMessages", key), collision(json, key),
                names(json, "entities"), names(json, "players"), box(json, key),
                GsonHelper.getAsBoolean(json, "joinable", true),
                leadWay(json, name), GsonHelper.getAsString(json, "leadOn", "").trim(), GsonHelper.getAsString(json, "leadIs", "").trim(),
                GsonHelper.getAsString(json, "leadSays", "You are the current round leader"), GsonHelper.getAsString(json, "leadRuns", "").trim(),
                GsonHelper.getAsBoolean(json, "balance", false), GsonHelper.getAsBoolean(json, "scoreboard", true),
                Math.max(0, GsonHelper.getAsInt(json, "picks", 0)), names(json, "picksFrom"), gives(key, json),
                standIn(json), standInAt(key, json), point(key, GsonHelper.getAsString(json, "spawn", ""), "Team file {} gives spawn '{}', which is not three whole numbers x,y,z, so the side has no spawn of its own"));
    }

    @Nullable static int[] point(ResourceLocation key, String asked, String refused) {
        String at = asked.trim();
        if (at.isEmpty()) { return null; }
        String[] parts = at.split(",");
        if (parts.length == 3) {
            try { return new int[] { Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()) }; }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.debug("{} holds {}, which does not read as whole numbers", key, at); }
        }
        ContentLog.LOGGER.error(refused, key, at);
        return null;
    }

    private static String standIn(JsonObject json) {
        if (!json.has("standIn") || !json.get("standIn").isJsonObject()) { return ""; }
        return GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, "standIn"), "entity", "").trim();
    }

    @Nullable private static int[] standInAt(ResourceLocation key, JsonObject json) {
        if (!json.has("standIn") || !json.get("standIn").isJsonObject()) { return null; }
        String at = GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, "standIn"), "at", "");
        int[] found = point(key, at, "Team file {} gives standIn an 'at' of '{}', which is not three whole numbers x,y,z, so no stand-in is kept");
        if (found == null && at.trim().isEmpty()) { ContentLog.LOGGER.error("Team file {} gives standIn no 'at', so no stand-in is kept", key); }
        return found;
    }

    private static List<ItemGiveDef> gives(ResourceLocation key, JsonObject json) {
        List<ItemGiveDef> values = new ArrayList<>();
        if (!json.has("gives")) { return values; }
        for (JsonElement held : GsonHelper.getAsJsonArray(json, "gives")) {
            if (held.isJsonPrimitive()) { values.add(new ItemGiveDef(held.getAsString().trim(), 1, false)); }
            else if (held.isJsonObject()) { values.add(new ItemGiveDef(GsonHelper.getAsString(held.getAsJsonObject(), "item", "").trim(), Math.max(1, GsonHelper.getAsInt(held.getAsJsonObject(), "count", 1)), GsonHelper.getAsBoolean(held.getAsJsonObject(), "unbreakable", false))); }
            else { ContentLog.LOGGER.error("Team file {} lists something under gives that is neither an item name nor an object, skipping it", key); }
        }
        return values;
    }

    private static String leadWay(JsonObject json, String team) {
        String asked = GsonHelper.getAsString(json, "lead", TeamDef.NONE).trim();
        if (TeamDef.NONE.equals(asked) || TeamDef.FIRST.equals(asked) || TeamDef.TOP_SCORE.equals(asked) || TeamDef.APPOINTED.equals(asked) || TeamDef.VOTE.equals(asked) || TeamDef.CLAIM.equals(asked)) { return asked; }
        ContentLog.LOGGER.error("Team {} chooses its lead by '{}', which is not none, first, topScore, appointed, vote or claim, so it has no lead", team, asked);
        return TeamDef.NONE;
    }

    private static Team.Visibility visible(JsonObject json, String field, ResourceLocation key) {
        String asked = GsonHelper.getAsString(json, field, "always").trim();
        for (Team.Visibility held : Team.Visibility.values()) {
            if (held.name.equalsIgnoreCase(asked)) { return held; }
        }
        ContentLog.LOGGER.error("Team file {} sets {} to '{}', which is not always, never, hideForOtherTeams or hideForOwnTeam, so it is always", key, field, asked);
        return Team.Visibility.ALWAYS;
    }

    private static Team.CollisionRule collision(JsonObject json, ResourceLocation key) {
        String asked = GsonHelper.getAsString(json, "collision", "always").trim();
        for (Team.CollisionRule held : Team.CollisionRule.values()) {
            if (held.name.equalsIgnoreCase(asked)) { return held; }
        }
        ContentLog.LOGGER.error("Team file {} sets collision to '{}', which is not always, never, pushOtherTeams or pushOwnTeam, so it is always", key, asked);
        return Team.CollisionRule.ALWAYS;
    }

    @Nullable private static int[] box(JsonObject json, ResourceLocation key) {
        if (!json.has("spawnBox")) { return null; }
        if (!json.get("spawnBox").isJsonArray() || json.getAsJsonArray("spawnBox").size() != 6) {
            ContentLog.LOGGER.error("Team file {} has a spawnBox that is not six whole numbers, x y z to x y z, so nothing joins by where it spawns", key);
            return null;
        }
        int[] box = new int[6];
        int at = 0;
        for (JsonElement entry : json.getAsJsonArray("spawnBox")) { box[at++] = entry.getAsInt(); }
        for (int side = 0; side < 3; side++) {
            if (box[side] > box[side + 3]) {
                int swap = box[side];
                box[side] = box[side + 3];
                box[side + 3] = swap;
            }
        }
        return box;
    }

    private static boolean displaySlotKnown(String slot) { return DisplaySlot.CODEC.byName(slot) != null; }
}
