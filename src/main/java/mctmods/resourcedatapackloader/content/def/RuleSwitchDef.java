package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.Difficulty;
import javax.annotation.Nullable;

public record RuleSwitchDef(long dayLength, int weather, float weatherSpeed, @Nullable Difficulty difficulty, float localDifficulty, boolean sleep, int wakeAt) {
    public static final int UNSET = -1;
    public static final int CLEAR = 0;
    public static final int THUNDER = 2;
    public static final RuleSwitchDef NONE = new RuleSwitchDef(-1L, UNSET, 1.0F, null, -1.0F, true, -1);
}
