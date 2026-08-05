package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerCommands extends CommandBase {
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "list", "which", "unused", "oregen", "generators", "gate", "dimensions", "biome", "pregen");
    private static final List<String> PREGEN_ACTIONS = Arrays.asList("stop", "status");
    private static final List<String> GATE_ACTIONS = Arrays.asList("list", "check", "grant", "revoke");

    @Override @Nonnull public String getName() { return "rdplserver"; }

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return Lang.tr(sender, "rdpl.command.serverusage"); }

    @Override public int getRequiredPermissionLevel() { return 3; }

    @Override @Nonnull public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) { return getListOfStringsMatchingLastWord(args, SUBCOMMANDS); }
        if (args.length == 2 && "gate".equals(args[0])) { return getListOfStringsMatchingLastWord(args, GATE_ACTIONS); }
        if (args.length == 3 && "gate".equals(args[0]) && !"list".equals(args[1])) { return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 2 && "pregen".equals(args[0])) { return getListOfStringsMatchingLastWord(args, PREGEN_ACTIONS); }
        if (args.length == 3 && "pregen".equals(args[0])) { return getListOfStringsMatchingLastWord(args, Collections.singletonList("relight")); }
        if (args.length == 4 && "gate".equals(args[0])) { return getListOfStringsMatchingLastWord(args, names()); }
        return Collections.emptyList();
    }

    @Override public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        ContentLog.LOGGER.info("{} ran /{} {}", sender.getName(), getName(), String.join(" ", args));
        if (args.length == 1 && "reload".equals(args[0])) { reload(server, sender); }
        else if (args.length == 1 && "list".equals(args[0])) { list(sender); }
        else if (args.length == 2 && "which".equals(args[0])) { which(sender, args[1]); }
        else if (args.length == 1 && "unused".equals(args[0])) { unused(sender); }
        else if (args.length == 1 && "oregen".equals(args[0])) { oregen(sender); }
        else if (args.length == 1 && "generators".equals(args[0])) { generators(sender); }
        else if (args.length >= 1 && "gate".equals(args[0])) { gate(server, sender, args); }
        else if (args.length == 1 && "dimensions".equals(args[0])) { dimensions(sender); }
        else if (args.length == 1 && "biome".equals(args[0])) { biome(sender); }
        else if (args.length >= 1 && "pregen".equals(args[0])) { pregen(sender, args); }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private void pregen(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2 && "status".equals(args[1])) {
            send(sender, TextFormatting.GREEN, ContentPregen.state());
            return;
        }
        if (args.length == 2 && "stop".equals(args[1])) {
            send(sender, TextFormatting.YELLOW, ContentPregen.stop() ? Lang.tr(sender, "rdpl.command.stopping") : Lang.tr(sender, "rdpl.command.nothing"));
            return;
        }
        boolean lightOnly = args.length == 3 && "relight".equals(args[2]);
        if (args.length != 2 && !lightOnly) { throw new WrongUsageException(Lang.tr(sender, "rdpl.command.pregenusage")); }
        if (ContentPregen.busy()) { throw new CommandException(Lang.tr(sender, "rdpl.command.busy")); }

        int radius = parseInt(args[1], 0, 8192);
        BlockPos at = sender.getPosition();
        int dimension = sender.getEntityWorld().provider.getDimension();
        long total = ContentPregen.start(sender, dimension, at.getX() >> 4, at.getZ() >> 4, radius, lightOnly);
        if (lightOnly) { send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.relighting", total, at.getX() >> 4, at.getZ() >> 4, dimension)); }
        else { send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.making", total, at.getX() >> 4, at.getZ() >> 4, dimension)); }
    }

    private void reload(MinecraftServer server, ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.noroot")); }
        long start = System.currentTimeMillis();
        PackManager.get().scan(root);
        PackManager.get().report();
        server.reload();
        int packs = PackManager.get().getPacks().size();
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.serverreloaded", packs, elapsed(start)));
        send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.serverrecipes"));
    }

    private void list(ICommandSender sender) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.nopacks", PackManager.get().getRoot()));
            return;
        }
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.packs", packs.size()));
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String tier = pack.isOverriding() ? Lang.tr(sender, "rdpl.command.overriding") : "";
            send(sender, TextFormatting.WHITE, "  " + pack.getName() + priority + tier
                    + TextFormatting.GRAY + "  advancements=" + pack.count(PackManager.ADVANCEMENTS, PackManager.JSON)
                    + " loot_tables=" + pack.count(PackManager.LOOT_TABLES, PackManager.JSON)
                    + " functions=" + pack.count(PackManager.FUNCTIONS, PackManager.MCFUNCTION)
                    + " namespaces=" + pack.getNamespaces());
        }
    }

    private void oregen(ICommandSender sender) {
        Map<String, Integer> blocked = ContentOreControl.blocked();
        if (blocked.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.orenone"));
            return;
        }

        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.oreblocked"));
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) {
            send(sender, TextFormatting.GRAY, "  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void generators(ICommandSender sender) {
        Map<String, Integer> blocked = ContentGeneratorControl.blocked();
        if (blocked.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.gennone"));
            return;
        }

        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.genblocked"));
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) {
            send(sender, TextFormatting.GRAY, "  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void unused(ICommandSender sender) {
        List<String> unused = PackManager.get().findUnused();
        if (unused.isEmpty()) {
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.allused"));
            return;
        }
        send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.unused", unused.size()));
        for (String entry : unused) { ContentLog.LOGGER.warn("  {}", entry); }
        send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.serverunusednote"));
    }

    private void which(ICommandSender sender, String target) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        List<RDPLPack> holders = PackManager.get().holders(namespace, path);
        if (holders.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.unprovided", namespace + ":" + path));
            return;
        }
        RDPLPack winner = holders.get(holders.size() - 1);
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.served", namespace + ":" + path, winner.getName()));
        for (int i = holders.size() - 2; i >= 0; i--) {
            send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.shadows", holders.get(i).getName()));
        }
    }

    private void gate(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!ContentGates.enabled()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.nogates"));
            return;
        }

        String action = args.length < 2 ? "list" : args[1];
        if ("list".equals(action)) {
            gateList(sender);
            return;
        }

        if (args.length < 3) { throw new WrongUsageException(getUsage(sender)); }
        EntityPlayerMP player = getPlayer(server, sender, args[2]);
        if ("check".equals(action)) {
            gateCheck(sender, player);
            return;
        }

        if (args.length < 4) { throw new WrongUsageException(getUsage(sender)); }
        GateDef def = ContentGates.find(args[3]);
        if (def == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.nogate", args[3])); }

        if ("grant".equals(action)) {
            ContentGates.unlock(player, def, false);
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gateopened", def.getKey(), player.getName(), def.getScope()));
        }
        else if ("revoke".equals(action)) {
            ContentGates.lock(player, def);
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gateclosed", def.getKey(), player.getName(), def.getScope()));
        }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private void biome(ICommandSender sender) {
        for (String line : ContentBiomeControl.inspect(sender.getEntityWorld(), sender.getPosition())) { send(sender, TextFormatting.WHITE, line); }
    }

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

    private void gateList(ICommandSender sender) {
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gates", ContentGates.all().size()));
        for (GateDef def : ContentGates.all().values()) {
            send(sender, TextFormatting.WHITE, "  " + def.getKey() + TextFormatting.GRAY + "  dimension=" + def.dimension + " scope=" + def.getScope() + (def.open ? Lang.tr(sender, "rdpl.command.open") : ""));
        }
    }

    private void gateCheck(ICommandSender sender, EntityPlayerMP player) {
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gatesfor", player.getName()));
        for (GateDef def : ContentGates.all().values()) {
            boolean unlocked = ContentGates.unlocked(player, def);
            send(sender, unlocked ? TextFormatting.WHITE : TextFormatting.GRAY, "  " + def.getKey() + Lang.tr(sender, unlocked ? "rdpl.command.open" : "rdpl.command.closed"));
        }
    }

    private static List<String> names() {
        List<String> names = new ArrayList<>();
        for (GateDef def : ContentGates.all().values()) { names.add(def.registryName.getPath()); }
        return names;
    }

    private static String elapsed(long start) {
        long time = System.currentTimeMillis() - start;
        return time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
    }

    private static void send(ICommandSender sender, TextFormatting color, String message) {
        sender.sendMessage(new TextComponentString(color + message));
        ContentLog.LOGGER.info("  {}", message);
    }
}
