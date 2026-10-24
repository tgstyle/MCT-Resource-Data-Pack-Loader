package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortway;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.world.Carried;
import mctmods.resourcedatapackloader.util.world.Travel;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class PortwayTravel {
    private PortwayTravel() {}

    public enum Result { ARRIVED, REFUSED, GONE }

    @Nullable private static GateDef gateOf(@Nullable PortwayDef def) { return def == null || def.gate.isEmpty() ? null : ContentGates.find(def.gate); }

    @Nullable public static GateDef lockOf(EntityPlayer player, @Nullable PortwayDef target, int dimension, int originDimension, boolean travelling) {
        GateDef own = gateOf(target);
        if (own != null && (travelling ? !ContentGates.admits(player, own) : ContentGates.bars(player, own))) { return own; }
        if (dimension == originDimension) { return null; }
        for (GateDef def : ContentGates.forDimension(dimension)) {
            if (travelling ? !ContentGates.admits(player, def) : ContentGates.bars(player, def)) { return def; }
        }
        return null;
    }

    public static boolean turnsBack(EntityPlayer player, World world, PortwayDef def, String block) {
        if (def.bars(world.provider.getDimension())) {
            PortwayFx.refused(player, "rdpl.portway.barred");
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, block, def.network, "dimension");
            return true;
        }
        GateDef gate = gateOf(def);
        if (gate != null && !ContentGates.admits(player, gate)) {
            ContentGates.refuse(player, gate);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, block, def.network, "gate");
            return true;
        }
        return false;
    }

    public static void bind(EntityPlayerMP player, World world, BlockPos pos, ItemStack stack, PortwayDef item) {
        PortwayStop stop = Portways.snapshot(world, Portways.base(world, pos));
        PortwayDef def = stop == null ? null : stop.def();
        if (def == null || !def.network.equals(item.network) || !def.global() && Portways.unknown(player, stop)) { return; }
        GateDef gate = lockOf(player, def, stop.dimension, stop.dimension, true);
        if (gate != null) {
            ContentGates.refuse(player, gate);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, stop.block, def.network, "gate");
            return;
        }
        PortwayBinding.write(stack, stop);
        PortwayFx.activated(player, "rdpl.portway.bound", Portways.label(stop));
        PortwayFx.card(player, CardRule.PORTWAY_BIND, stop.block, def.network, "");
        ContentLog.LOGGER.info("Portway bound by {} to {}", player.getName(), Portways.describe(stop));
    }

    @Nullable public static BlockPos landing(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        PortwayDef shape = state.getBlock() instanceof ContentBlockPortway ? ((ContentBlockPortway) state.getBlock()).settings() : PortwayDef.PLAIN;
        EnumFacing facing = shape.facing ? state.getValue(ContentBlockPortway.FACING) : null;
        List<EnumFacing> sides = new ArrayList<>();
        if (facing != null) { sides.add(facing); }
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
            if (side != facing) { sides.add(side); }
        }
        for (EnumFacing side : sides) {
            BlockPos beside = pos.offset(side);
            if (roomAt(world, beside)) { return beside; }
        }
        BlockPos above = pos.up(shape.tall ? 2 : 1);
        if (roomAt(world, above)) { return above; }
        return ContentStructureSearch.landing(world, pos);
    }

    private static boolean roomAt(World world, BlockPos feet) {
        return ContentStructureSearch.open(world, feet) && ContentStructureSearch.open(world, feet.up())
                && world.getBlockState(feet.down()).getMaterial().blocksMovement();
    }

    public static Result travel(EntityPlayerMP player, @Nullable PortwayStop from, PortwayStop target, PortwayDef opening) {
        MinecraftServer server = player.getServer();
        int origin = from == null ? player.dimension : from.dimension;
        if (server == null) { return Result.REFUSED; }
        if (!DimensionManager.isDimensionRegistered(target.dimension)) { return gone(player, target); }
        if (opening.bars(target.dimension) || !opening.crossDimension && target.dimension != origin) { return refused(player, target, opening, "dimension", "rdpl.portway.barred"); }
        WorldServer world = server.getWorld(target.dimension);
        PortwayStop live = Portways.snapshot(world, target.pos);
        PortwayDef held = live == null ? null : live.def();
        if (live == null || held == null) { return gone(player, target); }
        if (!held.network.equals(opening.network)) { return refused(player, target, opening, "network", "rdpl.portway.network"); }
        GateDef gate = lockOf(player, held, target.dimension, origin, true);
        if (gate != null) {
            ContentGates.refuse(player, gate);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, live.block, opening.network, "gate");
            return Result.REFUSED;
        }
        boolean cross = target.dimension != origin;
        double distance = Portways.distance(player, target, origin);
        PortwayCosts.Refusal declined = PortwayCosts.refusal(player, opening.cost, distance, cross);
        if (declined != null) {
            PortwayFx.said(player, declined.text);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, live.block, opening.network, declined.reason);
            return Result.REFUSED;
        }
        BlockPos landing = landing(world, live.pos);
        if (landing == null) { return refused(player, target, opening, "nowhere", "rdpl.portway.nowhere"); }
        ContentLog.LOGGER.info("Portway travel {} {} -> {}", player.getName(), from == null ? "item" : Portways.describe(from), Portways.describe(target));
        PortwayFx.card(player, CardRule.PORTWAY_TRAVEL, live.block, opening.network, "");
        return arrive(player, world, live, held, opening, landing, true, distance) ? Result.ARRIVED : Result.REFUSED;
    }

    public static boolean send(EntityPlayerMP player, PortwayStop target) {
        MinecraftServer server = player.getServer();
        if (server == null || !DimensionManager.isDimensionRegistered(target.dimension)) { return false; }
        WorldServer world = server.getWorld(target.dimension);
        PortwayStop live = Portways.snapshot(world, target.pos);
        PortwayDef held = live == null ? null : live.def();
        BlockPos landing = held == null ? null : landing(world, live.pos);
        return landing != null && arrive(player, world, live, held, held, landing, false, 0.0D);
    }

    private static boolean arrive(EntityPlayerMP player, WorldServer world, PortwayStop live, PortwayDef held, PortwayDef opening, BlockPos landing, boolean paying, double distance) {
        boolean cross = live.dimension != player.dimension;
        Carried carried = Carried.gather(player, opening.carry, cross);
        float yaw = held.facing ? world.getBlockState(live.pos).getValue(ContentBlockPortway.FACING).getHorizontalAngle() : player.rotationYaw;
        Entity ridden = player.getRidingEntity();
        List<Entity> riders = new ArrayList<>(player.getPassengers());
        World fromWorld = player.world;
        double fromX = player.posX;
        double fromY = player.posY;
        double fromZ = player.posZ;
        player.dismountRidingEntity();
        player.removePassengers();
        double x = landing.getX() + 0.5D;
        double y = ContentStructureSearch.stand(world, landing);
        double z = landing.getZ() + 0.5D;
        if (!Travel.to(player, live.dimension, x, y, z, yaw, player.rotationPitch)) {
            if (ridden != null && !ridden.isDead) { player.startRiding(ridden, true); }
            for (Entity rider : riders) {
                if (!rider.isDead) { rider.startRiding(player, true); }
            }
            PortwayFx.refused(player, "rdpl.portway.failed");
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, live.block, opening.network, "travel");
            return false;
        }
        PortwayFx.play(fromWorld, fromX, fromY, fromZ, opening.travelSound);
        PortwayFx.particles(fromWorld, fromX, fromY + 1.0D, fromZ, opening.travelParticle);
        boolean paid = paying && PortwayCosts.charge(player, opening.cost, distance, cross);
        if (paying) { PortwayCosts.stamp(player, opening.cost); }
        PortwayFx.play(world, x, y, z, opening.travelSound);
        PortwayFx.particles(world, x, y + 1.0D, z, opening.travelParticle);
        carried.bring(player, x, y, z, yaw);
        Portways.refresh(player, live);
        PortwayFx.card(player, CardRule.PORTWAY_ARRIVE, live.block, opening.network, "");
        if (paid) { PortwayFx.card(player, CardRule.PORTWAY_PAID, live.block, opening.network, ""); }
        return true;
    }

    private static Result refused(EntityPlayerMP player, PortwayStop target, PortwayDef opening, String reason, String key) {
        PortwayFx.refused(player, key);
        PortwayFx.card(player, CardRule.PORTWAY_REFUSED, target.block, opening.network, reason);
        return Result.REFUSED;
    }

    private static Result gone(EntityPlayerMP player, PortwayStop target) {
        forget(player, target, "gone");
        return Result.GONE;
    }

    public static void forget(EntityPlayerMP player, PortwayStop stop, String reason) {
        if ("player".equals(reason)) {
            Portways.forget(player, stop::equals);
            told(player, stop, reason, "rdpl.portway.forgot", Portways.label(stop));
        }
        else {
            Portways.forget(player, stop);
            told(player, stop, reason, "rdpl.portway.gone");
        }
    }

    private static void told(EntityPlayerMP player, PortwayStop stop, String reason, String key, Object... args) {
        PortwayFx.forgot(player, key, args);
        PortwayFx.card(player, CardRule.PORTWAY_FORGET, stop.block, "", reason);
        ContentLog.LOGGER.info("Portway {} forgotten by {} ({})", Portways.describe(stop), player.getName(), reason);
    }

    public static void prune(EntityPlayerMP player) {
        List<PortwayStop> every = new ArrayList<>(Portways.stops(player));
        Set<PortwayStop> seen = new HashSet<>(every);
        for (PortwayStop stop : PortwayGlobal.list(player.getServer())) {
            if (seen.add(stop)) { every.add(stop); }
        }
        for (PortwayStop stop : PortwayTeam.list(player.getServer(), Standing.teamOf(player))) {
            if (seen.add(stop)) { every.add(stop); }
        }
        List<PortwayStop> gone = new ArrayList<>();
        for (PortwayStop stop : every) {
            if (Portways.gone(stop)) { gone.add(stop); }
        }
        if (gone.isEmpty()) { return; }
        Set<PortwayStop> lost = new HashSet<>(gone);
        Portways.forget(player, lost::contains);
        PortwayGlobal.remove(player.getServer(), lost);
        PortwayTeam.remove(player.getServer(), lost);
        for (PortwayStop stop : gone) { told(player, stop, "gone", "rdpl.portway.gone"); }
    }
}
