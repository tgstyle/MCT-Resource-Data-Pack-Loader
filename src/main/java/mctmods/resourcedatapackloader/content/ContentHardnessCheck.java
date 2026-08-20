package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.HardnessDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IWeightedBakedModel;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IWeightedModel;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentHardnessCheck {
    private static boolean looked;

    private ContentHardnessCheck() {}

    public static void watching() { ContentLog.LOGGER.info("Watching for hardness blockstate mismatches, which are reported when a world is entered"); }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        ContentLog.LOGGER.debug("A world loaded while watching for hardness blockstate mismatches: looked={} remote={} debug={}", looked, event.getWorld().isRemote, Config.worldgen.worldgenDebug);
        if (looked || !event.getWorld().isRemote || !Config.worldgen.worldgenDebug) { return; }
        looked = true;
        look();
    }

    private static void look() {
        ContentLog.LOGGER.debug("Looking over {} whole block group(s) and {} exact state group(s) for hardness blockstate mismatches", ContentHardness.whole().size(), ContentHardness.exact().size());
        Set<IBlockState> seen = new LinkedHashSet<>();
        for (Map.Entry<Block, HardnessDef> entry : ContentHardness.whole().entrySet()) {
            for (IBlockState state : entry.getKey().getBlockState().getValidStates()) { measure(state, entry.getValue(), seen); }
        }
        for (Map.Entry<IBlockState, HardnessDef> entry : ContentHardness.exact().entrySet()) {
            measure(entry.getKey(), entry.getValue(), seen);
            others(entry.getKey(), entry.getValue());
        }
        if (seen.isEmpty()) { ContentLog.LOGGER.debug("No hardness group asks for more than one step, so no blockstate needs variants"); }
    }

    private static void measure(IBlockState state, HardnessDef def, Set<IBlockState> seen) {
        if (def.buckets <= 1 || empty(state) || !seen.add(state)) { return; }
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        shipped(state);
        if (!(model instanceof WeightedBakedModel)) {
            ContentLog.LOGGER.error("Hardness group {} rolls {} steps for {}, but the baked model is a {}, which holds one model, so every step will look the same. List {} variants of equal weight, hardest first, and check no other mod is replacing the model after it is baked", def.registryName, def.buckets, state, model.getClass().getName(), def.buckets);
            return;
        }
        IWeightedBakedModel weighted = (IWeightedBakedModel) model;
        int held = weighted.rdpl$getModels().size();
        for (Object entry : weighted.rdpl$getModels()) {
            IBakedModel baked = ((IWeightedModel) entry).rdpl$getModel();
            ContentLog.LOGGER.debug("  a variant that survived baking uses the texture {}", baked.getParticleTexture().getIconName());
        }
        int weight = weighted.rdpl$getTotalWeight();
        if (held != def.buckets) {
            ContentLog.LOGGER.error("Hardness group {} rolls {} steps for {}, but the baked model, a {}, holds {} variant(s), so the texture will not match how hard the block is. Make the two counts the same, and check no other mod is replacing the model after it is baked", def.registryName, def.buckets, state, model.getClass().getName(), held);
            return;
        }
        if (weight != held) {
            ContentLog.LOGGER.error("Hardness group {} lists {} variant(s) for {} but they do not weigh the same, adding up to {}, so the texture will not match how hard the block is. Give every variant a weight of 1", def.registryName, held, state, weight);
            return;
        }
        ContentLog.LOGGER.debug("Hardness group {} lines up with {}: {} step(s), {} variant(s) of equal weight", def.registryName, state, def.buckets, held);
    }

    private static boolean empty(IBlockState state) {
        if (!(state.getBlock() instanceof IContentBlock)) { return false; }
        IContentBlock block = (IContentBlock) state.getBlock();
        return block.getDef().at(state.getBlock().getMetaFromState(state)).hidden;
    }

    private static void others(IBlockState state, HardnessDef def) {
        for (IBlockState one : state.getBlock().getBlockState().getValidStates()) {
            if (one == state || empty(one)) { continue; }
            IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(one);
            int held = model instanceof WeightedBakedModel ? ((IWeightedBakedModel) model).rdpl$getModels().size() : 1;
            if (held <= 1) { continue; }
            ContentLog.LOGGER.warn("Hardness group {} does not name {}, but that state now draws from {} variant(s) too, so overriding the blockstate has changed a block the group was not meant to touch", def.registryName, one, held);
        }
    }

    private static void shipped(IBlockState state) {
        ResourceLocation name = state.getBlock().getRegistryName();
        if (name == null) { return; }
        String path = "blockstates/" + name.getPath() + ".json";
        chain(new ResourceLocation(name.getNamespace(), path));
        boolean overriding = PackManager.get().existsRaw(name.getNamespace(), path, true);
        boolean normal = PackManager.get().existsRaw(name.getNamespace(), path, false);
        ContentLog.LOGGER.debug("A pack ships {}:{} at the overriding tier={} and the normal tier={}", name.getNamespace(), path, overriding, normal);
        if (overriding) { count(name.getNamespace(), path, true); }
        if (normal) { count(name.getNamespace(), path, false); }
    }

    private static void chain(ResourceLocation location) {
        try {
            List<IResource> found = Minecraft.getMinecraft().getResourceManager().getAllResources(location);
            ContentLog.LOGGER.debug("  the game finds {} copy/copies of {}, in the order it merges them:", found.size(), location);
            for (IResource one : found) {
                StringBuilder says = new StringBuilder();
                try {
                    JsonObject held = new Gson().fromJson(new InputStreamReader(one.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (held != null && held.has("variants")) {
                        for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(held, "variants").entrySet()) {
                            says.append(' ').append(entry.getKey()).append('=').append(entry.getValue().isJsonArray() ? entry.getValue().getAsJsonArray().size() : 1);
                        }
                    }
                    else { says.append(" no variants block"); }
                }
                catch (Exception ex) { says.append(" could not be read: ").append(ex); }
                ContentLog.LOGGER.debug("    from {}, holding{}", one.getResourcePackName(), says);
            }
        }
        catch (Exception ex) { ContentLog.LOGGER.debug("  the game could not list copies of {}: {}", location, ex.toString()); }
    }

    private static void count(String namespace, String path, boolean overriding) {
        try (InputStream stream = PackManager.get().openRaw(namespace, path, overriding)) {
            if (stream == null) { return; }
            JsonObject json = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            if (json == null || !json.has("variants")) {
                ContentLog.LOGGER.debug("  the file the pack serves has no variants block at all");
                return;
            }
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "variants").entrySet()) {
                JsonElement held = entry.getValue();
                ContentLog.LOGGER.debug("  the file the pack serves lists {} entry/entries for '{}'", held.isJsonArray() ? held.getAsJsonArray().size() : 1, entry.getKey());
                if (!held.isJsonArray()) { continue; }
                for (JsonElement one : held.getAsJsonArray()) {
                    if (!one.isJsonObject() || !one.getAsJsonObject().has("model")) { continue; }
                    ResourceLocation named = new ResourceLocation(one.getAsJsonObject().get("model").getAsString());
                    String model = "models/block/" + named.getPath() + ".json";
                    ContentLog.LOGGER.debug("    it points at {}:{}, which the pack ships at the overriding tier={} and the normal tier={}", named.getNamespace(), model,
                            PackManager.get().existsRaw(named.getNamespace(), model, true), PackManager.get().existsRaw(named.getNamespace(), model, false));
                }
            }
        }
        catch (Exception ex) { ContentLog.LOGGER.debug("  could not read the file the pack serves: {}", ex.toString()); }
    }
}
