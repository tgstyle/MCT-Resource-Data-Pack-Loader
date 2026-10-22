package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Config;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

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
        for (Identifier biome : source.registryAccess().lookupOrThrow(Registries.BIOME).keySet()) { known.add(biome.toString()); }
        known.removeIf(place -> !mayGo(source, ContentStructureSearch.levelFor(place, "gotoLevel", Config.commands.gotoLevel())));
        return SharedSuggestionProvider.suggest(known, suggestions);
    }

    static boolean mayGo(CommandSourceStack source, int level) { return CommandShared.allowed(source, level) || !(source.source instanceof ServerPlayer) && CommandShared.allowed(source, CommandShared.MODERATOR); }

    @Nullable private static ServerPlayer guard(CommandSourceStack source, String place, String key, int fallback) {
        if (!mayGo(source, ContentStructureSearch.levelFor(place, key, fallback))) {
            CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotodenied", place));
            return null;
        }
        if (ContentPregen.busy()) {
            CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotomakingland"));
            return null;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) { CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotonoplayer")); }
        return player;
    }

    static int goWhere(CommandSourceStack source, String typed) {
        String line = typed.trim().replaceAll("\\s+", " ");
        int cut = line.lastIndexOf(' ');
        String last = cut < 0 ? "" : line.substring(cut + 1);
        if (NEXT.equals(last)) { return goTo(source, line.substring(0, cut), "gotoNextLevel", Config.commands.gotoNextLevel(), true); }
        if (BACK.equals(last)) { return goBack(source, line.substring(0, cut)); }
        return goTo(source, line, "gotoLevel", Config.commands.gotoLevel(), false);
    }

    static int goTo(CommandSourceStack source, String asked, String key, int fallback, boolean next) {
        ServerPlayer player = guard(source, asked, key, fallback);
        if (player == null) { return 0; }
        ServerLevel level = source.getLevel();
        String place = ContentStructureSearch.named(asked);
        BlockPos from = BlockPos.containing(source.getPosition());
        ResourceKey<Biome> biome = biomeOf(source, level, place);
        BlockPos found;
        if (biome != null) {
            place = biome.identifier().toString();
            CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.gotolooking", place));
            found = ContentBiomeSearch.find(level, player, biome, place, from, next);
        }
        else if (ContentLocate.names(level).contains(place)) { found = ContentLocate.nearest(level, place, from, next ? 128.0D : 0.0D); }
        else {
            CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.gotolooking", place));
            found = ContentStructureSearch.find(level, player, place, from, next);
        }
        if (found == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotonothing", asked)); }
        return carry(source, player, level, asked, place, found);
    }

    static int goBack(CommandSourceStack source, String asked) {
        ServerPlayer player = guard(source, asked, "gotoBackLevel", Config.commands.gotoBackLevel());
        if (player == null) { return 0; }
        String place = ContentStructureSearch.named(asked);
        ResourceKey<Biome> biome = biomeOf(source, source.getLevel(), place);
        if (biome != null) { place = biome.identifier().toString(); }
        BlockPos previous = ContentStructureSearch.stepBack(player, place);
        if (previous == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotonoback", asked)); }
        return carry(source, player, source.getLevel(), asked, place, previous);
    }

    @Nullable private static ResourceKey<Biome> biomeOf(CommandSourceStack source, ServerLevel level, String place) {
        if (ContentLocate.names(level).contains(place) || ContentStructureSearch.known(level, place)) { return null; }
        return CommandShared.findBiome(source, place.trim());
    }

    private static int carry(CommandSourceStack source, ServerPlayer player, ServerLevel level, String asked, String place, BlockPos found) {
        BlockPos landing = ContentStructureSearch.landing(level, found);
        if (landing == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.gotonoground", asked, found.getX(), found.getZ())); }
        ContentStructureSearch.remember(player, place, found);
        player.teleportTo(level, landing.getX() + 0.5D, ContentStructureSearch.stand(level, landing), landing.getZ() + 0.5D, Set.of(), player.getYRot(), player.getXRot(), true);
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.gotodone", asked, landing.getX(), landing.getY(), landing.getZ()));
        return 1;
    }
}
