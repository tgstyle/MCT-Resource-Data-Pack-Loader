package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class ConfigCommands {
    private final ForgeConfigSpec.IntValue gotoLevel;
    private final ForgeConfigSpec.IntValue gotoNextLevel;
    private final ForgeConfigSpec.IntValue gotoBackLevel;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> gotoPlaceLevels;

    ConfigCommands(ForgeConfigSpec.Builder builder) {
        builder.comment("Who may run which parts of the mod's own commands").push("commands");
        gotoLevel = builder.comment("The permission level needed for /rdplserver goto <name>, which carries the sender to the nearest one. 3 is an operator, the level every other part of the command sits at. 2 also lets a command block run it, so a pack can put the jump on a button or a pressure plate without handing anybody the rest of the command. 0 lets any player type it. The other parts of /rdplserver stay at 3 whatever this says [Default=3]").defineInRange("gotoLevel", 3, 0, 4);
        gotoNextLevel = builder.comment("The permission level for /rdplserver goto <name> next, which passes over the one it last carried the sender to and finds another. Same scale as gotoLevel [Default=3]").defineInRange("gotoNextLevel", 3, 0, 4);
        gotoBackLevel = builder.comment("The permission level for /rdplserver goto <name> back, which returns the sender to the one before. Same scale as gotoLevel [Default=3]").defineInRange("gotoBackLevel", 3, 0, 4);
        gotoPlaceLevels = builder.comment("Permission levels for single places, as name=level entries, one per line, overriding the three settings above for that place alone and in all three of its forms. The name is what you would type after goto, so a vanilla one such as Village or Mansion, or a name a pack registered for its own structures with locateAs. Same scale: 3 an operator, 2 also a command block, 0 anybody. A pack can then open the way to its own ruins while every vanilla structure stays shut, or the other way about. A name nothing has registered is ignored with a note in the log [Default=[]]").defineListAllowEmpty("gotoPlaceLevels", List.of(), each -> each instanceof String);
        builder.pop();
    }

    public int gotoLevel() { return Config.loaded() ? gotoLevel.get() : 3; }

    public int gotoNextLevel() { return Config.loaded() ? gotoNextLevel.get() : 3; }

    public int gotoBackLevel() { return Config.loaded() ? gotoBackLevel.get() : 3; }

    public List<String> gotoPlaceLevels() { return Config.loaded() ? List.copyOf(gotoPlaceLevels.get()) : List.of(); }
}
