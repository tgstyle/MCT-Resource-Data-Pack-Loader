package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentStructureSearch {
    private static final int SEARCH_CHUNKS = 128;
    private static final int HERE_CHUNKS = 8;
    private static final int OPERATOR = 3;
    private static final long BEEN_NEAR = 128L * 128L;
    private static final int LANDING_REACH = 6;
    private static final int HEAD_ROOM = 2;
    private static final int BESIDE_WELL = 4;
    private static final int ROOFED_TOP = 118;
    private static final String TEMPLES = "minecraft:desert_pyramid,minecraft:jungle_pyramid,minecraft:swamp_hut,minecraft:igloo";
    private static final Map<String, Deque<BlockPos>> VISITED = new LinkedHashMap<>();
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Map<String, String> ALIASES = new LinkedHashMap<>();

    static {
        ALIASES.put("village", "#minecraft:village");
        ALIASES.put("villages", "#minecraft:village");
        ALIASES.put("temple", TEMPLES);
        ALIASES.put("temples", TEMPLES);
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

    private static int level(String key, int fallback) { return Mth.clamp(ContentControl.number(ContentControl.COMMANDS, key, fallback), 0, OPERATOR + 1); }

    public static int levelFor(String place, String key, int fallback) {
        for (String entry : ContentControl.list(ContentControl.COMMANDS, "gotoPlaceLevels", Config.commands.gotoPlaceLevels())) {
            int split = entry.lastIndexOf('=');
            if (split <= 0) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("gotoPlaceLevels entry '{}' is not written as name=level, so it is left out", entry); }
                continue;
            }
            if (!entry.substring(0, split).trim().equalsIgnoreCase(place)) { continue; }
            try { return Mth.clamp(Integer.parseInt(entry.substring(split + 1).trim()), 0, OPERATOR + 1); }
            catch (NumberFormatException ignored) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("gotoPlaceLevels entry '{}' has no number after the =, so it is left out", entry); }
            }
        }
        return level(key, fallback);
    }

    public static int lowestLevel() {
        int lowest = Math.min(OPERATOR, level("gotoLevel", Config.commands.gotoLevel()));
        lowest = Math.min(lowest, level("gotoNextLevel", Config.commands.gotoNextLevel()));
        lowest = Math.min(lowest, level("gotoBackLevel", Config.commands.gotoBackLevel()));
        for (String entry : ContentControl.list(ContentControl.COMMANDS, "gotoPlaceLevels", Config.commands.gotoPlaceLevels())) {
            int split = entry.lastIndexOf('=');
            if (split <= 0) { continue; }
            try { lowest = Math.min(lowest, Mth.clamp(Integer.parseInt(entry.substring(split + 1).trim()), 0, OPERATOR + 1)); }
            catch (NumberFormatException ignored) { }
        }
        return lowest;
    }

    @Nullable public static BlockPos find(ServerLevel level, ServerPlayer player, String named, BlockPos from, boolean next) {
        HolderSet<Structure> wanted = wanted(level, named);
        if (wanted == null) { return null; }
        ChunkGeneratorStructureState state = level.getChunkSource().getGeneratorState();
        Map<StructurePlacement, Set<Holder<Structure>>> placements = new LinkedHashMap<>();
        for (Holder<Structure> holder : wanted) {
            for (StructurePlacement placement : state.getPlacementsForStructure(holder)) { placements.computeIfAbsent(placement, unused -> new LinkedHashSet<>()).add(holder); }
        }
        int middleX = SectionPos.blockToSectionCoord(from.getX());
        int middleZ = SectionPos.blockToSectionCoord(from.getZ());
        List<BlockPos> visited = next ? been(player, named) : List.of();
        Predicate<BlockPos> skipped = site -> next ? here(site, middleX, middleZ) || beenNear(visited, site) : explored(level, new ChunkPos(site));
        Predicate<BlockPos> pinSkipped = next ? site -> beenNear(visited, site) || beenNear(List.of(from), site) : skipped;
        for (StructurePlacement placement : placements.keySet()) {
            if (placement instanceof ContentStructureSpread spread && !spread.pins().isEmpty()) { return onPins(placements, from, pinSkipped); }
        }
        for (Holder<Structure> holder : wanted) {
            if (!(holder.value() instanceof ContentCityStructure) || CityDistricts.pinnedWells().isEmpty()) { continue; }
            return besideWells(from, pinSkipped);
        }
        BlockPos best = null;
        for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placements.entrySet()) {
            if (!(entry.getKey() instanceof ConcentricRingsStructurePlacement rings)) { continue; }
            List<ChunkPos> spots = state.getRingPositionsFor(rings);
            if (spots == null) { continue; }
            for (ChunkPos spot : spots) {
                if (best != null && from.distSqr(rings.getLocatePos(spot)) >= from.distSqr(best)) { continue; }
                best = nearer(from, best, standing(level, entry.getValue(), rings, spot, skipped));
            }
        }
        for (int ring = 0; ring <= SEARCH_CHUNKS; ring++) {
            boolean found = false;
            for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placements.entrySet()) {
                if (!(entry.getKey() instanceof RandomSpreadStructurePlacement spread)) { continue; }
                for (int awayX = -ring; awayX <= ring; awayX++) {
                    for (int awayZ = -ring; awayZ <= ring; awayZ++) {
                        if (Math.abs(awayX) != ring && Math.abs(awayZ) != ring) { continue; }
                        ChunkPos spot = spread.getPotentialStructureChunk(state.getLevelSeed(), middleX + spread.spacing() * awayX, middleZ + spread.spacing() * awayZ);
                        BlockPos site = standing(level, entry.getValue(), spread, spot, skipped);
                        if (site == null) { continue; }
                        best = nearer(from, best, site);
                        found = true;
                    }
                }
            }
            if (found) { return best; }
        }
        return best;
    }

    @Nullable private static BlockPos onPins(Map<StructurePlacement, Set<Holder<Structure>>> placements, BlockPos from, Predicate<BlockPos> skipped) {
        BlockPos best = null;
        for (StructurePlacement placement : placements.keySet()) {
            if (!(placement instanceof ContentStructureSpread spread)) { continue; }
            int beside = placements.get(placement).stream().anyMatch(holder -> holder.is(StructureTags.VILLAGE)) ? BESIDE_WELL : 0;
            for (List<Integer> pin : spread.pins()) {
                if (pin.size() != 2) { continue; }
                BlockPos at = new BlockPos(pin.get(0), from.getY(), pin.get(1));
                if (skipped.test(at)) { continue; }
                best = nearer(from, best, at.offset(beside, 0, beside));
            }
        }
        return best;
    }

    @Nullable private static BlockPos besideWells(BlockPos from, Predicate<BlockPos> skipped) {
        BlockPos best = null;
        for (int[] well : CityDistricts.pinnedWells()) {
            if (skipped.test(new BlockPos(well[0], from.getY(), well[1]))) { continue; }
            best = nearer(from, best, new BlockPos(well[0] + BESIDE_WELL, from.getY(), well[1] + BESIDE_WELL));
        }
        return best;
    }

    private static boolean here(BlockPos site, int middleX, int middleZ) {
        return Math.abs(SectionPos.blockToSectionCoord(site.getX()) - middleX) <= HERE_CHUNKS && Math.abs(SectionPos.blockToSectionCoord(site.getZ()) - middleZ) <= HERE_CHUNKS;
    }

    @Nullable private static BlockPos nearer(BlockPos from, @Nullable BlockPos best, @Nullable BlockPos site) {
        if (site == null) { return best; }
        return best == null || from.distSqr(site) < from.distSqr(best) ? site : best;
    }

    @Nullable private static BlockPos standing(ServerLevel level, Set<Holder<Structure>> holders, StructurePlacement placement, ChunkPos spot, Predicate<BlockPos> skipped) {
        StructureManager manager = level.structureManager();
        for (Holder<Structure> holder : holders) {
            StructureCheckResult checked = manager.checkStructurePresence(spot, holder.value(), false);
            if (checked == StructureCheckResult.START_NOT_PRESENT) { continue; }
            BlockPos site = null;
            if (checked == StructureCheckResult.START_PRESENT) { site = placement.getLocatePos(spot); }
            else {
                ChunkAccess chunk = level.getChunk(spot.x, spot.z, ChunkStatus.STRUCTURE_STARTS);
                StructureStart start = manager.getStartForStructure(SectionPos.bottomOf(chunk), holder.value(), chunk);
                if (start != null && start.isValid()) { site = placement.getLocatePos(start.getChunkPos()); }
            }
            if (site != null) { return skipped.test(site) ? null : site; }
        }
        return null;
    }

    private static boolean explored(ServerLevel level, ChunkPos spot) {
        if (level.getChunkSource().getChunkNow(spot.x, spot.z) != null) { return true; }
        return level.getChunkSource().chunkMap.read(spot).join().map(tag -> ChunkSerializer.getChunkTypeFromTag(tag) == ChunkStatus.ChunkType.LEVELCHUNK).orElse(false);
    }

    @Nullable private static HolderSet<Structure> wanted(ServerLevel level, String named) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        if (named.startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(named.substring(1));
            if (id == null) { return null; }
            return registry.getTag(TagKey.create(Registries.STRUCTURE, id)).orElse(null);
        }
        List<Holder<Structure>> held = new ArrayList<>();
        for (String one : named.split(",")) {
            ResourceLocation id = ResourceLocation.tryParse(one.trim());
            if (id == null) { continue; }
            registry.getHolder(ResourceKey.create(Registries.STRUCTURE, id)).ifPresent(held::add);
        }
        return held.isEmpty() ? null : HolderSet.direct(held);
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

    private static List<BlockPos> been(ServerPlayer player, String name) {
        Deque<BlockPos> held = VISITED.get(player.getUUID() + ":" + name);
        return held == null ? List.of() : new ArrayList<>(held);
    }

    private static boolean beenNear(List<BlockPos> been, BlockPos at) {
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
        for (int y = level.dimensionType().hasSkyLight() ? level.getMaxBuildHeight() - 1 : ROOFED_TOP; y > level.getMinBuildHeight(); y--) {
            if (open(level, ground.set(x, y, z))) { continue; }
            boolean room = true;
            for (int head = 1; head <= HEAD_ROOM; head++) { room &= open(level, ground.set(x, y + head, z)); }
            if (room) { return new BlockPos(x, y + 1, z); }
        }
        return null;
    }
}
