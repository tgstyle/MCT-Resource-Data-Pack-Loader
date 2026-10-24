package mctmods.resourcedatapackloader.command;

import static mctmods.resourcedatapackloader.command.CommandShared.config;
import static mctmods.resourcedatapackloader.command.CommandShared.blockedReport;
import static mctmods.resourcedatapackloader.command.CommandShared.rescan;
import static mctmods.resourcedatapackloader.command.CommandShared.send;
import static mctmods.resourcedatapackloader.command.CommandShared.unused;
import static mctmods.resourcedatapackloader.command.CommandShared.elapsed;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeNames;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeAt;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeHere;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeList;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeFind;
import static mctmods.resourcedatapackloader.command.CommandShared.findBiome;
import mctmods.resourcedatapackloader.content.ContentBoards;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.ContentHearts;
import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.ContentRoundReset;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.RoundSay;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.dialog.ContentDialogs;
import mctmods.resourcedatapackloader.content.dialog.DialogTalk;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreVein;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentReset;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.compat.CubicChunksWorlds;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerCommands extends CommandBase {
    static final int OPERATOR = 3;
    private static final int PACK_GOTO = 2;
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "list", "which", "unused", "oregen", "generators", "gate", "dimensions", "biome", "pregen", "intro", "config", "goto", "vein", "team", "reset", "round", "card", "environment", "game", "raid", "portway", "talk");
    private static final List<String> PREGEN_ACTIONS = Arrays.asList("stop", "status");
    private static final List<String> CONFIG_ACTIONS = Arrays.asList("unused", "prune");
    private static final List<String> BIOME_ACTIONS = Arrays.asList("list", "here", "find");
    private static final List<String> TEAM_ACTIONS = Arrays.asList("list", "join", "leave", "vote", "claim");
    private static final List<String> ROUND_ACTIONS = Arrays.asList("start", "reset", "vote");
    private static final List<String> STRUCTURE_NAMES = Arrays.asList("Village", "Temple", "Mansion", "Monument", "Mineshaft", "Stronghold", "Fortress", "EndCity");
    private static final Map<String, String> STRUCTURE_ALIASES = new HashMap<>();
    static {
        STRUCTURE_ALIASES.put("villages", "Village");
        STRUCTURE_ALIASES.put("temples", "Temple");
        STRUCTURE_ALIASES.put("mansions", "Mansion");
        STRUCTURE_ALIASES.put("monuments", "Monument");
        STRUCTURE_ALIASES.put("mineshafts", "Mineshaft");
        STRUCTURE_ALIASES.put("strongholds", "Stronghold");
        STRUCTURE_ALIASES.put("netherbridges", "Fortress");
        STRUCTURE_ALIASES.put("endcities", "EndCity");
    }

    @Override @Nonnull public String getName() { return "rdplserver"; }

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return Lang.tr(sender, "rdpl.command.serverusage"); }

    @Override public int getRequiredPermissionLevel() { return OPERATOR; }

    @Override public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) { return true; }

    private static int level(String key, int fallback) { return clamp(ContentControl.number(ContentControl.COMMANDS, key, fallback)); }

    private static int clamp(int level) { return MathHelper.clamp(level, 0, OPERATOR + 1); }

    static int listedLevel(String key, String[] global, String name, int fallback) {
        for (String entry : ContentControl.list(ContentControl.COMMANDS, key, global)) {
            String[] parts = Settings.pair(entry, key, "name=level");
            if (parts == null) { continue; }
            if (!parts[0].trim().equalsIgnoreCase(name)) { continue; }
            int parsed = parsedLevel(key, entry, parts);
            if (parsed >= 0) { return parsed; }
        }
        return fallback;
    }

    private static int parsedLevel(String key, String entry, String[] parts) {
        Integer parsed = Settings.clamped(parts[1], 0, OPERATOR + 1);
        if (parsed == null) { ContentLog.LOGGER.error("{} entry '{}' has no number after the =, ignoring it", key, entry); }
        return parsed == null ? -1 : parsed;
    }

    private static int neededFor(String place, String key, int fallback) { return listedLevel("gotoPlaceLevels", Config.commands.gotoPlaceLevels, place, level(key, fallback)); }

    public static int locateLevel() { return level("locateLevel", Config.commands.locateLevel); }

    public static void point(EntityPlayerMP player, String asked, Consumer<BlockPos> found) {
        World world = player.world;
        String name = STRUCTURE_ALIASES.getOrDefault(asked, asked);
        Biome biome = biomeFor(world, name);
        if (biome != null) { ContentBiomeSearch.start(player, String.valueOf(biome.getRegistryName()), biome, false, found); }
        else if (ContentLocate.names(world).contains(name)) { found.accept(ContentLocate.nearest(world, name, player.getPosition())); }
        else { ContentStructureSearch.start(player, name, keyFor(name), false, false, found); }
    }

    private static int lowestGotoLevel() {
        int lowest = OPERATOR;
        lowest = Math.min(lowest, level("gotoLevel", Config.commands.gotoLevel));
        lowest = Math.min(lowest, level("gotoNextLevel", Config.commands.gotoNextLevel));
        lowest = Math.min(lowest, level("gotoBackLevel", Config.commands.gotoBackLevel));
        for (String entry : ContentControl.list(ContentControl.COMMANDS, "gotoPlaceLevels", Config.commands.gotoPlaceLevels)) {
            String[] parts = Settings.pair(entry, "gotoPlaceLevels", "name=level");
            if (parts == null) { continue; }
            int parsed = parsedLevel("gotoPlaceLevels", entry, parts);
            if (parsed >= 0) { lowest = Math.min(lowest, parsed); }
        }
        return lowest;
    }

    private static List<String> forOperator() {
        if (ContentTeams.any()) { return SUBCOMMANDS; }
        List<String> shown = new ArrayList<>(SUBCOMMANDS);
        shown.remove("team");
        return shown;
    }

    private List<String> openTo(ICommandSender sender) {
        List<String> open = new ArrayList<>();
        open.add("intro");
        open.add("talk");
        if (ContentTeams.any()) { open.add("team"); }
        if (ContentScoring.any()) { open.add("round"); }
        if (sender.canUseCommand(lowestGotoLevel(), getName())) { open.add("goto"); }
        if (sender.canUseCommand(GameCommand.lowest(), getName())) { open.add("game"); }
        return open;
    }

    private void allow(ICommandSender sender, int needed) throws CommandException { if (!sender.canUseCommand(needed, getName())) { throw new CommandException(Lang.tr(sender, "rdpl.command.notallowed")); } }

    @Override @Nonnull public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) { return getListOfStringsMatchingLastWord(args, sender.canUseCommand(OPERATOR, getName()) ? forOperator() : openTo(sender)); }
        if (args.length >= 2 && "game".equals(args[0])) { return GameCommand.complete(server, sender, args); }
        if (args.length >= 2 && "environment".equals(args[0])) { return EnvironmentCommand.complete(args); }
        if (args.length >= 2 && "gate".equals(args[0])) { return GateCommand.complete(server, args); }
        if (args.length >= 2 && "portway".equals(args[0])) { return PortwayCommand.complete(server, args); }
        if (args.length == 2 && "card".equals(args[0])) { return getListOfStringsMatchingLastWord(args, CardRules.keys()); }
        if (args.length == 2 && "raid".equals(args[0])) { return getListOfStringsMatchingLastWord(args, ContentRaids.keys()); }
        if (args.length == 3 && "raid".equals(args[0])) { return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 3 && "card".equals(args[0])) { return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 2 && "pregen".equals(args[0])) { return getListOfStringsMatchingLastWord(args, PREGEN_ACTIONS); }
        if (args.length == 2 && "vein".equals(args[0])) { return getListOfStringsMatchingLastWord(args, ContentWorldgen.veinNames()); }
        if (args.length == 2 && "config".equals(args[0])) { return getListOfStringsMatchingLastWord(args, CONFIG_ACTIONS); }
        if (args.length == 2 && "biome".equals(args[0])) { return getListOfStringsMatchingLastWord(args, BIOME_ACTIONS); }
        if (args.length == 2 && "team".equals(args[0])) { return getListOfStringsMatchingLastWord(args, TEAM_ACTIONS); }
        if (args.length == 2 && "round".equals(args[0])) { return getListOfStringsMatchingLastWord(args, ROUND_ACTIONS); }
        if (args.length == 3 && "round".equals(args[0]) && "vote".equals(args[1])) { return getListOfStringsMatchingLastWord(args, Arrays.asList("yes", "no")); }
        if (args.length == 3 && "team".equals(args[0]) && "join".equals(args[1])) { return getListOfStringsMatchingLastWord(args, ContentTeams.joinableNames()); }
        if (args.length == 3 && "team".equals(args[0]) && "vote".equals(args[1])) { return getListOfStringsMatchingLastWord(args, Arrays.asList(server.getOnlinePlayerNames())); }
        if (args.length == 3 && "biome".equals(args[0]) && "list".equals(args[1])) { return getListOfStringsMatchingLastWord(args, Collections.singletonList("all")); }
        if (args.length == 3 && "biome".equals(args[0]) && "find".equals(args[1])) { return getListOfStringsMatchingLastWord(args, biomeNames()); }
        if (args.length == 3 && "biome".equals(args[0]) && "here".equals(args[1])) { return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 3 && "pregen".equals(args[0])) { return getListOfStringsMatchingLastWord(args, Collections.singletonList("relight")); }
        if (args.length == 2 && "goto".equals(args[0])) {
            List<String> known = new ArrayList<>(STRUCTURE_NAMES);
            known.addAll(ContentLocate.names(sender.getEntityWorld()));
            known.addAll(biomeNames());
            known.removeIf(place -> !sender.canUseCommand(neededFor(place, "gotoLevel", Config.commands.gotoLevel), getName()));
            return getListOfStringsMatchingLastWord(args, known);
        }
        if (args.length == 3 && "goto".equals(args[0])) { return getListOfStringsMatchingLastWord(args, Arrays.asList("next", "back")); }
        return Collections.emptyList();
    }

    @Override public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        ContentLog.LOGGER.debug("{} ran /{} {}", sender.getName(), getName(), String.join(" ", args));
        if (args.length == 0) { throw new WrongUsageException(getUsage(sender)); }
        if ("goto".equals(args[0])) {
            if (args.length < 2) { throw new WrongUsageException(getUsage(sender)); }
            String place = placeOf(args);
            String direction = directionOf(args);
            if ("next".equals(direction)) { allowGoto(sender, neededFor(place, "gotoNextLevel", Config.commands.gotoNextLevel)); }
            else if ("back".equals(direction)) { allowGoto(sender, neededFor(place, "gotoBackLevel", Config.commands.gotoBackLevel)); }
            else { allowGoto(sender, neededFor(place, "gotoLevel", Config.commands.gotoLevel)); }
        }
        else if ("card".equals(args[0])) { allow(sender, CardCommand.LEVEL); }
        else if ("portway".equals(args[0])) { allow(sender, PortwayCommand.LEVEL); }
        else if ("environment".equals(args[0])) { allow(sender, EnvironmentCommand.LEVEL); }
        else if (!"team".equals(args[0]) && !"round".equals(args[0]) && !"game".equals(args[0]) && !"talk".equals(args[0]) && (args.length != 1 || !"intro".equals(args[0]))) { allow(sender, OPERATOR); }
        if (args.length == 1 && "reload".equals(args[0])) { reload(server, sender); }
        else if (args.length == 1 && "list".equals(args[0])) { list(sender); }
        else if (args.length == 2 && "which".equals(args[0])) { which(sender, args[1]); }
        else if (args.length == 1 && "unused".equals(args[0])) { unused(sender, "rdpl.command.serverunusednote"); }
        else if (args.length == 1 && "oregen".equals(args[0])) { oregen(sender); }
        else if (args.length == 1 && "generators".equals(args[0])) { generators(sender); }
        else if ("gate".equals(args[0])) { GateCommand.run(server, sender, args, getUsage(sender)); }
        else if ("portway".equals(args[0])) { PortwayCommand.run(server, sender, args, getUsage(sender)); }
        else if (args.length == 1 && "dimensions".equals(args[0])) { dimensions(sender); }
        else if (args.length == 1 && "biome".equals(args[0])) { biome(sender); }
        else if (args.length >= 2 && "biome".equals(args[0]) && "list".equals(args[1])) { biomeList(sender, args.length > 2 && "all".equals(args[2])); }
        else if (args.length == 2 && "biome".equals(args[0]) && "here".equals(args[1])) { here(sender); }
        else if (args.length == 3 && "biome".equals(args[0]) && "here".equals(args[1])) { hereFor(server, sender, args[2]); }
        else if (args.length == 3 && "biome".equals(args[0]) && "find".equals(args[1])) { biomeFind(sender, args[2]); }
        else if (args.length == 1 && "reset".equals(args[0])) { reset(server, sender); }
        else if ("team".equals(args[0])) { team(sender, args); }
        else if (args.length == 2 && "round".equals(args[0]) && "start".equals(args[1])) { round(server, sender); }
        else if (args.length == 2 && "round".equals(args[0]) && "reset".equals(args[1])) { roundReset(server, sender); }
        else if (args.length == 3 && "round".equals(args[0]) && "vote".equals(args[1]) && ("yes".equals(args[2]) || "no".equals(args[2]))) { roundVote(server, sender, "yes".equals(args[2])); }
        else if ("pregen".equals(args[0])) { pregen(sender, args); }
        else if (args.length == 1 && "intro".equals(args[0])) { intro(sender); }
        else if (args.length == 2 && "config".equals(args[0])) { config(sender, args[1], getUsage(sender), "rdpl.command.config.servernote"); }
        else if ("goto".equals(args[0])) { goWhere(sender, args); }
        else if ((args.length == 2 || args.length == 3) && "vein".equals(args[0])) { vein(sender, args[1], args.length == 3 ? parseInt(args[2], 1, 64) : 8); }
        else if ("card".equals(args[0])) { CardCommand.run(server, sender, args, getUsage(sender)); }
        else if ("game".equals(args[0])) { GameCommand.run(server, sender, args); }
        else if ("raid".equals(args[0])) { RaidCommand.run(server, sender, args, getUsage(sender)); }
        else if ("environment".equals(args[0])) { EnvironmentCommand.run(server, sender, args, getUsage(sender)); }
        else if (args.length == 2 && "talk".equals(args[0])) { talk(sender, args[1]); }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private static String directionOf(String[] args) {
        String last = args[args.length - 1];
        return args.length > 2 && ("next".equals(last) || "back".equals(last)) ? last : "";
    }

    private static String placeOf(String[] args) { return String.join(" ", Arrays.copyOfRange(args, 1, args.length - (directionOf(args).isEmpty() ? 0 : 1))); }

    private void goWhere(ICommandSender sender, String[] args) throws CommandException {
        String place = placeOf(args);
        String direction = directionOf(args);
        if ("back".equals(direction)) { goBack(sender, place); }
        else { goTo(sender, place, "next".equals(direction)); }
    }

    private void goTo(ICommandSender sender, String asked, boolean next) throws CommandException {
        if (ContentPregen.busy()) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotomakingland")); }
        EntityPlayerMP player = CommandShared.goer(sender);
        World world = player.world;
        String name = STRUCTURE_ALIASES.getOrDefault(asked, asked);
        Biome biome = biomeFor(world, name);
        if (biome != null) {
            if (ContentBiomeSearch.looking()) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotobusy")); }
            ContentBiomeSearch.start(player, String.valueOf(biome.getRegistryName()), biome, next);
            return;
        }
        if (ContentLocate.names(world).contains(name)) {
            BlockPos found = ContentLocate.nearest(world, name, player.getPosition(), next ? 128.0D : 0.0D);
            if (found == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotonothing", name)); }
            BlockPos landing = ContentStructureSearch.landing(world, found);
            if (landing == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotonoground", name, found.getX(), found.getZ())); }
            ContentStructureSearch.remember(player, name, found);
            player.setPositionAndUpdate(landing.getX() + 0.5D, ContentStructureSearch.stand(world, landing), landing.getZ() + 0.5D);
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gotodone", name, landing.getX(), landing.getY(), landing.getZ()));
            return;
        }
        if (ContentStructureSearch.looking()) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotobusy")); }
        ContentStructureSearch.start(player, name, keyFor(name), !next, next);
    }

    private void goBack(ICommandSender sender, String asked) throws CommandException {
        if (ContentPregen.busy()) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotomakingland")); }
        EntityPlayerMP player = CommandShared.goer(sender);
        String name = STRUCTURE_ALIASES.getOrDefault(asked, asked);
        Biome biome = biomeFor(player.world, name);
        if (biome != null) { name = String.valueOf(biome.getRegistryName()); }
        BlockPos previous = ContentStructureSearch.stepBack(player, name);
        if (previous == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotonoback", name)); }
        BlockPos landing = ContentStructureSearch.landing(player.world, previous);
        if (landing == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.gotonoground", name, previous.getX(), previous.getZ())); }
        player.setPositionAndUpdate(landing.getX() + 0.5D, ContentStructureSearch.stand(player.world, landing), landing.getZ() + 0.5D);
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gotodone", name, landing.getX(), landing.getY(), landing.getZ()));
    }

    @Nullable private static Biome biomeFor(World world, String name) {
        if (STRUCTURE_NAMES.contains(name) || ContentLocate.names(world).contains(name)) { return null; }
        return findBiome(name);
    }

    private void allowGoto(ICommandSender sender, int needed) throws CommandException {
        if (!(sender instanceof EntityPlayerMP) && sender.canUseCommand(PACK_GOTO, getName())) { return; }
        allow(sender, needed);
    }

    private static String keyFor(String name) {
        for (Map.Entry<String, String> entry : STRUCTURE_ALIASES.entrySet()) {
            if (entry.getValue().equals(name)) { return entry.getKey(); }
        }
        return null;
    }

    private void reset(MinecraftServer server, ICommandSender sender) {
        int swept = ContentReset.run(server);
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.reset", swept));
    }

    private void round(MinecraftServer server, ICommandSender sender) {
        if (!(sender instanceof EntityPlayer)) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.round.start.playeronly"));
            return;
        }
        say(sender, ContentScoring.start(server, (EntityPlayer) sender, sender.canUseCommand(OPERATOR, getName())));
    }

    private void roundReset(MinecraftServer server, ICommandSender sender) {
        EntityPlayer who = CommandShared.viewer(sender);
        say(sender, ContentRoundReset.call(server, sender.getName(), who, sender.canUseCommand(OPERATOR, getName())));
    }

    private void roundVote(MinecraftServer server, ICommandSender sender, boolean yes) {
        if (!(sender instanceof EntityPlayer)) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.round.vote.playeronly"));
            return;
        }
        say(sender, ContentRoundReset.vote(server, (EntityPlayer) sender, yes));
    }

    private static void say(ICommandSender sender, RoundSay said) { send(sender, said.good ? TextFormatting.GREEN : TextFormatting.RED, Lang.tr(sender, said.key, said.args)); }

    private void team(ICommandSender sender, String[] args) throws CommandException {
        if (!ContentTeams.any()) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.none"));
            return;
        }
        if (args.length == 1 || "list".equals(args[1])) {
            for (TeamDef def : ContentTeams.all().values()) {
                String standing = def.joinable ? "" : Lang.tr(sender, "rdpl.team.closed");
                String lead = ContentTeams.leadOf(sender.getEntityWorld(), def);
                if (lead != null) { standing = standing + Lang.tr(sender, "rdpl.team.leadnote", lead); }
                send(sender, def.color, def.name + " - " + def.displayName + standing);
            }
            EntityPlayer asking = CommandShared.viewer(sender);
            String on = asking == null ? null : ContentTeams.standingOf(asking);
            send(sender, TextFormatting.GRAY, on == null ? Lang.tr(sender, "rdpl.team.onnone") : Lang.tr(sender, "rdpl.team.on", on));
            return;
        }
        EntityPlayer player = CommandShared.viewer(sender);
        if (player == null) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.playeronly"));
            return;
        }
        TeamDef mine = null;
        String standing = ContentTeams.standingOf(player);
        if (standing != null) { mine = ContentTeams.named(standing); }
        if ("vote".equals(args[1])) {
            if (mine == null) { send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.vote.noteam")); return; }
            if (args.length < 3) { throw new WrongUsageException(getUsage(sender)); }
            if (!"vote".equals(mine.lead)) { send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.vote.novote", mine.displayName)); return; }
            boolean stood = ContentTeams.standsFor(player.world, player.getName(), args[2], mine);
            send(sender, stood ? mine.color : TextFormatting.RED,
                    stood ? Lang.tr(sender, "rdpl.team.vote.cast", args[2]) : Lang.tr(sender, "rdpl.team.vote.notmember", args[2]));
            if (stood) {
                String lead = ContentTeams.leadOf(player.world, mine);
                send(sender, TextFormatting.GRAY, lead == null ? Lang.tr(sender, "rdpl.team.vote.tied") : Lang.tr(sender, "rdpl.team.vote.leads", lead, mine.displayName));
            }
            return;
        }
        if ("claim".equals(args[1])) {
            if (mine == null) { send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.claim.noteam")); return; }
            if (!"claim".equals(mine.lead)) { send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.claim.noclaim", mine.displayName)); return; }
            boolean took = ContentTeams.claim(player.world, player.getName(), mine);
            send(sender, took ? mine.color : TextFormatting.RED,
                    took ? mine.leadSays.replace("{side}", mine.displayName) : Lang.tr(sender, "rdpl.team.claim.held", ContentTeams.holding(mine), mine.displayName));
            return;
        }
        boolean locked = ContentScoring.roundRunning();
        if ("leave".equals(args[1])) {
            if (locked) { send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.leave.running")); return; }
            send(sender, TextFormatting.GREEN, Lang.tr(sender, ContentTeams.stand(player) ? "rdpl.team.leave.ok" : "rdpl.team.leave.none"));
            return;
        }
        if (!"join".equals(args[1])) { throw new WrongUsageException(getUsage(sender)); }
        TeamDef wanted = args.length >= 3
                ? ContentTeams.named(args[2])
                : ContentTeams.smallest(player.world);
        if (args.length < 3 && wanted == null) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.join.balance"));
            return;
        }
        if (wanted == null) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.join.unknown", args[2]));
            return;
        }
        if (!wanted.joinable) {
            send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.team.join.closed", wanted.name));
            return;
        }
        if (locked) {
            ContentTeams.waitFor(player, wanted);
            send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.team.join.waiting", wanted.displayName));
            return;
        }
        ContentTeams.take(player, wanted);
        send(sender, wanted.color, Lang.tr(sender, "rdpl.team.join.ok", wanted.displayName));
    }

    private void pregen(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2 && "status".equals(args[1])) {
            send(sender, TextFormatting.GREEN, ContentPregen.state(sender));
            return;
        }
        if (args.length == 2 && "stop".equals(args[1])) {
            send(sender, TextFormatting.YELLOW, ContentPregen.stop() ? Lang.tr(sender, "rdpl.command.stopping") : Lang.tr(sender, "rdpl.command.nothing"));
            return;
        }
        boolean lightOnly = args.length == 3 && "relight".equals(args[2]);
        if (args.length != 2 && !lightOnly) { throw new WrongUsageException(Lang.tr(sender, "rdpl.command.pregenusage")); }
        if (ContentPregen.busy()) { throw new CommandException(Lang.tr(sender, "rdpl.command.busy")); }
        if (CubicChunksWorlds.cubic(sender.getEntityWorld())) { throw new CommandException(Lang.tr(sender, "rdpl.command.cubicchunks")); }
        int radius = parseInt(args[1], 0, 8192);
        BlockPos at = sender.getPosition();
        int dimension = sender.getEntityWorld().provider.getDimension();
        long total = ContentPregen.start(sender, dimension, at.getX() >> 4, at.getZ() >> 4, radius, lightOnly);
        if (lightOnly) { send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.relighting", total, at.getX() >> 4, at.getZ() >> 4, dimension)); }
        else { send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.making", total, at.getX() >> 4, at.getZ() >> 4, dimension)); }
    }

    private void reload(MinecraftServer server, ICommandSender sender) throws CommandException {
        long start = System.currentTimeMillis();
        rescan(sender);
        server.reload();
        ContentTeams.load();
        ContentScoring.load();
        ContentDice.load();
        ContentBoards.load();
        ContentBoards.rebuild(server);
        CardRules.load();
        ContentDialogs.load();
        ContentHearts.load();
        ContentHearts.refresh(server);
        for (WorldServer world : server.worlds) {
            ContentTeams.field(world);
            ContentScoring.keep(world);
        }
        int packs = PackManager.get().getPacks().size();
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.serverreloaded", packs, elapsed(start)));
        send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.serverrecipes"));
    }

    private void list(ICommandSender sender) {
        CommandShared.listPacks(sender, (pack, label) -> send(sender, TextFormatting.WHITE, label
                + TextFormatting.GRAY + "  advancements=" + pack.count(PackManager.ADVANCEMENTS, PackManager.JSON)
                + " loot_tables=" + pack.count(PackManager.LOOT_TABLES, PackManager.JSON)
                + " functions=" + pack.count(PackManager.FUNCTIONS, PackManager.MCFUNCTION)
                + " namespaces=" + pack.getNamespaces()));
    }

    private void oregen(ICommandSender sender) { blockedReport(sender, ContentOreControl.blocked(), "rdpl.command.orenone", "rdpl.command.oreblocked"); }

    private void generators(ICommandSender sender) { blockedReport(sender, ContentGeneratorControl.blocked(), "rdpl.command.gennone", "rdpl.command.genblocked"); }

    private void which(ICommandSender sender, String target) { CommandShared.which(sender, target, false); }

    private void vein(ICommandSender sender, String asked, int radius) throws CommandException {
        WorldgenDef def = ContentWorldgen.byName(asked);
        if (def == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.veinunknown", asked)); }
        if (!(def.getShape() instanceof ContentOreVein)) { throw new CommandException(Lang.tr(sender, "rdpl.command.veinnotvein", def.registryName)); }
        ContentOreVein shape = (ContentOreVein) def.getShape();
        World world = sender.getEntityWorld();
        BlockPos here = sender.getPosition();
        int chunkX = here.getX() >> 4;
        int chunkZ = here.getZ() >> 4;
        List<ContentOreVein.Vein> found = new ArrayList<>();
        for (int cx = chunkX - radius; cx <= chunkX + radius; cx++) {
            for (int cz = chunkZ - radius; cz <= chunkZ + radius; cz++) {
                for (ContentOreVein.Vein vein : shape.veinsOf(world, cx, cz)) {
                    if (ContentWorldgen.allowsAt(def, world, new BlockPos(vein.x, vein.y, vein.z))) { found.add(vein); }
                }
            }
        }
        if (found.isEmpty()) { throw new CommandException(Lang.tr(sender, "rdpl.command.veinnone", def.registryName, radius)); }
        found.sort(Comparator.comparingDouble(vein -> here.distanceSq(vein.x, vein.y, vein.z)));
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.veinfound", found.size(), def.registryName, radius));
        for (ContentOreVein.Vein vein : found.subList(0, Math.min(5, found.size()))) {
            send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.veinat", vein.x, vein.y, vein.z, (int) Math.sqrt(here.distanceSq(vein.x, vein.y, vein.z))));
        }
    }

    private void biome(ICommandSender sender) { for (String line : ContentBiomeControl.inspect(sender.getEntityWorld(), sender.getPosition())) { send(sender, TextFormatting.WHITE, line); } }

    private void dimensions(ICommandSender sender) {
        Map<ResourceLocation, DimensionDef> defs = ContentDimensions.all();
        if (defs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.nodims"));
            return;
        }
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.dims", defs.size()));
        for (Map.Entry<ResourceLocation, DimensionDef> entry : defs.entrySet()) {
            DimensionDef def = entry.getValue();
            boolean live = DimensionManager.isDimensionRegistered(def.id);
            send(sender, live ? TextFormatting.WHITE : TextFormatting.GRAY, "  " + entry.getKey() + TextFormatting.GRAY
                    + "  id=" + def.id + " terrain=" + def.terrain + " biomes=" + def.biomeSource
                    + Lang.tr(sender, live ? "rdpl.command.registered" : "rdpl.command.unregistered"));
        }
    }

    private void here(ICommandSender sender) throws CommandException {
        if (sender instanceof MinecraftServer || sender instanceof RConConsoleSource) { throw new CommandException(Lang.tr(sender, "rdpl.command.hereplayer")); }
        biomeHere(sender);
    }

    private void hereFor(MinecraftServer server, ICommandSender sender, String name) throws CommandException {
        EntityPlayerMP player = getPlayer(server, sender, name);
        biomeAt(sender, player.world, player.getPosition());
    }

    private void intro(ICommandSender sender) throws CommandException {
        if (!ContentIntroPlay.enabled()) { throw new CommandException(Lang.tr(sender, "rdpl.command.intronone")); }
        if (!(sender instanceof EntityPlayerMP)) { throw new CommandException(Lang.tr(sender, "rdpl.command.introplayer")); }
        ContentIntroPlay.replay((EntityPlayerMP) sender);
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.introagain"));
    }

    private void talk(ICommandSender sender, String number) throws CommandException {
        EntityPlayerMP player = CommandShared.goer(sender);
        if (DialogTalk.idle(player)) { throw new CommandException(Lang.tr(sender, "rdpl.command.notalking")); }
        DialogTalk.pick(player, parseInt(number, 1));
    }
}
