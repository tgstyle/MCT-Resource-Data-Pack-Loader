package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public final class ConfigEntities {
    private final ModConfigSpec.BooleanValue slowDistantEntities;
    private final ModConfigSpec.ConfigValue<List<? extends String>> slowedKinds;
    private final ModConfigSpec.IntValue slowDistance;
    private final ModConfigSpec.IntValue slowRate;
    private final ModConfigSpec.ConfigValue<List<? extends String>> neverSlowed;
    private final ModConfigSpec.IntValue slowRecheck;

    ConfigEntities(ModConfigSpec.Builder builder) {
        builder.comment("How entities far from every player are ticked").push("entities");
        slowDistantEntities = builder.comment("Tick entities far from every player less often. Nothing is ever left unticked, only ticked at a slower pace [Default=true]").define("slowDistantEntities", true);
        slowedKinds = builder.comment("Which kinds are given fewer ticks: items, experience, projectiles. Anything that thinks for itself is always given a slower pace instead, without being named here, and machines are never slowed [Default=[items, experience]]").defineListAllowEmpty("slowedKinds", List.of("items", "experience"), () -> "", each -> each instanceof String);
        slowDistance = builder.comment("How far from the nearest player, in blocks, before a chunk is slowed. The game stops telling a player about most entities beyond 64, so nothing below that [Default=192]").defineInRange("slowDistance", 192, 64, 4096);
        slowRate = builder.comment("One tick in this many is given to a slowed chunk. 1 is no slowing at all, 20 is once a second [Default=4]").defineInRange("slowRate", 4, 1, 20);
        neverSlowed = builder.comment("Entities left alone however far away they are, as namespace:name [Default=[]]").defineListAllowEmpty("neverSlowed", List.of(), () -> "", each -> each instanceof String);
        slowRecheck = builder.comment("How often, in ticks, the distance to the nearest player is worked out again. Every player counts for themselves, so someone alone far away still has their own quiet space around them [Default=20]").defineInRange("slowRecheck", 20, 1, 100);
        builder.pop();
    }

    public boolean slowDistantEntities() { return !Config.loaded() || slowDistantEntities.get(); }

    public List<String> slowedKinds() { return Config.loaded() ? List.copyOf(slowedKinds.get()) : List.of("items", "experience"); }

    public int slowDistance() { return Config.loaded() ? slowDistance.get() : 192; }

    public int slowRate() { return Config.loaded() ? slowRate.get() : 4; }

    public List<String> neverSlowed() { return Config.loaded() ? List.copyOf(neverSlowed.get()) : List.of(); }

    public int slowRecheck() { return Config.loaded() ? slowRecheck.get() : 20; }
}
