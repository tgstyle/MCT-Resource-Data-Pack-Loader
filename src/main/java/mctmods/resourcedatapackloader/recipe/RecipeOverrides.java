package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.Config;
import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.mixin.InvokerJsonContext;
import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

public final class RecipeOverrides {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String CONSTANTS = "_constants.json";
    private static final String CONDITIONS = "conditions";
    private static final String ITEM = "item";
    private static final String EXTENSION = "." + PackManager.JSON;
    private static final Map<String, JsonContext> CONTEXTS = new HashMap<>();
    private static final Map<String, Boolean> REGISTERED = new HashMap<>();
    private static final Set<ResourceLocation> SERVED = new HashSet<>();
    private static int overridden;
    private static int skipped;

    private RecipeOverrides() {}

    private static boolean disabled() { return Config.settings.disableRecipeOverrides || PackManager.get().isEmpty(); }

    public static BiFunction<Path, Path, Boolean> wrap(BiFunction<Path, Path, Boolean> original) {
        boolean overrides = !disabled();
        boolean skipMissing = Config.settings.skipRecipesWithMissingItems;
        if (!overrides && !skipMissing) { return original; }
        return (root, file) -> {
            if (overrides && serve(root, file)) { return Boolean.TRUE; }
            if (skipMissing && skip(root, file)) { return Boolean.TRUE; }
            return original.apply(root, file);
        };
    }

    private static boolean serve(Path root, Path file) {
        String relative = root.relativize(file).toString().replace('\\', '/');
        if (!relative.endsWith(EXTENSION) || relative.startsWith("_")) { return false; }
        String modid = modIdOf(root);
        if (modid == null) { return false; }
        String path = relative.substring(0, relative.length() - EXTENSION.length());
        String contents = PackManager.get().read(modid, path, PackManager.RECIPES, PackManager.JSON);
        if (contents == null) { return false; }
        ResourceLocation key = new ResourceLocation(modid, path);
        IRecipe recipe = build(key, contents, context(modid, root));
        if (recipe == null) { return false; }
        setOwner(modid);
        ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
        SERVED.add(key);
        overridden++;
        return true;
    }

    private static boolean skip(Path root, Path file) {
        String relative = root.relativize(file).toString().replace('\\', '/');
        if (!relative.endsWith(EXTENSION) || relative.startsWith("_")) { return false; }
        String modid = modIdOf(root);
        if (modid == null) { return false; }
        JsonObject json = read(file);
        if (json == null) { return false; }
        String missing = findMissing(json, modid);
        if (missing == null) { return false; }
        if (!conditionsPass(json, modid, root)) { return false; }
        String path = relative.substring(0, relative.length() - EXTENSION.length());
        MCTMixin.LOGGER.debug("Skipping recipe {}:{}, it uses '{}' which is not registered", modid, path, missing);
        skipped++;
        return true;
    }

    @Nullable private static JsonObject read(Path file) {
        try {
            String contents = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            return JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        }
        catch (IOException | IllegalArgumentException | JsonParseException ex) { return null; }
    }

    private static boolean conditionsPass(JsonObject json, String modid, Path root) {
        try { return CraftingHelper.processConditions(json, CONDITIONS, context(modid, root)); }
        catch (RuntimeException ex) { return false; }
    }

