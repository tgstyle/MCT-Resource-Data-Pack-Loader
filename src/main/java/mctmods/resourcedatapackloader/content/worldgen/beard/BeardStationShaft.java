package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.util.Constants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

final class BeardStationShaft {
    private static final int HEADROOM = 2;
    private static final int LANDING_HEADROOM = HEADROOM + 1;
    private static final int APPROACH_FILL = 8;
    private static final int CLIMB_LEAST = 3;
    private static final int PASSAGE_MOST = 32;
    private static final int BOX_X = 0;
    private static final int BOX_Z = 0;
    private static final int DOOR = 7;
    private static final int HEAD_CLEARANCE = 8;

    private BeardStationShaft() {}

    private static IBlockState stationLight() { return BeardRoads.pathBlock("villageSubwayTunnelLightBlock", Config.worldgen.villageSubwayTunnelLightBlock, Blocks.AIR.getDefaultState()); }

    private static int stationLightRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageSubwayTunnelLightRun", Config.worldgen.villageSubwayTunnelLightRun)); }

    private static int wall(int near, int way) { return way > 0 ? near - 1 : near + BeardStations.WIDE; }

    private static int streetEdge(@Nullable StructureComponent street, boolean alongX, int near, int way) {
        if (street == null) { return wall(near, way); }
        StructureBoundingBox box = street.getBoundingBox();
        return way > 0 ? (alongX ? box.maxZ : box.maxX) + 1 : (alongX ? box.minZ : box.minX) - 1;
    }

    private static boolean deckAt(@Nullable StructureComponent street, int row) {
        if (!(street instanceof IRoadLayout)) { return false; }
        BeardRoads.Grade grade = ((IRoadLayout) street).rdpl$layout();
        return grade != null && grade.bridgedAt(row) && grade.groundAt(row) != Integer.MIN_VALUE;
    }

    static void approach(World world, StructureBoundingBox clip, boolean alongX, int row, int near, int way, int center, int surface, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        int wall = wall(near, way);
        StructureComponent street = BeardStations.streetAlong(ContentBeard.components(), alongX, center, row - 1, row + BeardStations.RUN + 1);
        int edge = streetEdge(street, alongX, near, way);
        int graded = 0;
        int freed = 0;
        for (int along = 0; along <= BeardStations.RUN; along++) {
            boolean deck = deckAt(street, row + along);
            for (int lane = 0; lane < BeardStations.WIDE; lane++) {
                int x = stepX(alongX, row, along, near, lane);
                int z = stepZ(alongX, row, along, near, lane);
                if (clip.isVecInside(at.setPos(x, surface + 1, z))) { freed += BeardBlocks.cutBank(world, at, x, z, surface + 1, surface + LANDING_HEADROOM); }
            }
            for (int across = edge; way > 0 ? across < wall : across > wall; across += way) {
                int x = alongX ? row + along : across;
                int z = alongX ? across : row + along;
                if (!clip.isVecInside(at.setPos(x, surface, z))) { continue; }
                graded += BeardBlocks.cutBank(world, at, x, z, surface + 1, surface + LANDING_HEADROOM);
                if (world.getBlockState(at.setPos(x, surface, z)).getMaterial().isSolid()) { continue; }
                int filled = deck ? 0 : BeardBlocks.fillBank(world, at, x, z, surface, surface - APPROACH_FILL, false);
                if (filled == 0) {
                    world.setBlockState(at.setPos(x, surface, z), linings.pick(world, x, surface, z), BeardStations.WORLDGEN_FLAGS);
                    filled = 1;
                }
                graded += filled;
            }
        }
        if (graded + freed > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded {} block(s) between the street edge at {} and the subway stairwell wall at {} over rows {} to {} and cut {} off the hillside standing over the stairwell mouth, so it is walked into at y {}", graded, edge, wall, row, row + BeardStations.RUN, freed, surface); }
    }

    private static int stationFoot() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationFoot", Config.worldgen.villageSubwayStationFoot)); }

    private static int stationRepeat() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRepeat", Config.worldgen.villageSubwayStationRepeat)); }

    private static int railing(World world, StructureBoundingBox clip, Template held, PlacementSettings how, BlockPos origin, int surface, BlockPos.MutableBlockPos at) {
        BeardRoads.Palette rails = BeardStations.railingBlocks();
        if (rails.first().getBlock() == Blocks.AIR) { return 0; }
        boolean[][] open = topOpening(held);
        int leastRow = Integer.MAX_VALUE;
        for (int x = 0; x < open.length; x++) {
            for (int z = 0; z < open[x].length; z++) {
                if (open[x][z]) { leastRow = Math.min(leastRow, x); }
            }
        }
        if (leastRow == Integer.MAX_VALUE) { return 0; }
        int laid = 0;
        for (int x = leastRow; x < open.length; x++) {
            for (int z = 0; z < open[x].length; z++) {
                if (open[x][z] || !openBeside(open, x, z)) { continue; }
                BlockPos spot = Template.transformedBlockPos(how, new BlockPos(x, 0, z)).add(origin);
                if (!clip.isVecInside(at.setPos(spot.getX(), surface + 1, spot.getZ()))) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid()) { continue; }
                world.setBlockState(at, rails.pick(world, spot.getX(), surface + 1, spot.getZ()), BeardStations.WORLDGEN_FLAGS);
                BeardKeep.holdSpot(spot.getX(), surface + 1, spot.getZ());
                laid++;
            }
        }
        return laid;
    }

