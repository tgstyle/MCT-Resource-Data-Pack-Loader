package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.world.SeededRandom;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.util.math.MathHelper;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoads {
    private BeardRoads() {}

    public static final class Grade {
        final int[] profile;
        final int[] ground;
        final boolean[] bridged;
        final boolean[] held;
        final int[] deck;
        final boolean[] covered;
        final int start;
        final int capped;
        Grade(int[] profile, int[] ground, boolean[] bridged, boolean[] held, int start, int capped) {
            this.profile = profile;
            this.ground = ground;
            this.bridged = bridged;
            this.held = held;
            this.start = start;
            this.capped = capped;
            this.deck = new int[profile.length];
            this.covered = new boolean[profile.length];
            for (int i = 0; i < profile.length; i++) { this.deck[i] = profile[i] == Integer.MIN_VALUE ? carried(profile, i) : Integer.MIN_VALUE; }
        }

        private Grade(int[] profile, int[] ground, boolean[] bridged, boolean[] held, int[] deck, boolean[] covered, int start, int capped) {
            this.profile = profile;
            this.ground = ground;
            this.bridged = bridged;
            this.held = held;
            this.deck = deck;
            this.covered = covered;
            this.start = start;
            this.capped = capped;
        }

        public int at(int row) { return profile[MathHelper.clamp(row - start, 0, profile.length - 1)]; }

        public int rows() { return profile.length; }

        public int start() { return start; }

        public int deckAt(int row) { return deck[MathHelper.clamp(row - start, 0, deck.length - 1)]; }

        public boolean bridgedAt(int row) { return row >= start && row < start + bridged.length && bridged[row - start]; }

        public int groundAt(int row) { return ground[MathHelper.clamp(row - start, 0, ground.length - 1)]; }

        public boolean tunneledAt(int row, int depth) {
            if (depth <= 0 || row < start || row >= start + profile.length) { return false; }
            int i = row - start;
            if (!roofed(i, depth)) { return false; }
            int from = i;
            while (from > 0 && roofed(from - 1, depth)) { from--; }
            int to = i;
            while (to + 1 < profile.length && roofed(to + 1, depth)) { to++; }
            return to - from + 1 >= BeardGrade.TUNNEL_LEAST;
        }

        private boolean roofed(int i, int depth) {
            if (!buried(i, 1)) { return false; }
            if (buried(i, depth)) { return true; }
            return buriedWithin(i, -1, depth) && buriedWithin(i, 1, depth);
        }

        private boolean buriedWithin(int i, int step, int depth) {
            for (int at = i + step, seen = 1; at >= 0 && at < profile.length && seen <= BeardGrade.TUNNEL_LEAST; at += step, seen++) {
                if (!buried(at, 1)) { return false; }
                if (buried(at, depth)) { return true; }
            }
            return false;
        }

        private boolean buried(int i, int depth) { return profile[i] != Integer.MIN_VALUE && !bridged[i] && ground[i] != Integer.MIN_VALUE && ground[i] - profile[i] >= depth; }

        public boolean buriedAt(int row, int depth) { return row >= start && row < start + profile.length && buried(row - start, depth); }

        public boolean sameAs(Grade other) { return start == other.start && Arrays.equals(profile, other.profile) && Arrays.equals(deck, other.deck) && Arrays.equals(bridged, other.bridged) && Arrays.equals(held, other.held); }

        public void write(NBTTagCompound tag) {
            tag.setInteger("RdplStart", start);
            tag.setInteger("RdplCapped", capped);
            tag.setIntArray("RdplProfile", profile);
            tag.setIntArray("RdplGround", ground);
            tag.setIntArray("RdplDeck", deck);
            tag.setByteArray("RdplBridged", packed(bridged));
            tag.setByteArray("RdplHeld", packed(held));
            tag.setByteArray("RdplLaid", packed(covered));
        }

        @Nullable public static Grade read(NBTTagCompound tag) {
            if (!tag.hasKey("RdplProfile", 11)) { return null; }
            int[] profile = tag.getIntArray("RdplProfile");
            int[] ground = tag.getIntArray("RdplGround");
            int[] deck = tag.getIntArray("RdplDeck");
            if (profile.length == 0 || ground.length != profile.length || deck.length != profile.length) { return null; }
            boolean[] bridged = unpacked(tag.getByteArray("RdplBridged"), profile.length);
            boolean[] held = unpacked(tag.getByteArray("RdplHeld"), profile.length);
            boolean[] covered = unpacked(tag.getByteArray("RdplLaid"), profile.length);
            return new Grade(profile, ground, bridged, held, deck, covered, tag.getInteger("RdplStart"), tag.getInteger("RdplCapped"));
        }

        static int carried(int[] profile, int i) {
            int before = Integer.MIN_VALUE;
            for (int back = i - 1; back >= 0; back--) {
                if (profile[back] == Integer.MIN_VALUE) { continue; }
                before = profile[back];
                break;
            }
            int after = Integer.MIN_VALUE;
            for (int on = i + 1; on < profile.length; on++) {
                if (profile[on] == Integer.MIN_VALUE) { continue; }
                after = profile[on];
                break;
            }
            if (before == Integer.MIN_VALUE) { return after; }
            if (after == Integer.MIN_VALUE) { return before; }
            return Math.max(before, after);
        }

        private static byte[] packed(boolean[] flags) {
            byte[] out = new byte[flags.length];
            for (int i = 0; i < flags.length; i++) { out[i] = (byte) (flags[i] ? 1 : 0); }
            return out;
        }

        private static boolean[] unpacked(byte[] bytes, int rows) {
            boolean[] out = new boolean[rows];
            for (int i = 0; i < rows && i < bytes.length; i++) { out[i] = bytes[i] != 0; }
            return out;
        }
    }

    static List<StructureComponent> villagePieces(World world) { return ContentBeard.everyone(world, ContentBeard.components()); }

    static List<StructureBoundingBox> crossings(List<StructureComponent> nearby, StructureComponent piece, StructureBoundingBox box) {
        List<StructureBoundingBox> found = new ArrayList<>();
        int reach = pathFullWidth() + 1;
        for (StructureComponent other : nearby) {
            if (other == piece || other instanceof RailPiece || !(other instanceof StructureVillagePieces.Road)) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            if (held.maxX >= box.minX - reach && held.minX <= box.maxX + reach && held.maxZ >= box.minZ - reach && held.minZ <= box.maxZ + reach) { found.add(held); }
        }
        return found;
    }

    static boolean insidePlaza(int x, int z) { return BeardPlots.insidePlaza(ContentBeard.components(), x, z); }

    public static boolean clearable(IBlockState held) {
        Block block = held.getBlock();
        if (block == Blocks.AIR) { return false; }
        if (block == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { return false; }
        return BeardBlocks.terrainBlock(block) || held.getMaterial() == Material.VINE || held.getMaterial() == Material.PLANTS;
    }

    public static IBlockState pathForGround(World world, int x, int z, IBlockState path, IBlockState gravel, boolean earthy) {
        Block ground = BeardBlocks.fillGround(world, x, z).getBlock();
        if (ground == Blocks.SAND) { return asked(Blocks.SANDSTONE.getDefaultState()); }
        if (ground == Blocks.HARDENED_CLAY) { return asked(Blocks.HARDENED_CLAY.getDefaultState()); }
        if (ground == Blocks.GRAVEL) { return asked(Blocks.GRAVEL.getDefaultState()); }
        return earthy || gravel.getBlock() == Blocks.CLAY ? path : gravel;
    }

    private static IBlockState asked(IBlockState picked) {
        IBlockState wanted = ContentVillages.swap(picked);
        return wanted != null ? wanted : picked;
    }

    private static final TemplateMemo<Widths> WIDTHS = new TemplateMemo<>();
    private static final TemplateMemo<Map<String, Widths>> KEYED_WIDTHS = new TemplateMemo<>();
    private static final Map<StructureBoundingBox, JsonObject> DRAWN = Collections.synchronizedMap(new WeakHashMap<>());

    private static final class Widths {
        final boolean chosen = !ContentControl.text(ContentControl.VILLAGES, "villagePathBlock", Config.worldgen.villagePathBlock).isEmpty();
        final int extraWidth = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth));
        final int lineColumns = ContentControl.text(ContentControl.VILLAGES, "villagePathLineBlock", Config.worldgen.villagePathLineBlock).isEmpty() ? 0 : 1;
        final int sidewalkWidth = ContentControl.text(ContentControl.VILLAGES, "villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock).isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth));
        final int fullWidth = 3 + 2 * (extraWidth + lineColumns + sidewalkWidth);
        final int alley = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathAlleyChance", Config.worldgen.villagePathAlleyChance));
        final int minimumWidth = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth));
    }

    private static Widths widths() {
        JsonObject road = ContentControl.roadKeys();
        if (road == null) { return WIDTHS.get(Widths::new); }
        return KEYED_WIDTHS.get(ConcurrentHashMap::new).computeIfAbsent(road.toString(), keys -> new Widths());
    }

    private static Widths widthsFor(@Nullable JsonObject keys) {
        JsonObject was = ContentControl.roadKeys(keys);
        try { return widths(); }
        finally { ContentControl.roadKeys(was); }
    }

    public static void drawn(StructureBoundingBox box, @Nullable JsonObject keys) {
        if (keys == null) { DRAWN.remove(box); }
        else { DRAWN.put(box, keys); }
    }

    @Nullable public static JsonObject drawnKeys(StructureBoundingBox box) { return DRAWN.get(box); }

    public static int fullWidthFor(@Nullable JsonObject keys) { return widthsFor(keys).fullWidth; }

    public static int[] bandsOf(StructureBoundingBox box) {
        Widths held = widthsFor(DRAWN.get(box));
        return new int[] { held.fullWidth, held.lineColumns, held.sidewalkWidth };
    }

    public static boolean pathChosen() { return widths().chosen; }

    public static int pathExtraWidth() { return widths().extraWidth; }

    public static int pathLineColumns() { return widths().lineColumns; }

    public static int pathSidewalkWidth() { return widths().sidewalkWidth; }

    public static int pathFullWidth() { return widths().fullWidth; }

    public static IBlockState alleyBlock(IBlockState path) { return pathBlock("villagePathAlleyBlock", Config.worldgen.villagePathAlleyBlock, path); }

    public static int alleyChance() { return widths().alley; }

    public static boolean roadNarrow(StructureBoundingBox box, boolean alongX) {
        if (DRAWN.containsKey(box)) { return false; }
        return (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1 < pathFullWidth();
    }

    public static int roadCore(StructureBoundingBox box, boolean alongX) { return BeardPlots.coreOf(BeardPlots.roadSpan(box, alongX)); }

    static int barrierHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight)); }

    public static int pathMinimumWidth() { return widths().minimumWidth; }

    @Nullable public static ContentStates.Spec pathSpec(String key, String fromConfig) {
        String named = ContentControl.text(ContentControl.VILLAGES, key, fromConfig);
        if (named.isEmpty()) { return null; }
        ContentStates.Spec spec = ContentStates.spec(named, key);
        if (spec == null) { ContentLog.LOGGER.error("{} '{}' is not a registered block, nothing is placed", key, named); }
        return spec;
    }

    private static final java.util.Map<String, IBlockState> PATH_BLOCKS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final PackGeneration PATH_GENERATION = new PackGeneration();

    public static final class Palette {
        private final IBlockState[] drawn;

        private Palette(IBlockState[] drawn) { this.drawn = drawn; }

        public IBlockState first() { return drawn[0]; }

        public boolean mixed() { return drawn.length > 1; }

        public IBlockState pick(@Nullable World world, int x, int y, int z) {
            if (drawn.length == 1 || world == null) { return drawn[0]; }
            return drawn[SeededRandom.at(world, x, y, z).nextInt(drawn.length)];
        }
    }

    private static final int MIX_MOST = 256;
    private static final Map<String, Palette> PATH_MIXES = new HashMap<>();

    public static Palette pathPalette(String key, String fromConfig, IBlockState vanilla) { return pathPalette(key, fromConfig, vanilla, BeardBiome.building()); }

    public static Palette pathPalette(String key, String fromConfig, IBlockState vanilla, @Nullable String section) {
        String named = ContentControl.text(ContentControl.VILLAGES, key, fromConfig, section).trim();
        if (named.isEmpty()) { return new Palette(new IBlockState[] { vanilla }); }
        if (PATH_GENERATION.stale()) { PATH_BLOCKS.clear(); PATH_MIXES.clear(); }
        Palette held = PATH_MIXES.get(named);
        if (held != null) { return held; }
        List<IBlockState> drawn = new ArrayList<>();
        for (String part : named.split(",")) {
            String entry = part.trim();
            if (entry.isEmpty()) { continue; }
            int weight = 1;
            int gap = entry.lastIndexOf(' ');
            if (gap > 0) {
                try {
                    weight = Integer.parseInt(entry.substring(gap + 1).trim());
                    entry = entry.substring(0, gap).trim();
                }
                catch (NumberFormatException ignored) { weight = 1; }
            }
            IBlockState state = ContentStates.parse(entry, key);
            if (state == null) {
                ContentLog.LOGGER.error("{} names '{}', which is not a registered block, so it is left out of the mix", key, entry);
                continue;
            }
            for (int i = 0; i < Math.max(1, weight) && drawn.size() < MIX_MOST; i++) { drawn.add(state); }
        }
        if (drawn.isEmpty()) { drawn.add(vanilla); }
        Palette made = new Palette(drawn.toArray(new IBlockState[0]));
        PATH_MIXES.put(named, made);
        if (made.mixed() && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} is a mix of {} draw(s) over {} block(s)", key, drawn.size(), named.split(",").length); }
        return made;
    }

    public static IBlockState pathBlock(String key, String fromConfig, IBlockState vanilla) { return pathBlock(key, fromConfig, vanilla, BeardBiome.building()); }

    public static IBlockState pathBlock(String key, String fromConfig, IBlockState vanilla, @Nullable String section) {
        String named = ContentControl.text(ContentControl.VILLAGES, key, fromConfig, section);
        if (named.isEmpty()) { return vanilla; }
        if (named.indexOf(',') >= 0) { return pathPalette(key, fromConfig, vanilla, section).first(); }
        if (PATH_GENERATION.stale()) { PATH_BLOCKS.clear(); PATH_MIXES.clear(); }
        IBlockState held = PATH_BLOCKS.get(named);
        if (held != null) { return held; }
        IBlockState state = ContentStates.parse(named, key);
        if (state == null) {
            ContentLog.LOGGER.error("{} '{}' is not a registered block, using the vanilla road block", key, named);
            return vanilla;
        }
        PATH_BLOCKS.put(named, state);
        return state;
    }
}
