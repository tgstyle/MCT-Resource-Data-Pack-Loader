package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IVillageBlock;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import com.google.gson.JsonParseException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentVillages {
    private static final String COMPONENT = "RDPL:Plot";
    private static final String AT = "at=";
    private static final String UNDER = "under=";
    private static final Map<String, VillageDef> DEFS = new LinkedHashMap<>();
    private static final Map<VillageDef, BlockPos> SIZES = new IdentityHashMap<>();
    @Nullable private static List<VillageDef> bySize;
    private static final Set<String> GROWN = new HashSet<>();
    private static final Map<IBlockState, IBlockState> BLOCKS = new HashMap<>();
    private static final List<VillageRule> RULES = new ArrayList<>();
    @Nullable private static WorldTemplateDef blocksFrom;
    private static boolean blocksLoaded;
    @Nullable private static WorldTemplateDef namedFrom;
    private static Set<String> named;
    private static final Map<VillageDef, Boolean> BARRED = new HashMap<>();
    @Nullable private static WorldTemplateDef barredFrom;
    private static boolean barredLoaded;
    private static int largest;
    @Nullable private static WorldTemplateDef largestFrom;
    private static boolean largestLoaded;
    private static final WeightedPicks BLOCK_SIZES = new WeightedPicks("villageBlockSizes");
    private static boolean loaded;
    private static boolean registered;

    private ContentVillages() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.villages) { return false; }
        PackManager.get().forEach(PackManager.VILLAGES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                VillageDef def = ContentParser.village(key, contents);
                if (def == null) { return; }
                if (missing(def)) {
                    ContentLog.LOGGER.debug("Village plot {} needs {}, which is not here, so it is left out", key, def.requires);
                    return;
                }
                DEFS.put(key.toString(), def);
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in village file {}, ignoring it", key, ex); }
        });
        if (!DEFS.isEmpty()) { Summary.info("villages", "Loaded " + DEFS.size() + " village plot(s) from packs"); }
        return !DEFS.isEmpty();
    }

    public static void register() {
        if (registered || DEFS.isEmpty()) { return; }
        registered = true;
        MapGenStructureIO.registerStructureComponent(ContentVillagePiece.class, COMPONENT);
        VillagerRegistry.instance().registerVillageCreationHandler(new Handler());
    }

    public static boolean filtering() {
        if (ContentControl.off(ContentControl.STRUCTURES)) { return false; }
        return !names().isEmpty();
    }

    public static boolean blocked(Class<?> piece) {
        if (piece == null) { return false; }
        boolean listed = names().contains(piece.getSimpleName().toLowerCase(Locale.ROOT));
        if (ContentControl.flag(ContentControl.STRUCTURES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist)) { return listed; }
        if (listed) { return false; }
        return piece.getEnclosingClass() == StructureVillagePieces.class;
    }

    @Nullable public static VillageDef byName(String name) { return defs().get(name); }

    public static BlockPos plotSize(VillageDef def) { return Handler.plotSize(def); }

    private static Map<String, VillageDef> defs() {
        load();
        return DEFS;
    }

    public static void reload() {
        DEFS.clear();
        SIZES.clear();
        bySize = null;
        BLOCKS.clear();
        RULES.clear();
        BARRED.clear();
        blocksLoaded = false;
        barredLoaded = false;
        largestLoaded = false;
        named = null;
        loaded = false;
    }

    private static Set<String> names() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (named == null || active != namedFrom) {
            named = Settings.lower(ContentControl.list(ContentControl.STRUCTURES, "villagePieces", Config.worldgen.villagePieces));
            namedFrom = active;
        }
        return named;
    }

    private static boolean missing(VillageDef def) { return !ContentRegistry.available(def.requires, def.registryName); }

    public static int plotsLeast() {
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsLeast", Config.worldgen.villagePlotsLeast));
        int most = plotsMost();
        return most > 0 ? Math.min(least, most) : least;
    }

    public static int plotsMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsMost", Config.worldgen.villagePlotsMost)); }

    public static int plots(List<StructureComponent> components) {
        int count = 0;
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Village)) { continue; }
            if (piece instanceof StructureVillagePieces.Road || piece instanceof StructureVillagePieces.Torch || piece instanceof StructureVillagePieces.Well) { continue; }
            count++;
        }
        return count;
    }

    public static void sizeBlock(World world, StructureVillagePieces.Start start) {
        if (BLOCK_SIZES.stale()) { BLOCK_SIZES.load(ContentControl.list(ContentControl.VILLAGES, "villageBlockSizes", Config.worldgen.villageBlockSizes)); }
        if (BLOCK_SIZES.isEmpty() || !(start instanceof IVillageBlock)) { return; }
        StructureBoundingBox well = start.getBoundingBox();
        WeightedPicks.Pick chosen = BLOCK_SIZES.pick(SeededRandom.at(world, well.minX, well.minZ));
        if (chosen == null) { return; }
        int size;
        try { size = Integer.parseInt(chosen.name); }
        catch (NumberFormatException wrong) {
            ContentLog.LOGGER.error("villageBlockSizes entry '{}' is not a whole number of blocks, so the district at {}, {} keeps the largest plot as its block", chosen.name, well.minX, well.minZ);
            return;
        }
        ((IVillageBlock) start).rdpl$block(Math.max(13, size));
        ContentLog.LOGGER.debug("The district at {}, {} lays its blocks {} deep", well.minX, well.minZ, Math.max(13, size));
    }

    public static int blockOf(@Nullable StructureComponent piece) {
        int held = piece instanceof IVillageBlock ? ((IVillageBlock) piece).rdpl$block() : 0;
        return held > 0 ? held : largestPlot();
    }

    private static boolean exceeds(VillageDef def, int block) { return span(def) > block; }

    public static int largestPlot() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (largestLoaded && active == largestFrom) { return largest; }
        int widest = 13;
        for (VillageDef def : defs().values()) {
            if (missing(def) || blocked(def)) { continue; }
            StructureMapDef map = ContentStructureMaps.byName(def.structure);
            if (map != null) {
                widest = Math.max(widest, Math.max(map.cellsWide, map.cellsDeep) * map.cell);
                continue;
            }
            widest = Math.max(widest, Math.max(def.width, def.depth));
        }
        largest = widest;
        largestFrom = active;
        largestLoaded = true;
        return largest;
    }

    @Nullable public static IBlockState swap(IBlockState original) {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (!blocksLoaded || active != blocksFrom) {
            loadBlocks();
            blocksFrom = active;
        }
        if (BLOCKS.isEmpty()) { return null; }
        IBlockState wanted = BLOCKS.get(original);
        if (wanted != null) { return wanted; }
        return BLOCKS.get(original.getBlock().getDefaultState());
    }

    @Nullable public static IBlockState ruled(World world, BlockPos pos, IBlockState laid) {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (!blocksLoaded || active != blocksFrom) {
            loadBlocks();
            blocksFrom = active;
        }
        IBlockState held = laid;
        if (!BLOCKS.containsValue(held)) {
            IBlockState swapped = swap(held);
            if (swapped != null) { held = swapped; }
        }
        for (VillageRule rule : RULES) {
            IBlockState wanted = rule.apply(world, pos, held);
            if (wanted != null) { return wanted; }
        }
        return held == laid ? null : held;
    }

    private static void loadBlocks() {
        BLOCKS.clear();
        RULES.clear();
        blocksLoaded = true;
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "villageBlocks", Config.worldgen.villageBlocks)) {
            VillageRule rule = rule(entry);
            if (rule == null) { continue; }
            if (rule.plain()) { BLOCKS.put(rule.original(), rule.replacement()); }
            else { RULES.add(rule); }
        }
        if (!BLOCKS.isEmpty()) { ContentLog.LOGGER.info("Villages build with {} replaced block(s), whatever any other mod asks for", BLOCKS.size()); }
        if (!RULES.isEmpty()) { ContentLog.LOGGER.info("Villages weather {} block(s) by rule as their pieces lay them", RULES.size()); }
    }

    @Nullable private static VillageRule rule(String entry) {
        String[] fields = entry.split(",");
        String[] parts = Settings.pair(fields[0], "villageBlocks", "original=replacement");
        if (parts == null) { return null; }
        IBlockState from = ContentStates.parse(parts[0], "villageBlocks");
        IBlockState to = ContentStates.parse(parts[1], "villageBlocks");
        if (from == null || to == null) { return null; }
        int chance = VillageRule.ALWAYS;
        IBlockState at = null;
        IBlockState under = null;
        for (int field = 1; field < fields.length; field++) {
            String said = fields[field].trim();
            if (said.isEmpty()) { continue; }
            if (said.startsWith(AT)) {
                at = ContentStates.parse(said.substring(AT.length()), "villageBlocks");
                if (at == null) { return null; }
            }
            else if (said.startsWith(UNDER)) {
                under = ContentStates.parse(said.substring(UNDER.length()), "villageBlocks");
                if (under == null) { return null; }
            }
            else {
                chance = chanceOf(said, entry);
                if (chance < 0) { return null; }
            }
        }
        return new VillageRule(from, to, chance, at, under);
    }

    private static int chanceOf(String said, String entry) {
        int asked;
        try { asked = Integer.parseInt(said); }
        catch (NumberFormatException wrong) {
            ContentLog.LOGGER.error("villageBlocks entry '{}' says '{}', which is neither a chance out of 100 nor an at= or under= block, ignoring the entry", entry, said);
            return -1;
        }
        if (asked >= 1 && asked <= VillageRule.ALWAYS) { return asked; }
        ContentLog.LOGGER.error("villageBlocks entry '{}' asks for a chance of {}, which is not between 1 and 100, ignoring the entry", entry, asked);
        return -1;
    }

    public static boolean blockedTemplate(ResourceLocation template) {
        if (template == null || !filtering()) { return false; }
        boolean listed = names().contains(template.toString().toLowerCase(Locale.ROOT)) || names().contains(template.getPath().toLowerCase(Locale.ROOT));
        if (!ContentControl.flag(ContentControl.STRUCTURES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist)) { return false; }
        return listed;
    }

    public static boolean blocked(VillageDef def) {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (!barredLoaded || active != barredFrom) {
            BARRED.clear();
            barredFrom = active;
            barredLoaded = true;
        }
        Boolean held = BARRED.get(def);
        if (held != null) { return held; }
        boolean listed = false;
        if (def.isTemplate() && !def.structure.isEmpty()) {
            ResourceLocation template = new ResourceLocation(def.structure);
            listed = names().contains(template.toString().toLowerCase(Locale.ROOT)) || names().contains(template.getPath().toLowerCase(Locale.ROOT));
        }
        if (!listed) { listed = names().contains(def.registryName.toString().toLowerCase(Locale.ROOT)) || names().contains(def.registryName.getPath().toLowerCase(Locale.ROOT)); }
        boolean barred = ContentControl.flag(ContentControl.STRUCTURES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist) == listed;
        BARRED.put(def, barred);
        return barred;
    }

    private static int span(VillageDef def) {
        BlockPos size = plotSize(def);
        return Math.max(size.getX(), size.getZ());
    }

    @Nullable private static VillageDef pick(Random random, int block) {
        boolean filtering = filtering();
        int total = 0;
        for (VillageDef def : defs().values()) {
            if ((filtering && blocked(def)) || exceeds(def, block)) { continue; }
            total += Math.max(1, def.weight) * span(def);
        }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (VillageDef def : defs().values()) {
            if ((filtering && blocked(def)) || exceeds(def, block)) { continue; }
            roll -= Math.max(1, def.weight) * span(def);
            if (roll < 0) { return def; }
        }
        return null;
    }

    public static final class Handler implements VillagerRegistry.IVillageCreationHandler {
        @Override public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
            int weight = 0;
            int least = Integer.MAX_VALUE;
            int most = 0;
            for (VillageDef def : defs().values()) {
                weight += Math.max(1, def.weight);
                least = Math.min(least, def.leastCount);
                most = Math.max(most, def.mostCount);
            }
            if (least == Integer.MAX_VALUE) { least = 0; }

            return new StructureVillagePieces.PieceWeight(ContentVillagePiece.class, weight, MathHelper.getInt(random, least + size, most + size));
        }

        @Override public Class<?> getComponentClass() { return ContentVillagePiece.class; }

        private static BlockPos plotSize(VillageDef def) {
            BlockPos known = SIZES.get(def);
            if (known != null) { return known; }
            BlockPos measured = measure(def);
            if (measured != null) { SIZES.put(def, measured); }
            return measured != null ? measured : new BlockPos(def.width, def.height, def.depth);
        }

        private static List<VillageDef> bySize() {
            if (bySize != null) { return bySize; }
            List<VillageDef> sorted = new ArrayList<>(defs().values());
            sorted.sort(Comparator.comparingInt(ContentVillages::span).reversed());
            bySize = sorted;
            return sorted;
        }

        @Nullable private static BlockPos measure(VillageDef def) {
            BlockPos declared = new BlockPos(def.width, def.height, def.depth);
            if (!def.isTemplate()) { return declared; }
            StructureMapDef map = ContentStructureMaps.byName(def.structure);
            if (map != null) { return new BlockPos(map.cellsWide * map.cell, (map.layers.length - map.ground) * map.cell, map.cellsDeep * map.cell); }
            World world = ContentBeard.samplerWorld;
            if (!(world instanceof WorldServer)) { return null; }
            Template template = ((WorldServer) world).getStructureTemplateManager().get(world.getMinecraftServer(), new ResourceLocation(def.structure));
            if (template == null) { return declared; }
            BlockPos size = template.getSize();
            if (size.getX() <= def.width && size.getY() <= def.height && size.getZ() <= def.depth) { return declared; }
            if (GROWN.add(def.registryName.toString())) {
                ContentLog.LOGGER.warn("Village plot {} declares {}x{}x{} but its template {} measures {}x{}x{}, so the plot is grown to fit rather than cutting the template short", def.registryName, def.width, def.height, def.depth, def.structure, size.getX(), size.getY(), size.getZ());
            }
            return new BlockPos(Math.max(def.width, size.getX()), Math.max(def.height, size.getY()), Math.max(def.depth, size.getZ()));
        }

        @Override public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight weight, StructureVillagePieces.Start start, List<StructureComponent> placed, Random random, int x, int y, int z, EnumFacing facing, int type) {
            int block = blockOf(start);
            VillageDef def = pick(random, block);
            if (def == null) { return null; }
            StructureVillagePieces.Village seated = seat(start, placed, x, y, z, facing, type, def);
            if (seated != null) { return seated; }
            for (VillageDef held : bySize()) {
                if (held == def || (filtering() && blocked(held)) || exceeds(held, block)) { continue; }
                seated = seat(start, placed, x, y, z, facing, type, held);
                if (seated != null) { return seated; }
            }
            return null;
        }

        @Nullable private static StructureVillagePieces.Village seat(StructureVillagePieces.Start start, List<StructureComponent> placed, int x, int y, int z, EnumFacing facing, int type, VillageDef def) {
            BlockPos size = plotSize(def);
            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, size.getX(), size.getY(), size.getZ(), facing);
            if (BeardPlots.collides(placed, box)) { return null; }
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Village plot {} is laid from template {}", def.registryName, def.isTemplate() ? def.structure : "none, it is a farm"); }
            return new ContentVillagePiece(start, type, box, facing, def);
        }
    }
}
