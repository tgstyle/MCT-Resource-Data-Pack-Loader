package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.raids.ContentRaids;
import mctmods.resourcedatapackloader.common.raids.RaidStorage;
import mctmods.resourcedatapackloader.common.raids.RaidTarget;
import mctmods.resourcedatapackloader.common.raids.RaidTargets;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

final class CommandRaids {
    private CommandRaids() {}

    static LiteralArgumentBuilder<CommandSourceStack> raid(String name) {
        return Commands.literal("raid").requires(source -> source.hasPermission(CommandShared.OPERATOR))
                .then(Commands.argument("raid", ResourceLocationArgument.id()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(ContentRaids.keys(), suggestions))
                        .executes(context -> target(context, name, context.getSource().getPlayerOrException(), null))
                        .then(Commands.argument("player", EntityArgument.player()).executes(context -> target(context, name, EntityArgument.getPlayer(context, "player"), null)))
                        .then(Commands.literal("base")
                                .executes(context -> target(context, name, context.getSource().getPlayerOrException(), RaidTarget.Kind.BASE))
                                .then(Commands.argument("player", EntityArgument.player()).executes(context -> target(context, name, EntityArgument.getPlayer(context, "player"), RaidTarget.Kind.BASE))))
                        .then(Commands.literal("player")
                                .then(Commands.argument("player", EntityArgument.player()).executes(context -> target(context, name, EntityArgument.getPlayer(context, "player"), RaidTarget.Kind.PLAYER))))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> start(context, name, context.getSource().getLevel(), BlockPosArgument.getBlockPos(context, "pos")))));
    }

    private static int target(CommandContext<CommandSourceStack> context, String name, ServerPlayer player, @Nullable RaidTarget.Kind kind) {
        CommandSourceStack source = context.getSource();
        ResourceLocation asked = ResourceLocationArgument.getId(context, "raid");
        CommandShared.ran(source, name, "raid " + asked + (kind == null ? "" : " " + kind.name().toLowerCase(Locale.ROOT)) + " " + player.getGameProfile().getName());
        RaidDef def = ContentRaids.def(asked);
        if (def == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.noraid", asked.toString())); }
        ServerLevel level = player.serverLevel();
        String who = player.getGameProfile().getName();
        if (kind != null && RaidStorage.get(level).ownerRaid(RaidOwner.of(player)) != null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.raidownerbusy", asked.toString(), who)); }
        RaidTarget target = kind == null ? RaidTargets.resolve(level, player, def) : RaidTargets.resolve(level, player, def, kind);
        if (target == null) { return CommandShared.refuse(source, CommandShared.tr(kind == null ? "rdpl.command.raidnotarget" : "rdpl.command.raidnobase", asked.toString(), who)); }
        if (!RaidTargets.launch(level, player, def, target)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.raidownerbusy", asked.toString(), who)); }
        BlockPos at = target.center();
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.raidstarted", asked.toString(), at.getX(), at.getY(), at.getZ()));
        return 1;
    }

    private static int start(CommandContext<CommandSourceStack> context, String name, ServerLevel level, BlockPos at) {
        CommandSourceStack source = context.getSource();
        ResourceLocation asked = ResourceLocationArgument.getId(context, "raid");
        CommandShared.ran(source, name, "raid " + asked);
        RaidDef def = ContentRaids.def(asked);
        if (def == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.noraid", asked.toString())); }
        if (!ContentRaids.begin(level, def, at)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.raidbusy", asked.toString())); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.raidstarted", asked.toString(), at.getX(), at.getY(), at.getZ()));
        return 1;
    }
}
