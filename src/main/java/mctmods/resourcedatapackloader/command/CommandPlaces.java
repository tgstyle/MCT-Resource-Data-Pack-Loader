package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Config;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

final class CommandPlaces {
    private static final String NEXT = "next";
    private static final String BACK = "back";
    private CommandPlaces() {}

    static CompletableFuture<Suggestions> suggestPlaces(CommandSourceStack source, SuggestionsBuilder suggestions) {
        String typed = suggestions.getRemaining();
        int space = typed.indexOf(' ');
        if (space >= 0) { return SharedSuggestionProvider.suggest(List.of(NEXT, BACK), suggestions.createOffset(suggestions.getStart() + space + 1)); }
        List<String> known = new ArrayList<>(ContentLocate.names(source.getLevel()));
        known.addAll(ContentStructureSearch.aliases());
        known.removeIf(place -> !source.hasPermission(ContentStructureSearch.levelFor(place, "gotoLevel", Config.commands.gotoLevel())));
        return SharedSuggestionProvider.suggest(known, suggestions);
    }

    private static boolean denied(CommandSourceStack source, String place, String key, int fallback) {
        if (source.hasPermission(ContentStructureSearch.levelFor(place, key, fallback))) { return false; }
        source.sendFailure(CommandShared.tr("rdpl.command.gotodenied", place));
        return true;
    }

    static int goWhere(CommandSourceStack source, String typed) {
        String[] words = typed.trim().split("\\s+");
        if (words.length == 1) { return goTo(source, words[0], "gotoLevel", Config.commands.gotoLevel(), false); }
        if (words.length == 2 && NEXT.equals(words[1])) { return goTo(source, words[0], "gotoNextLevel", Config.commands.gotoNextLevel(), true); }
        if (words.length == 2 && BACK.equals(words[1])) { return goBack(source, words[0]); }
        source.sendFailure(Component.translatable("command.unknown.argument"));
        return 0;
    }

    static int goTo(CommandSourceStack source, String asked, String key, int fallback, boolean next) {
        if (denied(source, asked, key, fallback)) { return 0; }
        if (ContentPregen.busy()) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotomakingland"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotonoplayer"));
            return 0;
        }
        ServerLevel level = source.getLevel();
        String place = ContentStructureSearch.named(asked);
        BlockPos from = BlockPos.containing(source.getPosition());
        BlockPos found = ContentLocate.names(level).contains(place) ? ContentLocate.nearest(level, place, from, next ? 128.0D : 0.0D)
                                                                   : ContentStructureSearch.find(level, player, place, from, next);
        if (found == null) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotonothing", asked));
            return 0;
        }
        return carry(source, player, level, asked, place, found);
    }

    static int goBack(CommandSourceStack source, String asked) {
        if (denied(source, asked, "gotoBackLevel", Config.commands.gotoBackLevel())) { return 0; }
        if (ContentPregen.busy()) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotomakingland"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotonoplayer"));
            return 0;
        }
        String place = ContentStructureSearch.named(asked);
        BlockPos previous = ContentStructureSearch.stepBack(player, place);
        if (previous == null) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotonoback", asked));
            return 0;
        }
        return carry(source, player, source.getLevel(), asked, place, previous);
    }

    private static int carry(CommandSourceStack source, ServerPlayer player, ServerLevel level, String asked, String place, BlockPos found) {
        BlockPos landing = ContentStructureSearch.landing(level, found);
        if (landing == null) {
            source.sendFailure(CommandShared.tr("rdpl.command.gotonoground", asked, found.getX(), found.getZ()));
            return 0;
        }
        ContentStructureSearch.remember(player, place, found);
        player.teleportTo(level, landing.getX() + 0.5D, ContentStructureSearch.stand(level, landing), landing.getZ() + 0.5D, player.getYRot(), player.getXRot());
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.gotodone", asked, landing.getX(), landing.getY(), landing.getZ()));
        return 1;
    }
}
