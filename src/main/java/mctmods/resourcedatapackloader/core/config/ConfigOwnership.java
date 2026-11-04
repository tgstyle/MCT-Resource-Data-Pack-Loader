package mctmods.resourcedatapackloader.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class ConfigOwnership {
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blocks;
    private final ForgeConfigSpec.ConfigValue<String> who;
    private final ForgeConfigSpec.ConfigValue<String> onJoinTeam;
    private final ForgeConfigSpec.ConfigValue<String> onDissolve;
    private final ForgeConfigSpec.ConfigValue<String> removeAs;
    private final ForgeConfigSpec.IntValue dissolvePerTick;
    private final ForgeConfigSpec.IntValue checkTicks;

    ConfigOwnership(ForgeConfigSpec.Builder builder) {
        builder.comment("Blocks that belong to whoever places them, and what happens to them when a team changes").push("ownership");
        blocks = builder.comment("Blocks that are owned by whoever places them, as modid:name, modid:* for every block of a mod, #modid:tag for every block in a tag, or * for every block [Default={}]").defineListAllowEmpty("blocks", List.of(), ConfigCore.IS_TEXT);
        who = builder.comment("Who an owned block belongs to. team tags only a placer on a team and gives the block to the team, teamOrPlayer gives a placer with no team the block themselves, player always gives it to the placer [team|teamOrPlayer|player] [Default=team]").define("who", "team");
        onJoinTeam = builder.comment("What happens to a player's own owned blocks when the player joins a team. keep leaves them the player's, move hands them to the team [keep|move] [Default=keep]").define("onJoinTeam", "keep");
        onDissolve = builder.comment("What happens to the owned blocks of a team that is dissolved. keep leaves them owned by the gone team, release makes them owned by nobody, remove takes them out of the world [keep|release|remove] [Default=keep]").define("onDissolve", "keep");
        removeAs = builder.comment("How removed blocks are taken out when onDissolve is remove. drop breaks them and drops their items, vanish takes them without drops [drop|vanish] [Default=drop]").define("removeAs", "drop");
        dissolvePerTick = builder.comment("How many owned blocks of a dissolved team are released or removed each tick [Default=64]").defineInRange("dissolvePerTick", 64, 1, 100000);
        checkTicks = builder.comment("How often, in ticks, the ownership work of a dissolved or changed team is checked for [Default=20]").defineInRange("checkTicks", 20, 1, 1200);
        builder.pop();
    }

    public List<String> blocks() { return ConfigCore.strings(blocks); }

    public String who() { return ConfigCore.text(who); }

    public String onJoinTeam() { return ConfigCore.text(onJoinTeam); }

    public String onDissolve() { return ConfigCore.text(onDissolve); }

    public String removeAs() { return ConfigCore.text(removeAs); }

    public int dissolvePerTick() { return ConfigCore.number(dissolvePerTick); }

    public int checkTicks() { return ConfigCore.number(checkTicks); }
}
