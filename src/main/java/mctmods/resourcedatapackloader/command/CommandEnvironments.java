package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.util.Settings;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;

final class CommandEnvironments {
    private CommandEnvironments() {}

    static LiteralArgumentBuilder<CommandSourceStack> environment(String name) {
        return Commands.literal("environment").requires(source -> CommandShared.allowed(source, CommandShared.MODERATOR)).executes(context -> list(context, name))
                .then(Commands.argument("dimension", IdentifierArgument.id()).suggests((_, suggestions) -> SharedSuggestionProvider.suggestResource(ContentEnvironments.dimensions(), suggestions))
                        .then(Commands.argument("name", StringArgumentType.word()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(choices(IdentifierArgument.getId(context, "dimension")), suggestions))
                                .executes(context -> set(context, name, true))
                                .then(Commands.literal("off").executes(context -> set(context, name, false)))));
    }

    private static List<String> choices(Identifier dimension) {
        List<String> names = new ArrayList<>(ContentEnvironments.names(dimension));
        names.add(ContentEnvironments.DEFAULT);
        return names;
    }

    private static int set(CommandContext<CommandSourceStack> context, String name, boolean on) {
        CommandSourceStack source = context.getSource();
        Identifier dimension = IdentifierArgument.getId(context, "dimension");
        String asked = Settings.lower(StringArgumentType.getString(context, "name"));
        CommandShared.ran(source, name, "environment " + dimension + " " + asked + (on ? "" : " off"));
        if (!choices(dimension).contains(asked)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.noenvironment", dimension.toString(), asked)); }
        ContentEnvironments.set(source.getServer(), dimension, asked, on);
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(on ? "rdpl.command.environmentset" : "rdpl.command.environmentoff", dimension.toString(), asked));
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "environment");
        if (ContentEnvironments.dimensions().isEmpty()) { CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.noenvironments")); }
        for (Identifier dimension : ContentEnvironments.dimensions()) {
            CommandShared.send(source, ChatFormatting.WHITE, CommandShared.tr("rdpl.command.environments", dimension.toString(), String.join(" + ", ContentEnvironments.active(dimension)), String.join(", ", ContentEnvironments.names(dimension))));
        }
        return ContentEnvironments.dimensions().size();
    }
}
