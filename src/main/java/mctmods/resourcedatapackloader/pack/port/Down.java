package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.JsonTree;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

final class Down {
    private static final String GAMERULE = "gamerule ";
    private static final String FIRE = "fire_spread_radius_around_player";
    private static final Set<String> INVERTED = new HashSet<>(Arrays.asList("elytra_movement_check", "player_movement_check", "raids"));
    private static final Map<String, String> OLD_RULES = DownFixes.pairs("allow_entering_nether_using_portals", "allowEnteringNetherUsingPortals", "show_advancement_messages", "announceAdvancements", "block_explosion_drop_decay",
            "blockExplosionDropDecay", "command_block_output", "commandBlockOutput", "command_blocks_work", "commandBlocksEnabled", "max_block_modifications", "commandModificationBlockLimit",
            "elytra_movement_check", "disableElytraMovementCheck", "player_movement_check", "disablePlayerMovementCheck", "raids", "disableRaids", "advance_time", "doDaylightCycle", "entity_drops",
            "doEntityDrops", "immediate_respawn", "doImmediateRespawn", "spawn_phantoms", "doInsomnia", "limited_crafting", "doLimitedCrafting", "mob_drops", "doMobLoot", "spawn_mobs", "doMobSpawning",
            "spawn_patrols", "doPatrolSpawning", "block_drops", "doTileDrops", "spawn_wandering_traders", "doTraderSpawning", "spread_vines", "doVinesSpread", "spawn_wardens", "doWardenSpawning",
            "advance_weather", "doWeatherCycle", "drowning_damage", "drowningDamage", "ender_pearls_vanish_on_death", "enderPearlsVanishOnDeath", "fall_damage", "fallDamage", "fire_damage", "fireDamage",
            "forgive_dead_players", "forgiveDeadPlayers", "freeze_damage", "freezeDamage", "global_sound_events", "globalSoundEvents", "keep_inventory", "keepInventory", "lava_source_conversion",
            "lavaSourceConversion", "locator_bar", "locatorBar", "log_admin_commands", "logAdminCommands", "max_command_sequence_length", "maxCommandChainLength", "max_command_forks", "maxCommandForkCount",
            "max_entity_cramming", "maxEntityCramming", "max_minecart_speed", "minecartMaxSpeed", "mob_explosion_drop_decay", "mobExplosionDropDecay", "mob_griefing", "mobGriefing",
            "natural_health_regeneration", "naturalRegeneration", "players_nether_portal_creative_delay", "playersNetherPortalCreativeDelay", "players_nether_portal_default_delay",
            "playersNetherPortalDefaultDelay", "players_sleeping_percentage", "playersSleepingPercentage", "projectiles_can_break_blocks", "projectilesCanBreakBlocks", "pvp", "pvp", "random_tick_speed",
            "randomTickSpeed", "reduced_debug_info", "reducedDebugInfo", "send_command_feedback", "sendCommandFeedback", "show_death_messages", "showDeathMessages", "max_snow_accumulation_height",
            "snowAccumulationHeight", "spawn_monsters", "spawnMonsters", "respawn_radius", "spawnRadius", "spawner_blocks_work", "spawnerBlocksEnabled", "spectators_generate_chunks",
            "spectatorsGenerateChunks", "tnt_explodes", "tntExplodes", "tnt_explosion_drop_decay", "tntExplosionDropDecay", "universal_anger", "universalAnger", "water_source_conversion",
            "waterSourceConversion");

    private Down() {}

    static void data(JsonObject json, Port.Kind kind, String file, Consumer<String> note) {
        switch (kind) {
            case RECIPE:
                DownRecipes.recipe(json, file, note);
                break;
            case LOOT:
                DownLoot.loot(json, file, note);
                break;
            case ADVANCEMENT:
                DownLoot.advancement(json, file, note);
                break;
            case MODEL:
                DownAssets.model(json);
                break;
            default: JsonTree.replace(json, DownFixes.names(json).getAsJsonObject());
        }
    }

    static String function(String contents) {
        String[] lines = contents.split("\n", -1);
        List<String> out = new ArrayList<>();
        boolean changed = false;
        for (String line : lines) {
            String trimmed = line.trim();
            String ported = trimmed.startsWith(GAMERULE) ? gamerule(trimmed) : commands(trimmed);
            if (ported.equals(trimmed)) {
                out.add(line);
                continue;
            }
            int start = line.indexOf(trimmed);
            out.add(line.substring(0, start) + ported + line.substring(start + trimmed.length()));
            changed = true;
        }
        return changed ? String.join("\n", out) : contents;
    }

    private static String commands(String line) {
        if (line.isEmpty() || line.startsWith("#")) { return line; }
        boolean macro = line.startsWith("$");
        List<String> args = split(macro ? line.substring(1) : line);
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            boolean attribute = i >= 2 && "attribute".equals(args.get(i - 2));
            args.set(i, attribute ? DownFixes.attribute(arg) : renamed(arg));
        }
        String out = (macro ? "$" : "") + String.join(" ", args);
        return out.equals(line) ? line : out;
    }

    private static List<String> split(String text) {
        List<String> out = new ArrayList<>();
        int depth = 0;
        char quote = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                if (c == '\\') { i++; }
                else if (c == quote) { quote = 0; }
                continue;
            }
            if (c == '"' || c == '\'') { quote = c; }
            else if (c == '[' || c == '{' || c == '(') { depth++; }
            else if (c == ']' || c == '}' || c == ')') { depth--; }
            else if (c == ' ' && depth == 0) {
                out.add(text.substring(start, i));
                start = i + 1;
            }
        }
        out.add(text.substring(start));
        return out;
    }

    private static String renamed(String arg) {
        int end = arg.length();
        for (char stop : new char[] {'[', '{'}) {
            int at = arg.indexOf(stop);
            if (at >= 0 && at < end) { end = at; }
        }
        String id = arg.substring(0, end);
        if (id.isEmpty() || id.startsWith("#") || id.startsWith("@")) { return arg; }
        String renamed = DownFixes.name(id);
        if (renamed.equals(id)) { return arg; }
        return (id.indexOf(':') < 0 ? renamed.substring(DownFixes.MINECRAFT.length()) : renamed) + arg.substring(end);
    }

    private static String gamerule(String line) {
        String[] args = line.split(" +");
        if (args.length < 2) { return line; }
        String name = args[1].startsWith(DownFixes.MINECRAFT) ? args[1].substring(DownFixes.MINECRAFT.length()) : args[1];
        String value = args.length > 2 ? args[2] : null;
        if (FIRE.equals(name)) {
            name = "doFireTick";
            value = value == null ? null : String.valueOf(!"0".equals(value));
        }
        else if (OLD_RULES.containsKey(name)) {
            if (INVERTED.contains(name) && value != null) { value = String.valueOf(!Boolean.parseBoolean(value)); }
            name = OLD_RULES.get(name);
        }
        return "gamerule " + name + (value != null ? " " + value : "");
    }
}
