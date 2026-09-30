package mctmods.resourcedatapackloader.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ConditionContext;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public final class RecipeFiles {
    private static final FileToIdConverter FILES = FileToIdConverter.registry(Registries.RECIPE);
    private static final Codec<Optional<Recipe<?>>> CODEC = ConditionalOps.createConditionalCodec(NeoForgeExtraCodecs.decodeOnly(Recipe.DIRECT_CODEC));

    private RecipeFiles() {}

    public static Map<Identifier, Resource> filter(Map<Identifier, Resource> listed, ResourceManager manager, RegistryOps.RegistryInfoLookup context, List<Registry.PendingTags<?>> pendingTags) {
        RegistryOps<JsonElement> ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, context), new ConditionContext(pendingTags, context, FeatureFlags.VANILLA_SET));
        Map<Identifier, JsonElement> read = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : listed.entrySet()) {
            JsonElement json = json(entry.getValue());
            if (json != null) { read.put(FILES.fileToId(entry.getKey()), json); }
        }
        Map<Identifier, JsonElement> kept = new HashMap<>(read);
        RecipeLoading.begin(kept, manager, json -> ICondition.conditionsMatched(ops, json), json -> CODEC.parse(ops, json).getOrThrow(JsonParseException::new));
        Set<Identifier> removed = new HashSet<>(read.keySet());
        removed.removeAll(kept.keySet());
        RecipeAdvancements.publish(context, removed);
        Map<Identifier, Resource> result = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : listed.entrySet()) {
            Identifier id = FILES.fileToId(entry.getKey());
            JsonElement now = kept.get(id);
            JsonElement was = read.get(id);
            if (now == null) {
                if (was == null) { result.put(entry.getKey(), entry.getValue()); }
                continue;
            }
            result.put(entry.getKey(), now == was ? entry.getValue() : rewritten(entry.getValue(), now));
        }
        return result;
    }

    private static Resource rewritten(Resource from, JsonElement json) {
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        return new Resource(from.source(), () -> new ByteArrayInputStream(bytes));
    }

    @Nullable private static JsonElement json(Resource resource) {
        try (Reader reader = resource.openAsReader()) { return StrictJsonParser.parse(reader); }
        catch (IOException | JsonParseException ex) { return null; }
    }
}
