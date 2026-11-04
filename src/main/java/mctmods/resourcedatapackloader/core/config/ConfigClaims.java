package mctmods.resourcedatapackloader.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public final class ConfigClaims {
    private final ModConfigSpec.ConfigValue<List<? extends String>> methods;
    private final ModConfigSpec.ConfigValue<String> claimWho;
    private final ModConfigSpec.IntValue commandRadius;
    private final ModConfigSpec.IntValue commandLevel;
    private final ModConfigSpec.IntValue opLevel;
    private final ModConfigSpec.ConfigValue<String> item;
    private final ModConfigSpec.BooleanValue itemConsume;
    private final ModConfigSpec.ConfigValue<String> block;
    private final ModConfigSpec.IntValue blockRadius;
    private final ModConfigSpec.ConfigValue<String> blockOverlap;
    private final ModConfigSpec.IntValue claimCheckTicks;
    private final ModConfigSpec.ConfigValue<String> limitMode;
    private final ModConfigSpec.IntValue limit;
    private final ModConfigSpec.IntValue limitPerMember;
    private final ModConfigSpec.ConfigValue<String> limitScore;
    private final ModConfigSpec.BooleanValue mustTouch;
    private final ModConfigSpec.IntValue minDistance;
    private final ModConfigSpec.IntValue spawnRadius;
    private final ModConfigSpec.ConfigValue<List<? extends String>> dimensions;
    private final ModConfigSpec.BooleanValue dimensionsAreBlacklist;
    private final ModConfigSpec.ConfigValue<String> orphanAccess;
    private final ModConfigSpec.ConfigValue<String> fakePlayers;
    private final ModConfigSpec.BooleanValue denyMessage;
    private final ModConfigSpec.BooleanValue protectBreak;
    private final ModConfigSpec.BooleanValue protectPlace;
    private final ModConfigSpec.BooleanValue protectUse;
    private final ModConfigSpec.BooleanValue protectExplosions;
    private final ModConfigSpec.BooleanValue protectMobGriefing;
    private final ModConfigSpec.BooleanValue protectFluids;
    private final ModConfigSpec.BooleanValue protectPistons;
    private final ModConfigSpec.BooleanValue protectFire;
    private final ModConfigSpec.BooleanValue protectPvp;
    private final ModConfigSpec.ConfigValue<List<? extends String>> useBlocks;
    private final ModConfigSpec.ConfigValue<List<? extends String>> useExceptions;
    private final ModConfigSpec.BooleanValue message;
    private final ModConfigSpec.BooleanValue messageWilderness;
    private final ModConfigSpec.ConfigValue<String> claimOnDissolve;
    private final ModConfigSpec.BooleanValue restore;
    private final ModConfigSpec.BooleanValue regenerate;
    private final ModConfigSpec.IntValue restorePerTick;
    private final ModConfigSpec.BooleanValue movePlayers;
    private final ModConfigSpec.ConfigValue<String> entities;

    ConfigClaims(ModConfigSpec.Builder builder) {
        builder.comment("Claims: areas a team or player takes with a command, an item or a block, and what others may do inside them [Default=nothing can be claimed]").push("claims");
        methods = builder.comment("How a claim can be made. command is the claim command, item is right clicking with the claim item, block is placing the claim block. Empty means nothing can be claimed [command|item|block] [Default=[]]").defineListAllowEmpty("methods", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        claimWho = builder.comment("Who a claim belongs to. team gives it to the claimer's team and needs one, teamOrPlayer gives a claimer with no team the claim themselves, player always gives it to the claimer [team|teamOrPlayer|player] [Default=team]").define("claimWho", "team");
        commandRadius = builder.comment("How many chunks around the claimer the claim command takes [Default=3]").defineInRange("commandRadius", 3, 0, 64);
        commandLevel = builder.comment("The permission level a player needs to use the claim command, 0 is everyone [Default=0]").defineInRange("commandLevel", 0, 0, 4);
        opLevel = builder.comment("The permission level that may change or remove claims that are not their own [Default=2]").defineInRange("opLevel", 2, 0, 4);
        item = builder.comment("The item that claims when used, as modid:name. Empty means no claim item [Default=]").define("item", "");
        itemConsume = builder.comment("On, using the claim item uses one up [Default=false]").define("itemConsume", false);
        block = builder.comment("The block that claims when placed, as modid:name. Empty means no claim block [Default=]").define("block", "");
        blockRadius = builder.comment("How many chunks around the claim block a placed claim block takes. 0 is its own chunk only [Default=0]").defineInRange("blockRadius", 0, 0, 64);
        blockOverlap = builder.comment("What a claim block does when its area meets a chunk that is already claimed. refuse is not placed, skip takes only the free chunks [refuse|skip] [Default=refuse]").define("blockOverlap", "refuse");
        claimCheckTicks = builder.comment("How often, in ticks, claim blocks are checked for being broken or moved [Default=100]").defineInRange("claimCheckTicks", 100, 1, 1200);
        limitMode = builder.comment("How many chunks a team or player may claim. flat gives everyone limit, perMember gives limitPerMember for each team member, score reads the scoreboard objective limitScore [flat|perMember|score] [Default=flat]").define("limitMode", "flat");
        limit = builder.comment("The most chunks each claimer may claim in flat mode [Default=16]").defineInRange("limit", 16, 0, 100000);
        limitPerMember = builder.comment("The most chunks each team member adds in perMember mode [Default=4]").defineInRange("limitPerMember", 4, 0, 100000);
        limitScore = builder.comment("The scoreboard objective that holds each claimer's limit in score mode. Empty means no limit from it [Default=]").define("limitScore", "");
        mustTouch = builder.comment("On, a new claim must touch a chunk the claimer already has, unless it is their first [Default=false]").define("mustTouch", false);
        minDistance = builder.comment("The fewest chunks a new claim keeps between itself and another claimer's claim. 0 allows touching [Default=0]").defineInRange("minDistance", 0, 0, 64);
        spawnRadius = builder.comment("How many chunks around the world spawn cannot be claimed. 0 allows all [Default=0]").defineInRange("spawnRadius", 0, 0, 64);
        dimensions = builder.comment("Dimensions claims work in, by id, such as minecraft:the_nether. Empty means every dimension [Default=[]]").defineListAllowEmpty("dimensions", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        dimensionsAreBlacklist = builder.comment("On, claims do not work in these dimensions. Off, they work only in them [Default=false]").define("dimensionsAreBlacklist", false);
        orphanAccess = builder.comment("What a claim does while its owner is gone, such as a dissolved team. locked keeps everyone out, open lets everyone in [locked|open] [Default=locked]").define("orphanAccess", "locked");
        fakePlayers = builder.comment("Whether fake players, such as machines, may act in claims of others. deny stops them, allow lets them [deny|allow] [Default=deny]").define("fakePlayers", "deny");
        denyMessage = builder.comment("On, a player stopped by a claim is told so [Default=true]").define("denyMessage", true);
        protectBreak = builder.comment("On, others cannot break blocks in a claim [Default=false]").define("protectBreak", false);
        protectPlace = builder.comment("On, others cannot place blocks in a claim [Default=false]").define("protectPlace", false);
        protectUse = builder.comment("On, others cannot use blocks and items in a claim, as far as useBlocks and useExceptions say [Default=false]").define("protectUse", false);
        protectExplosions = builder.comment("On, explosions do not damage blocks in a claim [Default=false]").define("protectExplosions", false);
        protectMobGriefing = builder.comment("On, mobs cannot change blocks in a claim, such as endermen and ravagers [Default=false]").define("protectMobGriefing", false);
        protectFluids = builder.comment("On, fluids do not flow into a claim from outside it [Default=false]").define("protectFluids", false);
        protectPistons = builder.comment("On, pistons cannot push blocks into or out of a claim from outside it [Default=false]").define("protectPistons", false);
        protectFire = builder.comment("On, fire does not spread into or burn blocks in a claim [Default=false]").define("protectFire", false);
        protectPvp = builder.comment("On, players cannot hurt each other in a claim [Default=false]").define("protectPvp", false);
        useBlocks = builder.comment("Blocks protectUse stops others from using, as modid:name, modid:* for every block of a mod, #modid:tag for a block tag, or * for every block [Default=[\"*\"]]").defineListAllowEmpty("useBlocks", List.of("*"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        useExceptions = builder.comment("Blocks protectUse lets others use anyway, written like useBlocks [Default=[]]").defineListAllowEmpty("useExceptions", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        message = builder.comment("On, a player who crosses a claim's edge is told whose it is [Default=false]").define("message", false);
        messageWilderness = builder.comment("On, a player who crosses into unclaimed land from a claim is told so too [Default=false]").define("messageWilderness", false);
        claimOnDissolve = builder.comment("What happens to the claims of a team that is dissolved. keep leaves them owned by the gone team, release makes them free, remove deletes them [keep|release|remove] [Default=keep]").define("claimOnDissolve", "keep");
        restore = builder.comment("On, a claim that is removed has its chunks put back from the pristine copy saved after pregeneration (pregenBackup) [Default=false]").define("restore", false);
        regenerate = builder.comment("On, a claim that is removed has its chunks regenerated from the world seed [Default=false]").define("regenerate", false);
        restorePerTick = builder.comment("How many chunks are restored or regenerated each tick [Default=1]").defineInRange("restorePerTick", 1, 1, 100000);
        movePlayers = builder.comment("On, players standing in a claim that is removed are moved out of it [Default=true]").define("movePlayers", true);
        entities = builder.comment("What happens to entities in a claim that is removed. keep leaves them, remove takes them out of the world [keep|remove] [Default=keep]").define("entities", "keep");
        builder.pop();
    }

    public List<String> methods() { return ConfigCore.strings(methods); }

    public String claimWho() { return ConfigCore.text(claimWho); }

    public int commandRadius() { return ConfigCore.number(commandRadius); }

    public int commandLevel() { return ConfigCore.number(commandLevel); }

    public int opLevel() { return ConfigCore.number(opLevel); }

    public String item() { return ConfigCore.text(item); }

    public boolean itemConsume() { return ConfigCore.flag(itemConsume); }

    public String block() { return ConfigCore.text(block); }

    public int blockRadius() { return ConfigCore.number(blockRadius); }

    public String blockOverlap() { return ConfigCore.text(blockOverlap); }

    public int claimCheckTicks() { return ConfigCore.number(claimCheckTicks); }

    public String limitMode() { return ConfigCore.text(limitMode); }

    public int limit() { return ConfigCore.number(limit); }

    public int limitPerMember() { return ConfigCore.number(limitPerMember); }

    public String limitScore() { return ConfigCore.text(limitScore); }

    public boolean mustTouch() { return ConfigCore.flag(mustTouch); }

    public int minDistance() { return ConfigCore.number(minDistance); }

    public int spawnRadius() { return ConfigCore.number(spawnRadius); }

    public List<String> dimensions() { return ConfigCore.strings(dimensions); }

    public boolean dimensionsAreBlacklist() { return ConfigCore.flag(dimensionsAreBlacklist); }

    public String orphanAccess() { return ConfigCore.text(orphanAccess); }

    public String fakePlayers() { return ConfigCore.text(fakePlayers); }

    public boolean denyMessage() { return ConfigCore.flag(denyMessage); }

    public boolean protectBreak() { return ConfigCore.flag(protectBreak); }

    public boolean protectPlace() { return ConfigCore.flag(protectPlace); }

    public boolean protectUse() { return ConfigCore.flag(protectUse); }

    public boolean protectExplosions() { return ConfigCore.flag(protectExplosions); }

    public boolean protectMobGriefing() { return ConfigCore.flag(protectMobGriefing); }

    public boolean protectFluids() { return ConfigCore.flag(protectFluids); }

    public boolean protectPistons() { return ConfigCore.flag(protectPistons); }

    public boolean protectFire() { return ConfigCore.flag(protectFire); }

    public boolean protectPvp() { return ConfigCore.flag(protectPvp); }

    public List<String> useBlocks() { return ConfigCore.strings(useBlocks); }

    public List<String> useExceptions() { return ConfigCore.strings(useExceptions); }

    public boolean message() { return ConfigCore.flag(message); }

    public boolean messageWilderness() { return ConfigCore.flag(messageWilderness); }

    public String claimOnDissolve() { return ConfigCore.text(claimOnDissolve); }

    public boolean restore() { return ConfigCore.flag(restore); }

    public boolean regenerate() { return ConfigCore.flag(regenerate); }

    public int restorePerTick() { return ConfigCore.number(restorePerTick); }

    public boolean movePlayers() { return ConfigCore.flag(movePlayers); }

    public String entities() { return ConfigCore.text(entities); }
}
