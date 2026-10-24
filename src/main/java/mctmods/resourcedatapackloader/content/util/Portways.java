package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.block.ContentPortwayBlock;
import mctmods.resourcedatapackloader.content.block.ContentPortwayBlockEntity;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class Portways {
    static final String SECTION = "rdplPortways";
    private static final String STOPS = "stops";

    private Portways() {}

    public static List<PortwayStop> stops(Player player) {
        ListTag tags = PlayerPersisted.read(player, SECTION).getList(STOPS, Tag.TAG_COMPOUND);
        List<PortwayStop> stops = new ArrayList<>();
        for (int index = 0; index < tags.size(); index++) {
            PortwayStop stop = PortwayStop.read(tags.getCompound(index));
            if (stop != null && !stop.block().isEmpty()) { stops.add(stop); }
        }
        return stops;
    }

    private static void save(Player player, List<PortwayStop> stops) {
        ListTag tags = new ListTag();
        for (PortwayStop stop : stops) { tags.add(stop.write()); }
        PlayerPersisted.section(player, SECTION).put(STOPS, tags);
    }

    private static boolean has(List<PortwayStop> stops, PortwayStop wanted) {
        for (PortwayStop stop : stops) {
            if (stop.same(wanted)) { return true; }
        }
        return false;
    }

    public static boolean allowed(PortwayDef def, ResourceKey<Level> dimension) { return def.lists(dimension.location().toString()); }

    private static List<PortwayStop> shown(List<PortwayStop> stops, PortwayDef opening, ResourceKey<Level> origin, Predicate<PortwayDef> scoped) {
        List<PortwayStop> kept = new ArrayList<>();
        for (PortwayStop stop : stops) {
            PortwayDef def = stop.def();
            if (def == null || !scoped.test(def) || !allowed(opening, stop.dimension()) || !def.network().equals(opening.network()) || !opening.crossDimension() && !stop.dimension().equals(origin)) { continue; }
            kept.add(stop);
        }
        return kept;
    }

    public static List<PortwayStop> listed(ServerPlayer player, PortwayDef opening, ResourceKey<Level> origin) {
        List<PortwayStop> everyone = shown(stops(player), opening, origin, def -> true);
        Set<PortwayStop.Key> seen = new HashSet<>();
        for (PortwayStop stop : everyone) { seen.add(stop.key()); }
        for (PortwayStop stop : shown(ContentPortwayGlobal.get(player.server).stops(), opening, origin, PortwayDef::global)) {
            if (seen.add(stop.key())) { everyone.add(stop); }
        }
        for (PortwayStop stop : shown(teamStops(player), opening, origin, PortwayDef::teamWide)) {
            if (seen.add(stop.key())) { everyone.add(stop); }
        }
        return everyone;
    }

    public static List<PortwayStop> teamStops(ServerPlayer player) {
        List<PortwayStop> kept = new ArrayList<>();
        for (PortwayStop stop : ContentPortwayTeam.list(player.server, Standing.teamOf(player))) {
            PortwayDef def = stop.def();
            if (def != null && def.teamWide()) { kept.add(stop); }
        }
        return kept;
    }

    public static List<String> labels(ServerPlayer player, List<PortwayStop> stops) {
        Map<String, Set<ResourceKey<Level>>> seen = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (PortwayStop stop : stops) {
            if (stop.name().isEmpty()) { continue; }
            counts.merge(stop.name(), 1, Integer::sum);
            seen.merge(stop.name(), new HashSet<>(Set.of(stop.dimension())), (held, added) -> { held.addAll(added); return held; });
        }
        List<String> labels = new ArrayList<>();
        for (PortwayStop stop : stops) {
            if (counts.getOrDefault(stop.name(), 0) < 2) {
                labels.add(stop.name());
                continue;
            }
            String where = seen.get(stop.name()).size() > 1 ? dimensionName(stop.dimension()) + " " : "";
            labels.add(Lang.tr(player, "rdpl.portway.twin", stop.name(), where + stop.pos().getX(), stop.pos().getY(), stop.pos().getZ()));
        }
        return labels;
    }

    public static String label(PortwayStop stop) { return stop.name().isEmpty() ? stop.describe() : stop.name(); }

    public static String dimensionName(ResourceKey<Level> dimension) { return dimension.location().getPath(); }

    public static BlockPos base(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof ContentPortwayBlock block && block.upper(state) ? pos.below() : pos;
    }

    @Nullable public static PortwayStop snapshot(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockState(pos).getBlock() instanceof ContentPortwayBlock block)) { return null; }
        String name = level.getBlockEntity(pos) instanceof ContentPortwayBlockEntity held ? held.title() : "";
        return new PortwayStop(level.dimension(), pos, name, String.valueOf(ForgeRegistries.BLOCKS.getKey(block)));
    }

    public static void publish(ServerLevel level, BlockPos pos) {
        PortwayStop stop = snapshot(level, pos);
        PortwayDef def = stop == null ? null : stop.def();
        if (def != null && def.global()) { ContentPortwayGlobal.get(level.getServer()).put(stop); }
    }

    public static void withdraw(ServerLevel level, BlockPos pos) {
        ContentPortwayGlobal.get(level.getServer()).remove(level.dimension(), pos);
        ContentPortwayTeam.remove(level.getServer(), level.dimension(), pos);
    }

    public static boolean unknown(ServerPlayer player, PortwayStop stop) { return !has(stops(player), stop) && !has(ContentPortwayGlobal.get(player.server).stops(), stop) && !has(teamStops(player), stop); }

    public static boolean unknown(ServerPlayer player, String block, String network) {
        List<PortwayStop> every = new ArrayList<>(stops(player));
        every.addAll(ContentPortwayGlobal.get(player.server).stops());
        every.addAll(teamStops(player));
        for (PortwayStop stop : every) {
            PortwayDef def = stop.def();
            if (def != null && (block.isEmpty() || block.equals(stop.block())) && (network.isEmpty() || network.equals(def.network()))) { return false; }
        }
        return true;
    }

    public static boolean gone(MinecraftServer server, PortwayStop stop) {
        if (stop.def() == null) { return true; }
        ServerLevel level = server.getLevel(stop.dimension());
        return level != null && level.isLoaded(stop.pos()) && !(level.getBlockState(stop.pos()).getBlock() instanceof ContentPortwayBlock);
    }

    public static double distance(Player player, PortwayStop stop, ResourceKey<Level> origin) { return stop.dimension().equals(origin) ? Math.sqrt(player.distanceToSqr(stop.pos().getCenter())) : 0.0D; }

    public static boolean learn(Player player, PortwayStop stop) {
        List<PortwayStop> stops = stops(player);
        if (has(stops, stop)) {
            stops.replaceAll(held -> held.same(stop) ? stop.keeping(held) : held);
            save(player, stops);
            return false;
        }
        stops.add(stop.fresh());
        save(player, stops);
        return true;
    }

    public static List<PortwayStop> forget(Player player, Predicate<PortwayStop> which) {
        List<PortwayStop> kept = new ArrayList<>();
        List<PortwayStop> removed = new ArrayList<>();
        for (PortwayStop stop : stops(player)) {
            if (which.test(stop)) { removed.add(stop); }
            else { kept.add(stop); }
        }
        if (!removed.isEmpty()) { save(player, kept); }
        return removed;
    }

    public static void forget(ServerPlayer player, PortwayStop stop) {
        forget(player, stop::same);
        ContentPortwayGlobal.get(player.server).remove(stop.dimension(), stop.pos());
        ContentPortwayTeam.remove(player.server, stop.dimension(), stop.pos());
    }

    public static void refresh(ServerPlayer player, PortwayStop live) {
        List<PortwayStop> stops = stops(player);
        if (has(stops, live)) {
            stops.replaceAll(held -> held.same(live) ? live.keeping(held) : held);
            save(player, stops);
        }
        PortwayDef def = live.def();
        if (def != null && def.global()) { ContentPortwayGlobal.get(player.server).put(live); }
        ContentPortwayTeam.refresh(player.server, live);
    }

    public static void touch(ServerPlayer player, ServerLevel level, BlockPos pos, PortwayDef def) {
        PortwayStop stop = snapshot(level, pos);
        if (stop == null) { return; }
        PortwayFx.card(player, CardRule.PORTWAY_ACTIVATE, stop.block(), def.network(), "");
        String who = player.getGameProfile().getName();
        if (def.global()) {
            ContentPortwayGlobal.get(level.getServer()).put(stop);
            PortwayFx.activated(player, "rdpl.portway.known");
            return;
        }
        String team = def.teamWide() ? Standing.teamOf(player) : "";
        boolean added = team.isEmpty() ? learn(player, stop) : ContentPortwayTeam.learn(level.getServer(), team, stop);
        if (added) {
            PortwayFx.play(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, def.activateSound());
            PortwayFx.particles(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, def.activateParticle());
            int total = team.isEmpty() ? stops(player).size() : ContentPortwayTeam.list(level.getServer(), team).size();
            PortwayFx.activated(player, "rdpl.portway.activated", label(stop), total);
            ContentLog.LOGGER.info("Portway activated by {} at {} ({} known)", who, stop.describe(), total);
        }
        else {
            PortwayFx.activated(player, "rdpl.portway.known");
            ContentLog.LOGGER.info("Portway activated by {} at {} already known", who, stop.describe());
        }
    }
}