    private static boolean[][] topOpening(Template held) {
        BlockPos size = held.getSize();
        boolean[][] open = new boolean[size.getX()][size.getZ()];
        NBTTagCompound saved = held.writeToNBT(new NBTTagCompound());
        NBTTagList palette = saved.getTagList("palette", Constants.NBT.TAG_COMPOUND);
        NBTTagList blocks = saved.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = blocks.getCompoundTagAt(i);
            NBTTagList pos = block.getTagList("pos", Constants.NBT.TAG_INT);
            if (pos.getIntAt(1) != size.getY() - 1) { continue; }
            int x = pos.getIntAt(0);
            int z = pos.getIntAt(2);
            if (x < 0 || z < 0 || x >= size.getX() || z >= size.getZ()) { continue; }
            open[x][z] = !NBTUtil.readBlockState(palette.getCompoundTagAt(block.getInteger("state"))).getMaterial().isSolid();
        }
        return open;
    }

    private static boolean openBeside(boolean[][] open, int x, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int nx = x + dx;
                int nz = z + dz;
                if ((dx != 0 || dz != 0) && nx >= 0 && nz >= 0 && nx < open.length && nz < open[nx].length && open[nx][nz]) { return true; }
            }
        }
        return false;
    }

    static int stamp(World world, StructureBoundingBox clip, Template held, boolean alongX, int way, int center, int bedHalf, int boxLeastX, int boxLeastZ, int level, int surface, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        IBlockState lining = linings.first();
        String named = BeardStations.stationNamed();
        BlockPos size = held.getSize();
        int tall = size.getY();
        Shaft shaft = new Shaft(tall, level, surface);
        int foot = shaft.foot;
        int repeat = shaft.repeat;
        int cap = shaft.cap;
        int copies = shaft.copies;
        int grown = shaft.grown;
        int base = shaft.base;
        PlacementSettings how = turned(alongX, way);
        how.setBoundingBox(clip);
        BlockPos origin = origin(size, how, boxLeastX, boxLeastZ, base);
        int leastX = origin.getX();
        int leastZ = origin.getZ();
        band(world, clip, held, lining, how, leastX, base, leastZ, base, base + foot - 1);
        for (int copy = 0; copy < copies; copy++) {
            int from = base + foot + copy * repeat;
            band(world, clip, held, lining, how, leastX, base + copy * repeat, leastZ, from, from + repeat - 1);
        }
        if (cap > 0) {
            int from = base + foot + copies * repeat;
            band(world, clip, held, lining, how, leastX, base + (copies - 1) * repeat, leastZ, from, from + cap - 1);
        }
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                BlockPos spot = Template.transformedBlockPos(how, new BlockPos(x, 0, z)).add(origin);
                for (int y = level; y <= surface; y++) { BeardKeep.holdSpot(spot.getX(), y, spot.getZ()); }
                for (int y = level; y < base; y++) {
                    if (!clip.isVecInside(at.setPos(spot.getX(), y, spot.getZ()))) { continue; }
                    world.setBlockState(at, lining, BeardStations.WORLDGEN_FLAGS);
                }
            }
        }
        int lit = dividerLamps(world, clip, size.getX(), along -> Template.transformedBlockPos(how, new BlockPos(along, 0, (size.getZ() - 1) / 2)).add(origin), base, base + grown - 1, at);

        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                BlockPos spot = Template.transformedBlockPos(how, new BlockPos(x, 0, z)).add(origin);
                if (!clip.isVecInside(at.setPos(spot.getX(), surface + 1, spot.getZ()))) { continue; }
                BeardBlocks.clearAbove(world, at, spot.getX(), spot.getZ(), surface + 1, surface + HEAD_CLEARANCE);
            }
        }

        int bored = bore(world, clip, alongX, way, mouths(how, origin), center, bedHalf, level + 1, linings, at);
        int railed = railing(world, clip, held, how, origin, surface, at);
        BlockPos seatFrom = Template.transformedBlockPos(how, new BlockPos(DOOR - 3, 0, 1)).add(origin);
        BlockPos seatTo = Template.transformedBlockPos(how, new BlockPos(DOOR + 1, 0, 1)).add(origin);
        boolean seatAlongX = seatFrom.getZ() == seatTo.getZ();
        int seated = BeardStations.bench(world, clip, seatAlongX, Math.min(seatAlongX ? seatFrom.getX() : seatFrom.getZ(), seatAlongX ? seatTo.getX() : seatTo.getZ()),
                           seatAlongX ? seatFrom.getZ() : seatFrom.getX(), surface + 1, BeardStations.awayFrom(alongX, way), at);
        if ((railed + seated) > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The station head is railed with {} block(s) and {} bench block(s) stand beside it", railed, seated); }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A subway station is laid from the build '{}' at {}, {}, {} turn, floor y {} to street y {}, {} block(s) of packing under it, {} of corridor to the platform and {} lamp(s) up the shaft", named, origin.getX(), origin.getZ(), how.getRotation(), base, surface, Math.max(0, base - level), bored, lit); }
        if (copies > 1 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The build '{}' is {} tall and the climb is {}, so its {} block band is laid {} times: the shaft stands {} block(s) tall", named, tall, surface - level, repeat, copies, grown); }
        return 1;
    }

    private static final class Shaft {
        private final int foot;
        private final int repeat;
        private final int cap;
        private final int copies;
        private final int grown;
        private final int base;

        private Shaft(int tall, int level, int surface) {
            int least = Math.min(stationFoot(), tall);
            int band = stationRepeat();
            int top = tall - least - band;
            int times = 1;
            if (band > 0 && top >= 0) { times = Math.max(1, (surface - level - least - top) / band); }
            else {
                band = 0;
                top = 0;
                least = tall;
            }
            foot = least;
            repeat = band;
            cap = top;
            copies = times;
            grown = foot + repeat * copies + cap;
            base = surface - grown + 1;
        }

        private boolean reaches(int level) { return base >= level + 1 && base - (level + 1) <= PASSAGE_MOST; }
    }

    private static PlacementSettings turned(boolean alongX, int way) {
        PlacementSettings how = new PlacementSettings();
        how.setRotation(alongX ? (way > 0 ? Rotation.NONE : Rotation.CLOCKWISE_180)
                               : (way > 0 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90));
        return how;
    }

    private static BlockPos origin(BlockPos size, PlacementSettings how, int boxLeastX, int boxLeastZ, int base) {
        BlockPos near = Template.transformedBlockPos(how, new BlockPos(BOX_X, 0, BOX_Z));
        BlockPos far = Template.transformedBlockPos(how, new BlockPos(size.getX() - 1, 0, size.getZ() - 1));
        return new BlockPos(boxLeastX - Math.min(near.getX(), far.getX()), base, boxLeastZ - Math.min(near.getZ(), far.getZ()));
    }

    private static List<BlockPos> mouths(PlacementSettings how, BlockPos origin) {
        List<BlockPos> mouths = new ArrayList<>();
        for (int door = DOOR; door <= DOOR + 1; door++) { mouths.add(Template.transformedBlockPos(how, new BlockPos(door, 1, 0)).add(origin)); }
        return mouths;
    }

    private static int wallAt(int center, int way, int bedHalf) { return center + way * (bedHalf + BeardStations.platformWidth() + 1); }

    private static int corridor(boolean alongX, BlockPos mouth, int wall, int platform) {
        int straight = Math.abs((alongX ? mouth.getZ() : mouth.getX()) - wall);
        return straight + turnBack(alongX, mouth, wall, platform + 1) + 2;
    }

    @Nullable static String unreachable(Template held, boolean alongX, int way, int center, int bedHalf, int row, int near, int level, int surface) {
        if (surface == Integer.MIN_VALUE) { return "no street runs along the line beside its stair head"; }
        if (surface - level < CLIMB_LEAST) { return "the street stands only " + (surface - level) + " block(s) over the platform"; }
        Shaft shaft = new Shaft(held.getSize().getY(), level, surface);
        if (!shaft.reaches(level)) { return "the build grown to " + shaft.grown + " block(s) cannot make the climb of " + (surface - level) + " from y" + level + " to the street"; }
        PlacementSettings how = turned(alongX, way);
        BlockPos origin = origin(held.getSize(), how, alongX ? row - 1 : near - 1, alongX ? near - 1 : row - 1, shaft.base);
        List<BlockPos> mouths = mouths(how, origin);
        int wall = wallAt(center, way, bedHalf);
        int length = corridor(alongX, mouths.get(0), wall, level + 1);
        if (length > PASSAGE_MOST) { return "its corridor to the platform would run " + length + " block(s), past the " + PASSAGE_MOST + " one is dug"; }
        int back = turnBack(alongX, mouths.get(0), wall, level + 2);
        int heart = row + BeardStations.RUN / 2;
        for (BlockPos mouth : mouths) {
            int landing = (alongX ? mouth.getX() : mouth.getZ()) - back;
            if (Math.abs(landing - heart) > BeardStations.length() / 2) { return "its corridor would turn back " + back + " block(s) along the line and come down at row " + landing + ", past the end of the platform"; }
        }
        return null;
    }

    private static int turnBack(boolean alongX, BlockPos first, int wall, int target) { return Math.max(0, first.getY() - target - Math.abs((alongX ? first.getZ() : first.getX()) - wall)); }

    private static void band(World world, StructureBoundingBox clip, Template held, IBlockState lining, PlacementSettings how, int leastX, int at, int leastZ, int from, int to) {
        if (to < from) { return; }
        StructureBoundingBox only = new StructureBoundingBox(clip.minX, Math.max(clip.minY, from), clip.minZ,
                                                             clip.maxX, Math.min(clip.maxY, to), clip.maxZ);
        if (only.minY > only.maxY) { return; }
        PlacementSettings step = new PlacementSettings();
        step.setRotation(how.getRotation());
        step.setMirror(how.getMirror());
        step.setBoundingBox(only);
        held.addBlocksToWorld(world, new BlockPos(leastX, at, leastZ), dressed(lining), step, BeardStations.WORLDGEN_FLAGS);
    }

    private static ITemplateProcessor dressed(final IBlockState lining) {
        return (reader, at, was) -> {
            if (was.blockState.getBlock() != Blocks.SPONGE) { return was; }
            return new Template.BlockInfo(at, lining, was.tileentityData);
        };
    }

    private static int bore(World world, StructureBoundingBox clip, boolean alongX, int way, List<BlockPos> mouths, int center, int bedHalf, int platform, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        if (mouths.isEmpty()) { return 0; }
        IBlockState air = Blocks.AIR.getDefaultState();
        IBlockState lamp = stationLight();
        int run = stationLightRun();
        int wall = wallAt(center, way, bedHalf);
        BlockPos first = mouths.get(0);
        int fromAcross = alongX ? first.getZ() : first.getX();
        int target = platform + 1;
        int drop = first.getY() - target;
        int straight = Math.abs(fromAcross - wall);
        int extra = turnBack(alongX, first, wall, target);

        List<int[]> dug = new ArrayList<>();
        Set<Long> taken = new HashSet<>();
        for (BlockPos mouth : mouths) {
            int acr = fromAcross;
            int here = alongX ? mouth.getX() : mouth.getZ();
            int walk = mouth.getY();
            List<int[]> path = new ArrayList<>();
            path.add(new int[] { here, acr });
            if (extra > 0) {
                acr -= way;
                path.add(new int[] { here, acr });
                for (int i = 1; i <= extra; i++) { path.add(new int[] { here - i, acr }); }
                here -= extra;
            }
            while (way > 0 ? acr - way >= wall : acr - way <= wall) {
                acr -= way;
                path.add(new int[] { here, acr });
            }
            boolean start = true;
            for (int[] spot : path) {
                if (!start && walk > target) { walk--; }
                start = false;
                dug.add(new int[] { spot[0], spot[1], walk, mouth.getY() });
                taken.add(packed(spot[0], spot[1]));
            }
        }

        int laid = 0;
        for (int[] spot : dug) {
            int x = alongX ? spot[0] : spot[1];
            int z = alongX ? spot[1] : spot[0];
            boolean litHere = corridorLamp(lamp, run, spot[0], spot[1]);
            for (int y = spot[2] - 1; y <= spot[3] + HEADROOM + 1; y++) {
                if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
                BeardKeep.holdSpot(x, y, z);
                boolean roof = y > spot[2] + HEADROOM;
                boolean shell = y == spot[2] - 1 || roof;
                world.setBlockState(at, roof && litHere ? lamp : shell ? linings.pick(world, x, y, z) : air, BeardStations.WORLDGEN_FLAGS);
                laid++;
            }
        }
        for (int[] spot : dug) {
            for (int side = 0; side < 4; side++) {
                int nr = spot[0] + (side == 0 ? 1 : side == 1 ? -1 : 0);
                int na = spot[1] + (side == 2 ? 1 : side == 3 ? -1 : 0);
                if (taken.contains(packed(nr, na))) { continue; }
                if (way > 0 ? na < wall : na > wall) { continue; }
                int x = alongX ? nr : na;
                int z = alongX ? na : nr;
                for (int y = spot[2] - 1; y <= spot[3] + HEADROOM + 1; y++) {
                    if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    world.setBlockState(at, linings.pick(world, x, y, z), BeardStations.WORLDGEN_FLAGS);
                    laid++;
                }
            }
        }
        if (extra > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The station corridor turns back along the line for {} block(s) to fall {} onto the platform, {} straight across would not have been enough", extra, drop, straight); }
        return laid;
    }

    private static boolean corridorLamp(IBlockState lamp, int run, int row, int across) { return lamp.getBlock() != Blocks.AIR && Math.floorMod(row + across, run) == 0; }

    private static int dividerLamps(World world, StructureBoundingBox clip, int length, IntFunction<BlockPos> divider, int base, int top, BlockPos.MutableBlockPos at) {
        IBlockState lamp = stationLight();
        if (lamp.getBlock() == Blocks.AIR) { return 0; }
        int run = stationLightRun();
        int lit = 0;
        for (int along : new int[] { length / 3, length * 2 / 3 }) {
            BlockPos mid = divider.apply(along);
            for (int y = base + 2; y < top; y += run) {
                if (!clip.isVecInside(at.setPos(mid.getX(), y, mid.getZ()))) { continue; }
                world.setBlockState(at, lamp, BeardStations.WORLDGEN_FLAGS);
                BeardKeep.holdSpot(mid.getX(), y, mid.getZ());
                lit++;
            }
        }
        return lit;
    }

    private static long packed(int row, int across) { return ((long) row << 32) ^ (across & 0xFFFFFFFFL); }

    private static int stepX(boolean alongX, int row, int along, int near, int lane) { return alongX ? row + along : near + lane; }

    private static int stepZ(boolean alongX, int row, int along, int near, int lane) { return alongX ? near + lane : row + along; }
}
