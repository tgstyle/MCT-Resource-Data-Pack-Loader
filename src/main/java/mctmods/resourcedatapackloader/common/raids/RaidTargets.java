package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.raids.base.Base;
import mctmods.resourcedatapackloader.common.raids.base.BaseFinder;
import mctmods.resourcedatapackloader.common.raids.base.BaseLedger;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;
import mctmods.resourcedatapackloader.common.rules.ContentRaidSwitch;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiManager;

public final class RaidTargets {
    private static final int VILLAGE_SEARCH = 32;
    private static final int DOOR_REACH = 6;

    private RaidTargets() {}

    @Nullable public static RaidTarget resolve(ServerLevel level, ServerPlayer player, RaidDef def) {
        for (RaidTarget.Kind kind : def.target()) {
            RaidTarget found = resolve(level, player, def, kind);
            if (found != null) { return found; }
        }
        return def.anywhere() && lived(level, player.blockPosition()) == null ? free(level, player, def) : null;
    }

    @Nullable public static RaidTarget resolve(ServerLevel level, ServerPlayer player, RaidDef def, RaidTarget.Kind kind) {
        if (kind == RaidTarget.Kind.VILLAGE) { return village(level, player); }
        if (kind == RaidTarget.Kind.BASE) { return base(level, player, def); }
        if (kind == RaidTarget.Kind.PLAYER) { return player(level, player, def); }
        return null;
    }

    public static boolean launch(ServerLevel level, ServerPlayer player, RaidDef def, RaidTarget target) {
        if (target.ownerHandle() != 0 && RaidStorage.get(level).ownerRaid(target.ownerHandle()) != null) { return false; }
        ActiveRaid.start(level, def, target);
        if (target.kind() != RaidTarget.Kind.FREE) { ContentRaidSwitch.reset(level, player); }
        return true;
    }

    @Nullable static RaidVillage lived(ServerLevel level, BlockPos at) {
        RaidVillage village = RaidVillage.nearest(level, at, 0);
        return village != null && occupied(level, village) ? village : null;
    }

    private static boolean occupied(ServerLevel level, RaidVillage village) {
        List<BlockPos> points = level.getPoiManager().findAll(type -> type.is(PoiTypeTags.VILLAGE), _ -> true, village.center(), village.radius(), PoiManager.Occupancy.IS_OCCUPIED).toList();
        for (BlockPos point : points) {
            for (BlockPos near : BlockPos.betweenClosed(point.offset(-DOOR_REACH, -DOOR_REACH, -DOOR_REACH), point.offset(DOOR_REACH, DOOR_REACH, DOOR_REACH))) {
                if (level.isLoaded(near) && level.getBlockState(near).is(BlockTags.DOORS)) { return true; }
            }
        }
        return false;
    }

    @Nullable private static RaidTarget village(ServerLevel level, ServerPlayer player) {
        RaidVillage village = lived(level, player.blockPosition());
        if (village == null || RaidStorage.get(level).raided(village.center(), VILLAGE_SEARCH + village.radius())) { return null; }
        return new RaidTarget(RaidTarget.Kind.VILLAGE, village.center(), village.radius(), 0, null, false, 0);
    }

    @Nullable private static RaidTarget base(ServerLevel level, ServerPlayer player, RaidDef def) {
        BaseDef rules = def.base();
        if (rules == null) { return null; }
        Base base = BaseFinder.find(level, player, rules);
        if (base == null || refused(level, rules, base.ownerHandle(), base.center(), base.radius())) { return null; }
        return new RaidTarget(RaidTarget.Kind.BASE, base.center(), base.radius(), base.ownerHandle(), player.getUUID(), false, (int) Math.round(base.score()));
    }

    @Nullable private static RaidTarget player(ServerLevel level, ServerPlayer player, RaidDef def) {
        BaseDef rules = def.base();
        if (rules == null) { return null; }
        int owner = RaidOwner.of(player);
        BlockPos at = player.blockPosition();
        if (refused(level, rules, owner, at, rules.radius())) { return null; }
        return new RaidTarget(RaidTarget.Kind.PLAYER, at, rules.radius(), owner, player.getUUID(), false, 0);
    }

    @Nullable private static RaidTarget free(ServerLevel level, ServerPlayer player, RaidDef def) {
        BlockPos at = player.blockPosition();
        if (RaidStorage.get(level).raided(at, def.reach())) { return null; }
        return new RaidTarget(RaidTarget.Kind.FREE, at, def.reach(), 0, null, true, 0);
    }

    private static boolean refused(ServerLevel level, BaseDef rules, int owner, BlockPos center, int radius) {
        if (rules.cooldown() > 0 && BaseLedger.get(level).raidedWithin(level.getServer(), owner, level.getGameTime(), rules.cooldown())) { return true; }
        if (rules.onlineOnly() && !RaidDefenders.online(level, owner, null)) { return true; }
        return RaidStorage.get(level).raided(center, radius + VILLAGE_SEARCH);
    }
}
