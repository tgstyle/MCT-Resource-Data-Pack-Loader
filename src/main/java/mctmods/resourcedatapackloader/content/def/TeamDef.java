package mctmods.resourcedatapackloader.content.def;

import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import java.util.List;

public final class TeamDef {
    public final String name;
    public final String displayName;
    public final TextFormatting color;
    public final String prefix;
    public final String suffix;
    public final boolean friendlyFire;
    public final boolean mobFriendlyFire;
    public final boolean seeFriendlyInvisibles;
    public final Team.EnumVisible nameTags;
    public final Team.EnumVisible deathMessages;
    public final Team.CollisionRule collision;
    public final List<String> entities;
    public final List<String> players;
    public final int[] spawnBox;
    public final boolean joinable;
    public final String lead;
    public final String leadOn;
    public final String leadIs;
    public final boolean balance;
    public final boolean scoreboard;

    public TeamDef(String name, String displayName, TextFormatting color, String prefix, String suffix,
                   boolean friendlyFire, boolean mobFriendlyFire, boolean seeFriendlyInvisibles, Team.EnumVisible nameTags,
                   Team.EnumVisible deathMessages, Team.CollisionRule collision,
                   List<String> entities, List<String> players, int[] spawnBox, boolean joinable,
                   String lead, String leadOn, String leadIs, boolean balance, boolean scoreboard) {
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.friendlyFire = friendlyFire;
        this.mobFriendlyFire = mobFriendlyFire;
        this.seeFriendlyInvisibles = seeFriendlyInvisibles;
        this.nameTags = nameTags;
        this.deathMessages = deathMessages;
        this.collision = collision;
        this.entities = entities;
        this.players = players;
        this.spawnBox = spawnBox;
        this.joinable = joinable;
        this.lead = lead;
        this.leadOn = leadOn;
        this.leadIs = leadIs;
        this.balance = balance;
        this.scoreboard = scoreboard;
    }

    public boolean leads() { return !"none".equals(lead); }

    public boolean holds(double x, double y, double z) {
        return spawnBox != null
                && x >= spawnBox[0] && x <= spawnBox[3]
                && y >= spawnBox[1] && y <= spawnBox[4]
                && z >= spawnBox[2] && z <= spawnBox[5];
    }
}