    @Nullable private static String findMissing(JsonElement element, String modid) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                String missing = findMissing(child, modid);
                if (missing != null) { return missing; }
            }
            return null;
        }
        if (!element.isJsonObject()) { return null; }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if (CONDITIONS.equals(entry.getKey())) { continue; }
            JsonElement value = entry.getValue();
            if (ITEM.equals(entry.getKey()) && value.isJsonPrimitive()) {
                String name = qualify(value.getAsString(), modid);
                if (name != null && !registered(name)) { return name; }
                continue;
            }
            String missing = findMissing(value, modid);
            if (missing != null) { return missing; }
        }
        return null;
    }

    @Nullable private static String qualify(String name, String modid) {
        if (name.isEmpty() || name.charAt(0) == '#') { return null; }
        return name.indexOf(':') < 0 ? modid + ":" + name : name;
    }

    private static boolean registered(String name) {
        Boolean known = REGISTERED.get(name);
        if (known != null) { return known; }
        boolean present;
        try { present = ForgeRegistries.ITEMS.containsKey(new ResourceLocation(name)); }
        catch (RuntimeException ex) { present = true; }
        REGISTERED.put(name, present);
        return present;
    }

    @Nullable private static String modIdOf(Path root) {
        Path parent = root.getParent();
        if (parent == null) { return null; }
        Path name = parent.getFileName();
        if (name == null) { return null; }
        String raw = name.toString();
        if (raw.endsWith("/") || raw.endsWith("\\")) { return raw.substring(0, raw.length() - 1); }
        return raw;
    }

    @Nullable private static IRecipe build(ResourceLocation key, String contents, JsonContext ctx) {
        try {
            JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
            if (json == null) {
                MCTMixin.LOGGER.error("Recipe {} is empty or null, leaving the original in place", key);
                return null;
            }
            if (!CraftingHelper.processConditions(json, CONDITIONS, ctx)) {
                MCTMixin.LOGGER.info("Recipe {} was skipped by its own conditions, leaving the original in place", key);
                return null;
            }
            IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
            if (recipe == null) { MCTMixin.LOGGER.error("Recipe {} produced no result, leaving the original in place", key); }
            return recipe;
        }
        catch (IllegalArgumentException | JsonParseException ex) {
            MCTMixin.LOGGER.error("Parsing error in recipe {}, leaving the original in place", key, ex);
            return null;
        }
    }

    private static void setOwner(String modid) {
        ModContainer owner = Loader.instance().getIndexedModList().get(modid);
        if (owner != null) { Loader.instance().setActiveModContainer(owner); }
    }

    private static JsonContext context(String modid, @Nullable Path root) {
        JsonContext ctx = CONTEXTS.get(modid);
        if (ctx != null) { return ctx; }
        ctx = new JsonContext(modid);
        if (root != null) { loadConstants(root, ctx); }
        CONTEXTS.put(modid, ctx);
        return ctx;
    }

    private static void loadConstants(Path root, JsonContext ctx) {
        Path file = root.resolve(CONSTANTS);
        if (!Files.isRegularFile(file)) { return; }
        try {
            String contents = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            JsonObject[] json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject[].class);
            if (json != null) { ((InvokerJsonContext) ctx).rdpl$loadConstants(json); }
        }
        catch (IOException | IllegalArgumentException | JsonParseException ex) {
            MCTMixin.LOGGER.error("Could not read {} for {}, recipe constants will be unavailable", CONSTANTS, ctx.getModId(), ex);
        }
    }

    public static void registerAdditions(IForgeRegistry<IRecipe> registry) {
        if (skipped > 0) { MCTMixin.LOGGER.info("Skipped {} recipe(s) that use items which are not registered, usually content a mod's config has disabled", skipped); }
        skipped = 0;
        REGISTERED.clear();
        if (disabled()) { return; }
        int[] added = new int[1];
        int[] replaced = new int[1];
        PackManager.get().forEach(PackManager.RECIPES, PackManager.JSON, (namespace, path, contents) -> {
            if (path.startsWith("_")) { return; }
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (SERVED.contains(key)) { return; }
            IRecipe recipe = build(key, contents, context(namespace, null));
            if (recipe == null) { return; }
            boolean existing = registry.containsKey(key);
            if (existing && !remove(registry, key)) { return; }
            setOwner(namespace);
            registry.register(recipe.setRegistryName(key));
            if (existing) { replaced[0]++; }
            else { added[0]++; }
        });
        int total = overridden + replaced[0];
        if (total > 0 || added[0] > 0) { MCTMixin.LOGGER.info("Recipes: {} replaced, {} added", total, added[0]); }
        SERVED.clear();
        CONTEXTS.clear();
        overridden = 0;
    }

    private static boolean remove(IForgeRegistry<IRecipe> registry, ResourceLocation key) {
        if (!(registry instanceof IForgeRegistryModifiable)) {
            MCTMixin.LOGGER.error("Cannot replace recipe {}, the registry does not allow removal", key);
            return false;
        }
        ((IForgeRegistryModifiable<IRecipe>) registry).remove(key);
        return true;
    }
}
