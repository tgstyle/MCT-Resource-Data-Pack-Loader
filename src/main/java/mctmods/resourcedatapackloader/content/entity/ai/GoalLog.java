package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.EntityCreature;

final class GoalLog {
    private GoalLog() {}

    static void debug(EntityCreature mob, String message, Object... details) {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }
        Object[] all = new Object[details.length + 4];
        all[0] = mob.getName();
        all[1] = (int) mob.posX;
        all[2] = (int) mob.posY;
        all[3] = (int) mob.posZ;
        System.arraycopy(details, 0, all, 4, details.length);
        ContentLog.LOGGER.debug("{} at {}, {}, {} " + message, all);
    }
}
