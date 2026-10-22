package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.EnumDifficulty;
import javax.annotation.Nullable;

public final class RuleSwitchDef {
    public static final int UNSET = -1;
    public static final int CLEAR = 0;
    public static final int RAIN = 1;
    public static final int THUNDER = 2;
    public static final RuleSwitchDef NONE = new RuleSwitchDef(-1L, UNSET, 1.0F, null, -1.0F, true, -1);
    public final long dayLength;
    public final int weather;
    public final float weatherSpeed;
    @Nullable public final EnumDifficulty difficulty;
    public final float localDifficulty;
    public final boolean sleep;
    public final int wakeAt;

    public RuleSwitchDef(long dayLength, int weather, float weatherSpeed, @Nullable EnumDifficulty difficulty, float localDifficulty, boolean sleep, int wakeAt) {
        this.dayLength = dayLength;
        this.weather = weather;
        this.weatherSpeed = weatherSpeed;
        this.difficulty = difficulty;
        this.localDifficulty = localDifficulty;
        this.sleep = sleep;
        this.wakeAt = wakeAt;
    }
}
