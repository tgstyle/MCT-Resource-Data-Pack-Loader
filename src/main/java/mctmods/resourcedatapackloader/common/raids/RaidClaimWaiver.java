package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public final class RaidClaimWaiver {
    private RaidClaimWaiver() {}

    public static boolean lifts(Entity mob) {
        NBTTagCompound data = mob.getEntityData();
        if (!(mob.world instanceof WorldServer) || !data.hasKey(ActiveRaid.RAIDER) || !data.hasKey(ActiveRaid.DOOR)) { return false; }
        WorldServer world = (WorldServer) mob.world;
        ActiveRaid raid = RaidStorage.get(world).raidOf(NBTUtil.getPosFromTag(data.getCompoundTag(ActiveRaid.RAIDER)));
        if (raid == null || raid.kind() != RaidTarget.Kind.BASE && raid.kind() != RaidTarget.Kind.PLAYER) { return false; }
        BaseDef base = raid.def().base();
        if (base == null || !"allow".equals(base.breakClaimed)) { return false; }
        int claim = ClaimGuard.covers(world, BlockPos.fromLong(data.getLong(ActiveRaid.DOOR)));
        return claim != 0 && RaidOwner.same(world.getMinecraftServer(), claim, raid.owner());
    }
}
