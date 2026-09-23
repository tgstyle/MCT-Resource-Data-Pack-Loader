package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentRoundReset;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.def.TeamDef;

import com.mojang.brigadier.context.CommandContext;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CommandTeams {
    private CommandTeams() {}

    static int teamList(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "team");
        if (!ContentTeams.any()) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("No pack has fielded any team"));
            return 0;
        }
        ServerPlayer asking = source.getPlayer();
        for (TeamDef def : ContentTeams.all().values()) {
            String standing = def.joinable() ? "" : " (closed)";
            String lead = ContentTeams.leadOf(source.getLevel(), def);
            if (lead != null) { standing = standing + " - lead " + lead; }
            CommandShared.send(source, def.color(), Component.literal(def.name() + " - " + def.displayName() + standing));
        }
        String on = asking == null ? null : ContentTeams.standingOf(asking);
        CommandShared.send(source, ChatFormatting.GRAY, Component.literal(on == null ? "You are on no team" : "You are on " + on));
        return 1;
    }

    @Nullable private static TeamDef mine(ServerPlayer player) {
        String standing = ContentTeams.standingOf(player);
        return standing == null ? null : ContentTeams.named(standing);
    }

    @Nullable private static ServerPlayer teamPlayer(CommandSourceStack source, String name, String action) {
        CommandShared.ran(source, name, "team " + action);
        if (!ContentTeams.any()) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("No pack has fielded any team"));
            return null;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) { CommandShared.send(source, ChatFormatting.RED, Component.literal("Only a player can join or leave a team")); }
        return player;
    }

    static int teamVote(CommandContext<CommandSourceStack> context, String name, String choice) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "vote " + choice);
        if (player == null) { return 0; }
        TeamDef mine = mine(player);
        if (mine == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("You are on no team, so there is nobody to vote for"));
            return 0;
        }
        if (!TeamDef.VOTE.equals(mine.lead())) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal(mine.displayName() + " does not choose its lead by a vote"));
            return 0;
        }
        boolean stood = ContentTeams.standsFor(player.serverLevel(), player.getGameProfile().getName(), choice, mine);
        CommandShared.send(source, stood ? mine.color() : ChatFormatting.RED, Component.literal(stood ? "You voted for " + choice : choice + " is not on your team"));
        if (stood) {
            String lead = ContentTeams.leadOf(player.serverLevel(), mine);
            CommandShared.send(source, ChatFormatting.GRAY, Component.literal(lead == null ? "The vote is tied, so nobody leads" : lead + " leads " + mine.displayName()));
        }
        return stood ? 1 : 0;
    }

    static int teamClaim(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "claim");
        if (player == null) { return 0; }
        TeamDef mine = mine(player);
        if (mine == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("You are on no team, so there is nothing to claim"));
            return 0;
        }
        if (!TeamDef.CLAIM.equals(mine.lead())) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal(mine.displayName() + " does not let its lead be claimed"));
            return 0;
        }
        boolean took = ContentTeams.claim(player.serverLevel(), player.getGameProfile().getName(), mine);
        CommandShared.send(source, took ? mine.color() : ChatFormatting.RED, Component.literal(took ? mine.leadSays().replace("{side}", mine.displayName()) : ContentTeams.holding(mine) + " already leads " + mine.displayName()));
        return took ? 1 : 0;
    }

    static int roundStart(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "round start");
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("Only a player starts a round"));
            return 0;
        }
        String said = ContentScoring.start(source.getServer(), player, player.getGameProfile().getName(), source.hasPermission(CommandShared.OPERATOR));
        CommandShared.send(source, "The round starts".equals(said) ? ChatFormatting.GREEN : ChatFormatting.RED, Component.literal(said));
        return 1;
    }

    static int roundReset(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "round reset");
        String said = ContentRoundReset.call(source.getServer(), source.getTextName(), source.getPlayer(), source.hasPermission(CommandShared.OPERATOR));
        CommandShared.send(source, ContentRoundReset.DONE.equals(said) || ContentRoundReset.CALLED.equals(said) ? ChatFormatting.GREEN : ChatFormatting.RED, Component.literal(said));
        return 1;
    }

    static int roundVote(CommandContext<CommandSourceStack> context, String name, boolean yes) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "round vote " + (yes ? "yes" : "no"));
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("Only a player votes"));
            return 0;
        }
        String said = ContentRoundReset.vote(source.getServer(), player, yes);
        CommandShared.send(source, said.startsWith("You voted") ? ChatFormatting.GREEN : ChatFormatting.RED, Component.literal(said));
        return 1;
    }

    static int teamLeave(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "leave");
        if (player == null) { return 0; }
        if (ContentScoring.roundRunning()) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("A round is running, so you cannot leave your team until it ends"));
            return 0;
        }
        CommandShared.send(source, ChatFormatting.GREEN, Component.literal(ContentTeams.stand(player) ? "You left your team" : "You were on no team"));
        return 1;
    }

    static int teamJoin(CommandContext<CommandSourceStack> context, String name, @Nullable String asked) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "join " + (asked == null ? "" : asked));
        if (player == null) { return 0; }
        TeamDef wanted = asked != null ? ContentTeams.named(asked) : ContentTeams.smallest(player.serverLevel());
        if (asked == null && wanted == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("No team takes players by balance, so name the one you want"));
            return 0;
        }
        if (wanted == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal("There is no team called " + asked));
            return 0;
        }
        if (!wanted.joinable()) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal(wanted.name() + " is not a team you can join"));
            return 0;
        }
        if (ContentScoring.roundRunning()) {
            ContentTeams.waitFor(player, wanted);
            CommandShared.send(source, ChatFormatting.GRAY, Component.literal("A round is running, so you join " + wanted.displayName() + " when it ends"));
            return 1;
        }
        ContentTeams.take(player, wanted);
        CommandShared.send(source, wanted.color(), Component.literal("You joined " + wanted.displayName()));
        return 1;
    }
}
