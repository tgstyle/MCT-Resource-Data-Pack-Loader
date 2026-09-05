package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.HardnessDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IWeightedBakedModel;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.level.LevelEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentHardnessCheck {
    private static final Gson GSON = new GsonBuilder().create();
    private static boolean looked;

    private ContentHardnessCheck() {}

    public static void watching() { ContentLog.LOGGER.info("Watching for hardness blockstate mismatches, which are reported when a world is entered"); }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (looked || !event.getLevel().isClientSide() || !Config.worldgen.worldgenDebug()) { return; }
        looked = true;
        look();
    }

    private static void look() {
        ContentLog.LOGGER.debug("Looking over {} whole block group(s) and {} exact state group(s) for hardness blockstate mismatches", ContentHardness.whole().size(), ContentHardness.exact().size());
        Set<BlockState> seen = new LinkedHashSet<>();
        for (Map.Entry<Block, HardnessDef> entry : ContentHardness.whole().entrySet()) {
            for (BlockState state : entry.getKey().getStateDefinition().getPossibleStates()) { measure(state, entry.getValue(), seen); }
        }
        for (Map.Entry<BlockState, HardnessDef> entry : ContentHardness.exact().entrySet()) {
            measure(entry.getKey(), entry.getValue(), seen);
            others(entry.getKey(), entry.getValue());
        }
        if (seen.isEmpty()) { ContentLog.LOGGER.debug("No hardness group asks for more than one step, so no blockstate needs variants"); }
    }

    private static void measure(BlockState state, HardnessDef def, Set<BlockState> seen) {
        if (def.buckets() <= 1 || !seen.add(state)) { return; }
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        shipped(state);
        if (!(model instanceof WeightedBakedModel weighted)) {
            ContentLog.LOGGER.error("Hardness group {} rolls {} steps for {}, but the baked model is a {}, which holds one model, so every step will look the same. List {} variants of equal weight, hardest first, and check no other mod is replacing the model after it is baked", def.key(), def.buckets(), state, model.getClass().getName(), def.buckets());
            return;
        }
        List<WeightedEntry.Wrapper<BakedModel>> models = ((IWeightedBakedModel) weighted).rdpl$getList();
        for (WeightedEntry.Wrapper<BakedModel> entry : models) { ContentLog.LOGGER.debug("  a variant that survived baking uses the texture {}", texture(entry.getData())); }
        int held = models.size();
        int weight = ((IWeightedBakedModel) weighted).rdpl$getTotalWeight();
        if (held != def.buckets()) {
            ContentLog.LOGGER.error("Hardness group {} rolls {} steps for {}, but the baked model holds {} variant(s), so the texture will not match how hard the block is. Make the two counts the same, and check no other mod is replacing the model after it is baked", def.key(), def.buckets(), state, held);
            return;
        }
        if (weight != held) {
            ContentLog.LOGGER.error("Hardness group {} lists {} variant(s) for {} but they do not weigh the same, adding up to {}, so the texture will not match how hard the block is. Give every variant a weight of 1", def.key(), held, state, weight);
            return;
        }
        ContentLog.LOGGER.debug("Hardness group {} lines up with {}: {} step(s), {} variant(s) of equal weight", def.key(), state, def.buckets(), held);
    }

    @SuppressWarnings("resource") private static ResourceLocation texture(BakedModel model) { return model.getParticleIcon(ModelData.EMPTY).contents().name(); }

    private static void others(BlockState state, HardnessDef def) {
        for (BlockState one : state.getBlock().getStateDefinition().getPossibleStates()) {
            if (one == state) { continue; }
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(one);
            int held = model instanceof WeightedBakedModel weighted ? ((IWeightedBakedModel) weighted).rdpl$getList().size() : 1;
            if (held <= 1) { continue; }
            ContentLog.LOGGER.warn("Hardness group {} does not name {}, but that state now draws from {} variant(s) too, so overriding the blockstate has changed a block the group was not meant to touch", def.key(), one, held);
        }
    }

    private static void shipped(BlockState state) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (name == null) { return; }
        String path = "blockstates/" + name.getPath() + ".json";
        chain(ResourceLocation.fromNamespaceAndPath(name.getNamespace(), path));
        boolean overriding = PackManager.get().existsRaw(PackType.CLIENT_RESOURCES, name.getNamespace(), path, true);
        boolean normal = PackManager.get().existsRaw(PackType.CLIENT_RESOURCES, name.getNamespace(), path, false);
        ContentLog.LOGGER.debug("A pack ships {}:{} at the overriding tier={} and the normal tier={}", name.getNamespace(), path, overriding, normal);
        if (overriding) { count(name.getNamespace(), path, true); }
        if (normal) { count(name.getNamespace(), path, false); }
    }

    private static void chain(ResourceLocation location) {
        List<Resource> found = Minecraft.getInstance().getResourceManager().getResourceStack(location);
        ContentLog.LOGGER.debug("  the game finds {} copy/copies of {}, in the order it merges them:", found.size(), location);
        for (Resource one : found) {
            StringBuilder says = new StringBuilder();
            try (InputStream stream = one.open()) {
                JsonObject held = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                if (held != null && held.has("variants")) {
                    for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(held, "variants").entrySet()) {
                        says.append(' ').append(entry.getKey()).append('=').append(entry.getValue().isJsonArray() ? entry.getValue().getAsJsonArray().size() : 1);
                    }
                }
                else { says.append(" no variants block"); }
            }
            catch (Exception ex) { says.append(" could not be read: ").append(ex); }
            ContentLog.LOGGER.debug("    from {}, holding{}", one.sourcePackId(), says);
        }
    }

    private static void count(String namespace, String path, boolean overriding) {
        try (InputStream stream = PackManager.get().openRaw(PackType.CLIENT_RESOURCES, namespace, path, overriding)) {
            if (stream == null) { return; }
            JsonObject json = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            if (json == null || !json.has("variants")) {
                ContentLog.LOGGER.debug("  the file the pack serves has no variants block at all");
                return;
            }
            for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "variants").entrySet()) {
                JsonElement held = entry.getValue();
                ContentLog.LOGGER.debug("  the file the pack serves lists {} entry/entries for '{}'", held.isJsonArray() ? held.getAsJsonArray().size() : 1, entry.getKey());
                if (!held.isJsonArray()) { continue; }
                for (JsonElement one : held.getAsJsonArray()) {
                    if (!one.isJsonObject() || !one.getAsJsonObject().has("model")) { continue; }
                    ResourceLocation named = ResourceLocation.tryParse(one.getAsJsonObject().get("model").getAsString());
                    if (named == null) { continue; }
                    String model = "models/" + named.getPath() + ".json";
                    ContentLog.LOGGER.debug("    it points at {}:{}, which the pack ships at the overriding tier={} and the normal tier={}", named.getNamespace(), model,
                            PackManager.get().existsRaw(PackType.CLIENT_RESOURCES, named.getNamespace(), model, true), PackManager.get().existsRaw(PackType.CLIENT_RESOURCES, named.getNamespace(), model, false));
                }
            }
        }
        catch (Exception ex) { ContentLog.LOGGER.debug("  could not read the file the pack serves: {}", ex.toString()); }
    }
}
