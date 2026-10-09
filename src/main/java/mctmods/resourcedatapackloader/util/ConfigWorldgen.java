package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public final class ConfigWorldgen extends ConfigWorldgenRails {
    private final ModConfigSpec.ConfigValue<String> villageSewerBlock;
    private final ModConfigSpec.IntValue villageSewerDepth;
    private final ModConfigSpec.IntValue villageSewerHeight;
    private final ModConfigSpec.IntValue villageSewerWidth;
    private final ModConfigSpec.ConfigValue<String> villageSewerWaterBlock;
    private final ModConfigSpec.ConfigValue<String> villageSewerWalkBlock;
    private final ModConfigSpec.ConfigValue<String> villageSewerLightBlock;
    private final ModConfigSpec.IntValue villageSewerLightRun;
    private final ModConfigSpec.ConfigValue<String> villageSewerLadderBlock;
    private final ModConfigSpec.ConfigValue<String> villageSewerCoverBlock;
    private final ModConfigSpec.ConfigValue<String> villageSewerMossBlock;
    private final ModConfigSpec.IntValue villageSewerMossChance;
    private final ModConfigSpec.ConfigValue<String> villageSewerVineBlock;
    private final ModConfigSpec.IntValue villageSewerVineChance;
    private final ModConfigSpec.BooleanValue villageSewerWellEntrance;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldGravity;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldFallDamage;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldJumpStrength;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldTerminalVelocity;
    private final ModConfigSpec.ConfigValue<List<? extends String>> weatherCeiling;
    private final ModConfigSpec.ConfigValue<List<? extends String>> cloudHeight;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldBelow;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldAbove;
    private final ModConfigSpec.BooleanValue worldSeamEntities;
    private final ModConfigSpec.BooleanValue worldSeamBedrock;
    private final ModConfigSpec.ConfigValue<List<? extends String>> threatItems;
    private final ModConfigSpec.ConfigValue<List<? extends String>> threatLevels;
    private final ModConfigSpec.IntValue threatMost;
    private final ModConfigSpec.DoubleValue threatSpawnRate;
    private final ModConfigSpec.DoubleValue threatNotice;
    private final ModConfigSpec.ConfigValue<List<? extends String>> threatSays;
    private final ModConfigSpec.ConfigValue<List<? extends String>> blockReplacements;
    private final ModConfigSpec.ConfigValue<List<? extends String>> blockReplacementDimensions;
    private final ModConfigSpec.BooleanValue blockReplacementDimensionsAreBlacklist;
    private final ModConfigSpec.IntValue blockReplacementMinHeight;
    private final ModConfigSpec.IntValue blockReplacementMaxHeight;
    private final ModConfigSpec.ConfigValue<String> blockReplacementKey;
    private final ModConfigSpec.BooleanValue logBlockReplacements;
    private final ModConfigSpec.BooleanValue blockWorldGenerators;
    private final ModConfigSpec.ConfigValue<List<? extends String>> generatorWhitelist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> blockedGenerators;
    private final ModConfigSpec.ConfigValue<List<? extends String>> generatorTypes;
    private final ModConfigSpec.BooleanValue generatorTypesAreBlacklist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> generatorTypeMap;
    private final ModConfigSpec.ConfigValue<List<? extends String>> blockGeneratorDimensions;
    private final ModConfigSpec.BooleanValue blockGeneratorDimensionsAreBlacklist;
    private final ModConfigSpec.BooleanValue logBlockedGenerators;

    ConfigWorldgen(ModConfigSpec.Builder builder) {
        super(builder);
        villageSewerBlock = builder.comment("The block a sewer is lined with under a city's streets and alleys: its floor, walls and roof. Empty digs no sewers [Default=empty]").define("villageSewerBlock", "");
        villageSewerDepth = builder.comment("How far under a street's own surface the sewer floor sits. The sewer follows the street, so a climbing street carries a climbing sewer. Needs villageSewerBlock [Default=8]").defineInRange("villageSewerDepth", 8, 4, Integer.MAX_VALUE);
        villageSewerHeight = builder.comment("How many blocks of headroom stand over the sewer walkway [Default=3]").defineInRange("villageSewerHeight", 3, 2, Integer.MAX_VALUE);
        villageSewerWidth = builder.comment("How wide a sewer runs, counted across including its two walls. Even numbers are rounded up so the channel keeps the middle [Default=5]").defineInRange("villageSewerWidth", 5, 3, Integer.MAX_VALUE);
        villageSewerWaterBlock = builder.comment("The block filling the channel down the middle of a sewer. Empty leaves the channel dry [Default=minecraft:water]").define("villageSewerWaterBlock", "minecraft:water");
        villageSewerWalkBlock = builder.comment("The block the walkways either side of the channel are surfaced with. Empty walks on the lining block [Default=empty]").define("villageSewerWalkBlock", "");
        villageSewerLightBlock = builder.comment("The block set into a sewer roof over the channel as a light. Empty lights none [Default=empty]").define("villageSewerLightBlock", "");
        villageSewerLightRun = builder.comment("How many blocks apart the sewer lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villageSewerLightRun", 8, 1, Integer.MAX_VALUE);
        villageSewerLadderBlock = builder.comment("The block a manhole shaft is climbed by, set down the shaft from the street to the sewer roof. Empty leaves the shaft open [Default=empty]").define("villageSewerLadderBlock", "");
        villageSewerCoverBlock = builder.comment("The block covering a manhole, set flush in an east-west street wherever a street or alley meets it, and on the plaza where that street crosses the sewer loop. Empty leaves the shaft mouth open [Default=empty]").define("villageSewerCoverBlock", "");
        villageSewerMossBlock = builder.comment("A second block mixed into the sewer lining here and there, mossy stone among plain for instance. Empty lines the sewer with one block throughout [Default=empty]").define("villageSewerMossBlock", "");
        villageSewerMossChance = builder.comment("The percentage of lining blocks that come out as villageSewerMossBlock. Rolled per block position from the world seed, so a repave lays the same pattern [Default=25]").defineInRange("villageSewerMossChance", 25, 0, Integer.MAX_VALUE);
        villageSewerVineBlock = builder.comment("A block hung on the inside of the sewer walls here and there, vines for instance. Empty hangs nothing [Default=empty]").define("villageSewerVineBlock", "");
        villageSewerVineChance = builder.comment("The percentage of wall-side cells that carry villageSewerVineBlock. Rolled per block position from the world seed, so a repave hangs the same pattern [Default=20]").defineInRange("villageSewerVineChance", 20, 0, Integer.MAX_VALUE);
        villageSewerWellEntrance = builder.comment("On, a city with sewers gets a loop of sewer under the plaza around the well, the sewers of the streets meeting there running through it, and a manhole on the plaza down onto the loop on each side where an east-west street crosses it, so the sewers are one connected system with an entrance at the town center. Off, each street's sewer ends at the well and the plaza has no way down [Default=true]").define("villageSewerWellEntrance", true);
        worldGravity = builder.comment("Scale gravity, as a multiplier of vanilla where 1.0 is unchanged and 0.17 is moon-like, for every entity. A bare value covers every dimension, and an entry written as dimension=value covers that dimension alone and wins over the bare one. Empty leaves gravity alone [Default=[]]").defineListAllowEmpty("worldGravity", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        worldFallDamage = builder.comment("Scale fall damage the same way, 0.5 halving it and 2.0 doubling it [Default=[]]").defineListAllowEmpty("worldFallDamage", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        worldJumpStrength = builder.comment("Scale jump strength the same way, 1.5 jumping half again as high [Default=[]]").defineListAllowEmpty("worldJumpStrength", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        worldTerminalVelocity = builder.comment("Scale the fastest a mob or player falls the same way, 0.5 falling at half vanilla's top speed [Default=[]]").defineListAllowEmpty("worldTerminalVelocity", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        weatherCeiling = builder.comment("Highest y rain and snow reach, as dimension=y entries. A bare number covers every dimension. Above it there is no rainfall, no snow build up and nothing drawn. Empty means no ceiling [Default=[]]").defineListAllowEmpty("weatherCeiling", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        cloudHeight = builder.comment("The y clouds are drawn at, as dimension=y entries. A bare number covers every dimension. Empty keeps the game's own cloud height, 192 in the overworld [Default=[]]").defineListAllowEmpty("cloudHeight", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        worldBelow = builder.comment("Stack another dimension under this one: falling out of the bottom of the world carries you into the named dimension, arriving under its ceiling at the same x and z, still falling. Entries are written as dimension=target, such as minecraft:overworld=minecraft:the_nether to hang the nether under the overworld; a bare id covers every dimension. Digging through needs the floor's bedrock left out, which worldSeamBedrock decides. Empty means the floor stays the floor [Default=[]]").defineListAllowEmpty("worldBelow", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        worldAbove = builder.comment("The same for the ceiling: rising past the top of the world carries you into the named dimension, arriving above its floor. Written the same way as worldBelow [Default=[]]").defineListAllowEmpty("worldAbove", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        worldSeamEntities = builder.comment("Whether dropped items, mobs and other entities ride the world seams too, or only players. Riders and mounts cross one at a time [Default=true]").define("worldSeamEntities", true);
        worldSeamBedrock = builder.comment("Keep the bedrock at a seam boundary anyway. Off, a dimension whose floor or ceiling carries a worldBelow or worldAbove seam generates no bedrock there, so the way through can be dug. Already generated chunks keep whatever they have [Default=false]").define("worldSeamBedrock", false);
        threatItems = builder.comment("Items that raise a player's threat level, as item=level,count entries with an optional ,each or ,batch at the end, e.g. minecraft:diamond_sword=5,1 or minecraft:diamond=1,16,batch. Each, the default, adds the level for every one held, counting no more than count of them; batch adds the level once for every count held. A count above the item's stack size is cut to the stack size. Every loaded entity holding items is a carrier: a player's main inventory, armor and off hand, a dropped stack, anything with an item inventory such as a chest mule or a chest minecart, and the held items and armor of other mobs. Empty turns the threat level off [Default=[]]").defineListAllowEmpty("threatItems", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        threatLevels = builder.comment("Rising scores that open each threat band, e.g. 5, 15, 40 for three bands. A player below the first is in band 0. Empty turns the threat level off [Default=[]]").defineListAllowEmpty("threatLevels", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        threatMost = builder.comment("The highest score a carrier can reach, -1 for no cap [Default=-1]").defineInRange("threatMost", -1, -1, 100000);
        threatSpawnRate = builder.comment("Multiplied into the hostile spawn rate near carriers in the top band, scaled down through the lower bands. 1.0 changes nothing, 2.0 doubles spawns at the top [Default=1.0]").defineInRange("threatSpawnRate", 1.0D, 0.0D, 8.0D);
        threatNotice = builder.comment("How many blocks farther hostile mobs notice a carrier in the top band, scaled down through the lower bands. 0 changes nothing [Default=0.0]").defineInRange("threatNotice", 0.0D, 0.0D, 64.0D);
        threatSays = builder.comment("Lines said to a player entering a band, as band=message entries [Default=[]]").defineListAllowEmpty("threatSays", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockReplacements = builder.comment("Blocks swapped out of chunks as they load, written as block=block with an optional state on either side, such as minecraft:andesite=minecraft:stone or minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Every chunk is done once, new ones included [Default=[]]").defineListAllowEmpty("blockReplacements", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockReplacementDimensions = builder.comment("Dimensions block replacement applies to, by id. Empty means every dimension [Default=[]]").defineListAllowEmpty("blockReplacementDimensions", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockReplacementDimensionsAreBlacklist = builder.comment("On, block replacement skips these dimensions. Off, it applies only to them [Default=false]").define("blockReplacementDimensionsAreBlacklist", false);
        blockReplacementMinHeight = builder.comment("Lowest y block replacement looks at [Default=-64]").defineInRange("blockReplacementMinHeight", -64, -2032, 2031);
        blockReplacementMaxHeight = builder.comment("Highest y block replacement looks at [Default=319]").defineInRange("blockReplacementMaxHeight", 319, -2032, 2031);
        blockReplacementKey = builder.comment("Change this to make every chunk go through block replacement again [Default=0000]").define("blockReplacementKey", "0000");
        logBlockReplacements = builder.comment("Log the first time each replacement is made, and a total when a world catches up [Default=true]").define("logBlockReplacements", true);
        blockWorldGenerators = builder.comment("Stop every other mod from generating anything through the placed features it adds to biomes: its ores, trees, lakes, rocks and the rest. Only the mods in generatorWhitelist still generate. Minecraft's own features and this mod's own pack generation are never blocked [Default=false]").define("blockWorldGenerators", false);
        generatorWhitelist = builder.comment("Mod ids allowed to generate while blockWorldGenerators is on [Default=[minecraft]]").defineListAllowEmpty("generatorWhitelist", List.of("minecraft"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockedGenerators = builder.comment("Mod ids, or parts of a placed feature id such as slime_island, blocked outright whatever the whitelist says [Default=[]]").defineListAllowEmpty("blockedGenerators", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        generatorTypes = builder.comment("World generation types blocked outright, whatever the whitelist says. One of ores, structures, flora, lakes, terrain or unknown. Types are worked out from the placed feature id, so use generatorTypeMap for the ones that guess wrong [Default=[]]").defineListAllowEmpty("generatorTypes", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        generatorTypesAreBlacklist = builder.comment("On, the types in generatorTypes are blocked. Off, only those types generate and everything else is blocked [Default=true]").define("generatorTypesAreBlacklist", true);
        generatorTypeMap = builder.comment("Types for placed features the id does not describe, written as pattern=type, where pattern is a mod id or part of a placed feature id. Checked before the built in patterns [Default=[]]").defineListAllowEmpty("generatorTypeMap", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockGeneratorDimensions = builder.comment("Dimensions world generation blocking applies to, by id. Empty means every dimension [Default=[minecraft:overworld]]").defineListAllowEmpty("blockGeneratorDimensions", List.of("minecraft:overworld"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockGeneratorDimensionsAreBlacklist = builder.comment("On, generation blocking skips these dimensions. Off, it applies only to them [Default=false]").define("blockGeneratorDimensionsAreBlacklist", false);
        logBlockedGenerators = builder.comment("Log the first time each mod and placed feature is blocked, so you can see what to whitelist [Default=true]").define("logBlockedGenerators", true);
        builder.pop();
    }

    public String villageSewerBlock() { return ConfigCore.text(villageSewerBlock); }

    public int villageSewerDepth() { return ConfigCore.number(villageSewerDepth); }

    public int villageSewerHeight() { return ConfigCore.number(villageSewerHeight); }

    public int villageSewerWidth() { return ConfigCore.number(villageSewerWidth); }

    public String villageSewerWaterBlock() { return ConfigCore.text(villageSewerWaterBlock); }

    public String villageSewerWalkBlock() { return ConfigCore.text(villageSewerWalkBlock); }

    public String villageSewerLightBlock() { return ConfigCore.text(villageSewerLightBlock); }

    public int villageSewerLightRun() { return ConfigCore.number(villageSewerLightRun); }

    public String villageSewerLadderBlock() { return ConfigCore.text(villageSewerLadderBlock); }

    public String villageSewerCoverBlock() { return ConfigCore.text(villageSewerCoverBlock); }

    public String villageSewerMossBlock() { return ConfigCore.text(villageSewerMossBlock); }

    public int villageSewerMossChance() { return ConfigCore.number(villageSewerMossChance); }

    public String villageSewerVineBlock() { return ConfigCore.text(villageSewerVineBlock); }

    public int villageSewerVineChance() { return ConfigCore.number(villageSewerVineChance); }

    public boolean villageSewerWellEntrance() { return ConfigCore.flag(villageSewerWellEntrance); }

    public List<String> worldGravity() { return ConfigCore.strings(worldGravity); }

    public List<String> worldFallDamage() { return ConfigCore.strings(worldFallDamage); }

    public List<String> worldJumpStrength() { return ConfigCore.strings(worldJumpStrength); }

    public List<String> worldTerminalVelocity() { return ConfigCore.strings(worldTerminalVelocity); }

    public List<String> weatherCeiling() { return ConfigCore.strings(weatherCeiling); }

    public List<String> cloudHeight() { return ConfigCore.strings(cloudHeight); }

    public List<String> worldBelow() { return ConfigCore.strings(worldBelow); }

    public List<String> worldAbove() { return ConfigCore.strings(worldAbove); }

    public boolean worldSeamEntities() { return ConfigCore.flag(worldSeamEntities); }

    public boolean worldSeamBedrock() { return ConfigCore.flag(worldSeamBedrock); }

    public List<String> threatItems() { return ConfigCore.strings(threatItems); }

    public List<String> threatLevels() { return ConfigCore.strings(threatLevels); }

    public int threatMost() { return ConfigCore.number(threatMost); }

    public float threatSpawnRate() { return ConfigCore.decimal(threatSpawnRate); }

    public float threatNotice() { return ConfigCore.decimal(threatNotice); }

    public List<String> threatSays() { return ConfigCore.strings(threatSays); }

    public List<String> blockReplacements() { return ConfigCore.strings(blockReplacements); }

    public List<String> blockReplacementDimensions() { return ConfigCore.strings(blockReplacementDimensions); }

    public boolean blockReplacementDimensionsAreBlacklist() { return ConfigCore.flag(blockReplacementDimensionsAreBlacklist); }

    public int blockReplacementMinHeight() { return ConfigCore.number(blockReplacementMinHeight); }

    public int blockReplacementMaxHeight() { return ConfigCore.number(blockReplacementMaxHeight); }

    public String blockReplacementKey() { return ConfigCore.text(blockReplacementKey); }

    public boolean logBlockReplacements() { return ConfigCore.flag(logBlockReplacements); }

    public boolean blockWorldGenerators() { return ConfigCore.flag(blockWorldGenerators); }

    public List<String> generatorWhitelist() { return ConfigCore.strings(generatorWhitelist); }

    public List<String> blockedGenerators() { return ConfigCore.strings(blockedGenerators); }

    public List<String> generatorTypes() { return ConfigCore.strings(generatorTypes); }

    public boolean generatorTypesAreBlacklist() { return ConfigCore.flag(generatorTypesAreBlacklist); }

    public List<String> generatorTypeMap() { return ConfigCore.strings(generatorTypeMap); }

    public List<String> blockGeneratorDimensions() { return ConfigCore.strings(blockGeneratorDimensions); }

    public boolean blockGeneratorDimensionsAreBlacklist() { return ConfigCore.flag(blockGeneratorDimensionsAreBlacklist); }

    public boolean logBlockedGenerators() { return ConfigCore.flag(logBlockedGenerators); }
}
