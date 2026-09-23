package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public abstract class ConfigWorldgenStreets extends ConfigWorldgenWorld {
    private final ForgeConfigSpec.BooleanValue terrainAdaptation;
    private final ForgeConfigSpec.ConfigValue<String> villagePathBlock;
    private final ForgeConfigSpec.IntValue villagePathExtraWidth;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageBlockSizes;
    private final ForgeConfigSpec.IntValue villageCitySpacing;
    private final ForgeConfigSpec.ConfigValue<String> villagePathAlleyBlock;
    private final ForgeConfigSpec.IntValue villagePathAlleyChance;
    private final ForgeConfigSpec.IntValue villagePathMinimumWidth;
    private final ForgeConfigSpec.IntValue villagePathFlatRun;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePieces;
    private final ForgeConfigSpec.BooleanValue villagePiecesAreBlacklist;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageBlocks;
    private final ForgeConfigSpec.IntValue villagePlotsLeast;
    private final ForgeConfigSpec.IntValue villagePlotsMost;
    private final ForgeConfigSpec.BooleanValue villagePlotsBackRow;
    private final ForgeConfigSpec.BooleanValue villageTieStreets;
    private final ForgeConfigSpec.ConfigValue<String> villageLayout;
    private final ForgeConfigSpec.ConfigValue<String> villagePathCenterBlock;
    private final ForgeConfigSpec.IntValue villagePathCenterDash;
    private final ForgeConfigSpec.ConfigValue<String> villagePathLineBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathSidewalkBlock;
    private final ForgeConfigSpec.IntValue villagePathSidewalkWidth;
    private final ForgeConfigSpec.ConfigValue<String> villagePathLampBlock;
    private final ForgeConfigSpec.IntValue villagePathLampHeight;
    private final ForgeConfigSpec.ConfigValue<String> villagePathLampTopBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathLampSideBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathLampStructure;
    private final ForgeConfigSpec.ConfigValue<String> villagePathSupportBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeSidewalkBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeBarrierBlock;
    private final ForgeConfigSpec.IntValue villagePathBridgeBarrierHeight;
    private final ForgeConfigSpec.ConfigValue<String> villagePathVergeBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathVergeWaterBlock;
    private final ForgeConfigSpec.IntValue villagePathBridgeDrop;
    private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeFrameBlock;
    private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeFrameTopBlock;
    private final ForgeConfigSpec.IntValue villagePathBridgeFrameHeight;
    private final ForgeConfigSpec.IntValue villagePathBridgeFrameRun;
    private final ForgeConfigSpec.IntValue villagePathBridgeFrameLeast;
    private final ForgeConfigSpec.ConfigValue<String> villagePathTunnelBlock;
    private final ForgeConfigSpec.IntValue villagePathTunnelDepth;
    private final ForgeConfigSpec.ConfigValue<String> villagePathTunnelLightBlock;
    private final ForgeConfigSpec.IntValue villagePathTunnelLightRun;

    ConfigWorldgenStreets(ForgeConfigSpec.Builder builder) {
        super(builder);
        terrainAdaptation = builder.comment("Lay RDPL's own city streets, seated into the terrain instead of standing on stilts over every dip, and read the villagePath and villageRail options with them. Changes the terrain, so a world made with it on differs from one made without. Cities are seeded as villageCitySpacing sets out, and at 0 none are [Default=false]").define("terrainAdaptation", false);
        villagePathBlock = builder.comment("The block city roads are paved with when terrainAdaptation lays them. Empty paves them with dirt path [Default=empty]").define("villagePathBlock", "");
        villagePathExtraWidth = builder.comment("Extra blocks of road width on each side beyond the usual 3, when terrainAdaptation lays the roads. Widens the streets themselves, so the blocks between them stand back from wide roads [Default=0]").defineInRange("villagePathExtraWidth", 0, 0, Integer.MAX_VALUE);
        villageBlockSizes = builder.comment("How deep the blocks between a city's parallel streets are, one weighted entry per line written size=weight like 32=3, rolled once per district. A district builds no plot wider or deeper than its roll, and a size under 13 counts as 13. Empty uses the largest plot [Default=[]]").defineListAllowEmpty("villageBlockSizes", List.of(), each -> each instanceof String);
        villageCitySpacing = builder.comment("How far apart city districts are seeded, in districts sized from the plots (twice the largest plot, plus a plaza and a street each side, rounded up to 16 blocks, at least 96): one district in every square of this many carries a city center, founded on the square's flattest district in a village biome (or where structureAt city=x,z pins it), from which the city grows ring by ring to villagePlotsLeast plots but never past a quarter of this spacing in any direction, so neighboring cities stay apart. At 1 every district is one, a plaza with the well at its center and streets out of it that join the next district's. 0 seeds none. When a pack does not set it, structureSpacing villages=chunks does [Default=16]").defineInRange("villageCitySpacing", 16, 0, 256);
        villagePathAlleyBlock = builder.comment("The block alleys are laid with. An alley is a street too narrow for lines and sidewalks. Empty lays alleys with the street block [Default=empty]").define("villagePathAlleyBlock", "");
        villagePathAlleyChance = builder.comment("The percent chance a street is laid as an alley rather than at its full width. 0 lays no alleys [Default=0]").defineInRange("villagePathAlleyChance", 0, 0, Integer.MAX_VALUE);
        villagePathMinimumWidth = builder.comment("The narrowest street allowed. A street that would be laid narrower than this is not laid at all, and the district lays out around the gap. 0 never refuses [Default=0]").defineInRange("villagePathMinimumWidth", 0, 0, Integer.MAX_VALUE);
        villagePathFlatRun = builder.comment("Streets hold each grade for at least this many blocks before stepping, anchored to world coordinates so segments agree across pieces. 0 or 1 lets a street step every block [Default=6]").defineInRange("villagePathFlatRun", 6, 0, Integer.MAX_VALUE);
        villagePieces = builder.comment("Village plots named here, one per line, by the full id of a villages file such as mypack:smithy, by its bare name, or by the name of the structure a template plot builds. While villagePiecesAreBlacklist is on, a structure named here is also left empty wherever the game loads it [Default=[]]").defineListAllowEmpty("villagePieces", List.of(), each -> each instanceof String);
        villagePiecesAreBlacklist = builder.comment("On, the plots in villagePieces are blocked. Off, only those plots are built [Default=true]").define("villagePiecesAreBlacklist", true);
        villageBlocks = builder.comment("Blocks village plots are built from, as original=replacement pairs, minecraft:cobblestone=mypack:ruby_brick, either side with an optional state in brackets. A pair may add a chance out of 100, at=block and under=block. Farms and the game's own village houses are ruled, streets, wells, lamps and your template plots are not [Default=[]]").defineListAllowEmpty("villageBlocks", List.of(), each -> each instanceof String);
        villagePlotsLeast = builder.comment("How many plots a city grows to: districts are added ring by ring around its center until they hold at least this many, never more than villagePlotsMost. 0 lays the center district alone [Default=0]").defineInRange("villagePlotsLeast", 0, 0, Integer.MAX_VALUE);
        villagePlotsMost = builder.comment("The most plots a city may hold: growth stops before the district that would pass it, no district seats more, and a district that reaches it leaves out the alleys no plot fronts. 0 sets no ceiling [Default=0]").defineInRange("villagePlotsMost", 0, 0, Integer.MAX_VALUE);
        villagePlotsBackRow = builder.comment("On, a second pass seats a plot directly behind every plot that fronts a street, turned to face it, with the same roll and the same room test, so the inside of a block between two streets is built rather than left bare. Off leaves plots on the street fronts only [Default=true]").define("villagePlotsBackRow", true);
        villageTieStreets = builder.comment("On, a district a drawn city map grows beside it that cannot meet its streets gets a straight tie street laid from one of its street ends to the nearest street it lines up with, when a level and free line exists within 112 blocks. Off, such a district is taken back down [Default=true]").define("villageTieStreets", true);
        villageLayout = builder.comment("A city map laid out instead of planning the district, named like mypack:downtown and read from that pack's citymaps folder. Empty plans the district as usual [Default=empty]").define("villageLayout", "");
        villagePathCenterBlock = builder.comment("The block of the center line down the middle of a city street. Empty draws no center line [Default=empty]").define("villagePathCenterBlock", "");
        villagePathCenterDash = builder.comment("Dashes the center line: N blocks of line, then one of street, anchored to world coordinates so segments continue each other. 0 keeps the line solid [Default=0]").defineInRange("villagePathCenterDash", 0, 0, Integer.MAX_VALUE);
        villagePathLineBlock = builder.comment("The block of the edge lines between street and sidewalk. Empty draws no edge lines [Default=empty]").define("villagePathLineBlock", "");
        villagePathSidewalkBlock = builder.comment("The block sidewalks are laid with, level with the street, outside the edge lines. Empty lays no sidewalks [Default=empty]").define("villagePathSidewalkBlock", "");
        villagePathSidewalkWidth = builder.comment("How many blocks wide each sidewalk is, when villagePathSidewalkBlock is set. A street too narrow to carry its lines and sidewalks is laid bare instead [Default=2]").defineInRange("villagePathSidewalkWidth", 2, 0, Integer.MAX_VALUE);
        villagePathLampBlock = builder.comment("The block a lamp post along a street is built from, stacked villagePathLampHeight tall on the curb, at each end of a street, where another meets it and every 7 to 12 blocks between, on one side. Empty stands no lamp posts [Default=minecraft:oak_fence]").define("villagePathLampBlock", "minecraft:oak_fence");
        villagePathLampHeight = builder.comment("How many blocks tall the lamp post stands before its head [Default=3]").defineInRange("villagePathLampHeight", 3, 1, Integer.MAX_VALUE);
        villagePathLampTopBlock = builder.comment("The head that sits on top of a lamp post. Empty leaves the post bare [Default=minecraft:black_wool]").define("villagePathLampTopBlock", "minecraft:black_wool");
        villagePathLampSideBlock = builder.comment("The light hung on each side of a lamp post head. Empty hangs none [Default=minecraft:torch]").define("villagePathLampSideBlock", "minecraft:torch");
        villagePathLampStructure = builder.comment("A structure file placed as the lamp instead of stacking the lamp blocks, named like mypack:street_lamp, centered on the lamp's spot with its lowest layer on the curb. Empty stacks the lamp blocks [Default=empty]").define("villagePathLampStructure", "");
        villagePathSupportBlock = builder.comment("The surface itself where the ground is bare rock, and the piers and legs under a street over water. Empty keeps vanilla gravel, sandstone in desert cities [Default=empty]").define("villagePathSupportBlock", "");
        villagePathBridgeBlock = builder.comment("The block a street or pier crosses water with. Empty decks it in planks of the village wood: acacia in a savanna village, spruce in a taiga village, oak elsewhere [Default=empty]").define("villagePathBridgeBlock", "");
        villagePathBridgeSidewalkBlock = builder.comment("The block bridge sidewalks are decked with where a street crosses water. Empty keeps the normal sidewalk block on bridges [Default=empty]").define("villagePathBridgeSidewalkBlock", "");
        villagePathBridgeBarrierBlock = builder.comment("The block bridge barriers are built from, stacked along both edges of the deck. None stands where the deck rests on ground. Empty builds no barriers [Default=empty]").define("villagePathBridgeBarrierBlock", "");
        villagePathBridgeBarrierHeight = builder.comment("How many blocks tall the bridge barriers stand [Default=1]").defineInRange("villagePathBridgeBarrierHeight", 1, 1, Integer.MAX_VALUE);
        villagePathVergeBlock = builder.comment("The block the ground beside a street and under a plot is filled with where the city has to make land: the grooves between plots and the fill down to a street across a gap. Empty follows the ground it stands on, sand, terracotta, gravel or dirt with grass on top where it would be dirt [Default=empty]").define("villagePathVergeBlock", "");
        villagePathVergeWaterBlock = builder.comment("The block that same fill becomes where it stands over water, so a verge carried out onto a lake is not a column of dirt, and the block a stone doorstep over water is dressed in [Default=minecraft:oak_planks]").define("villagePathVergeWaterBlock", "minecraft:oak_planks");
        villagePathBridgeDrop = builder.comment("How far a street's grade must stand clear of the ground before the drop under it is bridged rather than filled solid. 0 keeps streets out of the air, bridging water only [Default=0]").defineInRange("villagePathBridgeDrop", 0, 0, Integer.MAX_VALUE);
        villagePathBridgeFrameBlock = builder.comment("The block an overhead frame over a long bridge is built from: a post up each side of the deck and a beam across the top. Empty builds none [Default=empty]").define("villagePathBridgeFrameBlock", "");
        villagePathBridgeFrameTopBlock = builder.comment("The block the beam across the top of that frame is made of. Empty uses villagePathBridgeFrameBlock [Default=empty]").define("villagePathBridgeFrameTopBlock", "");
        villagePathBridgeFrameHeight = builder.comment("How many blocks of clear headroom the frame leaves over the deck. The beam lies one block above that [Default=4]").defineInRange("villagePathBridgeFrameHeight", 4, 1, Integer.MAX_VALUE);
        villagePathBridgeFrameRun = builder.comment("How many rows apart the frames stand when a bridge is long enough for several. They are spread symmetrically about the middle of the bridged run [Default=24]").defineInRange("villagePathBridgeFrameRun", 24, 1, Integer.MAX_VALUE);
        villagePathBridgeFrameLeast = builder.comment("The shortest bridged run that gets a frame at all, in rows. A shorter bridge is left plain [Default=24]").defineInRange("villagePathBridgeFrameLeast", 24, 1, Integer.MAX_VALUE);
        villagePathTunnelBlock = builder.comment("The block a street is lined with where it bores through a hill instead of cutting it open: the walls either side of the bore and the roof over it. Empty bores no tunnels and lets a street climb the hill [Default=empty]").define("villagePathTunnelBlock", "");
        villagePathTunnelDepth = builder.comment("How much ground must stand over the street surface before a stretch is bored as a tunnel rather than climbed. A rise that deep anywhere along it is held level and bored through. Needs villagePathTunnelBlock [Default=10]").defineInRange("villagePathTunnelDepth", 10, 1, Integer.MAX_VALUE);
        villagePathTunnelLightBlock = builder.comment("The block set into a tunnel roof down its center line as a light. Empty lights none [Default=empty]").define("villagePathTunnelLightBlock", "");
        villagePathTunnelLightRun = builder.comment("How many blocks apart the tunnel lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villagePathTunnelLightRun", 8, 1, Integer.MAX_VALUE);
    }

    public boolean terrainAdaptation() { return Config.loaded() && terrainAdaptation.get(); }

    public String villagePathBlock() { return Config.loaded() ? villagePathBlock.get() : ""; }

    public int villagePathExtraWidth() { return Config.loaded() ? villagePathExtraWidth.get() : 0; }

    public List<String> villageBlockSizes() { return Config.loaded() ? List.copyOf(villageBlockSizes.get()) : List.of(); }

    public int villageCitySpacing() { return Config.loaded() ? villageCitySpacing.get() : 16; }

    public String villagePathAlleyBlock() { return Config.loaded() ? villagePathAlleyBlock.get() : ""; }

    public int villagePathAlleyChance() { return Config.loaded() ? villagePathAlleyChance.get() : 0; }

    public int villagePathMinimumWidth() { return Config.loaded() ? villagePathMinimumWidth.get() : 0; }

    public int villagePathFlatRun() { return Config.loaded() ? villagePathFlatRun.get() : 6; }

    public List<String> villagePieces() { return Config.loaded() ? List.copyOf(villagePieces.get()) : List.of(); }

    public boolean villagePiecesAreBlacklist() { return !Config.loaded() || villagePiecesAreBlacklist.get(); }

    public List<String> villageBlocks() { return Config.loaded() ? List.copyOf(villageBlocks.get()) : List.of(); }

    public int villagePlotsLeast() { return Config.loaded() ? villagePlotsLeast.get() : 0; }

    public int villagePlotsMost() { return Config.loaded() ? villagePlotsMost.get() : 0; }

    public boolean villagePlotsBackRow() { return !Config.loaded() || villagePlotsBackRow.get(); }

    public boolean villageTieStreets() { return !Config.loaded() || villageTieStreets.get(); }

    public String villageLayout() { return Config.loaded() ? villageLayout.get() : ""; }

    public String villagePathCenterBlock() { return Config.loaded() ? villagePathCenterBlock.get() : ""; }

    public int villagePathCenterDash() { return Config.loaded() ? villagePathCenterDash.get() : 0; }

    public String villagePathLineBlock() { return Config.loaded() ? villagePathLineBlock.get() : ""; }

    public String villagePathSidewalkBlock() { return Config.loaded() ? villagePathSidewalkBlock.get() : ""; }

    public int villagePathSidewalkWidth() { return Config.loaded() ? villagePathSidewalkWidth.get() : 2; }

    public String villagePathLampBlock() { return Config.loaded() ? villagePathLampBlock.get() : "minecraft:oak_fence"; }

    public int villagePathLampHeight() { return Config.loaded() ? villagePathLampHeight.get() : 3; }

    public String villagePathLampTopBlock() { return Config.loaded() ? villagePathLampTopBlock.get() : "minecraft:black_wool"; }

    public String villagePathLampSideBlock() { return Config.loaded() ? villagePathLampSideBlock.get() : "minecraft:torch"; }

    public String villagePathLampStructure() { return Config.loaded() ? villagePathLampStructure.get() : ""; }

    public String villagePathSupportBlock() { return Config.loaded() ? villagePathSupportBlock.get() : ""; }

    public String villagePathBridgeBlock() { return Config.loaded() ? villagePathBridgeBlock.get() : ""; }

    public String villagePathBridgeSidewalkBlock() { return Config.loaded() ? villagePathBridgeSidewalkBlock.get() : ""; }

    public String villagePathBridgeBarrierBlock() { return Config.loaded() ? villagePathBridgeBarrierBlock.get() : ""; }

    public int villagePathBridgeBarrierHeight() { return Config.loaded() ? villagePathBridgeBarrierHeight.get() : 1; }

    public String villagePathVergeBlock() { return Config.loaded() ? villagePathVergeBlock.get() : ""; }

    public String villagePathVergeWaterBlock() { return Config.loaded() ? villagePathVergeWaterBlock.get() : "minecraft:oak_planks"; }

    public int villagePathBridgeDrop() { return Config.loaded() ? villagePathBridgeDrop.get() : 0; }

    public String villagePathBridgeFrameBlock() { return Config.loaded() ? villagePathBridgeFrameBlock.get() : ""; }

    public String villagePathBridgeFrameTopBlock() { return Config.loaded() ? villagePathBridgeFrameTopBlock.get() : ""; }

    public int villagePathBridgeFrameHeight() { return Config.loaded() ? villagePathBridgeFrameHeight.get() : 4; }

    public int villagePathBridgeFrameRun() { return Config.loaded() ? villagePathBridgeFrameRun.get() : 24; }

    public int villagePathBridgeFrameLeast() { return Config.loaded() ? villagePathBridgeFrameLeast.get() : 24; }

    public String villagePathTunnelBlock() { return Config.loaded() ? villagePathTunnelBlock.get() : ""; }

    public int villagePathTunnelDepth() { return Config.loaded() ? villagePathTunnelDepth.get() : 10; }

    public String villagePathTunnelLightBlock() { return Config.loaded() ? villagePathTunnelLightBlock.get() : ""; }

    public int villagePathTunnelLightRun() { return Config.loaded() ? villagePathTunnelLightRun.get() : 8; }
}
