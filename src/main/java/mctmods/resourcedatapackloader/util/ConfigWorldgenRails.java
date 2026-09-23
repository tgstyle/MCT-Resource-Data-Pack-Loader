package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public abstract class ConfigWorldgenRails extends ConfigWorldgenStreets {
    private final ForgeConfigSpec.IntValue villageRailLines;
    private final ForgeConfigSpec.IntValue villageRailSpacing;
    private final ForgeConfigSpec.ConfigValue<String> villageRailDirection;
    private final ForgeConfigSpec.IntValue villageRailWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageRailBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailTrackSeat;
    private final ForgeConfigSpec.ConfigValue<String> villageRailBedBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailTieBlock;
    private final ForgeConfigSpec.IntValue villageRailTieRun;
    private final ForgeConfigSpec.IntValue villageRailTracks;
    private final ForgeConfigSpec.IntValue villageRailTrackGap;
    private final ForgeConfigSpec.ConfigValue<String> villageRailShoulderBlock;
    private final ForgeConfigSpec.IntValue villageRailShoulderWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageRailPowerBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailPowerBase;
    private final ForgeConfigSpec.IntValue villageRailPowerRun;
    private final ForgeConfigSpec.IntValue villageRailClimb;
    private final ForgeConfigSpec.IntValue villageRailTail;
    private final ForgeConfigSpec.ConfigValue<String> villageRailSupportBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailDeckBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailBarrierBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailBridgeFrameBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageRailBridgeFrameTopBlock;
    private final ForgeConfigSpec.IntValue villageRailBridgeFrameHeight;
    private final ForgeConfigSpec.IntValue villageRailBridgeFrameRun;
    private final ForgeConfigSpec.IntValue villageRailBridgeFrameLeast;
    private final ForgeConfigSpec.ConfigValue<String> villageRailTunnelBlock;
    private final ForgeConfigSpec.IntValue villageRailTunnelDepth;
    private final ForgeConfigSpec.ConfigValue<String> villageRailTunnelLightBlock;
    private final ForgeConfigSpec.IntValue villageRailTunnelLightRun;
    private final ForgeConfigSpec.BooleanValue villageRailLinks;
    private final ForgeConfigSpec.IntValue villageRailLinkLeast;
    private final ForgeConfigSpec.IntValue villageRailLinkMost;
    private final ForgeConfigSpec.IntValue villageRailLinkBridgeMost;
    private final ForgeConfigSpec.IntValue villageRailLinkTunnelMost;
    private final ForgeConfigSpec.ConfigValue<String> villageRailLinkStation;
    private final ForgeConfigSpec.IntValue villageRailLinkStationLength;
    private final ForgeConfigSpec.IntValue villageRailLinkPlatformWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageRailLinkPlatformBlock;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageWellStructure;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathDeadEnds;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathPiers;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathPierCargo;
    private final ForgeConfigSpec.ConfigValue<String> villagePathPierLoot;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathIntersects;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageDecor;
    private final ForgeConfigSpec.IntValue villageSubwayLines;
    private final ForgeConfigSpec.IntValue villageSubwayDepth;
    private final ForgeConfigSpec.IntValue villageSubwaySpacing;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayDirection;
    private final ForgeConfigSpec.IntValue villageSubwayWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayTrackSeat;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayBedBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayTieBlock;
    private final ForgeConfigSpec.IntValue villageSubwayTieRun;
    private final ForgeConfigSpec.IntValue villageSubwayTracks;
    private final ForgeConfigSpec.IntValue villageSubwayTrackGap;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayShoulderBlock;
    private final ForgeConfigSpec.IntValue villageSubwayShoulderWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayPowerBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayPowerBase;
    private final ForgeConfigSpec.IntValue villageSubwayPowerRun;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayTunnelBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayTunnelLightBlock;
    private final ForgeConfigSpec.IntValue villageSubwayTunnelLightRun;
    private final ForgeConfigSpec.IntValue villageSubwayClimb;
    private final ForgeConfigSpec.IntValue villageSubwayTail;
    private final ForgeConfigSpec.IntValue villageSubwayStationLength;
    private final ForgeConfigSpec.IntValue villageSubwayStationRun;
    private final ForgeConfigSpec.IntValue villageSubwayPlatformWidth;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayPlatformBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayRailingBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayBenchBlock;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayBenchEndBlock;
    private final ForgeConfigSpec.IntValue villageSubwayBenchLength;
    private final ForgeConfigSpec.IntValue villageSubwaySurfaces;
    private final ForgeConfigSpec.ConfigValue<String> villageSubwayStation;
    private final ForgeConfigSpec.IntValue villageSubwayStationFoot;
    private final ForgeConfigSpec.IntValue villageSubwayStationRepeat;

    ConfigWorldgenRails(ForgeConfigSpec.Builder builder) {
        super(builder);
        villageRailLines = builder.comment("How many railway lines run through a city, laid before any street so the town grows around them: the first clear of the plots around the city's first well, the rest to either side of it at least villageRailSpacing apart, each running the length of the city and villageRailTail beyond it and stopping short of any other city in its way. At villageCitySpacing 1 every district is a city and carries its own. 0 lays none [Default=0]").defineInRange("villageRailLines", 0, 0, Integer.MAX_VALUE);
        villageRailSpacing = builder.comment("The fewest blocks of clear ground between one railway line's bed and the next of the same city. 1 lays them a block apart, which is how a pack builds a yard of parallel lines [Default=48]").defineInRange("villageRailSpacing", 48, 1, Integer.MAX_VALUE);
        villageRailDirection = builder.comment("Which way a city's railway lines run: ew for east to west, ns for north to south, any to roll it per city [Default=any]").define("villageRailDirection", "any");
        villageRailWidth = builder.comment("The least the railbed is, in blocks. 3 carries one track down the middle and 5 carries two; a bed asked for more tracks than this fits widens to hold them, and villageRailShoulderWidth is added outside it [Default=3]").defineInRange("villageRailWidth", 3, 3, Integer.MAX_VALUE);
        villageRailBlock = builder.comment("The track block laid on the bed. Empty lays vanilla rails, which minecarts ride [Default=empty]").define("villageRailBlock", "");
        villageRailTrackSeat = builder.comment("Where the track sits: auto seats a rail block on the bed and sets any other block into the bed surface, on always lays it on the bed, in always sets it flush into the bed [Default=auto]").define("villageRailTrackSeat", "auto");
        villageRailBedBlock = builder.comment("The bed the track lies on. Empty lays gravel [Default=empty]").define("villageRailBedBlock", "");
        villageRailTieBlock = builder.comment("The sleeper laid across the bed every villageRailTieRun rows. Empty lays oak planks [Default=empty]").define("villageRailTieBlock", "");
        villageRailTieRun = builder.comment("How many rows apart the sleepers lie [Default=2]").defineInRange("villageRailTieRun", 2, 1, Integer.MAX_VALUE);
        villageRailTracks = builder.comment("How many tracks the one bed carries, side by side, villageRailTrackGap apart. The bed widens to hold them all. 0 lays one track on a bed under five wide and two on a wider one [Default=0]").defineInRange("villageRailTracks", 0, 0, Integer.MAX_VALUE);
        villageRailTrackGap = builder.comment("How many blocks apart the tracks on a bed sit, center to center. 2, the least allowed, leaves one block of bed between them, which is what keeps them from curving into one another the way touching rails do [Default=2]").defineInRange("villageRailTrackGap", 2, 2, Integer.MAX_VALUE);
        villageRailShoulderBlock = builder.comment("The block dressing the outermost columns of the bed, a maintenance path beside the track, the railway counterpart of a street sidewalk. Empty lays none and leaves the bed its full width [Default=empty]").define("villageRailShoulderBlock", "");
        villageRailShoulderWidth = builder.comment("How many columns wide that shoulder is on each side, added outside villageRailWidth. Needs villageRailShoulderBlock [Default=1]").defineInRange("villageRailShoulderWidth", 1, 0, Integer.MAX_VALUE);
        villageRailPowerBlock = builder.comment("The powered track set into the line every villageRailPowerRun rows. Empty uses a vanilla powered rail; a block that is not a rail is simply laid there [Default=empty]").define("villageRailPowerBlock", "");
        villageRailPowerBase = builder.comment("What sits under a powered track to feed it. Empty uses a redstone block [Default=empty]").define("villageRailPowerBase", "");
        villageRailPowerRun = builder.comment("How many rows apart a powered rail over its base is set into the track, so minecarts keep going. 0 powers none [Default=0]").defineInRange("villageRailPowerRun", 0, 0, Integer.MAX_VALUE);
        villageRailClimb = builder.comment("How many rows a railway line runs level for each block it climbs or falls. 1 grades it as steep as a street [Default=8]").defineInRange("villageRailClimb", 8, 1, Integer.MAX_VALUE);
        villageRailTail = builder.comment("How far a railway line runs on past the last district of the city at either end [Default=48]").defineInRange("villageRailTail", 48, 0, Integer.MAX_VALUE);
        villageRailSupportBlock = builder.comment("The post block under a trestle, where the line runs over water or a drop. Empty uses oak logs [Default=empty]").define("villageRailSupportBlock", "");
        villageRailDeckBlock = builder.comment("The deck a trestle carries the bed on. Empty uses oak planks [Default=empty]").define("villageRailDeckBlock", "");
        villageRailBarrierBlock = builder.comment("Barriers stood along both edges of a trestle deck. Empty stands none [Default=empty]").define("villageRailBarrierBlock", "");
        villageRailBridgeFrameBlock = builder.comment("The block an overhead frame over a long trestle is built from: a post up each side of the deck and a beam across the top. Empty builds none [Default=empty]").define("villageRailBridgeFrameBlock", "");
        villageRailBridgeFrameTopBlock = builder.comment("The block the beam across the top of that frame is made of. Empty uses villageRailBridgeFrameBlock [Default=empty]").define("villageRailBridgeFrameTopBlock", "");
        villageRailBridgeFrameHeight = builder.comment("How many blocks of clear headroom the frame leaves over the deck. The beam lies one block above that [Default=4]").defineInRange("villageRailBridgeFrameHeight", 4, 2, Integer.MAX_VALUE);
        villageRailBridgeFrameRun = builder.comment("How many rows apart the frames stand when a trestle is long enough for several. They are spread symmetrically about the middle of the trestle [Default=24]").defineInRange("villageRailBridgeFrameRun", 24, 2, Integer.MAX_VALUE);
        villageRailBridgeFrameLeast = builder.comment("The shortest trestle that gets a frame at all, in rows. A shorter trestle is left plain [Default=24]").defineInRange("villageRailBridgeFrameLeast", 24, 2, Integer.MAX_VALUE);
        villageRailTunnelBlock = builder.comment("The block a railway line is lined with where it bores through a hill instead of climbing it. Empty bores no tunnels [Default=empty]").define("villageRailTunnelBlock", "");
        villageRailTunnelDepth = builder.comment("How much ground must stand over the bed before a stretch is bored as a tunnel rather than climbed. Needs villageRailTunnelBlock [Default=6]").defineInRange("villageRailTunnelDepth", 6, 1, Integer.MAX_VALUE);
        villageRailTunnelLightBlock = builder.comment("The block set into a railway tunnel roof down its center line as a light. Empty lights none [Default=empty]").define("villageRailTunnelLightBlock", "");
        villageRailTunnelLightRun = builder.comment("How many blocks apart those tunnel lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villageRailTunnelLightRun", 8, 1, Integer.MAX_VALUE);
        villageRailLinks = builder.comment("On, a city's first railway line runs on past its tail as a spur to the seam of its city cell and joins a trunk laid along that seam to the neighbor's spur, so neighboring cities whose lines face each other are linked. A city with only subway lines links its first subway line, which climbs out to meet the trunk. Needs villageRailLines or villageSubwayLines [Default=false]").define("villageRailLinks", false);
        villageRailLinkLeast = builder.comment("The shortest link laid, spur plus trunk plus spur, in blocks. A shorter one is not laid [Default=128]").defineInRange("villageRailLinkLeast", 128, 0, Integer.MAX_VALUE);
        villageRailLinkMost = builder.comment("The longest link laid, spur plus trunk plus spur, in blocks. A longer one is not laid [Default=1024]").defineInRange("villageRailLinkMost", 1024, 0, Integer.MAX_VALUE);
        villageRailLinkBridgeMost = builder.comment("The longest bridge a link may need, in blocks. A link that would cross wider water or a deeper drop is dropped whole rather than half built [Default=96]").defineInRange("villageRailLinkBridgeMost", 96, 0, Integer.MAX_VALUE);
        villageRailLinkTunnelMost = builder.comment("The longest tunnel a link may need, in blocks, where villageRailTunnelBlock bores tunnels. A link that would bore further is dropped whole [Default=192]").defineInRange("villageRailLinkTunnelMost", 192, 0, Integer.MAX_VALUE);
        villageRailLinkStation = builder.comment("The station built on each spur just before it joins the trunk: both lays a platform either side of the line, one lays a single platform on the left of a train arriving at the trunk, none builds none [Default=both]").define("villageRailLinkStation", "both");
        villageRailLinkStationLength = builder.comment("How many rows long a link station's platforms are. 0 builds no stations [Default=16]").defineInRange("villageRailLinkStationLength", 16, 0, Integer.MAX_VALUE);
        villageRailLinkPlatformWidth = builder.comment("How many blocks wide each link station platform is. The railing of villageSubwayRailingBlock stands on the column beyond it and the benches are the villageSubwayBench settings [Default=3]").defineInRange("villageRailLinkPlatformWidth", 3, 0, Integer.MAX_VALUE);
        villageRailLinkPlatformBlock = builder.comment("The block link station platforms are built of. Empty uses stone bricks [Default=empty]").define("villageRailLinkPlatformBlock", "");
        villageWellStructure = builder.comment("Structure files placed as the centerpiece of every plaza, one weighted entry per line written name=weight like mypack:plaza_spire=3, rolled once per plaza. Its lowest layer sits on the plaza floor. Empty, the empty share or a structure that cannot be loaded builds the game's own well [Default=[]]").defineListAllowEmpty("villageWellStructure", List.of(), each -> each instanceof String);
        villagePathDeadEnds = builder.comment("How a street that dead ends is closed off, one entry per line, rolled per end: sidewalk paves the end row with the sidewalk block and barrier stands villagePathBridgeBarrierBlock along it villagePathBridgeBarrierHeight tall; any other entry is ignored. They close only an end that grew no cul-de-sac, a style whose block is not set drops out of the roll, and an alley end takes only barrier. Empty leaves such ends open [Default=[]]").defineListAllowEmpty("villagePathDeadEnds", List.of(), each -> each instanceof String);
        villagePathPiers = builder.comment("Pier styles for a street that dead ends over water: the bridged tail becomes a pier instead of a bridge to nowhere. The styles are railed, pilings and boardwalk; several entries roll one per pier. Empty leaves such a tail a plain bridge [Default=[]]").defineListAllowEmpty("villagePathPiers", List.of(), each -> each instanceof String);
        villagePathPierCargo = builder.comment("Cargo stood along the inside of a pier's rails, as block=weight entries, block=weight,height to stack it, or empty=weight for the share left clear. Every other row rolls the list on each side. Empty leaves piers bare [Default=[]]").defineListAllowEmpty("villagePathPierCargo", List.of(), each -> each instanceof String);
        villagePathPierLoot = builder.comment("The loot table cargo blocks with an inventory are filled from, rolled the first time one is opened. A pack may replace the built-in table by shipping its own loot table at that name. Empty leaves them empty [Default=resourcedatapackloader:chests/pier_cargo]").define("villagePathPierLoot", "resourcedatapackloader:chests/pier_cargo");
        villagePathIntersects = builder.comment("Path intersect designs painted where streets cross, by registry key from a pack's pathintersects folder. One entry paints every crossing alike; several roll one per crossing, weighted by each design. Empty paints nothing [Default=[]]").defineListAllowEmpty("villagePathIntersects", List.of(), each -> each instanceof String);
        villageDecor = builder.comment("Decoration scattered along city streets, as name=weight pairs naming worldgen from a pack, mypack:street_flowers=2. The name empty is the share of spots left bare. Every third block of verge on each side of a street rolls the list. Empty scatters nothing [Default=[]]").defineListAllowEmpty("villageDecor", List.of(), each -> each instanceof String);
        villageSubwayLines = builder.comment("How many underground railway lines a city digs: the first from the city's first well, the rest to either side at least villageSubwaySpacing apart, each slid onto the nearest street within that spacing so it runs under a road. 0 digs none and rolls nothing, so the city is laid exactly as it would be without them [Default=0]").defineInRange("villageSubwayLines", 0, 0, Integer.MAX_VALUE);
        villageSubwayDepth = builder.comment("How far under the surface a subway's bed sits. The line is graded from the ground above it, so it follows the land at that depth rather than running level [Default=24]").defineInRange("villageSubwayDepth", 24, 6, Integer.MAX_VALUE);
        villageSubwaySpacing = builder.comment("How far apart a city's subway lines are kept from one another [Default=64]").defineInRange("villageSubwaySpacing", 64, 1, Integer.MAX_VALUE);
        villageSubwayDirection = builder.comment("Which way subway lines run: ew for east to west, ns for north to south, or any to roll per city [Default=any]").define("villageSubwayDirection", "any");
        villageSubwayWidth = builder.comment("How wide the bed is, before shoulders [Default=3]").defineInRange("villageSubwayWidth", 3, 3, Integer.MAX_VALUE);
        villageSubwayBlock = builder.comment("The track block. Empty lays vanilla rail [Default=empty]").define("villageSubwayBlock", "");
        villageSubwayTrackSeat = builder.comment("Whether the track sits on the bed, in it, or auto to let the block decide [Default=auto]").define("villageSubwayTrackSeat", "auto");
        villageSubwayBedBlock = builder.comment("The block the bed is made of. Empty uses gravel [Default=empty]").define("villageSubwayBedBlock", "");
        villageSubwayTieBlock = builder.comment("The block laid across the bed as sleepers. Empty uses planks [Default=empty]").define("villageSubwayTieBlock", "");
        villageSubwayTieRun = builder.comment("How many blocks apart the sleepers sit [Default=2]").defineInRange("villageSubwayTieRun", 2, 1, Integer.MAX_VALUE);
        villageSubwayTracks = builder.comment("How many parallel tracks the bed carries. 0 takes as many as the width allows [Default=0]").defineInRange("villageSubwayTracks", 0, 0, Integer.MAX_VALUE);
        villageSubwayTrackGap = builder.comment("How far apart parallel tracks sit [Default=2]").defineInRange("villageSubwayTrackGap", 2, 2, Integer.MAX_VALUE);
        villageSubwayShoulderBlock = builder.comment("The block either side of the bed. Empty leaves no shoulder [Default=empty]").define("villageSubwayShoulderBlock", "");
        villageSubwayShoulderWidth = builder.comment("How wide that shoulder is [Default=1]").defineInRange("villageSubwayShoulderWidth", 1, 0, Integer.MAX_VALUE);
        villageSubwayPowerBlock = builder.comment("The powered track block. Empty uses vanilla powered rail [Default=empty]").define("villageSubwayPowerBlock", "");
        villageSubwayPowerBase = builder.comment("The block set under a powered track to drive it. Empty uses a redstone block [Default=empty]").define("villageSubwayPowerBase", "");
        villageSubwayPowerRun = builder.comment("How many blocks apart the powered tracks sit. 0 lays none [Default=0]").defineInRange("villageSubwayPowerRun", 0, 0, Integer.MAX_VALUE);
        villageSubwayTunnelBlock = builder.comment("The block the bore is lined with: the walls either side and the roof over it. Empty digs the bore and its stations unlined [Default=empty]").define("villageSubwayTunnelBlock", "");
        villageSubwayTunnelLightBlock = builder.comment("The block set into the tunnel roof as a light. Empty lights none [Default=empty]").define("villageSubwayTunnelLightBlock", "");
        villageSubwayTunnelLightRun = builder.comment("How many blocks apart those lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villageSubwayTunnelLightRun", 8, 1, Integer.MAX_VALUE);
        villageSubwayClimb = builder.comment("How many blocks a line runs before it may step one block up or down [Default=8]").defineInRange("villageSubwayClimb", 8, 1, Integer.MAX_VALUE);
        villageSubwayTail = builder.comment("How far past the city's own pieces a line runs before it stops [Default=48]").defineInRange("villageSubwayTail", 48, 0, Integer.MAX_VALUE);
        villageSubwayStationLength = builder.comment("How many blocks long a subway station chamber is. 0 builds no stations at all [Default=0]").defineInRange("villageSubwayStationLength", 0, 0, Integer.MAX_VALUE);
        villageSubwayStationRun = builder.comment("How many blocks apart further stations sit along a line, past the one nearest the city's first well. 0 builds only the one at the well [Default=0]").defineInRange("villageSubwayStationRun", 0, 0, Integer.MAX_VALUE);
        villageSubwayPlatformWidth = builder.comment("How far the chamber is opened out either side of the bed to make a platform [Default=3]").defineInRange("villageSubwayPlatformWidth", 3, 0, Integer.MAX_VALUE);
        villageSubwayPlatformBlock = builder.comment("The block the platform is floored with. Empty floors it with the tunnel lining [Default=empty]").define("villageSubwayPlatformBlock", "");
        villageSubwayRailingBlock = builder.comment("The block railed around the head of a station's stairs where they open on the street, so nobody walks into the well. Empty leaves the head unrailed [Default=minecraft:iron_bars]").define("villageSubwayRailingBlock", "minecraft:iron_bars");
        villageSubwayBenchBlock = builder.comment("The seat of the benches set on a station's platform and beside its stair head. A stairs block reads as a bench; any block works. Empty leaves the benches out [Default=minecraft:oak_stairs]").define("villageSubwayBenchBlock", "minecraft:oak_stairs");
        villageSubwayBenchEndBlock = builder.comment("The arms at each end of a station bench. Empty leaves the seat bare at both ends [Default=minecraft:oak_log]").define("villageSubwayBenchEndBlock", "minecraft:oak_log");
        villageSubwayBenchLength = builder.comment("How long a station bench is, arms included. 0 leaves the benches out [Default=5]").defineInRange("villageSubwayBenchLength", 5, 0, 32);
        villageSubwaySurfaces = builder.comment("The chance in a hundred that a subway line climbs to the surface at one end and carries on from there as an ordinary railway, tunnel behind it and open track ahead. The climb takes villageSubwayClimb rows per block, so a deep line spends a long run coming up. 0 keeps every subway buried for its whole length [Default=25]").defineInRange("villageSubwaySurfaces", 25, 0, 100);
        villageSubwayStation = builder.comment("A structure from a pack's structures folder used as the station itself. Lift one out of a world built by hand with #scripts/rdpl-grab-template.py: its solid cells are laid and its air cells are carved, so the shape is the build and not a description of it. A line gets stations only when this names a build that loads: empty, or a name that cannot be loaded, builds no station at all [Default=empty]").define("villageSubwayStation", "");
        villageSubwayStationFoot = builder.comment("How many layers at the foot of a station build are laid once, before the part that repeats. The floor and the doorway out to the platform live here [Default=4]").defineInRange("villageSubwayStationFoot", 4, 0, 64);
        villageSubwayStationRepeat = builder.comment("How many layers of a station build repeat, so one build serves any depth: the shaft grows by whole copies of this band and the corridor absorbs what is left over. It must be a whole turn of the stairs or the flights will not join. 0 never grows the build [Default=12]").defineInRange("villageSubwayStationRepeat", 12, 0, 64);
    }

    public int villageRailLines() { return Config.loaded() ? villageRailLines.get() : 0; }

    public int villageRailSpacing() { return Config.loaded() ? villageRailSpacing.get() : 48; }

    public String villageRailDirection() { return Config.loaded() ? villageRailDirection.get() : "any"; }

    public int villageRailWidth() { return Config.loaded() ? villageRailWidth.get() : 3; }

    public String villageRailBlock() { return Config.loaded() ? villageRailBlock.get() : ""; }

    public String villageRailTrackSeat() { return Config.loaded() ? villageRailTrackSeat.get() : "auto"; }

    public String villageRailBedBlock() { return Config.loaded() ? villageRailBedBlock.get() : ""; }

    public String villageRailTieBlock() { return Config.loaded() ? villageRailTieBlock.get() : ""; }

    public int villageRailTieRun() { return Config.loaded() ? villageRailTieRun.get() : 2; }

    public int villageRailTracks() { return Config.loaded() ? villageRailTracks.get() : 0; }

    public int villageRailTrackGap() { return Config.loaded() ? villageRailTrackGap.get() : 2; }

    public String villageRailShoulderBlock() { return Config.loaded() ? villageRailShoulderBlock.get() : ""; }

    public int villageRailShoulderWidth() { return Config.loaded() ? villageRailShoulderWidth.get() : 1; }

    public String villageRailPowerBlock() { return Config.loaded() ? villageRailPowerBlock.get() : ""; }

    public String villageRailPowerBase() { return Config.loaded() ? villageRailPowerBase.get() : ""; }

    public int villageRailPowerRun() { return Config.loaded() ? villageRailPowerRun.get() : 0; }

    public int villageRailClimb() { return Config.loaded() ? villageRailClimb.get() : 8; }

    public int villageRailTail() { return Config.loaded() ? villageRailTail.get() : 48; }

    public String villageRailSupportBlock() { return Config.loaded() ? villageRailSupportBlock.get() : ""; }

    public String villageRailDeckBlock() { return Config.loaded() ? villageRailDeckBlock.get() : ""; }

    public String villageRailBarrierBlock() { return Config.loaded() ? villageRailBarrierBlock.get() : ""; }

    public String villageRailBridgeFrameBlock() { return Config.loaded() ? villageRailBridgeFrameBlock.get() : ""; }

    public String villageRailBridgeFrameTopBlock() { return Config.loaded() ? villageRailBridgeFrameTopBlock.get() : ""; }

    public int villageRailBridgeFrameHeight() { return Config.loaded() ? villageRailBridgeFrameHeight.get() : 4; }

    public int villageRailBridgeFrameRun() { return Config.loaded() ? villageRailBridgeFrameRun.get() : 24; }

    public int villageRailBridgeFrameLeast() { return Config.loaded() ? villageRailBridgeFrameLeast.get() : 24; }

    public String villageRailTunnelBlock() { return Config.loaded() ? villageRailTunnelBlock.get() : ""; }

    public int villageRailTunnelDepth() { return Config.loaded() ? villageRailTunnelDepth.get() : 6; }

    public String villageRailTunnelLightBlock() { return Config.loaded() ? villageRailTunnelLightBlock.get() : ""; }

    public int villageRailTunnelLightRun() { return Config.loaded() ? villageRailTunnelLightRun.get() : 8; }

    public boolean villageRailLinks() { return Config.loaded() && villageRailLinks.get(); }

    public int villageRailLinkLeast() { return Config.loaded() ? villageRailLinkLeast.get() : 128; }

    public int villageRailLinkMost() { return Config.loaded() ? villageRailLinkMost.get() : 1024; }

    public int villageRailLinkBridgeMost() { return Config.loaded() ? villageRailLinkBridgeMost.get() : 96; }

    public int villageRailLinkTunnelMost() { return Config.loaded() ? villageRailLinkTunnelMost.get() : 192; }

    public String villageRailLinkStation() { return Config.loaded() ? villageRailLinkStation.get() : "both"; }

    public int villageRailLinkStationLength() { return Config.loaded() ? villageRailLinkStationLength.get() : 16; }

    public int villageRailLinkPlatformWidth() { return Config.loaded() ? villageRailLinkPlatformWidth.get() : 3; }

    public String villageRailLinkPlatformBlock() { return Config.loaded() ? villageRailLinkPlatformBlock.get() : ""; }

    public List<String> villageWellStructure() { return Config.loaded() ? List.copyOf(villageWellStructure.get()) : List.of(); }

    public List<String> villagePathDeadEnds() { return Config.loaded() ? List.copyOf(villagePathDeadEnds.get()) : List.of(); }

    public List<String> villagePathPiers() { return Config.loaded() ? List.copyOf(villagePathPiers.get()) : List.of(); }

    public List<String> villagePathPierCargo() { return Config.loaded() ? List.copyOf(villagePathPierCargo.get()) : List.of(); }

    public String villagePathPierLoot() { return Config.loaded() ? villagePathPierLoot.get() : "resourcedatapackloader:chests/pier_cargo"; }

    public List<String> villagePathIntersects() { return Config.loaded() ? List.copyOf(villagePathIntersects.get()) : List.of(); }

    public List<String> villageDecor() { return Config.loaded() ? List.copyOf(villageDecor.get()) : List.of(); }

    public int villageSubwayLines() { return Config.loaded() ? villageSubwayLines.get() : 0; }

    public int villageSubwayDepth() { return Config.loaded() ? villageSubwayDepth.get() : 24; }

    public int villageSubwaySpacing() { return Config.loaded() ? villageSubwaySpacing.get() : 64; }

    public String villageSubwayDirection() { return Config.loaded() ? villageSubwayDirection.get() : "any"; }

    public int villageSubwayWidth() { return Config.loaded() ? villageSubwayWidth.get() : 3; }

    public String villageSubwayBlock() { return Config.loaded() ? villageSubwayBlock.get() : ""; }

    public String villageSubwayTrackSeat() { return Config.loaded() ? villageSubwayTrackSeat.get() : "auto"; }

    public String villageSubwayBedBlock() { return Config.loaded() ? villageSubwayBedBlock.get() : ""; }

    public String villageSubwayTieBlock() { return Config.loaded() ? villageSubwayTieBlock.get() : ""; }

    public int villageSubwayTieRun() { return Config.loaded() ? villageSubwayTieRun.get() : 2; }

    public int villageSubwayTracks() { return Config.loaded() ? villageSubwayTracks.get() : 0; }

    public int villageSubwayTrackGap() { return Config.loaded() ? villageSubwayTrackGap.get() : 2; }

    public String villageSubwayShoulderBlock() { return Config.loaded() ? villageSubwayShoulderBlock.get() : ""; }

    public int villageSubwayShoulderWidth() { return Config.loaded() ? villageSubwayShoulderWidth.get() : 1; }

    public String villageSubwayPowerBlock() { return Config.loaded() ? villageSubwayPowerBlock.get() : ""; }

    public String villageSubwayPowerBase() { return Config.loaded() ? villageSubwayPowerBase.get() : ""; }

    public int villageSubwayPowerRun() { return Config.loaded() ? villageSubwayPowerRun.get() : 0; }

    public String villageSubwayTunnelBlock() { return Config.loaded() ? villageSubwayTunnelBlock.get() : ""; }

    public String villageSubwayTunnelLightBlock() { return Config.loaded() ? villageSubwayTunnelLightBlock.get() : ""; }

    public int villageSubwayTunnelLightRun() { return Config.loaded() ? villageSubwayTunnelLightRun.get() : 8; }

    public int villageSubwayClimb() { return Config.loaded() ? villageSubwayClimb.get() : 8; }

    public int villageSubwayTail() { return Config.loaded() ? villageSubwayTail.get() : 48; }

    public int villageSubwayStationLength() { return Config.loaded() ? villageSubwayStationLength.get() : 0; }

    public int villageSubwayStationRun() { return Config.loaded() ? villageSubwayStationRun.get() : 0; }

    public int villageSubwayPlatformWidth() { return Config.loaded() ? villageSubwayPlatformWidth.get() : 3; }

    public String villageSubwayPlatformBlock() { return Config.loaded() ? villageSubwayPlatformBlock.get() : ""; }

    public String villageSubwayRailingBlock() { return Config.loaded() ? villageSubwayRailingBlock.get() : "minecraft:iron_bars"; }

    public String villageSubwayBenchBlock() { return Config.loaded() ? villageSubwayBenchBlock.get() : "minecraft:oak_stairs"; }

    public String villageSubwayBenchEndBlock() { return Config.loaded() ? villageSubwayBenchEndBlock.get() : "minecraft:oak_log"; }

    public int villageSubwayBenchLength() { return Config.loaded() ? villageSubwayBenchLength.get() : 5; }

    public int villageSubwaySurfaces() { return Config.loaded() ? villageSubwaySurfaces.get() : 25; }

    public String villageSubwayStation() { return Config.loaded() ? villageSubwayStation.get() : ""; }

    public int villageSubwayStationFoot() { return Config.loaded() ? villageSubwayStationFoot.get() : 4; }

    public int villageSubwayStationRepeat() { return Config.loaded() ? villageSubwayStationRepeat.get() : 12; }
}
