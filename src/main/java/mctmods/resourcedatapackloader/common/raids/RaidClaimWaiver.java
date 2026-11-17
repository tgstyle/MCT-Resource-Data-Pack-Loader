package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;
import mctmods.resourcedatapackloader.common.util.world.PosTags;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public final class RaidClaimWaiver {
    private RaidClaimWaiver() {}

    public static boolean lifts(Entity mob) {
        CompoundTag data = mob.getPersistentData();
        if (!(mob.level() instanceof ServerLevel level) || !data.contains(ActiveRaid.RAIDER) || !data.contains(ActiveRaid.DOOR)) { return false; }
        ActiveRaid raid = RaidStorage.get(level).raidOf(PosTags.read(data.getCompoundOrEmpty(ActiveRaid.RAIDER)));
        if (raid == null || raid.kind() != RaidTarget.Kind.BASE && raid.kind() != RaidTarget.Kind.PLAYER) { return false; }
        BaseDef base = raid.def().base();
        if (base == null || !"allow".equals(base.breakClaimed())) { return false; }
        int claim = ClaimGuard.covers(level, BlockPos.of(data.getLongOr(ActiveRaid.DOOR, 0L)));
        return claim != 0 && RaidOwner.same(level.getServer(), claim, raid.owner());
    }
}
