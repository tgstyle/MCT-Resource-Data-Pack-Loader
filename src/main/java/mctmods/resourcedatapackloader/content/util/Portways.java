package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortway;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.tile.TilePortway;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
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

    public static List<PortwayStop> stops(EntityPlayer player) {
        NBTTagList tags = PlayerPersisted.read(player, SECTION).getTagList(STOPS, Constants.NBT.TAG_COMPOUND);
        List<PortwayStop> stops = new ArrayList<>();
        for (int index = 0; index < tags.tagCount(); index++) {
            PortwayStop stop = PortwayStop.read(tags.getCompoundTagAt(index));
            if (!stop.block.isEmpty()) { stops.add(stop); }
        }
        return stops;
    }

    private static void save(EntityPlayer player, List<PortwayStop> stops) {
        NBTTagList tags = new NBTTagList();
        for (PortwayStop stop : stops) { tags.appendTag(stop.write()); }
        PlayerPersisted.section(player, SECTION).setTag(STOPS, tags);
    }

    private static List<PortwayStop> shown(List<PortwayStop> stops, PortwayDef opening, int originDimension, Predicate<PortwayDef> scoped) {
        List<PortwayStop> kept = new ArrayList<>();
        for (PortwayStop stop : stops) {
            PortwayDef def = stop.def();
            if (def == null || !scoped.test(def) || opening.bars(stop.dimension) || !def.network.equals(opening.network) || !opening.crossDimension && stop.dimension != originDimension) { continue; }
            kept.add(stop);
        }
        return kept;
    }

    public static List<PortwayStop> listed(EntityPlayer player, PortwayDef opening, int originDimension) {
        List<PortwayStop> everyone = shown(stops(player), opening, originDimension, def -> true);
        Set<PortwayStop> seen = new HashSet<>(everyone);
        for (PortwayStop stop : shown(PortwayGlobal.list(player.getServer()), opening, originDimension, PortwayDef::global)) {
            if (seen.add(stop)) { everyone.add(stop); }
        }
        for (PortwayStop stop : shown(teamStops(player), opening, originDimension, PortwayDef::teamWide)) {
            if (seen.add(stop)) { everyone.add(stop); }
        }
        return everyone;
    }

    public static List<PortwayStop> teamStops(EntityPlayer player) {
        List<PortwayStop> kept = new ArrayList<>();
        for (PortwayStop stop : PortwayTeam.list(player.getServer(), Standing.teamOf(player))) {
            PortwayDef def = stop.def();
            if (def != null && def.teamWide()) { kept.add(stop); }
        }
        return kept;
    }

    public static List<String> labels(EntityPlayer player, List<PortwayStop> stops) {
        Map<String, Set<Integer>> seen = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (PortwayStop stop : stops) {
            if (stop.name.isEmpty()) { continue; }
            counts.merge(stop.name, 1, Integer::sum);
            seen.computeIfAbsent(stop.name, name -> new HashSet<>()).add(stop.dimension);
        }
        List<String> labels = new ArrayList<>();
        for (PortwayStop stop : stops) {
            if (counts.getOrDefault(stop.name, 0) < 2) {
                labels.add(stop.name);
                continue;
            }
            String where = seen.get(stop.name).size() > 1 ? dimensionName(stop.dimension) + " " : "";
            labels.add(Lang.tr(player, "rdpl.portway.twin", stop.name, where + stop.pos.getX(), stop.pos.getY(), stop.pos.getZ()));
        }
        return labels;
    }

    public static String dimensionName(int dimension) { return DimensionManager.isDimensionRegistered(dimension) ? DimensionManager.getProviderType(dimension).getName() : String.valueOf(dimension); }

    public static String describe(PortwayStop stop) { return dimensionName(stop.dimension) + " " + stop.pos.getX() + " " + stop.pos.getY() + " " + stop.pos.getZ(); }

    public static String counted(String key, int amount) { return amount == 1 ? key + ".one" : key; }

    public static String label(PortwayStop stop) { return stop.name.isEmpty() ? describe(stop) : stop.name; }

    public static BlockPos base(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof ContentBlockPortway && ((ContentBlockPortway) state.getBlock()).upper(state) ? pos.down() : pos;
    }

    @Nullable public static PortwayStop snapshot(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof ContentBlockPortway) || block.getRegistryName() == null) { return null; }
        TileEntity tile = world.getTileEntity(pos);
        String name = tile instanceof TilePortway ? ((TilePortway) tile).label(((ContentBlockPortway) block).settings().names) : "";
        return new PortwayStop(world.provider.getDimension(), pos, name, block.getRegistryName().toString());
    }

    public static void register(World world, BlockPos pos) {
        PortwayStop stop = snapshot(world, pos);
        PortwayDef def = stop == null ? null : stop.def();
        if (def != null && def.global()) { PortwayGlobal.upsert(world.getMinecraftServer(), stop); }
    }

    public static boolean unknown(EntityPlayer player, PortwayStop stop) { return !stops(player).contains(stop) && !PortwayGlobal.list(player.getServer()).contains(stop) && !teamStops(player).contains(stop); }

    public static boolean unknown(EntityPlayer player, String block, String network) {
        List<PortwayStop> every = new ArrayList<>(stops(player));
        every.addAll(PortwayGlobal.list(player.getServer()));
        every.addAll(teamStops(player));
        for (PortwayStop stop : every) {
            PortwayDef def = stop.def();
            if (def != null && (block.isEmpty() || block.equals(stop.block)) && (network.isEmpty() || network.equals(def.network))) { return false; }
        }
        return true;
    }

    public static boolean gone(PortwayStop stop) {
        if (stop.def() == null) { return true; }
        World world = DimensionManager.getWorld(stop.dimension);
        return world != null && world.isBlockLoaded(stop.pos) && !(world.getBlockState(stop.pos).getBlock() instanceof ContentBlockPortway);
    }

    public static double distance(EntityPlayer player, PortwayStop stop, int originDimension) { return stop.dimension == originDimension ? Math.sqrt(stop.pos.distanceSq(player.posX, player.posY, player.posZ)) : 0.0D; }

    public static boolean learn(EntityPlayer player, PortwayStop stop) {
        List<PortwayStop> stops = stops(player);
        int at = stops.indexOf(stop);
        if (at >= 0) {
            stops.set(at, stop.keeping(stops.get(at)));
            save(player, stops);
            return false;
        }
        stops.add(stop.fresh());
        save(player, stops);
        return true;
    }

    public static List<PortwayStop> forget(EntityPlayer player, Predicate<PortwayStop> which) {
        List<PortwayStop> kept = new ArrayList<>();
        List<PortwayStop> removed = new ArrayList<>();
        for (PortwayStop stop : stops(player)) {
            if (which.test(stop)) { removed.add(stop); }
            else { kept.add(stop); }
        }
        if (!removed.isEmpty()) { save(player, kept); }
        return removed;
    }

    public static void forget(EntityPlayer player, PortwayStop stop) {
        forget(player, stop::equals);
        PortwayGlobal.remove(player.getServer(), stop.dimension, stop.pos);
        PortwayTeam.remove(player.getServer(), stop.dimension, stop.pos);
    }

    public static void refresh(EntityPlayerMP player, PortwayStop live) {
        List<PortwayStop> stops = stops(player);
        int at = stops.indexOf(live);
        if (at >= 0) {
            stops.set(at, live.keeping(stops.get(at)));
            save(player, stops);
        }
        PortwayDef def = live.def();
        if (def != null && def.global()) { PortwayGlobal.upsert(player.getServer(), live); }
        PortwayTeam.refresh(player.getServer(), live);
    }

    public static void touch(EntityPlayer player, World world, BlockPos pos, PortwayDef def) {
        PortwayStop stop = snapshot(world, pos);
        if (stop == null) { return; }
        PortwayFx.card(player, CardRule.PORTWAY_ACTIVATE, stop.block, def.network, "");
        if (def.global()) {
            PortwayGlobal.upsert(world.getMinecraftServer(), stop);
            PortwayFx.activated(player, "rdpl.portway.known");
            return;
        }
        String team = def.teamWide() ? Standing.teamOf(player) : "";
        boolean added = team.isEmpty() ? learn(player, stop) : PortwayTeam.learn(world.getMinecraftServer(), team, stop);
        if (added) {
            PortwayFx.play(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, def.activateSound);
            PortwayFx.particles(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, def.activateParticle);
            int total = team.isEmpty() ? stops(player).size() : PortwayTeam.list(world.getMinecraftServer(), team).size();
            PortwayFx.activated(player, "rdpl.portway.activated", label(stop), total);
            ContentLog.LOGGER.info("Portway activated by {} at {} ({} known)", player.getName(), describe(stop), total);
        }
        else {
            PortwayFx.activated(player, "rdpl.portway.known");
            ContentLog.LOGGER.info("Portway activated by {} at {} already known", player.getName(), describe(stop));
        }
    }
}
