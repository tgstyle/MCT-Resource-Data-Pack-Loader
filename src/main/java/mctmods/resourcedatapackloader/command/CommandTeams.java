package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentRoundReset;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.RoundSay;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.util.Lang;

import com.mojang.brigadier.context.CommandContext;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

final class CommandTeams {
    private CommandTeams() {}

    private static MutableComponent text(CommandSourceStack source, String key, Object... args) { return Component.literal(Lang.tr(source.getPlayer(), key, args)); }

    static int teamList(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "team");
        if (!ContentTeams.any()) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.none"));
            return 0;
        }
        ServerPlayer asking = source.getPlayer();
        for (TeamDef def : ContentTeams.all().values()) {
            String standing = def.joinable() ? "" : Lang.tr(asking, "rdpl.team.closed");
            String lead = ContentTeams.leadOf(source.getLevel(), def);
            if (lead != null) { standing = standing + Lang.tr(asking, "rdpl.team.leadnote", lead); }
            CommandShared.send(source, def.color(), Component.literal(def.name() + " - " + def.displayName() + standing));
        }
        String on = asking == null ? null : ContentTeams.standingOf(asking);
        CommandShared.send(source, ChatFormatting.GRAY, on == null ? text(source, "rdpl.team.onnone") : text(source, "rdpl.team.on", on));
        return 1;
    }

    @Nullable private static TeamDef mine(ServerPlayer player) {
        String standing = ContentTeams.standingOf(player);
        return standing == null ? null : ContentTeams.named(standing);
    }

    @Nullable private static ServerPlayer teamPlayer(CommandSourceStack source, String name, String action) {
        CommandShared.ran(source, name, "team " + action);
        if (!ContentTeams.any()) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.none"));
            return null;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) { CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.playeronly")); }
        return player;
    }

    static int teamVote(CommandContext<CommandSourceStack> context, String name, String choice) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "vote " + choice);
        if (player == null) { return 0; }
        TeamDef mine = mine(player);
        if (mine == null) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.vote.noteam"));
            return 0;
        }
        if (!TeamDef.VOTE.equals(mine.lead())) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.vote.novote", mine.displayName()));
            return 0;
        }
        boolean stood = ContentTeams.standsFor(player.level(), player.getGameProfile().name(), choice, mine);
        CommandShared.send(source, stood ? mine.color() : ChatFormatting.RED, stood ? text(source, "rdpl.team.vote.cast", choice) : text(source, "rdpl.team.vote.notmember", choice));
        if (stood) {
            String lead = ContentTeams.leadOf(player.level(), mine);
            CommandShared.send(source, ChatFormatting.GRAY, lead == null ? text(source, "rdpl.team.vote.tied") : text(source, "rdpl.team.vote.leads", lead, mine.displayName()));
        }
        return stood ? 1 : 0;
    }

    static int teamClaim(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "claim");
        if (player == null) { return 0; }
        TeamDef mine = mine(player);
        if (mine == null) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.claim.noteam"));
            return 0;
        }
        if (!TeamDef.CLAIM.equals(mine.lead())) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.claim.noclaim", mine.displayName()));
            return 0;
        }
        boolean took = ContentTeams.claim(player.level(), player.getGameProfile().name(), mine);
        CommandShared.send(source, took ? mine.color() : ChatFormatting.RED, took ? Component.literal(mine.leadSays().replace("{side}", mine.displayName())) : text(source, "rdpl.team.claim.held", ContentTeams.holding(mine), mine.displayName()));
        return took ? 1 : 0;
    }

    static int roundStart(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "round start");
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal(Lang.tr("rdpl.round.start.playeronly")));
            return 0;
        }
        return say(source, player, ContentScoring.start(source.getServer(), player, player.getGameProfile().name(), CommandShared.allowed(source, CommandShared.OPERATOR)));
    }

    private static int say(CommandSourceStack source, @Nullable ServerPlayer player, RoundSay said) {
        CommandShared.send(source, said.good ? ChatFormatting.GREEN : ChatFormatting.RED, Component.literal(Lang.tr(player, said.key, said.args)));
        return said.good ? 1 : 0;
    }

    static int roundReset(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "round reset");
        ServerPlayer player = source.getPlayer();
        return say(source, player, ContentRoundReset.call(source.getServer(), source.getTextName(), player, CommandShared.allowed(source, CommandShared.OPERATOR)));
    }

    static int roundVote(CommandContext<CommandSourceStack> context, String name, boolean yes) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "round vote " + (yes ? "yes" : "no"));
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            CommandShared.send(source, ChatFormatting.RED, Component.literal(Lang.tr("rdpl.round.vote.playeronly")));
            return 0;
        }
        return say(source, player, ContentRoundReset.vote(source.getServer(), player, yes));
    }

    static int teamLeave(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "leave");
        if (player == null) { return 0; }
        if (ContentScoring.roundRunning()) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.leave.running"));
            return 0;
        }
        CommandShared.send(source, ChatFormatting.GREEN, text(source, ContentTeams.stand(player) ? "rdpl.team.leave.ok" : "rdpl.team.leave.none"));
        return 1;
    }

    static int teamJoin(CommandContext<CommandSourceStack> context, String name, @Nullable String asked) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "join " + (asked == null ? "" : asked));
        if (player == null) { return 0; }
        TeamDef wanted = asked != null ? ContentTeams.named(asked) : ContentTeams.smallest(player.level());
        if (asked == null && wanted == null) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.join.balance"));
            return 0;
        }
        if (wanted == null) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.join.unknown", asked));
            return 0;
        }
        if (!wanted.joinable()) {
            CommandShared.send(source, ChatFormatting.RED, text(source, "rdpl.team.join.closed", wanted.name()));
            return 0;
        }
        if (ContentScoring.roundRunning()) {
            ContentTeams.waitFor(player, wanted);
            CommandShared.send(source, ChatFormatting.GRAY, text(source, "rdpl.team.join.waiting", wanted.displayName()));
            return 1;
        }
        ContentTeams.take(player, wanted);
        CommandShared.send(source, wanted.color(), text(source, "rdpl.team.join.ok", wanted.displayName()));
        return 1;
    }
}
