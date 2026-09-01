package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

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
import java.util.LinkedHashMap;
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
    private static final Set<String> GROWN = new HashSet<>();
    private static final Map<IBlockState, IBlockState> BLOCKS = new HashMap<>();
    private static final List<VillageRule> RULES = new ArrayList<>();
    @Nullable private static WorldTemplateDef blocksFrom;
    private static boolean blocksLoaded;
    @Nullable private static WorldTemplateDef namedFrom;
    private static Set<String> named;
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

    @Nullable public static VillageDef byName(String name) { return DEFS.get(name); }

    public static void reload() {
        DEFS.clear();
        BLOCKS.clear();
        RULES.clear();
        blocksLoaded = false;
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

    public static int largestPlot() {
        int largest = 13;
        for (VillageDef def : DEFS.values()) {
            if (missing(def) || blocked(def)) { continue; }
            largest = Math.max(largest, Math.max(def.width, def.depth));
        }
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
        boolean listed = false;
        if (def.isTemplate() && !def.structure.isEmpty()) {
            ResourceLocation template = new ResourceLocation(def.structure);
            listed = names().contains(template.toString().toLowerCase(Locale.ROOT)) || names().contains(template.getPath().toLowerCase(Locale.ROOT));
        }
        if (!listed) { listed = names().contains(def.registryName.toString().toLowerCase(Locale.ROOT)) || names().contains(def.registryName.getPath().toLowerCase(Locale.ROOT)); }
        if (ContentControl.flag(ContentControl.STRUCTURES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist)) { return listed; }
        return !listed;
    }

    @Nullable private static VillageDef pick(Random random) {
        boolean filtering = filtering();
        int total = 0;
        for (VillageDef def : DEFS.values()) {
            if (filtering && blocked(def)) { continue; }
            total += Math.max(1, def.weight);
        }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (VillageDef def : DEFS.values()) {
            if (filtering && blocked(def)) { continue; }
            roll -= Math.max(1, def.weight);
            if (roll < 0) { return def; }
        }
        return null;
    }

    public static final class Handler implements VillagerRegistry.IVillageCreationHandler {
        @Override public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
            int weight = 0;
            int least = Integer.MAX_VALUE;
            int most = 0;
            for (VillageDef def : DEFS.values()) {
                weight += Math.max(1, def.weight);
                least = Math.min(least, def.leastCount);
                most = Math.max(most, def.mostCount);
            }
            if (least == Integer.MAX_VALUE) { least = 0; }

            return new StructureVillagePieces.PieceWeight(ContentVillagePiece.class, weight, MathHelper.getInt(random, least + size, most + size));
        }

        @Override public Class<?> getComponentClass() { return ContentVillagePiece.class; }

        private static BlockPos plotSize(VillageDef def) {
            BlockPos declared = new BlockPos(def.width, def.height, def.depth);
            if (!def.isTemplate()) { return declared; }
            World world = ContentBeard.samplerWorld;
            if (!(world instanceof WorldServer)) { return declared; }
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
            VillageDef def = pick(random);
            if (def == null) { return null; }
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Village plot {} is laid from template {}", def.registryName, def.isTemplate() ? def.structure : "none, it is a farm"); }
            BlockPos size = plotSize(def);
            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, size.getX(), size.getY(), size.getZ(), facing);
            for (StructureComponent piece : placed) {
                if (piece.getBoundingBox().intersectsWith(box)) { return null; }
            }
            return new ContentVillagePiece(start, type, box, facing, def);
        }
    }
}
