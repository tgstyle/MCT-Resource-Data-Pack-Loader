package mctmods.resourcedatapackloader.content.def;

import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;
import java.util.List;
import javax.annotation.Nullable;

public record TeamDef(String name, String displayName, ChatFormatting color, String prefix, String suffix, boolean friendlyFire, boolean mobFriendlyFire,
                      boolean seeFriendlyInvisibles, Team.Visibility nameTags, Team.Visibility deathMessages, Team.CollisionRule collision, List<String> entities,
                      List<String> players, @Nullable int[] spawnBox, boolean joinable, String lead, String leadOn, String leadIs, boolean balance, boolean scoreboard) {
    public static final String NONE = "none";
    public static final String TOP_SCORE = "topScore";
    public static final String APPOINTED = "appointed";
    public static final String VOTE = "vote";
    public static final String CLAIM = "claim";

    public boolean leads() { return !NONE.equals(lead); }

    public boolean holds(double x, double y, double z) {
        return spawnBox != null && x >= spawnBox[0] && x <= spawnBox[3] && y >= spawnBox[1] && y <= spawnBox[4] && z >= spawnBox[2] && z <= spawnBox[5];
    }
}
