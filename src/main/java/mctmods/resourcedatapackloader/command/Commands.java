package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLPack;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Commands extends CommandBase {
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "list", "which");

    @Override @Nonnull public String getName() { return "rdpl"; }

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return "/rdpl <reload|list|which <namespace:path>>"; }

    @Override public int getRequiredPermissionLevel() { return 3; }

    @Override @Nonnull public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) { return getListOfStringsMatchingLastWord(args, SUBCOMMANDS); }
        return Collections.emptyList();
    }

    @Override public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && "reload".equals(args[0])) { reload(server, sender); }
        else if (args.length == 1 && "list".equals(args[0])) { list(sender); }
        else if (args.length == 2 && "which".equals(args[0])) { which(sender, args[1]); }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private void reload(MinecraftServer server, ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException("Pack root is not known yet"); }
        PackManager.get().scan(root);
        PackManager.get().report();
        server.reload();
        int packs = PackManager.get().getPacks().size();
        send(sender, TextFormatting.GREEN, "Rescanned " + packs + " pack(s) and reloaded advancements, loot tables and functions");
        send(sender, TextFormatting.GRAY, "Recipes are not reloadable and still need a restart");
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
            send(sender, TextFormatting.WHITE, "  " + pack.getName() + priority
                    + TextFormatting.GRAY + "  advancements=" + pack.count(PackManager.ADVANCEMENTS, PackManager.JSON)
                    + " loot_tables=" + pack.count(PackManager.LOOT_TABLES, PackManager.JSON)
                    + " namespaces=" + pack.getNamespaces());
        }
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

    private static void send(ICommandSender sender, TextFormatting colour, String message) {
        sender.sendMessage(new TextComponentString(colour + message));
    }
}
