package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.util.PortwayCosts;
import mctmods.resourcedatapackloader.content.util.PortwayFx;
import mctmods.resourcedatapackloader.content.util.PortwayStop;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

final class CommandPortways {
    private static final String PLAYER = "player";
    private static final String POSITION = "pos";
    private static final String DIMENSION = "dimension";

    private CommandPortways() {}

    private interface Action {
        int run(CommandContext<CommandSourceStack> context, ServerPlayer player, ServerLevel level, BlockPos pos) throws CommandSyntaxException;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> player() { return Commands.argument(PLAYER, EntityArgument.player()); }

    private static ArgumentBuilder<CommandSourceStack, ?> placed(Action action) {
        return Commands.argument(POSITION, BlockPosArgument.blockPos())
                .executes(context -> action.run(context, EntityArgument.getPlayer(context, PLAYER), context.getSource().getLevel(), BlockPosArgument.getBlockPos(context, POSITION)))
                .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                        .executes(context -> action.run(context, EntityArgument.getPlayer(context, PLAYER), DimensionArgument.getDimension(context, DIMENSION), BlockPosArgument.getBlockPos(context, POSITION))));
    }

    static LiteralArgumentBuilder<CommandSourceStack> tree(String name) {
        return Commands.literal("portway").requires(source -> source.hasPermission(CommandShared.MODERATOR))
                .then(Commands.literal("list").then(player().executes(context -> list(context, name))))
                .then(Commands.literal("grant").then(player().then(placed((context, player, level, pos) -> grant(context, name, player, level, pos)))))
                .then(Commands.literal("forget").then(player()
                        .then(Commands.literal("all").executes(context -> forget(context, name, EntityArgument.getPlayer(context, PLAYER), "all", Objects::nonNull)))
                        .then(Commands.literal("network").then(Commands.argument("network", StringArgumentType.word()).executes(context -> {
                            String network = StringArgumentType.getString(context, "network");
                            return forget(context, name, EntityArgument.getPlayer(context, PLAYER), "network " + network, stop -> network.equals(network(stop)));
                        })))
                        .then(placed((context, player, level, pos) -> forget(context, name, player, "at " + pos.toShortString(), stop -> stop.at(level.dimension(), pos))))))
                .then(Commands.literal("send").then(player().then(placed((context, player, level, pos) -> send(context, name, player, level, pos)))))
                .then(Commands.literal("cooldown").then(player().then(Commands.argument("seconds", IntegerArgumentType.integer(0)).executes(context -> cooldown(context, name)))));
    }

    @Nullable private static String network(PortwayStop stop) {
        PortwayDef def = stop.def();
        return def == null ? null : def.network();
    }

    @Nullable private static PortwayStop stopAt(ServerLevel level, BlockPos pos) { return Portways.snapshot(level, Portways.base(level, pos)); }

    private static int list(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        CommandShared.ran(source, name, "portway list " + player.getGameProfile().getName());
        List<PortwayStop> stops = Portways.stops(player);
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.portwaylist", player.getGameProfile().getName(), stops.size()));
        for (PortwayStop stop : stops) { CommandShared.send(source, ChatFormatting.WHITE, Component.literal("  " + Portways.label(stop) + "  ").append(Component.literal(stop.describe()).withStyle(ChatFormatting.GRAY))); }
        return stops.size();
    }

    private static int grant(CommandContext<CommandSourceStack> context, String name, ServerPlayer player, ServerLevel level, BlockPos pos) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "portway grant " + player.getGameProfile().getName() + " " + pos.toShortString());
        PortwayStop stop = stopAt(level, pos);
        if (stop == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.portwaymissing", pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + Portways.dimensionName(level.dimension()))); }
        Portways.learn(player, stop);
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.portwaygrant", player.getGameProfile().getName(), stop.describe()));
        return 1;
    }

    private static int forget(CommandContext<CommandSourceStack> context, String name, ServerPlayer player, String what, Predicate<PortwayStop> which) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "portway forget " + player.getGameProfile().getName() + " " + what);
        List<PortwayStop> removed = Portways.forget(player, which);
        for (PortwayStop stop : removed) { PortwayFx.card(player, CardRule.PORTWAY_FORGET, stop.block(), "", "command"); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.portwayforget", player.getGameProfile().getName(), removed.size()));
        return removed.size();
    }

    private static int send(CommandContext<CommandSourceStack> context, String name, ServerPlayer player, ServerLevel level, BlockPos pos) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "portway send " + player.getGameProfile().getName() + " " + pos.toShortString());
        PortwayStop stop = stopAt(level, pos);
        if (stop == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.portwaymissing", pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + Portways.dimensionName(level.dimension()))); }
        if (!PortwayTravel.send(player, stop)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.portwaynone", player.getGameProfile().getName())); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.portwaysend", player.getGameProfile().getName(), stop.describe()));
        return 1;
    }

    private static int cooldown(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        int seconds = IntegerArgumentType.getInteger(context, "seconds");
        CommandShared.ran(source, name, "portway cooldown " + player.getGameProfile().getName() + " " + seconds);
        PortwayCosts.cooldown(player, seconds);
        if (seconds > 0) { CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.portwaycooldown", player.getGameProfile().getName(), seconds)); }
        else { CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.portwaycleared", player.getGameProfile().getName())); }
        return 1;
    }
}
