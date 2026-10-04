package mctmods.resourcedatapackloader.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;

public final class TurnClock {
    private static final int FIRST_WARNING = 10;
    private static final int LAST_WARNING = 3;
    private int left;

    public void start(int seconds) { left = Math.max(0, seconds); }

    public void stop() { left = 0; }

    public int left() { return left; }

    public boolean runsOut() {
        if (left <= 0) { return false; }
        left--;
        return left == 0;
    }

    public boolean warns() { return left == FIRST_WARNING || left == LAST_WARNING; }

    public boolean lastWarning() { return left == LAST_WARNING; }

    public static void ping(EntityPlayerMP player, boolean last) { player.connection.sendPacket(new SPacketSoundEffect(SoundEvents.BLOCK_NOTE_PLING, SoundCategory.MASTER, player.posX, player.posY, player.posZ, 1.0F, last ? 2.0F : 1.0F)); }
}
