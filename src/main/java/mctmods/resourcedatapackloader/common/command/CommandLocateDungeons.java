package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

final class CommandLocateDungeons {
    private CommandLocateDungeons() {}

    static void apply(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("locate").then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests((context, suggestions) -> SharedSuggestionProvider.suggest(ContentDungeons.locateNames(context.getSource().getLevel()), suggestions))
                .executes(context -> locate(context.getSource(), StringArgumentType.getString(context, "name")))));
    }

    private static int locate(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        if (!ContentDungeons.locateNames(level).contains(name)) { return CommandShared.refuse(source, CommandShared.tr("commands.locate.structure.invalid", name)); }
        BlockPos from = BlockPos.containing(source.getPosition());
        BlockPos found = ContentDungeons.nearestEntrance(level, name, from, pin -> false);
        if (found == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotonothing", name)); }
        int dx = found.getX() - from.getX();
        int dz = found.getZ() - from.getZ();
        int away = Mth.floor(Mth.sqrt(dx * dx + dz * dz));
        Component at = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", found.getX(), "~", found.getZ())).withStyle(style -> style.withColor(ChatFormatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + found.getX() + " ~ " + found.getZ()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip"))));
        source.sendSuccess(() -> Component.translatable("commands.locate.structure.success", name, at, away), false);
        return away;
    }
}
