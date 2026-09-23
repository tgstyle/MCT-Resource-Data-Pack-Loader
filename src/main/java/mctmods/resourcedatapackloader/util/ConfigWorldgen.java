package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class ConfigWorldgen extends ConfigWorldgenRails {
    private final ForgeConfigSpec.ConfigValue<String> villageSewerBlock;
    private final ForgeConfigSpec.IntValue villageSewerDepth;
    private final ForgeConfigSpec.IntValue villageSewerHeight;
    private final ForgeConfigSpec.IntValue villageSewerWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerWaterBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerWalkBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerLightBlock;
    private final ForgeConfigSpec.IntValue villageSewerLightRun;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerLadderBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerCoverBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerMossBlock;
    private final ForgeConfigSpec.IntValue villageSewerMossChance;
    private final ForgeConfigSpec.ConfigValue<String> villageSewerVineBlock;
    private final ForgeConfigSpec.IntValue villageSewerVineChance;
    private final ForgeConfigSpec.BooleanValue villageSewerWellEntrance;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldGravity;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldFallDamage;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldJumpStrength;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldTerminalVelocity;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> weatherCeiling;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> cloudHeight;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldBelow;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldAbove;
    private final ForgeConfigSpec.BooleanValue worldSeamEntities;
    private final ForgeConfigSpec.BooleanValue worldSeamBedrock;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> threatItems;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> threatLevels;
    private final ForgeConfigSpec.IntValue threatMost;
    private final ForgeConfigSpec.DoubleValue threatSpawnRate;
    private final ForgeConfigSpec.DoubleValue threatNotice;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> threatSays;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockReplacements;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockReplacementDimensions;
    private final ForgeConfigSpec.BooleanValue blockReplacementDimensionsAreBlacklist;
    private final ForgeConfigSpec.IntValue blockReplacementMinHeight;
    private final ForgeConfigSpec.IntValue blockReplacementMaxHeight;
    private final ForgeConfigSpec.ConfigValue<String> blockReplacementKey;
    private final ForgeConfigSpec.BooleanValue logBlockReplacements;
    private final ForgeConfigSpec.BooleanValue blockWorldGenerators;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> generatorWhitelist;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockedGenerators;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> generatorTypes;
    private final ForgeConfigSpec.BooleanValue generatorTypesAreBlacklist;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> generatorTypeMap;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockGeneratorDimensions;
    private final ForgeConfigSpec.BooleanValue blockGeneratorDimensionsAreBlacklist;
    private final ForgeConfigSpec.BooleanValue logBlockedGenerators;

    ConfigWorldgen(ForgeConfigSpec.Builder builder) {
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
        worldGravity = builder.comment("Scale gravity, as a multiplier of vanilla where 1.0 is unchanged and 0.17 is moon-like. Covers players, mobs, dropped items, falling blocks, arrows, thrown things, TNT and experience orbs. A bare value covers every dimension, and an entry written as dimension=value covers that dimension alone and wins over the bare one. Empty leaves gravity alone [Default=[]]").defineListAllowEmpty("worldGravity", List.of(), each -> each instanceof String);
        worldFallDamage = builder.comment("Scale fall damage the same way, 0.5 halving it and 2.0 doubling it [Default=[]]").defineListAllowEmpty("worldFallDamage", List.of(), each -> each instanceof String);
        worldJumpStrength = builder.comment("Scale jump strength the same way, 1.5 jumping half again as high [Default=[]]").defineListAllowEmpty("worldJumpStrength", List.of(), each -> each instanceof String);
        worldTerminalVelocity = builder.comment("Scale the fastest a mob or player falls the same way, 0.5 falling at half vanilla's top speed [Default=[]]").defineListAllowEmpty("worldTerminalVelocity", List.of(), each -> each instanceof String);
        weatherCeiling = builder.comment("Highest y rain and snow reach, as dimension=y entries. A bare number covers every dimension. Above it there is no rainfall, no snow build up and nothing drawn. Empty means no ceiling [Default=[]]").defineListAllowEmpty("weatherCeiling", List.of(), each -> each instanceof String);
        cloudHeight = builder.comment("The y clouds are drawn at, as dimension=y entries. A bare number covers every dimension. Empty keeps the game's own cloud height, 192 in the overworld [Default=[]]").defineListAllowEmpty("cloudHeight", List.of(), each -> each instanceof String);
        worldBelow = builder.comment("Stack another dimension under this one: falling out of the bottom of the world carries you into the named dimension, arriving under its ceiling at the same x and z, still falling. Entries are written as dimension=target, such as minecraft:overworld=minecraft:the_nether to hang the nether under the overworld; a bare id covers every dimension. Digging through needs the floor's bedrock left out, which worldSeamBedrock decides. Empty means the floor stays the floor [Default=[]]").defineListAllowEmpty("worldBelow", List.of(), each -> each instanceof String);
        worldAbove = builder.comment("The same for the ceiling: rising past the top of the world carries you into the named dimension, arriving above its floor. Written the same way as worldBelow [Default=[]]").defineListAllowEmpty("worldAbove", List.of(), each -> each instanceof String);
        worldSeamEntities = builder.comment("Whether dropped items, mobs and other entities ride the world seams too, or only players. Riders and mounts cross one at a time [Default=true]").define("worldSeamEntities", true);
        worldSeamBedrock = builder.comment("Keep the bedrock at a seam boundary anyway. Off, a dimension whose floor or ceiling carries a worldBelow or worldAbove seam generates no bedrock there, so the way through can be dug. Already generated chunks keep whatever they have [Default=false]").define("worldSeamBedrock", false);
        threatItems = builder.comment("Items that raise a player's threat level, as item=level,count entries with an optional ,each or ,batch at the end, e.g. minecraft:diamond_sword=5,1 or minecraft:diamond=1,16,batch. Each, the default, adds the level for every one held, counting no more than count of them; batch adds the level once for every count held. A count above the item's stack size is cut to the stack size. Every loaded entity holding items is a carrier: a player's main inventory, armor and off hand, a dropped stack, anything with an item inventory such as a chest mule or a chest minecart, and the held items and armor of other mobs. Empty turns the threat level off [Default=[]]").defineListAllowEmpty("threatItems", List.of(), each -> each instanceof String);
        threatLevels = builder.comment("Rising scores that open each threat band, e.g. 5, 15, 40 for three bands. A player below the first is in band 0. Empty turns the threat level off [Default=[]]").defineListAllowEmpty("threatLevels", List.of(), each -> each instanceof String);
        threatMost = builder.comment("The highest score a carrier can reach, -1 for no cap [Default=-1]").defineInRange("threatMost", -1, -1, 100000);
        threatSpawnRate = builder.comment("Multiplied into the hostile spawn rate near carriers in the top band, scaled down through the lower bands. 1.0 changes nothing, 2.0 doubles spawns at the top [Default=1.0]").defineInRange("threatSpawnRate", 1.0D, 0.0D, 8.0D);
        threatNotice = builder.comment("How many blocks farther hostile mobs notice a carrier in the top band, scaled down through the lower bands. 0 changes nothing [Default=0.0]").defineInRange("threatNotice", 0.0D, 0.0D, 64.0D);
        threatSays = builder.comment("Lines said to a player entering a band, as band=message entries [Default=[]]").defineListAllowEmpty("threatSays", List.of(), each -> each instanceof String);
        blockReplacements = builder.comment("Blocks swapped out of chunks as they load, written as block=block with an optional state on either side, such as minecraft:andesite=minecraft:stone or minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Every chunk is done once, new ones included [Default=[]]").defineListAllowEmpty("blockReplacements", List.of(), each -> each instanceof String);
        blockReplacementDimensions = builder.comment("Dimensions block replacement applies to, by id. Empty means every dimension [Default=[]]").defineListAllowEmpty("blockReplacementDimensions", List.of(), each -> each instanceof String);
        blockReplacementDimensionsAreBlacklist = builder.comment("On, block replacement skips these dimensions. Off, it applies only to them [Default=false]").define("blockReplacementDimensionsAreBlacklist", false);
        blockReplacementMinHeight = builder.comment("Lowest y block replacement looks at [Default=-64]").defineInRange("blockReplacementMinHeight", -64, -2032, 2031);
        blockReplacementMaxHeight = builder.comment("Highest y block replacement looks at [Default=319]").defineInRange("blockReplacementMaxHeight", 319, -2032, 2031);
        blockReplacementKey = builder.comment("Change this to make every chunk go through block replacement again [Default=0000]").define("blockReplacementKey", "0000");
        logBlockReplacements = builder.comment("Log the first time each replacement is made, and a total when a world catches up [Default=true]").define("logBlockReplacements", true);
        blockWorldGenerators = builder.comment("Stop every other mod from generating anything through the placed features it adds to biomes: its ores, trees, lakes, rocks and the rest. Only the mods in generatorWhitelist still generate. Minecraft's own features and this mod's own pack generation are never blocked [Default=false]").define("blockWorldGenerators", false);
        generatorWhitelist = builder.comment("Mod ids allowed to generate while blockWorldGenerators is on [Default=[minecraft]]").defineListAllowEmpty("generatorWhitelist", List.of("minecraft"), each -> each instanceof String);
        blockedGenerators = builder.comment("Mod ids, or parts of a placed feature id such as slime_island, blocked outright whatever the whitelist says [Default=[]]").defineListAllowEmpty("blockedGenerators", List.of(), each -> each instanceof String);
        generatorTypes = builder.comment("World generation types blocked outright, whatever the whitelist says. One of ores, structures, flora, lakes, terrain or unknown. Types are worked out from the placed feature id, so use generatorTypeMap for the ones that guess wrong [Default=[]]").defineListAllowEmpty("generatorTypes", List.of(), each -> each instanceof String);
        generatorTypesAreBlacklist = builder.comment("On, the types in generatorTypes are blocked. Off, only those types generate and everything else is blocked [Default=true]").define("generatorTypesAreBlacklist", true);
        generatorTypeMap = builder.comment("Types for placed features the id does not describe, written as pattern=type, where pattern is a mod id or part of a placed feature id. Checked before the built in patterns [Default=[]]").defineListAllowEmpty("generatorTypeMap", List.of(), each -> each instanceof String);
        blockGeneratorDimensions = builder.comment("Dimensions world generation blocking applies to, by id. Empty means every dimension [Default=[minecraft:overworld]]").defineListAllowEmpty("blockGeneratorDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
        blockGeneratorDimensionsAreBlacklist = builder.comment("On, generation blocking skips these dimensions. Off, it applies only to them [Default=false]").define("blockGeneratorDimensionsAreBlacklist", false);
        logBlockedGenerators = builder.comment("Log the first time each mod and placed feature is blocked, so you can see what to whitelist [Default=true]").define("logBlockedGenerators", true);
        builder.pop();
    }

    public String villageSewerBlock() { return Config.loaded() ? villageSewerBlock.get() : ""; }

    public int villageSewerDepth() { return Config.loaded() ? villageSewerDepth.get() : 8; }

    public int villageSewerHeight() { return Config.loaded() ? villageSewerHeight.get() : 3; }

    public int villageSewerWidth() { return Config.loaded() ? villageSewerWidth.get() : 5; }

    public String villageSewerWaterBlock() { return Config.loaded() ? villageSewerWaterBlock.get() : "minecraft:water"; }

    public String villageSewerWalkBlock() { return Config.loaded() ? villageSewerWalkBlock.get() : ""; }

    public String villageSewerLightBlock() { return Config.loaded() ? villageSewerLightBlock.get() : ""; }

    public int villageSewerLightRun() { return Config.loaded() ? villageSewerLightRun.get() : 8; }

    public String villageSewerLadderBlock() { return Config.loaded() ? villageSewerLadderBlock.get() : ""; }

    public String villageSewerCoverBlock() { return Config.loaded() ? villageSewerCoverBlock.get() : ""; }

    public String villageSewerMossBlock() { return Config.loaded() ? villageSewerMossBlock.get() : ""; }

    public int villageSewerMossChance() { return Config.loaded() ? villageSewerMossChance.get() : 25; }

    public String villageSewerVineBlock() { return Config.loaded() ? villageSewerVineBlock.get() : ""; }

    public int villageSewerVineChance() { return Config.loaded() ? villageSewerVineChance.get() : 20; }

    public boolean villageSewerWellEntrance() { return !Config.loaded() || villageSewerWellEntrance.get(); }

    public List<String> worldGravity() { return Config.loaded() ? List.copyOf(worldGravity.get()) : List.of(); }

    public List<String> worldFallDamage() { return Config.loaded() ? List.copyOf(worldFallDamage.get()) : List.of(); }

    public List<String> worldJumpStrength() { return Config.loaded() ? List.copyOf(worldJumpStrength.get()) : List.of(); }

    public List<String> worldTerminalVelocity() { return Config.loaded() ? List.copyOf(worldTerminalVelocity.get()) : List.of(); }

    public List<String> weatherCeiling() { return Config.loaded() ? List.copyOf(weatherCeiling.get()) : List.of(); }

    public List<String> cloudHeight() { return Config.loaded() ? List.copyOf(cloudHeight.get()) : List.of(); }

    public List<String> worldBelow() { return Config.loaded() ? List.copyOf(worldBelow.get()) : ConfigCore.strings("worldgen.worldBelow", List.of()); }

    public List<String> worldAbove() { return Config.loaded() ? List.copyOf(worldAbove.get()) : ConfigCore.strings("worldgen.worldAbove", List.of()); }

    public boolean worldSeamEntities() { return !Config.loaded() || worldSeamEntities.get(); }

    public boolean worldSeamBedrock() { return Config.loaded() ? worldSeamBedrock.get() : ConfigCore.flag("worldgen.worldSeamBedrock", false); }

    public List<String> threatItems() { return Config.loaded() ? List.copyOf(threatItems.get()) : List.of(); }

    public List<String> threatLevels() { return Config.loaded() ? List.copyOf(threatLevels.get()) : List.of(); }

    public int threatMost() { return Config.loaded() ? threatMost.get() : -1; }

    public float threatSpawnRate() { return Config.loaded() ? threatSpawnRate.get().floatValue() : 1.0F; }

    public float threatNotice() { return Config.loaded() ? threatNotice.get().floatValue() : 0.0F; }

    public List<String> threatSays() { return Config.loaded() ? List.copyOf(threatSays.get()) : List.of(); }

    public List<String> blockReplacements() { return Config.loaded() ? List.copyOf(blockReplacements.get()) : List.of(); }

    public List<String> blockReplacementDimensions() { return Config.loaded() ? List.copyOf(blockReplacementDimensions.get()) : List.of(); }

    public boolean blockReplacementDimensionsAreBlacklist() { return Config.loaded() && blockReplacementDimensionsAreBlacklist.get(); }

    public int blockReplacementMinHeight() { return Config.loaded() ? blockReplacementMinHeight.get() : -64; }

    public int blockReplacementMaxHeight() { return Config.loaded() ? blockReplacementMaxHeight.get() : 319; }

    public String blockReplacementKey() { return Config.loaded() ? blockReplacementKey.get() : "0000"; }

    public boolean logBlockReplacements() { return !Config.loaded() || logBlockReplacements.get(); }

    public boolean blockWorldGenerators() { return Config.loaded() ? blockWorldGenerators.get() : ConfigCore.flag("worldgen.blockWorldGenerators", false); }

    public List<String> generatorWhitelist() { return Config.loaded() ? List.copyOf(generatorWhitelist.get()) : ConfigCore.strings("worldgen.generatorWhitelist", List.of("minecraft")); }

    public List<String> blockedGenerators() { return Config.loaded() ? List.copyOf(blockedGenerators.get()) : ConfigCore.strings("worldgen.blockedGenerators", List.of()); }

    public List<String> generatorTypes() { return Config.loaded() ? List.copyOf(generatorTypes.get()) : ConfigCore.strings("worldgen.generatorTypes", List.of()); }

    public boolean generatorTypesAreBlacklist() { return Config.loaded() ? generatorTypesAreBlacklist.get() : ConfigCore.flag("worldgen.generatorTypesAreBlacklist", true); }

    public List<String> generatorTypeMap() { return Config.loaded() ? List.copyOf(generatorTypeMap.get()) : ConfigCore.strings("worldgen.generatorTypeMap", List.of()); }

    public List<String> blockGeneratorDimensions() { return Config.loaded() ? List.copyOf(blockGeneratorDimensions.get()) : ConfigCore.strings("worldgen.blockGeneratorDimensions", List.of("minecraft:overworld")); }

    public boolean blockGeneratorDimensionsAreBlacklist() { return Config.loaded() ? blockGeneratorDimensionsAreBlacklist.get() : ConfigCore.flag("worldgen.blockGeneratorDimensionsAreBlacklist", false); }

    public boolean logBlockedGenerators() { return Config.loaded() ? logBlockedGenerators.get() : ConfigCore.flag("worldgen.logBlockedGenerators", true); }
}
