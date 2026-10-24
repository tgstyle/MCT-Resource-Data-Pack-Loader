package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.dialog.ContentDialogs;
import mctmods.resourcedatapackloader.content.dialog.DialogTalk;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import java.util.Collection;

final class CommandTalk {
    private CommandTalk() {}

    static LiteralArgumentBuilder<CommandSourceStack> talk(String name) {
        return Commands.literal("talk").then(Commands.argument("number", IntegerArgumentType.integer(1)).executes(context -> pick(context, name)))
                .then(Commands.literal("open").requires(source -> source.hasPermission(CommandShared.MODERATOR))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("dialog", StringArgumentType.string()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(ContentDialogs.keys(), suggestions))
                                        .then(Commands.argument("npc", EntityArgument.entity()).executes(context -> open(context, name))))));
    }

    private static int open(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String asked = StringArgumentType.getString(context, "dialog");
        CommandShared.ran(source, name, "talk open " + asked);
        ContentDialogs.load();
        DialogDef def = ContentDialogs.find(asked);
        if (def == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.nodialog", asked)); }
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        Entity npc = EntityArgument.getEntity(context, "npc");
        for (ServerPlayer player : players) {
            if (player.level() != npc.level() || npc.distanceTo(player) > DialogTalk.REACH) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.talkfar", player.getGameProfile().getName())); }
        }
        EntityVariantDef variant = ContentEntities.def(npc);
        for (ServerPlayer player : players) { DialogTalk.open(player, npc, variant == null ? "" : variant.name(), def); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.talkopened", def.key(), players.size()));
        return players.size();
    }

    private static int pick(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        int number = IntegerArgumentType.getInteger(context, "number");
        CommandShared.ran(source, name, "talk " + number);
        ServerPlayer player = source.getPlayer();
        if (player == null || DialogTalk.idle(player)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.notalking")); }
        DialogTalk.pick(player, number);
        return 1;
    }
}
