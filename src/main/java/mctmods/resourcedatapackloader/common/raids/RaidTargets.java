package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.raids.base.Base;
import mctmods.resourcedatapackloader.common.raids.base.BaseFinder;
import mctmods.resourcedatapackloader.common.raids.base.BaseLedger;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;
import mctmods.resourcedatapackloader.common.rules.ContentRaidSwitch;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.WorldServer;

public final class RaidTargets {
    private static final int VILLAGE_SEARCH = 32;

    private RaidTargets() {}

    @Nullable public static RaidTarget resolve(WorldServer world, EntityPlayerMP player, RaidDef def) {
        for (RaidTarget.Kind kind : def.target()) {
            RaidTarget found = resolve(world, player, def, kind);
            if (found != null) { return found; }
        }
        return def.anywhere && ActiveRaid.nearestVillage(world, new BlockPos(player), 0) == null ? free(world, player, def) : null;
    }

    @Nullable public static RaidTarget resolve(WorldServer world, EntityPlayerMP player, RaidDef def, RaidTarget.Kind kind) {
        if (kind == RaidTarget.Kind.VILLAGE) { return village(world, player); }
        if (kind == RaidTarget.Kind.BASE) { return base(world, player, def); }
        if (kind == RaidTarget.Kind.PLAYER) { return player(world, player, def); }
        return null;
    }

    public static boolean launch(WorldServer world, EntityPlayerMP player, RaidDef def, RaidTarget target) {
        if (target.ownerHandle() != 0 && RaidStorage.get(world).ownerRaid(target.ownerHandle()) != null) { return false; }
        ActiveRaid.start(world, def, target);
        if (target.kind() != RaidTarget.Kind.FREE) { ContentRaidSwitch.reset(world, player); }
        return true;
    }

    @Nullable private static RaidTarget village(WorldServer world, EntityPlayerMP player) {
        Village village = ActiveRaid.nearestVillage(world, new BlockPos(player), 0);
        if (village == null || RaidStorage.get(world).near(village.getCenter(), VILLAGE_SEARCH + village.getVillageRadius()) != null) { return null; }
        return new RaidTarget(RaidTarget.Kind.VILLAGE, village.getCenter(), village.getVillageRadius(), 0, null, false, 0);
    }

    @Nullable private static RaidTarget base(WorldServer world, EntityPlayerMP player, RaidDef def) {
        BaseDef rules = def.base();
        if (rules == null) { return null; }
        Base base = BaseFinder.find(world, player, rules);
        if (base == null || refused(world, rules, base.ownerHandle(), base.center(), base.radius())) { return null; }
        return new RaidTarget(RaidTarget.Kind.BASE, base.center(), base.radius(), base.ownerHandle(), player.getUniqueID(), false, (int) Math.round(base.score()));
    }

    @Nullable private static RaidTarget player(WorldServer world, EntityPlayerMP player, RaidDef def) {
        BaseDef rules = def.base();
        if (rules == null) { return null; }
        int owner = RaidOwner.of(player);
        BlockPos at = new BlockPos(player);
        if (refused(world, rules, owner, at, rules.radius)) { return null; }
        return new RaidTarget(RaidTarget.Kind.PLAYER, at, rules.radius, owner, player.getUniqueID(), false, 0);
    }

    @Nullable private static RaidTarget free(WorldServer world, EntityPlayerMP player, RaidDef def) {
        BlockPos at = new BlockPos(player);
        if (RaidStorage.get(world).near(at, def.reach) != null) { return null; }
        return new RaidTarget(RaidTarget.Kind.FREE, at, def.reach, 0, null, true, 0);
    }

    private static boolean refused(WorldServer world, BaseDef rules, int owner, BlockPos center, int radius) {
        if (rules.cooldown > 0 && BaseLedger.get(world).raidedWithin(world.getMinecraftServer(), owner, world.getTotalWorldTime(), rules.cooldown)) { return true; }
        if (rules.onlineOnly && !RaidDefenders.online(world, owner, null)) { return true; }
        return RaidStorage.get(world).near(center, radius + VILLAGE_SEARCH) != null;
    }
}
