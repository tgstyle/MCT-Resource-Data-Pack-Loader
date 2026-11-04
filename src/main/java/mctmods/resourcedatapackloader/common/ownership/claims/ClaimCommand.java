package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.util.entity.Scores;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.ChunkPos;

public final class ClaimCommand {
    private static final String OWNER = "owner";
    private static final String RADIUS = "radius";
    private static final String DISABLED = "rdpl.claim.disabled";
    private static final String NO_OWNER = "rdpl.claim.noowner";

    private ClaimCommand() {}

    public static boolean listed() { return ClaimSettings.any() && ClaimSettings.allows(ClaimSettings.Method.COMMAND); }

    public static LiteralArgumentBuilder<CommandSourceStack> claim(String name) {
        return Commands.literal("claim").requires(ClaimCommand::open)
                .executes(context -> claimHere(context, name, 0))
                .then(Commands.argument(RADIUS, IntegerArgumentType.integer(0)).executes(context -> claimHere(context, name, radius(context))))
                .then(Commands.literal("for")
                        .then(Commands.argument(OWNER, StringArgumentType.word()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(owners(context.getSource().getServer()), suggestions))
                                .executes(context -> claimFor(context, name))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> unclaim(String name) {
        return Commands.literal("unclaim").requires(ClaimCommand::open)
                .executes(context -> unclaimHere(context, name, -1))
                .then(Commands.literal("all").executes(context -> unclaimAll(context, name)))
                .then(Commands.argument(RADIUS, IntegerArgumentType.integer(0)).executes(context -> unclaimHere(context, name, radius(context))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> claims(String name) {
        return Commands.literal("claims").requires(ClaimCommand::open)
                .executes(context -> list(context, name, false))
                .then(Commands.argument(OWNER, StringArgumentType.word())
                        .suggests((context, suggestions) -> SharedSuggestionProvider.suggest(owners(context.getSource().getServer()), suggestions))
                        .executes(context -> list(context, name, true)));
    }

    public static void say(CommandSourceStack source, ClaimActions.Result result) { CommandShared.send(source, result.ok() ? ChatFormatting.GREEN : ChatFormatting.RED, result.text()); }

    private static boolean open(CommandSourceStack source) { return CommandShared.allowed(source, ClaimSettings.commandLevel()); }

    private static int radius(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int asked = IntegerArgumentType.getInteger(context, RADIUS);
        int most = ClaimSettings.commandRadius();
        if (asked > most) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().create(asked, most); }
        return asked;
    }

    private static List<String> owners(MinecraftServer server) {
        List<String> names = new ArrayList<>(Scores.board(server).getTeamNames());
        names.addAll(List.of(server.getPlayerNames()));
        return names;
    }

    private static int claimHere(CommandContext<CommandSourceStack> context, String name, int radius) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, radius == 0 ? "claim" : "claim " + radius);
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        ServerPlayer player = source.getPlayerOrException();
        ChunkPos at = player.chunkPosition();
        return reply(source, radius == 0 ? ClaimActions.claim(player, player.level(), at.x(), at.z(), 0) : ClaimActions.claimRadius(player, player.level(), at.x(), at.z(), radius));
    }

    private static int claimFor(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String owner = StringArgumentType.getString(context, OWNER);
        CommandShared.ran(source, name, "claim for " + owner);
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        ServerPlayer player = source.getPlayerOrException();
        if (!ClaimOwners.bypasses(player)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.notallowed")); }
        int handle = ownerNamed(source.getServer(), owner);
        if (handle == 0) { return CommandShared.refuse(source, CommandShared.tr(NO_OWNER, owner)); }
        ChunkPos at = player.chunkPosition();
        return reply(source, ClaimActions.claimFor(player.level(), at.x(), at.z(), handle));
    }

    private static int unclaimHere(CommandContext<CommandSourceStack> context, String name, int radius) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, radius < 0 ? "unclaim" : "unclaim " + radius);
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        ServerPlayer player = source.getPlayerOrException();
        ChunkPos at = player.chunkPosition();
        return reply(source, radius < 0 ? ClaimActions.unclaim(player, player.level(), at.x(), at.z()) : ClaimActions.unclaimRadius(player, player.level(), at.x(), at.z(), radius));
    }

    private static int unclaimAll(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "unclaim all");
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        return reply(source, ClaimActions.unclaimAll(source.getPlayerOrException()));
    }

    private static int list(CommandContext<CommandSourceStack> context, String name, boolean named) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        String owner = named ? StringArgumentType.getString(context, OWNER) : "";
        CommandShared.ran(source, name, named ? "claims " + owner : "claims");
        if (named && !CommandShared.allowed(source, ClaimSettings.opLevel())) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.notallowed")); }
        int handle = named ? ownerNamed(server, owner) : ClaimOwners.claimantOf(source.getPlayerOrException());
        if (handle == 0) { return CommandShared.refuse(source, named ? CommandShared.tr(NO_OWNER, owner) : CommandShared.tr(ClaimActions.NO_CLAIMANT)); }
        for (ClaimActions.Result line : ClaimActions.listFor(server, handle)) { say(source, line); }
        return 1;
    }

    private static int ownerNamed(MinecraftServer server, String name) {
        if (Scores.team(Scores.board(server), name) != null) { return OwnerTable.handleForTeam(server, name); }
        NameAndId profile = server.services().nameToIdCache().get(name).orElse(null);
        return profile == null ? 0 : OwnerTable.handleForPlayer(server, profile.id(), profile.name());
    }

    private static int reply(CommandSourceStack source, ClaimActions.Result result) {
        if (!result.ok()) { return CommandShared.refuse(source, result.text()); }
        say(source, result);
        return 1;
    }
}
