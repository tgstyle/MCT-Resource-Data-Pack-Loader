package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CommandShared {
    private CommandShared() {}

    static LiteralArgumentBuilder<CommandSourceStack> tree(String name, Command<CommandSourceStack> reload, String unusedNote, String configNote) {
        return Commands.literal(name)
                .then(Commands.literal("reload").executes(reload))
                .then(Commands.literal("list").executes(context -> {
                    ran(context.getSource(), name, "list");
                    list(context.getSource(), name);
                    return 1;
                }))
                .then(Commands.literal("which").then(Commands.argument("file", StringArgumentType.greedyString()).executes(context -> {
                    String target = StringArgumentType.getString(context, "file");
                    ran(context.getSource(), name, "which " + target);
                    which(context.getSource(), target);
                    return 1;
                })))
                .then(Commands.literal("unused").executes(context -> {
                    ran(context.getSource(), name, "unused");
                    unused(context.getSource(), unusedNote);
                    return 1;
                }))
                .then(Commands.literal("config")
                        .then(Commands.literal("unused").executes(context -> {
                            ran(context.getSource(), name, "config unused");
                            config(context.getSource(), false, configNote);
                            return 1;
                        }))
                        .then(Commands.literal("prune").executes(context -> {
                            ran(context.getSource(), name, "config prune");
                            config(context.getSource(), true, configNote);
                            return 1;
                        })));
    }

    static void ran(CommandSourceStack source, String name, String rest) { ContentLog.LOGGER.debug("{} ran /{} {}", source.getTextName(), name, rest); }

    static MutableComponent tr(String key, Object... args) { return Component.translatable(key, args); }

    static void send(CommandSourceStack source, ChatFormatting color, MutableComponent message) {
        source.sendSuccess(() -> message.withStyle(color), false);
        ContentLog.LOGGER.debug("  {}", message.getString());
    }

    static void send(CommandSourceStack source, Component message, String logged) {
        source.sendSuccess(() -> message, false);
        ContentLog.LOGGER.debug("  {}", logged);
    }

    static String elapsed(long start) {
        long time = System.currentTimeMillis() - start;
        return time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
    }

    static CompletableFuture<Void> reloadServer(MinecraftServer server) {
        return CompletableFuture.supplyAsync(() -> {
            PackRepository repository = server.getPackRepository();
            repository.reload();
            return repository.getSelectedIds();
        }, server).thenCompose(server::reloadResources);
    }

    static void list(CommandSourceStack source, String name) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.nopacks", String.valueOf(PackManager.get().getRoot())));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.packs", packs.size()));
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String detail = "files=" + pack.getFileCount()
                    + "\nassets=" + pack.getNamespaces(PackType.CLIENT_RESOURCES)
                    + "\ndata=" + pack.getNamespaces(PackType.SERVER_DATA);
            MutableComponent line = Component.literal("  " + pack.getName() + priority);
            if (pack.isOverriding()) { line.append(tr("rdpl.command.overriding")); }
            line.withStyle(style -> style.withColor(pack.isOverriding() ? ChatFormatting.AQUA : ChatFormatting.WHITE)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(detail + "\n").append(tr("rdpl.command.clickhint"))))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + name + " which " + firstNamespace(pack) + ":")));
            send(source, line, "  " + pack.getName() + priority + (pack.isOverriding() ? " (overriding)" : "") + " " + detail.replace('\n', ' '));
        }
    }

    private static String firstNamespace(RDPLPack pack) {
        for (PackType type : PackType.values()) {
            for (String namespace : pack.getNamespaces(type)) { return namespace; }
        }
        return "minecraft";
    }

    static void which(CommandSourceStack source, String target) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        boolean found = false;
        for (PackType type : PackType.values()) {
            List<RDPLPack> holders = PackManager.get().holders(type, namespace, path);
            if (holders.isEmpty()) { continue; }
            found = true;
            String shown = type.getDirectory() + "/" + namespace + "/" + path;
            RDPLPack winner = holders.getLast();
            send(source, ChatFormatting.GREEN, tr("rdpl.command.provided", shown, winner.getName(), winner.isOverriding() ? tr("rdpl.command.overriding") : Component.empty()));
            send(source, ChatFormatting.GRAY, tr("rdpl.command.providednote"));
            for (int i = holders.size() - 2; i >= 0; i--) { send(source, ChatFormatting.GRAY, tr("rdpl.command.shadows", holders.get(i).getName())); }
        }
        if (!found) { send(source, ChatFormatting.YELLOW, tr("rdpl.command.unprovided", namespace + ":" + path)); }
    }

    static void unused(CommandSourceStack source, String note) {
        List<String> unused = PackManager.get().findUnused();
        if (unused.isEmpty()) {
            send(source, ChatFormatting.GREEN, tr("rdpl.command.allused"));
            return;
        }
        send(source, ChatFormatting.YELLOW, tr("rdpl.command.unused", unused.size()));
        for (String entry : unused) { ContentLog.LOGGER.warn("  {}", entry); }
        send(source, ChatFormatting.GRAY, tr(note));
    }

    static void config(CommandSourceStack source, boolean prune, String note) {
        List<String> stale = PackOptions.orphans();
        if (stale.isEmpty()) {
            send(source, ChatFormatting.GREEN, tr("rdpl.command.config.none"));
            return;
        }
        if (!prune) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.config.unused", stale.size()));
            for (String one : stale) { send(source, ChatFormatting.GRAY, Component.literal("  " + one + ".json")); }
            send(source, ChatFormatting.GRAY, tr(note));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.config.pruned", PackOptions.prune()));
    }
}
