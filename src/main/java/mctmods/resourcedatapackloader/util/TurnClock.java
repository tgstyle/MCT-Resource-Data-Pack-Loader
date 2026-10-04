package mctmods.resourcedatapackloader.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

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

    public static void ping(ServerPlayer player, boolean last) { player.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER, 1.0F, last ? 2.0F : 1.0F); }
}
