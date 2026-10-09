package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.entity.PathfinderMob;

final class GoalLog {
    private GoalLog() {}

    static void debug(PathfinderMob mob, String message, Object... details) {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }
        Object[] all = new Object[details.length + 4];
        all[0] = mob.getName().getString();
        all[1] = mob.getBlockX();
        all[2] = mob.getBlockY();
        all[3] = mob.getBlockZ();
        System.arraycopy(details, 0, all, 4, details.length);
        ContentLog.LOGGER.debug("{} at {}, {}, {} " + message, all);
    }
}
