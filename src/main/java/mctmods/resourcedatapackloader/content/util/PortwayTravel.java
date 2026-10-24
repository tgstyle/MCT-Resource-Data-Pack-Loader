package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.block.ContentPortwayBlock;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Carried;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Travel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class PortwayTravel {
    private PortwayTravel() {}

    public enum Result { ARRIVED, REFUSED, GONE }

    @Nullable private static GateDef gateOf(@Nullable PortwayDef def) { return def == null || def.gate().isEmpty() ? null : ContentGates.find(def.gate()); }

    @Nullable public static GateDef lockOf(ServerPlayer player, @Nullable PortwayDef target, ResourceKey<Level> dimension, ResourceKey<Level> origin, boolean travelling) {
        GateDef own = gateOf(target);
        if (own != null && (travelling ? !ContentGates.admits(player, own) : ContentGates.bars(player, own))) { return own; }
        if (dimension.equals(origin)) { return null; }
        for (GateDef gate : ContentGates.forDimension(dimension.location())) {
            if (travelling ? !ContentGates.admits(player, gate) : ContentGates.bars(player, gate)) { return gate; }
        }
        return null;
    }

    public static boolean admit(ServerPlayer player, ServerLevel level, PortwayDef def, String block) {
        if (!Portways.allowed(def, level.dimension())) {
            PortwayFx.refused(player, "rdpl.portway.barred");
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, block, def.network(), "dimension");
            return false;
        }
        GateDef gate = gateOf(def);
        if (gate != null && !ContentGates.admits(player, gate)) {
            ContentGates.refuse(player, gate);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, block, def.network(), "gate");
            return false;
        }
        return true;
    }

    public static void bind(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack stack, PortwayDef item) {
        PortwayStop stop = Portways.snapshot(level, Portways.base(level, pos));
        PortwayDef def = stop == null ? null : stop.def();
        if (stop == null || def == null || !def.network().equals(item.network()) || !def.global() && Portways.unknown(player, stop)) { return; }
        GateDef gate = lockOf(player, def, stop.dimension(), stop.dimension(), true);
        if (gate != null) {
            ContentGates.refuse(player, gate);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, stop.block(), def.network(), "gate");
            return;
        }
        PortwayBinding.write(stack, stop);
        PortwayFx.activated(player, "rdpl.portway.bound", Portways.label(stop));
        PortwayFx.card(player, CardRule.PORTWAY_BIND, stop.block(), def.network(), "");
        ContentLog.LOGGER.info("Portway bound by {} to {}", player.getGameProfile().getName(), stop.describe());
    }

    @Nullable public static BlockPos landing(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        PortwayDef shape = state.getBlock() instanceof ContentPortwayBlock block ? block.settings() : PortwayDef.PLAIN;
        Direction facing = shape.facing() ? state.getValue(ContentPortwayBlock.FACING) : null;
        List<Direction> sides = new ArrayList<>();
        if (facing != null) { sides.add(facing); }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (side != facing) { sides.add(side); }
        }
        for (Direction side : sides) {
            BlockPos beside = pos.relative(side);
            if (roomAt(level, beside)) { return beside; }
        }
        BlockPos above = pos.above(shape.tall() ? 2 : 1);
        if (roomAt(level, above)) { return above; }
        return ContentStructureSearch.landing(level, pos);
    }

    private static boolean roomAt(ServerLevel level, BlockPos feet) {
        return ContentStructureSearch.open(level, feet) && ContentStructureSearch.open(level, feet.above())
                && !level.getBlockState(feet.below()).getCollisionShape(level, feet.below()).isEmpty();
    }

    public static Result travel(ServerPlayer player, @Nullable PortwayStop from, PortwayStop target, PortwayDef opening) {
        ResourceKey<Level> origin = from == null ? player.level().dimension() : from.dimension();
        ServerLevel level = player.server.getLevel(target.dimension());
        if (level == null) { return gone(player, target); }
        if (!Portways.allowed(opening, target.dimension()) || !opening.crossDimension() && !target.dimension().equals(origin)) { return refused(player, target, opening, "dimension", "rdpl.portway.barred"); }
        PortwayStop live = Portways.snapshot(level, target.pos());
        PortwayDef held = live == null ? null : live.def();
        if (live == null || held == null) { return gone(player, target); }
        if (!held.network().equals(opening.network())) { return refused(player, target, opening, "network", "rdpl.portway.network"); }
        GateDef gate = lockOf(player, held, target.dimension(), origin, true);
        if (gate != null) {
            ContentGates.refuse(player, gate);
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, live.block(), opening.network(), "gate");
            return Result.REFUSED;
        }
        boolean cross = !target.dimension().equals(origin);
        double distance = Portways.distance(player, target, origin);
        PortwayCosts.Refusal declined = PortwayCosts.refusal(player, opening.cost(), distance, cross);
        if (declined != null) {
            PortwayFx.said(player, declined.text());
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, live.block(), opening.network(), declined.reason());
            return Result.REFUSED;
        }
        BlockPos landing = landing(level, live.pos());
        if (landing == null) { return refused(player, target, opening, "nowhere", "rdpl.portway.nowhere"); }
        ContentLog.LOGGER.info("Portway travel {} {} -> {}", player.getGameProfile().getName(), from == null ? "item" : from.describe(), target.describe());
        PortwayFx.card(player, CardRule.PORTWAY_TRAVEL, live.block(), opening.network(), "");
        return arrive(player, level, live, held, opening, landing, true, distance) ? Result.ARRIVED : Result.REFUSED;
    }

    public static boolean send(ServerPlayer player, PortwayStop target) {
        ServerLevel level = player.server.getLevel(target.dimension());
        if (level == null) { return false; }
        PortwayStop live = Portways.snapshot(level, target.pos());
        PortwayDef held = live == null ? null : live.def();
        if (live == null || held == null) { return false; }
        BlockPos landing = landing(level, live.pos());
        return landing != null && arrive(player, level, live, held, held, landing, false, 0.0D);
    }

    private static boolean arrive(ServerPlayer player, ServerLevel level, PortwayStop live, PortwayDef held, PortwayDef opening, BlockPos landing, boolean paying, double distance) {
        ServerLevel origin = player.serverLevel();
        boolean cross = origin != level;
        Carried carried = Carried.gather(player, opening.carry(), cross);
        float yaw = held.facing() ? level.getBlockState(live.pos()).getValue(ContentPortwayBlock.FACING).toYRot() : player.getYRot();
        Entity ridden = player.getVehicle();
        List<Entity> riders = new ArrayList<>(player.getPassengers());
        double fromX = player.getX();
        double fromY = player.getY();
        double fromZ = player.getZ();
        player.stopRiding();
        player.ejectPassengers();
        double x = landing.getX() + 0.5D;
        double y = ContentStructureSearch.stand(level, landing);
        double z = landing.getZ() + 0.5D;
        if (!Travel.to(player, level, x, y, z, yaw, player.getXRot())) {
            if (ridden != null && ridden.isAlive()) { player.startRiding(ridden, true); }
            for (Entity rider : riders) {
                if (rider.isAlive()) { rider.startRiding(player, true); }
            }
            PortwayFx.refused(player, "rdpl.portway.failed");
            PortwayFx.card(player, CardRule.PORTWAY_REFUSED, live.block(), opening.network(), "travel");
            return false;
        }
        PortwayFx.play(origin, fromX, fromY, fromZ, opening.travelSound());
        PortwayFx.particles(origin, fromX, fromY + 1.0D, fromZ, opening.travelParticle());
        boolean paid = paying && PortwayCosts.pay(player, opening.cost(), distance, cross);
        if (paying) { PortwayCosts.stamp(player, opening.cost()); }
        PortwayFx.play(level, x, y, z, opening.travelSound());
        PortwayFx.particles(level, x, y + 1.0D, z, opening.travelParticle());
        carried.bring(player, level, x, y, z, yaw);
        Portways.refresh(player, live);
        PortwayFx.card(player, CardRule.PORTWAY_ARRIVE, live.block(), opening.network(), "");
        if (paid) { PortwayFx.card(player, CardRule.PORTWAY_PAID, live.block(), opening.network(), ""); }
        return true;
    }

    private static Result refused(ServerPlayer player, PortwayStop target, PortwayDef opening, String reason, String key) {
        PortwayFx.refused(player, key);
        PortwayFx.card(player, CardRule.PORTWAY_REFUSED, target.block(), opening.network(), reason);
        return Result.REFUSED;
    }

    private static Result gone(ServerPlayer player, PortwayStop target) {
        forget(player, target, "gone");
        return Result.GONE;
    }

    public static void forget(ServerPlayer player, PortwayStop stop, String reason) {
        if ("player".equals(reason)) {
            Portways.forget(player, stop::same);
            told(player, stop, reason, "rdpl.portway.forgot", Portways.label(stop));
        }
        else {
            Portways.forget(player, stop);
            told(player, stop, reason, "rdpl.portway.gone");
        }
    }

    private static void told(ServerPlayer player, PortwayStop stop, String reason, String key, Object... args) {
        PortwayFx.forgot(player, key, args);
        PortwayFx.card(player, CardRule.PORTWAY_FORGET, stop.block(), "", reason);
        ContentLog.LOGGER.info("Portway {} forgotten by {} ({})", stop.describe(), player.getGameProfile().getName(), reason);
    }

    public static void prune(ServerPlayer player) {
        List<PortwayStop> every = new ArrayList<>(Portways.stops(player));
        Set<PortwayStop.Key> seen = new HashSet<>();
        for (PortwayStop stop : every) { seen.add(stop.key()); }
        for (PortwayStop stop : ContentPortwayGlobal.get(player.server).stops()) {
            if (seen.add(stop.key())) { every.add(stop); }
        }
        for (PortwayStop stop : ContentPortwayTeam.list(player.server, Standing.teamOf(player))) {
            if (seen.add(stop.key())) { every.add(stop); }
        }
        List<PortwayStop> gone = new ArrayList<>();
        for (PortwayStop stop : every) {
            if (Portways.gone(player.server, stop)) { gone.add(stop); }
        }
        if (gone.isEmpty()) { return; }
        Set<PortwayStop.Key> lost = new HashSet<>();
        for (PortwayStop stop : gone) { lost.add(stop.key()); }
        Portways.forget(player, stop -> lost.contains(stop.key()));
        ContentPortwayGlobal.get(player.server).remove(lost);
        ContentPortwayTeam.remove(player.server, lost);
        for (PortwayStop stop : gone) { told(player, stop, "gone", "rdpl.portway.gone"); }
    }
}
