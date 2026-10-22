package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

public final class LocatorSearch {
    private LocatorSearch() {}

    @Nullable public static BlockPos find(ServerPlayer player, String asked) {
        ServerLevel level = player.level();
        String place = ContentStructureSearch.named(asked);
        BlockPos from = player.blockPosition();
        if (ContentLocate.names(level).contains(place)) { return ContentLocate.nearest(level, place, from); }
        if (ContentStructureSearch.known(level, place)) {
            player.sendSystemMessage(Component.translatable("rdpl.command.locatelooking", place).withStyle(ChatFormatting.GREEN));
            return ContentStructureSearch.locate(level, place, from);
        }
        ResourceKey<Biome> biome = CommandShared.findBiome(player.createCommandSourceStack(), place.trim());
        return biome == null ? null : ContentBiomeSearch.locate(level, biome, from);
    }
}
