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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ClaimCommand {
    private static final String DISABLED = "rdpl.claim.disabled";
    private static final String NO_OWNER = "rdpl.claim.noowner";
    private static final String RADIUS = "radius";
    private static final String OWNER = "owner";

    private ClaimCommand() {}

    public static boolean listed() { return ClaimSettings.any() && ClaimSettings.allows(ClaimSettings.Method.COMMAND); }

    private static boolean allowed(CommandSourceStack source) { return source.hasPermission(ClaimSettings.commandLevel()); }

    public static LiteralArgumentBuilder<CommandSourceStack> claim(String name) {
        return Commands.literal("claim").requires(ClaimCommand::allowed).executes(context -> claim(context, name, 0))
                .then(Commands.argument(RADIUS, IntegerArgumentType.integer(0)).executes(context -> claim(context, name, IntegerArgumentType.getInteger(context, RADIUS))))
                .then(Commands.literal("for")
                        .then(Commands.argument(OWNER, StringArgumentType.word()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(owners(context.getSource().getServer()), suggestions))
                                .executes(context -> claimFor(context, name))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> unclaim(String name) {
        return Commands.literal("unclaim").requires(ClaimCommand::allowed).executes(context -> unclaim(context, name, -1))
                .then(Commands.literal("all").executes(context -> unclaimAll(context, name)))
                .then(Commands.argument(RADIUS, IntegerArgumentType.integer(0)).executes(context -> unclaim(context, name, IntegerArgumentType.getInteger(context, RADIUS))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> claims(String name) {
        return Commands.literal("claims").requires(ClaimCommand::allowed).executes(context -> list(context, name, null))
                .then(Commands.argument(OWNER, StringArgumentType.word())
                        .suggests((context, suggestions) -> SharedSuggestionProvider.suggest(owners(context.getSource().getServer()), suggestions))
                        .executes(context -> list(context, name, StringArgumentType.getString(context, OWNER))));
    }

    public static void say(CommandSourceStack source, ClaimActions.Result result) { CommandShared.send(source, result.ok() ? ChatFormatting.GREEN : ChatFormatting.RED, CommandShared.tr(result.key(), result.args())); }

    private static List<String> owners(MinecraftServer server) {
        List<String> names = new ArrayList<>(Scores.board(server).getTeamNames());
        names.addAll(List.of(server.getPlayerNames()));
        return names;
    }

    private static int claim(CommandContext<CommandSourceStack> context, String name, int radius) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "claim " + radius);
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        if (tooWide(source, radius)) { return 0; }
        ServerPlayer player = source.getPlayerOrException();
        return reply(source, ClaimActions.claimRadius(player, player.serverLevel(), player.blockPosition().getX() >> 4, player.blockPosition().getZ() >> 4, radius));
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
        ServerLevel level = player.serverLevel();
        return reply(source, ClaimActions.claimFor(level, player.blockPosition().getX() >> 4, player.blockPosition().getZ() >> 4, handle));
    }

    private static int unclaim(CommandContext<CommandSourceStack> context, String name, int radius) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "unclaim " + radius);
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        if (tooWide(source, radius)) { return 0; }
        ServerPlayer player = source.getPlayerOrException();
        int chunkX = player.blockPosition().getX() >> 4;
        int chunkZ = player.blockPosition().getZ() >> 4;
        return reply(source, radius < 0 ? ClaimActions.unclaim(player, player.serverLevel(), chunkX, chunkZ) : ClaimActions.unclaimRadius(player, player.serverLevel(), chunkX, chunkZ, radius));
    }

    private static int unclaimAll(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "unclaim all");
        if (!listed()) { return CommandShared.refuse(source, CommandShared.tr(DISABLED)); }
        return reply(source, ClaimActions.unclaimAll(source.getPlayerOrException()));
    }

    private static int list(CommandContext<CommandSourceStack> context, String name, String owner) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "claims " + (owner == null ? "" : owner));
        if (owner != null && !source.hasPermission(ClaimSettings.opLevel())) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.notallowed")); }
        int handle = owner == null ? ClaimOwners.claimantOf(source.getPlayerOrException()) : ownerNamed(source.getServer(), owner);
        if (handle == 0) { return CommandShared.refuse(source, owner == null ? CommandShared.tr(ClaimActions.NO_CLAIMANT) : CommandShared.tr(NO_OWNER, owner)); }
        for (ClaimActions.Result line : ClaimActions.listFor(source.getServer(), handle)) { say(source, line); }
        return 1;
    }

    private static boolean tooWide(CommandSourceStack source, int radius) {
        if (radius <= ClaimSettings.commandRadius()) { return false; }
        CommandShared.refuse(source, CommandShared.tr("argument.integer.big", ClaimSettings.commandRadius(), radius));
        return true;
    }

    private static int ownerNamed(MinecraftServer server, String name) {
        if (Scores.board(server).getTeamNames().contains(name)) { return OwnerTable.handleForTeam(server, name); }
        return server.getProfileCache() == null ? 0 : server.getProfileCache().get(name).map(profile -> OwnerTable.handleForPlayer(server, profile.getId(), profile.getName())).orElse(0);
    }

    private static int reply(CommandSourceStack source, ClaimActions.Result result) {
        if (!result.ok()) { return CommandShared.refuse(source, CommandShared.tr(result.key(), result.args())); }
        say(source, result);
        return 1;
    }
}
