package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentStructureSearch {
    private static final int SEARCH_CHUNKS = 128;
    private static final long BEEN_NEAR = 128L * 128L;
    private static final int LANDING_REACH = 6;
    private static final int HEAD_ROOM = 2;
    private static final Map<String, Deque<BlockPos>> VISITED = new LinkedHashMap<>();
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Map<String, String> ALIASES = new LinkedHashMap<>();

    static {
        ALIASES.put("village", "#minecraft:village");
        ALIASES.put("villages", "#minecraft:village");
        ALIASES.put("temple", "minecraft:desert_pyramid");
        ALIASES.put("temples", "minecraft:desert_pyramid");
        ALIASES.put("mansion", "minecraft:mansion");
        ALIASES.put("mansions", "minecraft:mansion");
        ALIASES.put("monument", "minecraft:monument");
        ALIASES.put("monuments", "minecraft:monument");
        ALIASES.put("mineshaft", "#minecraft:mineshaft");
        ALIASES.put("mineshafts", "#minecraft:mineshaft");
        ALIASES.put("stronghold", "minecraft:stronghold");
        ALIASES.put("strongholds", "minecraft:stronghold");
        ALIASES.put("fortress", "minecraft:fortress");
        ALIASES.put("netherbridges", "minecraft:fortress");
        ALIASES.put("endcity", "minecraft:end_city");
        ALIASES.put("endcities", "minecraft:end_city");
    }

    private ContentStructureSearch() {}

    public static void forget() { VISITED.clear(); }

    public static String named(String asked) { return ALIASES.getOrDefault(asked.toLowerCase(Locale.ROOT), asked); }

    public static List<String> aliases() { return List.copyOf(ALIASES.keySet()); }

    public static int levelFor(String place, String key, int fallback) {
        int wanted = ContentControl.number(ContentControl.COMMANDS, key, fallback);
        for (String entry : ContentControl.list(ContentControl.COMMANDS, "gotoPlaceLevels", Config.commands.gotoPlaceLevels())) {
            int split = entry.lastIndexOf('=');
            if (split <= 0) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("gotoPlaceLevels entry '{}' is not written as name=level, so it is left out", entry); }
                continue;
            }
            if (!entry.substring(0, split).trim().equalsIgnoreCase(place)) { continue; }
            try { return Math.max(0, Math.min(4, Integer.parseInt(entry.substring(split + 1).trim()))); }
            catch (NumberFormatException ignored) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("gotoPlaceLevels entry '{}' has no number after the =, so it is left out", entry); }
            }
        }
        return wanted;
    }

    public static int lowestLevel() {
        int lowest = Math.min(ContentControl.number(ContentControl.COMMANDS, "gotoLevel", Config.commands.gotoLevel()),
                Math.min(ContentControl.number(ContentControl.COMMANDS, "gotoNextLevel", Config.commands.gotoNextLevel()),
                        ContentControl.number(ContentControl.COMMANDS, "gotoBackLevel", Config.commands.gotoBackLevel())));
        for (String entry : ContentControl.list(ContentControl.COMMANDS, "gotoPlaceLevels", Config.commands.gotoPlaceLevels())) {
            int split = entry.lastIndexOf('=');
            if (split <= 0) { continue; }
            try { lowest = Math.min(lowest, Integer.parseInt(entry.substring(split + 1).trim())); }
            catch (NumberFormatException ignored) { }
        }
        return Math.max(0, lowest);
    }

    @Nullable public static BlockPos find(ServerLevel level, String named, BlockPos from) {
        HolderSet<Structure> wanted = wanted(level, named);
        if (wanted == null) { return null; }
        com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>> found = level.getChunkSource().getGenerator()
                .findNearestMapStructure(level, wanted, from, SEARCH_CHUNKS, false);
        return found == null ? null : found.getFirst();
    }

    @Nullable private static HolderSet<Structure> wanted(ServerLevel level, String named) {
        if (named.startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(named.substring(1));
            if (id == null) { return null; }
            return level.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(TagKey.create(Registries.STRUCTURE, id)).orElse(null);
        }
        ResourceLocation id = ResourceLocation.tryParse(named);
        if (id == null) { return null; }
        Optional<Holder.Reference<Structure>> held = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolder(ResourceKey.create(Registries.STRUCTURE, id));
        return held.map(HolderSet::direct).orElse(null);
    }

    public static void remember(ServerPlayer player, String name, BlockPos site) {
        Deque<BlockPos> held = VISITED.computeIfAbsent(player.getUUID() + ":" + name, unused -> new ArrayDeque<>());
        BlockPos last = held.peekLast();
        if (last == null || last.distSqr(site) > BEEN_NEAR) { held.addLast(site.immutable()); }
    }

    @Nullable public static BlockPos stepBack(ServerPlayer player, String name) {
        Deque<BlockPos> held = VISITED.get(player.getUUID() + ":" + name);
        if (held == null || held.size() < 2) { return null; }
        held.pollLast();
        return held.peekLast();
    }

    public static List<BlockPos> been(ServerPlayer player, String name) {
        Deque<BlockPos> held = VISITED.get(player.getUUID() + ":" + name);
        return held == null ? List.of() : new ArrayList<>(held);
    }

    public static boolean beenNear(List<BlockPos> been, BlockPos at) {
        for (BlockPos site : been) {
            long awayX = at.getX() - (long) site.getX();
            long awayZ = at.getZ() - (long) site.getZ();
            if (awayX * awayX + awayZ * awayZ < BEEN_NEAR) { return true; }
        }
        return false;
    }

    public static double stand(ServerLevel level, BlockPos landing) {
        BlockPos below = landing.below();
        return below.getY() + level.getBlockState(below).getCollisionShape(level, below).max(net.minecraft.core.Direction.Axis.Y);
    }

    @Nullable public static BlockPos landing(ServerLevel level, BlockPos found) {
        for (int reach = 0; reach <= LANDING_REACH; reach += 2) {
            for (int awayX = -reach; awayX <= reach; awayX += Math.max(1, reach)) {
                for (int awayZ = -reach; awayZ <= reach; awayZ += Math.max(1, reach)) {
                    BlockPos footing = footing(level, found.getX() + awayX, found.getZ() + awayZ);
                    if (footing != null) { return footing; }
                }
            }
        }
        return null;
    }

    private static boolean open(ServerLevel level, BlockPos at) {
        return level.getBlockState(at).getCollisionShape(level, at).isEmpty() && level.getFluidState(at).isEmpty();
    }

    @Nullable private static BlockPos footing(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos ground = new BlockPos.MutableBlockPos();
        for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
            if (open(level, ground.set(x, y, z))) { continue; }
            boolean room = true;
            for (int head = 1; head <= HEAD_ROOM; head++) { room &= open(level, ground.set(x, y + head, z)); }
            if (room) { return new BlockPos(x, y + 1, z); }
        }
        return null;
    }
}
