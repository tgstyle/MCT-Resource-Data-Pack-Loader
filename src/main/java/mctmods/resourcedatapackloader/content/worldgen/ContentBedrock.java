package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentBedrock {
    private static final String BEDROCK_FLOOR = "bedrock_floor";
    private static final String BEDROCK_ROOF = "bedrock_roof";
    private ContentBedrock() {}

    static void flattenBedrock(JsonObject settings, String dimension) {
        if (!bedrockBiomeTypes().isEmpty()) {
            ContentLog.LOGGER.debug("flatBedrockBiomeTypes is set, so the bedrock of {} is flattened chunk by chunk instead of in its noise settings", dimension);
            return;
        }
        int layers = layers();
        String filler = bedrockFiller(dimension);
        JsonArray sequence = WorldgenJson.sequenceOf(settings);
        for (int index = 0; index < sequence.size(); index++) {
            JsonElement element = sequence.get(index);
            if (!element.isJsonObject()) { continue; }
            JsonObject entry = element.getAsJsonObject();
            String gradient = WorldgenJson.gradientName(entry);
            if (gradient == null) { continue; }
            boolean roof = gradient.endsWith(BEDROCK_ROOF);
            if (roof && !roofWanted()) { continue; }
            if (!roof && !gradient.endsWith(BEDROCK_FLOOR)) { continue; }
            JsonObject bedrock = WorldgenJson.block("minecraft:bedrock");
            JsonObject flat;
            if (roof) { flat = WorldgenJson.condition(WorldgenJson.yAbove(WorldgenJson.anchor("below_top", layers - 1)), bedrock); }
            else {
                flat = WorldgenJson.condition(WorldgenJson.not(WorldgenJson.yAbove(WorldgenJson.anchor("above_bottom", layers))), bedrock);
            }
            if (filler != null) { flat = WorldgenJson.sequence(List.of(flat, WorldgenJson.condition(GsonHelper.getAsJsonObject(entry, "if_true").deepCopy(), WorldgenJson.block(filler)))); }
            List<String> biomes = bedrockBiomes();
            if (biomes.isEmpty()) {
                sequence.set(index, flat);
                continue;
            }
            JsonObject inBiomes = WorldgenJson.biomeIs(BiomeNames.ids(biomes));
            JsonObject outside = WorldgenJson.not(inBiomes);
            boolean blacklist = ContentControl.flag(ContentControl.BEDROCK, "flatBedrockBiomesAreBlacklist", Config.worldgen.flatBedrockBiomesAreBlacklist());
            sequence.set(index, WorldgenJson.sequence(List.of(WorldgenJson.condition(blacklist ? outside : inBiomes, flat), WorldgenJson.condition(blacklist ? inBiomes : outside, entry))));
        }
        ContentLog.LOGGER.debug("Flattened the bedrock of {} to {} layer(s){}", dimension, layers, roofWanted() ? " including the roof" : "");
    }

    static boolean bedrockAsked() {
        if (ContentControl.off(ContentControl.BEDROCK)) { return false; }
        return ContentControl.flag(ContentControl.BEDROCK, "flatBedrock", Config.worldgen.flatBedrock());
    }

    static void report() {
        if (!bedrockAsked()) { return; }
        List<String> dimensions = ContentControl.list(ContentControl.BEDROCK, "flatBedrockDimensions", Config.worldgen.flatBedrockDimensions());
        Summary.info("bedrock", "Flattening bedrock to " + layers() + " layer(s) in " + (dimensions.isEmpty() ? "every dimension" : "dimension(s) " + dimensions));
    }

    static boolean bedrockApplies(String dimension) {
        if (!bedrockAsked()) { return false; }
        return ContentWorldShape.listed(dimension, ContentControl.list(ContentControl.BEDROCK, "flatBedrockDimensions", Config.worldgen.flatBedrockDimensions()),
                ContentControl.flag(ContentControl.BEDROCK, "flatBedrockDimensionsAreBlacklist", Config.worldgen.flatBedrockDimensionsAreBlacklist()));
    }

    static int layers() { return Mth.clamp(ContentControl.number(ContentControl.BEDROCK, "bedrockLayers", Config.worldgen.bedrockLayers()), 1, ContentWorldShape.MAX_LAYERS); }

    private static boolean roofWanted() { return ContentControl.flag(ContentControl.BEDROCK, "flatBedrockRoof", Config.worldgen.flatBedrockRoof()); }

    private static List<String> bedrockBiomes() { return ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomes", Config.worldgen.flatBedrockBiomes()); }

    private static List<TagKey<Biome>> bedrockBiomeTypes() {
        List<TagKey<Biome>> tags = new ArrayList<>();
        for (String type : ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomeTypes", Config.worldgen.flatBedrockBiomeTypes())) {
            String named = type.indexOf(':') >= 0 ? type.trim() : ContentFormats.biomeTag(type);
            ResourceLocation id = named == null ? null : ResourceLocation.tryParse(named.startsWith("#") ? named.substring(1) : named);
            if (id != null) { tags.add(TagKey.create(Registries.BIOME, id)); }
            else if (ContentWorldShape.WARNED.add("flatBedrockBiomeTypes:" + type)) { ContentLog.LOGGER.error("flatBedrockBiomeTypes names '{}', which no biome tag on this line answers to", type); }
        }
        return tags;
    }

    @Nullable private static String bedrockFiller(String dimension) {
        for (String entry : ContentControl.list(ContentControl.BEDROCK, "flatBedrockFillers", Config.worldgen.flatBedrockFillers())) {
            int split = entry.indexOf('=');
            if (split < 0) {
                if (ContentWorldShape.WARNED.add("flatBedrockFillers:" + entry)) { ContentLog.LOGGER.error("flatBedrockFillers entry '{}' is not written as dimension=block, ignoring it", entry); }
                continue;
            }
            if (!ContentFormats.dimensionId(entry.substring(0, split)).equals(dimension)) { continue; }
            String name = entry.substring(split + 1).trim();
            if (ContentWorldShape.block(name, "flatBedrockFillers") != null) { return name; }
        }
        String configured = ContentControl.text(ContentControl.BEDROCK, "flatBedrockFiller", Config.worldgen.flatBedrockFiller()).trim();
        return configured.isEmpty() || ContentWorldShape.block(configured, "flatBedrockFiller") == null ? null : configured;
    }

    static String bedrockToken() { return "bedrock:" + layers() + (roofWanted() ? ":roof" : "") + "#" + Config.worldgen.flatBedrockRetrogenKey(); }

    static boolean bedrockRetrogen() { return bedrockAsked() && ContentControl.flag(ContentControl.BEDROCK, "flatBedrockRetrogen", Config.worldgen.flatBedrockRetrogen()); }

    static boolean flattens(ServerLevel level) { return bedrockApplies(level.dimension().location().toString()); }

    public static void flattenChunk(WorldGenLevel level, ChunkPos at) {
        ServerLevel server = level.getLevel();
        if (!flattens(server)) { return; }
        int baseX = at.getMinBlockX();
        int baseZ = at.getMinBlockZ();
        int bottom = server.getMinBuildHeight();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        if (unfloored(level, pos, baseX, baseZ, bottom)) { return; }
        String dimension = server.dimension().location().toString();
        String fillerName = bedrockFiller(dimension);
        Block chosen = fillerName == null ? null : ContentWorldShape.block(fillerName, "flatBedrockFiller");
        BlockState filler = chosen != null ? chosen.defaultBlockState() : server.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator noise ? noise.generatorSettings().value().defaultBlock() : Blocks.STONE.defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        int layers = layers();
        List<String> names = bedrockBiomes();
        List<TagKey<Biome>> types = bedrockBiomeTypes();
        boolean filtered = !names.isEmpty() || !types.isEmpty();
        boolean blacklist = ContentControl.flag(ContentControl.BEDROCK, "flatBedrockBiomesAreBlacklist", Config.worldgen.flatBedrockBiomesAreBlacklist());
        int top = bottom + server.dimensionType().logicalHeight() - 1;
        boolean roof = roofWanted() && level.getBlockState(pos.set(baseX, top, baseZ)).is(Blocks.BEDROCK);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (filtered && bedrockBiome(level.getBiome(pos.set(baseX + x, bottom, baseZ + z)), names, types) == blacklist) { continue; }
                for (int y = bottom + ContentWorldShape.MAX_LAYERS - 1; y >= bottom + layers; y--) { swap(level, pos.set(baseX + x, y, baseZ + z), filler, true); }
                for (int y = bottom + layers - 1; y >= bottom; y--) { swap(level, pos.set(baseX + x, y, baseZ + z), bedrock, false); }
                if (!roof) { continue; }
                for (int y = top - ContentWorldShape.MAX_LAYERS + 1; y <= top - layers; y++) { swap(level, pos.set(baseX + x, y, baseZ + z), filler, true); }
                for (int y = top - layers + 1; y <= top; y++) { swap(level, pos.set(baseX + x, y, baseZ + z), bedrock, false); }
            }
        }
    }

    private static void swap(WorldGenLevel level, BlockPos.MutableBlockPos pos, BlockState state, boolean fromBedrock) {
        if (level.getBlockState(pos).is(Blocks.BEDROCK) == fromBedrock) { level.setBlock(pos, state, 2 | 16); }
    }

    private static boolean unfloored(WorldGenLevel level, BlockPos.MutableBlockPos pos, int baseX, int baseZ, int bottom) {
        for (int offset = 0; offset < 16; offset += 5) {
            if (level.getBlockState(pos.set(baseX + offset, bottom, baseZ + offset)).is(Blocks.BEDROCK)) { return false; }
        }
        return true;
    }

    private static boolean bedrockBiome(Holder<Biome> biome, List<String> names, List<TagKey<Biome>> types) {
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        if (id != null && BiomeNames.named(id, names)) { return true; }
        for (TagKey<Biome> type : types) {
            if (biome.is(type)) { return true; }
        }
        return false;
    }
}
