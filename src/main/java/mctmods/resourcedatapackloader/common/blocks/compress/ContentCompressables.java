package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.DropDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.io.port.Ids;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserCompressable;
import mctmods.resourcedatapackloader.common.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@SuppressWarnings("deprecation") public final class ContentCompressables {
    private static final Map<ResourceLocation, CompressableDef> PARSED = new LinkedHashMap<>();
    private static final List<CompressedSet> SETS = new ArrayList<>();
    private static final Map<ResourceLocation, CompressedSet> BY_BLOCK = new HashMap<>();

    private ContentCompressables() {}

    public static List<CompressedSet> sets() { return Collections.unmodifiableList(SETS); }

    @Nullable public static CompressedSet owner(ResourceLocation block) { return BY_BLOCK.get(block); }

    public static void mapOrphanMetas() {
        for (ResourceLocation name : BY_BLOCK.keySet()) {
            Block block = Registries.find(ForgeRegistries.BLOCKS, name);
            if (block == null) { continue; }
            int id = Block.getIdFromBlock(block) << 4;
            for (int meta = 0; meta < BlockDef.MAX_META; meta++) {
                IBlockState state = block.getStateFromMeta(meta);
                int own = block.getMetaFromState(state);
                if (own != meta) {
                    Block.BLOCK_STATE_IDS.put(state, id | meta);
                    Block.BLOCK_STATE_IDS.put(state, id | own);
                }
            }
        }
    }

    public static boolean read(ResourceLocation key, String contents) {
        if (!contents.contains(ContentParserCompressable.MEMBER)) { return false; }
        JsonObject json = Json.object(key, contents, "block definition");
        if (json == null || !json.has(ContentParserCompressable.MEMBER)) { return false; }
        CompressableDef def = ContentParserCompressable.parse(key, json);
        if (def != null) { PARSED.put(key, def); }
        return ContentParserCompressable.standalone(json);
    }

    public static void generate(Map<ResourceLocation, BlockDef> defs) {
        for (CompressableDef def : PARSED.values()) {
            CompressedSource source = CompressedSource.of(def, defs);
            if (source == null || !ContentRegistry.available(source.requires, def.key)) { continue; }
            CompressedSet set = new CompressedSet(def, source);
            Map<ResourceLocation, BlockDef> made = new LinkedHashMap<>();
            for (int index = 0; index < set.blockCount(); index++) { made.put(set.blockName(index * BlockDef.MAX_META + 1), blockDef(set, index)); }
            ResourceLocation taken = null;
            for (ResourceLocation name : made.keySet()) {
                if (defs.containsKey(name)) { taken = name; }
            }
            if (taken != null) {
                ContentLog.LOGGER.error("The compressable {} would make block {}, which a pack already defines, so it gets no compressed layers", def.key, taken);
                continue;
            }
            defs.putAll(made);
            SETS.add(set);
            for (ResourceLocation name : made.keySet()) { BY_BLOCK.put(name, set); }
            ContentLog.LOGGER.debug("Compressable {} makes {} layer(s) in {} block(s)", def.key, set.layers, made.size());
        }
    }

    private static BlockDef blockDef(CompressedSet set, int index) {
        CompressedSource source = set.source;
        int first = index * BlockDef.MAX_META + 1;
        int last = Math.min(set.layers, first + BlockDef.MAX_META - 1);
        ResourceLocation name = set.blockName(first);
        BlockVariant[] byMeta = new BlockVariant[BlockDef.MAX_META];
        List<BlockVariant> visible = new ArrayList<>();
        String ore = Ids.camel(set.name.replace('/', '_'), true);
        for (int layer = first; layer <= last; layer++) {
            int meta = set.meta(layer);
            DropDef own = new DropDef(name, null, meta, AmountDef.of(1), 100, 0, new int[0]);
            BlockVariant variant = new BlockVariant(set.variantName(layer), meta, EnumRarity.COMMON, 64, Collections.singletonList("compressed" + ore + layer + "x"), source.hardness * (layer + 1), source.resistance * (layer + 1), source.harvestLevel, 0, null, Collections.singletonList(own), false);
            byMeta[meta] = variant;
            visible.add(variant);
        }
        BlockVariant highest = byMeta[set.meta(last)];
        if (set.meta(last) < BlockDef.MAX_META - 1) {
            ContentLog.LOGGER.info("Compressable {} has {} layer(s) in {}, so metas {}-{} of it, which a longer set may have left in a world, are read as layer {}", set.def.key, last - first + 1, name, set.meta(last) + 1, BlockDef.MAX_META - 1, last);
        }
        for (int meta = 0; meta < byMeta.length; meta++) {
            if (byMeta[meta] == null) { byMeta[meta] = highest; }
        }
        return new BlockDef(name, ContentBlockTypes.DEFAULT, source.material, source.mapColor, source.sound, source.creativeTab, source.tool, source.toolLevel, true, 0, 0, 1.0F, byMeta, Collections.unmodifiableList(visible), source.requires,
                ContentBlockTypes.defaultLayer(ContentBlockTypes.DEFAULT, true), true, true, 255, 0.6F, null, 0, 0, "minecraft:stone", 0, false, BlockDef.PARTICLE_FLAME, true, 0xFFFFFF, "", "", 7, null, null, null,
                Collections.emptyList(), Collections.emptyList(), "", "", 5, null, "", null, null, Collections.emptyList(), true, null, -1);
    }
}
