package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.util.entity.Scores;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class ClaimCommand {
    private static final String CLAIM = "claim";
    private static final String UNCLAIM = "unclaim";
    private static final String LIST = "claims";
    private static final String OWNER = "owner";
    private static final String RADIUS = "radius";
    private static final String DISABLED = "rdpl.claim.disabled";
    private static final String NOT_ALLOWED = "rdpl.command.notallowed";
    private static final String NO_OWNER = "rdpl.claim.noowner";
    private static final SuggestionProvider<CommandSourceStack> OWNERS = (context, suggestions) -> SharedSuggestionProvider.suggest(owners(context.getSource().getServer()), suggestions);

    private ClaimCommand() {}

    public static boolean listed() { return ClaimSettings.any() && ClaimSettings.allows(ClaimSettings.Method.COMMAND); }

    public static LiteralArgumentBuilder<CommandSourceStack> claim(String name) {
        return Commands.literal(CLAIM).requires(ClaimCommand::mayUse).executes(context -> claim(context, name, -1))
                .then(Commands.literal("for")
                        .then(Commands.argument(OWNER, StringArgumentType.word()).suggests(OWNERS).executes(context -> claimFor(context, name))))
                .then(Commands.argument(RADIUS, IntegerArgumentType.integer(0)).executes(context -> claim(context, name, IntegerArgumentType.getInteger(context, RADIUS))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> unclaim(String name) {
        return Commands.literal(UNCLAIM).requires(ClaimCommand::mayUse).executes(context -> unclaim(context, name, -1))
                .then(Commands.literal("all").executes(context -> unclaimAll(context, name)))
                .then(Commands.argument(RADIUS, IntegerArgumentType.integer(0)).executes(context -> unclaim(context, name, IntegerArgumentType.getInteger(context, RADIUS))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> claims(String name) {
        return Commands.literal(LIST).requires(ClaimCommand::mayUse).executes(context -> list(context, name, null))
                .then(Commands.argument(OWNER, StringArgumentType.word()).suggests(OWNERS).executes(context -> list(context, name, StringArgumentType.getString(context, OWNER))));
    }

    private static boolean mayUse(CommandSourceStack source) { return source.hasPermission(ClaimSettings.commandLevel()); }

    private static List<String> owners(MinecraftServer server) {
        List<String> names = new ArrayList<>(Scores.board(server).getTeamNames());
        names.addAll(List.of(server.getPlayerNames()));
        return names;
    }

    private static int claim(CommandContext<CommandSourceStack> context, String name, int radius) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, CLAIM + (radius < 0 ? "" : " " + radius));
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        if (radius > ClaimSettings.commandRadius()) { return tooBig(source, radius); }
        ServerPlayer player = source.getPlayerOrException();
        ChunkPos at = player.chunkPosition();
        return reply(source, radius < 0 ? ClaimActions.claim(player, player.serverLevel(), at.x, at.z, 0) : ClaimActions.claimRadius(player, player.serverLevel(), at.x, at.z, radius));
    }

    private static int claimFor(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String owner = StringArgumentType.getString(context, OWNER);
        CommandShared.ran(source, name, CLAIM + " for " + owner);
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        ServerPlayer player = source.getPlayerOrException();
        if (!ClaimOwners.bypasses(player)) { return CommandShared.refuse(source, CommandShared.tr(NOT_ALLOWED)); }
        int handle = ownerNamed(source.getServer(), owner);
        if (handle == 0) { return CommandShared.refuse(source, CommandShared.tr(NO_OWNER, owner)); }
        ChunkPos at = player.chunkPosition();
        return reply(source, ClaimActions.claimFor(player.serverLevel(), at.x, at.z, handle));
    }

    private static int unclaim(CommandContext<CommandSourceStack> context, String name, int radius) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, UNCLAIM + (radius < 0 ? "" : " " + radius));
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        if (radius > ClaimSettings.commandRadius()) { return tooBig(source, radius); }
        ServerPlayer player = source.getPlayerOrException();
        ChunkPos at = player.chunkPosition();
        return reply(source, radius < 0 ? ClaimActions.unclaim(player, player.serverLevel(), at.x, at.z) : ClaimActions.unclaimRadius(player, player.serverLevel(), at.x, at.z, radius));
    }

    private static int unclaimAll(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, UNCLAIM + " all");
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        return reply(source, ClaimActions.unclaimAll(source.getPlayerOrException()));
    }

    private static int list(CommandContext<CommandSourceStack> context, String name, String owner) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, LIST + (owner == null ? "" : " " + owner));
        int handle;
        if (owner == null) { handle = ClaimOwners.claimantOf(source.getPlayerOrException()); }
        else {
            if (!source.hasPermission(ClaimSettings.opLevel())) { return CommandShared.refuse(source, CommandShared.tr(NOT_ALLOWED)); }
            handle = ownerNamed(source.getServer(), owner);
            if (handle == 0) { return CommandShared.refuse(source, CommandShared.tr(NO_OWNER, owner)); }
        }
        if (handle == 0) { return CommandShared.refuse(source, CommandShared.tr(ClaimActions.NO_CLAIMANT)); }
        for (ClaimActions.Result line : ClaimActions.listFor(source.getServer(), handle)) { reply(source, line); }
        return 1;
    }

    private static int ownerNamed(MinecraftServer server, String name) {
        if (Scores.board(server).getTeamNames().contains(name)) { return OwnerTable.handleForTeam(server, name); }
        Optional<GameProfile> profile = server.getProfileCache() == null ? Optional.empty() : server.getProfileCache().get(name);
        return profile.map(found -> OwnerTable.handleForPlayer(server, found.getId(), found.getName())).orElse(0);
    }

    private static int tooBig(CommandSourceStack source, int radius) { return CommandShared.refuse(source, Component.translatable("argument.integer.big", ClaimSettings.commandRadius(), radius)); }

    private static int reply(CommandSourceStack source, ClaimActions.Result result) {
        if (!result.ok) { return CommandShared.refuse(source, CommandShared.tr(result.key, result.args)); }
        say(source, result);
        return 1;
    }

    public static void say(CommandSourceStack source, ClaimActions.Result result) { CommandShared.send(source, result.ok ? ChatFormatting.GREEN : ChatFormatting.RED, CommandShared.tr(result.key, result.args)); }

    public static void say(ServerPlayer player, ClaimActions.Result result) { say(player.createCommandSourceStack(), result); }
}
