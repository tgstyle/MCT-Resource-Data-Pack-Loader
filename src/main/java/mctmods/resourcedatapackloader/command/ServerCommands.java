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

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return "/rdplserver <reload|list|which <namespace:path>|unused|oregen|generators|gate <list|check <player>|grant <player> <gate>|revoke <player> <gate>>|pregen <radius [relight]|stop|status>>"; }

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
            send(sender, TextFormatting.YELLOW, ContentPregen.stop() ? "Stopping" : "Nothing is being made at the moment");
            return;
        }
        boolean lightOnly = args.length == 3 && "relight".equals(args[2]);
        if (args.length != 2 && !lightOnly) { throw new WrongUsageException("/rdplserver pregen <radius in chunks> [relight]|stop|status"); }
        if (ContentPregen.busy()) { throw new CommandException("Something is already being made, stop it first"); }

        int radius = parseInt(args[1], 0, 8192);
        BlockPos at = sender.getPosition();
        int dimension = sender.getEntityWorld().provider.getDimension();
        long total = ContentPregen.start(sender, dimension, at.getX() >> 4, at.getZ() >> 4, radius, lightOnly);
        if (lightOnly) { send(sender, TextFormatting.GREEN, "Going over " + total + " chunk(s) around " + (at.getX() >> 4) + ", " + (at.getZ() >> 4) + " in dimension " + dimension + ", lighting any the light never reached and making none"); }
        else { send(sender, TextFormatting.GREEN, "Making " + total + " chunk(s) around " + (at.getX() >> 4) + ", " + (at.getZ() >> 4) + " in dimension " + dimension); }
    }

    private void reload(MinecraftServer server, ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException("Pack root is not known yet"); }
        long start = System.currentTimeMillis();
        PackManager.get().scan(root);
        PackManager.get().report();
        server.reload();
        int packs = PackManager.get().getPacks().size();
        send(sender, TextFormatting.GREEN, "Rescanned " + packs + " pack(s) and reloaded advancements and loot tables in " + elapsed(start));
        send(sender, TextFormatting.GRAY, "Recipes and functions are not reloadable and still need a restart");
    }

    private void list(ICommandSender sender) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, "No packs loaded from " + PackManager.get().getRoot());
            return;
        }
        send(sender, TextFormatting.GREEN, packs.size() + " pack(s), lowest priority first:");
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String tier = pack.isOverriding() ? " (overriding)" : "";
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
            send(sender, TextFormatting.YELLOW, "No ore generation has been blocked. Either the settings are off, or no chunk that would have generated ore has been made yet");
            return;
        }

        send(sender, TextFormatting.GREEN, "Ore generation blocked so far:");
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) {
            send(sender, TextFormatting.GRAY, "  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void generators(ICommandSender sender) {
        Map<String, Integer> blocked = ContentGeneratorControl.blocked();
        if (blocked.isEmpty()) {
            send(sender, TextFormatting.YELLOW, "No world generator has been blocked. Either the settings are off, or no chunk that would have run one has been made yet");
            return;
        }

        send(sender, TextFormatting.GREEN, "World generation blocked so far, by mod and type:");
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) {
            send(sender, TextFormatting.GRAY, "  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void unused(ICommandSender sender) {
        List<String> unused = PackManager.get().findUnused();
        if (unused.isEmpty()) {
            send(sender, TextFormatting.GREEN, "Every file in your packs has been asked for at least once");
            return;
        }
        send(sender, TextFormatting.YELLOW, unused.size() + " file(s) have not been asked for:");
        for (String entry : unused) { ContentLog.LOGGER.warn("  {}", entry); }
        send(sender, TextFormatting.GRAY, "Some only load when they are needed, so check the paths rather than deleting them");
    }

    private void which(ICommandSender sender, String target) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        List<RDPLPack> holders = PackManager.get().holders(namespace, path);
        if (holders.isEmpty()) {
            send(sender, TextFormatting.YELLOW, namespace + ":" + path + " is not provided by any pack, Minecraft or a mod will serve it");
            return;
        }
        RDPLPack winner = holders.get(holders.size() - 1);
        send(sender, TextFormatting.GREEN, namespace + ":" + path + " is served by '" + winner.getName() + "'");
        for (int i = holders.size() - 2; i >= 0; i--) {
            send(sender, TextFormatting.GRAY, "  shadows '" + holders.get(i).getName() + "'");
        }
    }

    private void gate(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!ContentGates.enabled()) {
            send(sender, TextFormatting.YELLOW, "No gates are loaded, so no dimension is being guarded");
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
        if (def == null) { throw new CommandException("No gate is named " + args[3]); }

        if ("grant".equals(action)) {
            ContentGates.unlock(player, def, false);
            send(sender, TextFormatting.GREEN, "Opened " + def.getKey() + " for " + player.getName() + " (" + def.getScope() + " scope)");
        }
        else if ("revoke".equals(action)) {
            ContentGates.lock(player, def);
            send(sender, TextFormatting.GREEN, "Closed " + def.getKey() + " for " + player.getName() + " (" + def.getScope() + " scope)");
        }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private void biome(ICommandSender sender) {
        for (String line : ContentBiomeControl.inspect(sender.getEntityWorld(), sender.getPosition())) { send(sender, TextFormatting.WHITE, line); }
    }

    private void dimensions(ICommandSender sender) {
        Map<ResourceLocation, DimensionDef> defs = ContentDimensions.all();
        if (defs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, "No pack defines a dimension");
            return;
        }

        send(sender, TextFormatting.GREEN, defs.size() + " dimension(s) defined:");
        for (Map.Entry<ResourceLocation, DimensionDef> entry : defs.entrySet()) {
            DimensionDef def = entry.getValue();
            boolean live = DimensionManager.isDimensionRegistered(def.id);
            send(sender, live ? TextFormatting.WHITE : TextFormatting.GRAY, "  " + entry.getKey() + TextFormatting.GRAY
                    + "  id=" + def.id + " terrain=" + def.terrain + " biomes=" + def.biomeSource
                    + (live ? " registered" : " NOT registered"));
        }
    }

    private void gateList(ICommandSender sender) {
        send(sender, TextFormatting.GREEN, ContentGates.all().size() + " gate(s):");
        for (GateDef def : ContentGates.all().values()) {
            send(sender, TextFormatting.WHITE, "  " + def.getKey() + TextFormatting.GRAY + "  dimension=" + def.dimension + " scope=" + def.getScope() + (def.open ? " open" : ""));
        }
    }

    private void gateCheck(ICommandSender sender, EntityPlayerMP player) {
        send(sender, TextFormatting.GREEN, "Gates for " + player.getName() + ":");
        for (GateDef def : ContentGates.all().values()) {
            boolean unlocked = ContentGates.unlocked(player, def);
            send(sender, unlocked ? TextFormatting.WHITE : TextFormatting.GRAY, "  " + def.getKey() + (unlocked ? " open" : " closed"));
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
