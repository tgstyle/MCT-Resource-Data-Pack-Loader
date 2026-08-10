package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
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
    private static final Map<String, VillageDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> GROWN = new HashSet<>();
    @Nullable private static Map<IBlockState, IBlockState> BLOCKS;
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
                if (!present(def)) {
                    ContentLog.LOGGER.info("Village plot {} needs {}, which is not here, so it is left out", key, def.requires);
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
        BLOCKS = null;
        named = null;
        loaded = false;
    }

    private static Set<String> names() {
        if (named == null) { named = Names.lower(ContentControl.list(ContentControl.STRUCTURES, "villagePieces", Config.worldgen.villagePieces)); }

        return named;
    }

    private static boolean present(VillageDef def) { return ContentRegistry.available(def.requires, def.registryName); }

    @Nullable public static IBlockState swap(IBlockState original) {
        if (BLOCKS == null) { loadBlocks(); }
        if (BLOCKS.isEmpty()) { return null; }

        IBlockState wanted = BLOCKS.get(original);
        if (wanted != null) { return wanted; }

        return BLOCKS.get(original.getBlock().getDefaultState());
    }

    private static void loadBlocks() {
        BLOCKS = new HashMap<>();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "villageBlocks", Config.worldgen.villageBlocks)) {
            int split = entry.indexOf('=');
            if (split <= 0 || split == entry.length() - 1) {
                ContentLog.LOGGER.error("villageBlocks entry '{}' is not written as original=replacement, ignoring it", entry);
                continue;
            }
            IBlockState from = ContentStates.parse(entry.substring(0, split).trim(), "villageBlocks");
            IBlockState to = ContentStates.parse(entry.substring(split + 1).trim(), "villageBlocks");
            if (from == null || to == null) { continue; }

            BLOCKS.put(from, to);
        }
        if (!BLOCKS.isEmpty()) { ContentLog.LOGGER.info("Villages build with {} replaced block(s), whatever any other mod asks for", BLOCKS.size()); }
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
