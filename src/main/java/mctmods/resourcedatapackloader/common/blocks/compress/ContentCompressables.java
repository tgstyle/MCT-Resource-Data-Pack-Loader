package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.DropDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserCompressable;
import mctmods.resourcedatapackloader.common.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public final class ContentCompressables {
    private static final Map<Identifier, CompressableDef> PARSED = new LinkedHashMap<>();
    private static final List<CompressedSet> SETS = new ArrayList<>();
    private static final Map<Identifier, CompressedSet> BY_BLOCK = new HashMap<>();

    private ContentCompressables() {}

    public static List<CompressedSet> sets() { return Collections.unmodifiableList(SETS); }

    @Nullable public static CompressedSet owner(Identifier block) { return BY_BLOCK.get(block); }

    public static boolean draws(String namespace, String name) {
        Identifier id = Identifier.tryParse(namespace + ":" + name);
        return id != null && owner(id) != null;
    }

    public static boolean read(Identifier key, String contents) {
        if (!contents.contains(ContentParserCompressable.MEMBER)) { return false; }
        JsonObject json = Json.object(key, contents, "block definition");
        if (json == null || !json.has(ContentParserCompressable.MEMBER)) { return false; }
        CompressableDef def = ContentParserCompressable.parse(key, json);
        if (def != null) { PARSED.put(key, def); }
        return ContentParserCompressable.standalone(json);
    }

    public static void generate() {
        Map<Identifier, BlockDef> defs = new LinkedHashMap<>();
        for (BlockDef def : ContentRegistry.blockDefs()) { defs.put(def.key(), def); }
        for (CompressableDef def : PARSED.values()) {
            CompressedSource source = CompressedSource.of(def, defs);
            if (source == null || !ContentRegistry.available(source.requires, def.key())) { continue; }
            CompressedSet set = new CompressedSet(def, source);
            BlockDef made = blockDef(set);
            Identifier taken = taken(defs, made);
            if (taken != null) {
                ContentLog.LOGGER.error("The compressable {} would make block {}, which a pack already defines, so it gets no compressed layers", def.key(), taken);
                continue;
            }
            ContentRegistry.addDef(made);
            defs.put(made.key(), made);
            SETS.add(set);
            for (BlockVariant variant : made.variants()) { BY_BLOCK.put(variant.id(), set); }
            ContentLog.LOGGER.debug("Compressable {} makes {} layer(s)", def.key(), set.layers);
        }
    }

    @Nullable private static Identifier taken(Map<Identifier, BlockDef> defs, BlockDef made) {
        if (defs.containsKey(made.key())) { return made.key(); }
        for (BlockVariant variant : made.variants()) {
            if (defs.containsKey(variant.id())) { return variant.id(); }
        }
        return null;
    }

    private static BlockDef blockDef(CompressedSet set) {
        CompressedSource source = set.source;
        List<BlockVariant> variants = new ArrayList<>();
        for (int layer = 1; layer <= set.layers; layer++) {
            Identifier id = set.blockId(layer);
            DropDef self = new DropDef(id, null, AmountDef.of(1), 100, 0, new int[0]);
            variants.add(new BlockVariant(id, set.variantName(layer), "common", 64, List.of("c:compressed/" + set.name + "/" + layer + "x"), source.hardness * (layer + 1), source.resistance * (layer + 1), source.harvestLevel, 0, List.of(self), null));
        }
        Identifier key = Identifier.fromNamespaceAndPath(set.def.key().getNamespace(), set.name + "_compressed");
        return new BlockDef(key, ContentBlockTypes.BASIC, source.material, source.mapColor, source.sound, "", source.tool, source.toolLevel, true, 0, 0, 1.0F, Collections.unmodifiableList(variants), source.requires, "", true, true, 255, 0.6F, null, 0, 0, "minecraft:stone", false, BlockDef.PARTICLE_FLAME, true, 0xFFFFFF, "", "", 7, null, null, Collections.emptyList(), "", "", 5, null, "", null, null, null, Collections.emptyList(), null, -1);
    }
}
