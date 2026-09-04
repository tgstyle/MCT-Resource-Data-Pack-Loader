package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.OverrideDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockBehaviour;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockStateBase;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IFireBlock;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IItem;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IPotion;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentOverrides {
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<String> TOOLS = List.of("pickaxe", "axe", "shovel", "hoe");
    private static final List<String> TIERS = List.of("minecraft:needs_stone_tool", "minecraft:needs_iron_tool", "minecraft:needs_diamond_tool");
    private static final int DEFAULT_FIRE_SPREAD = 5;
    private static final float DEFAULT_EAT_SECONDS = 1.6F;
    private static final Map<ResourceLocation, OverrideDef> DEFS = new LinkedHashMap<>();
    private static final Map<Block, BlockSnapshot> BLOCKS = new IdentityHashMap<>();
    private static final Map<Block, FireSnapshot> FIRE = new IdentityHashMap<>();
    private static final Map<Item, ItemSnapshot> ITEMS = new IdentityHashMap<>();
    private static final Map<Potion, List<MobEffectInstance>> POTIONS = new IdentityHashMap<>();
    private static final PackGeneration LOADED = new PackGeneration();
    private static final PackGeneration APPLIED = new PackGeneration();
    private static volatile Map<Block, Integer> lightBlocks = Collections.emptyMap();

    private ContentOverrides() {}

    @Nullable public static Integer lightBlock(Block block) {
        Map<Block, Integer> held = lightBlocks;
        return held.isEmpty() ? null : held.get(block);
    }

    public static void reload() {
        if (!APPLIED.stale()) { return; }
        load();
        restoreAll();
        Map<Block, Integer> lights = new IdentityHashMap<>();
        int applied = 0;
        for (OverrideDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.target())) { continue; }
            if (apply(def, lights)) { applied++; }
        }
        lightBlocks = lights.isEmpty() ? Collections.emptyMap() : lights;
        if (applied > 0) { Summary.info("overrides", "Applied " + applied + " override(s) to existing blocks, items and potion types"); }
        ContentGenerated.retag();
    }

    public static void harvestTags(Map<String, Set<String>> tags, Map<String, Set<String>> removed) {
        load();
        for (OverrideDef def : DEFS.values()) {
            if (def.harvestTool() == null || !ContentRegistry.available(def.requires(), def.target()) || !BuiltInRegistries.BLOCK.containsKey(def.target())) { continue; }
            ContentGenerated.harvestTags(tags, def.target(), def.harvestTool(), def.harvestToolLevel());
            for (String tool : TOOLS) {
                if (!tool.equals(def.harvestTool())) { ContentGenerated.tag(removed, "minecraft:mineable/" + tool, def.target()); }
            }
            int tier = Math.min(def.harvestToolLevel(), TIERS.size());
            for (int i = 0; i < TIERS.size(); i++) {
                if (i + 1 != tier) { ContentGenerated.tag(removed, TIERS.get(i), def.target()); }
            }
        }
    }

    private static void load() {
        if (!LOADED.stale()) { return; }
        DEFS.clear();
        if (!Config.content.overrides()) { return; }
        Json.eachFile(PackManager.OVERRIDES, "override file", (key, contents) -> {
            ResourceLocation source = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), PackManager.OVERRIDES + "/" + key.getPath());
            String path = key.getPath();
            int split = path.indexOf('/');
            if (split <= 0 || split == path.length() - 1) {
                ContentLog.LOGGER.error("Override file {} does not name a target. The path must be overrides/<namespace>/<name>.json, such as overrides/minecraft/stone.json", source);
                return;
            }
            ResourceLocation target = ResourceLocation.tryParse(path.substring(0, split) + ":" + path.substring(split + 1));
            if (target == null) {
                ContentLog.LOGGER.error("Override file {} names target '{}', which is not a valid id", source, path);
                return;
            }
            OverrideDef def = read(target, source, contents);
            if (def == null) { return; }
            OverrideDef previous = DEFS.put(target, def);
            if (previous != null) { ContentLog.LOGGER.debug("Override for {} from {} replaces the one from {}", target, source, previous.source()); }
        });
    }

    @Nullable private static OverrideDef read(ResourceLocation target, ResourceLocation source, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Override file {} is empty, ignoring it", source);
            return null;
        }
        String tool = json.has("harvestTool") ? GsonHelper.getAsString(json, "harvestTool").trim().toLowerCase(Locale.ROOT) : null;
        if (tool != null && !TOOLS.contains(tool)) {
            ContentLog.LOGGER.error("Override file {} names harvestTool '{}', which is not one of {}, leaving the tool alone", source, tool, TOOLS);
            tool = null;
        }
        List<PotionEffectDef> effects = null;
        if (json.has("effects")) {
            effects = ContentParser.effects(source, json);
            if (effects.isEmpty()) {
                ContentLog.LOGGER.error("Override file {} has an effects list with no usable effects. To empty a potion type, that is not supported; remove the key instead", source);
                effects = null;
            }
        }
        OverrideDef def = new OverrideDef(target, source,
                floatOrNull(json, "hardness"), floatOrNull(json, "resistance"), floatOrNull(json, "slipperiness"),
                intOrNull(json, "light"), intOrNull(json, "lightOpacity"), stringOrNull(json, "soundType"),
                tool, Math.max(0, GsonHelper.getAsInt(json, "harvestToolLevel", 0)),
                intOrNull(json, "flammability"), Math.max(0, GsonHelper.getAsInt(json, "fireSpread", DEFAULT_FIRE_SPREAD)),
                intOrNull(json, "maxStackSize"), intOrNull(json, "maxDamage"), stringOrNull(json, "containerItem"),
                effects, json.has("food") ? food(source, GsonHelper.getAsJsonObject(json, "food")) : null,
                Json.strings(json, "requires"));
        if (!def.touchesBlock() && !def.touchesItem() && !def.touchesPotionType()) {
            ContentLog.LOGGER.error("Override file {} changes nothing it knows how to change, ignoring it", source);
            return null;
        }
        return def;
    }

    private static OverrideDef.FoodDef food(ResourceLocation source, JsonObject json) {
        return new OverrideDef.FoodDef(Math.max(0, GsonHelper.getAsInt(json, "heal", 1)), Math.max(0.0F, GsonHelper.getAsFloat(json, "saturation", 0.6F)), GsonHelper.getAsBoolean(json, "alwaysEdible", false), ContentParser.effects(source, json));
    }

    private static boolean apply(OverrideDef def, Map<Block, Integer> lights) {
        boolean block = def.touchesBlock() && applyBlock(def, lights);
        boolean item = def.touchesItem() && applyItem(def);
        boolean potion = def.touchesPotionType() && applyPotion(def);
        if (!block && !item && !potion) {
            ContentLog.LOGGER.error("Override {} found nothing named {} it could change. Check the name and whether the mod that owns it is installed", def.source(), def.target());
            return false;
        }
        return true;
    }

    private static boolean applyBlock(OverrideDef def, Map<Block, Integer> lights) {
        Block block = Registered.find(BuiltInRegistries.BLOCK, def.target());
        if (block == null) { return false; }
        BLOCKS.computeIfAbsent(block, BlockSnapshot::of);
        IBlockBehaviour inside = (IBlockBehaviour) block;
        if (def.resistance() != null) { inside.rdpl$setExplosionResistance(Math.max(0.0F, def.resistance())); }
        if (def.slipperiness() != null) { inside.rdpl$setFriction(def.slipperiness()); }
        if (def.soundType() != null) { inside.rdpl$setSoundType(ContentTypes.soundType(def.soundType(), inside.rdpl$getSoundType(), def.source())); }
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            IBlockStateBase held = (IBlockStateBase) state;
            if (def.hardness() != null) { held.rdpl$setDestroySpeed(def.hardness()); }
            if (def.light() != null) { held.rdpl$setLightEmission(Math.clamp(def.light(), 0, 15)); }
            if (def.harvestTool() != null) { held.rdpl$setRequiresCorrectToolForDrops(true); }
        }
        if (def.lightOpacity() != null) { lights.put(block, Math.clamp(def.lightOpacity(), 0, 15)); }
        if (def.flammability() != null) {
            FIRE.computeIfAbsent(block, FireSnapshot::of);
            fire().setFlammable(block, def.fireSpread(), Math.max(0, def.flammability()));
        }
        return true;
    }

    private static boolean applyItem(OverrideDef def) {
        Item item = Registered.find(BuiltInRegistries.ITEM, def.target());
        if (item == null) { return false; }
        ITEMS.computeIfAbsent(item, ItemSnapshot::of);
        IItem inside = (IItem) item;
        DataComponentMap.Builder builder = DataComponentMap.builder().addAll(item.components());
        if (def.maxStackSize() != null) { builder.set(DataComponents.MAX_STACK_SIZE, Math.clamp(def.maxStackSize(), 1, 64)); }
        if (def.maxDamage() != null) {
            builder.set(DataComponents.MAX_DAMAGE, Math.max(0, def.maxDamage()));
            builder.set(DataComponents.DAMAGE, 0);
        }
        if (def.containerItem() != null) {
            Item container = ContentStacks.find(def.source(), def.containerItem());
            if (container != null) { inside.rdpl$setCraftingRemainingItem(container); }
        }
        OverrideDef.FoodDef food = def.food();
        if (food != null) { builder.set(DataComponents.FOOD, food(def, food, item.components().get(DataComponents.FOOD))); }
        inside.rdpl$setComponents(builder.build());
        return true;
    }

    private static FoodProperties food(OverrideDef def, OverrideDef.FoodDef food, @Nullable FoodProperties was) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(food.heal()).saturationModifier(food.saturation());
        if (food.alwaysEdible()) { builder.alwaysEdible(); }
        if (was != null && was.eatSeconds() < DEFAULT_EAT_SECONDS) { builder.fast(); }
        if (was != null) { was.usingConvertsTo().ifPresent(stack -> builder.usingConvertsTo(stack.getItem())); }
        if (food.effects().isEmpty() && was != null) {
            for (FoodProperties.PossibleEffect effect : was.effects()) { builder.effect(effect.effectSupplier(), effect.probability()); }
        }
        for (PotionEffectDef entry : food.effects()) {
            MobEffectInstance made = effect(def, entry);
            if (made != null) { builder.effect(() -> new MobEffectInstance(made), 1.0F); }
        }
        return builder.build();
    }

    private static boolean applyPotion(OverrideDef def) {
        List<PotionEffectDef> effects = def.effects();
        Potion potion = effects == null ? null : Registered.find(BuiltInRegistries.POTION, def.target());
        if (potion == null) { return false; }
        List<MobEffectInstance> resolved = new ArrayList<>();
        for (PotionEffectDef entry : effects) {
            MobEffectInstance made = effect(def, entry);
            if (made != null) { resolved.add(made); }
        }
        POTIONS.computeIfAbsent(potion, held -> List.copyOf(held.getEffects()));
        ((IPotion) potion).rdpl$setEffects(List.copyOf(resolved));
        return true;
    }

    @Nullable private static MobEffectInstance effect(OverrideDef def, PotionEffectDef entry) {
        Holder<MobEffect> effect = Registered.holder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.tryParse(entry.potion()));
        if (effect == null) {
            ContentLog.LOGGER.error("Override {} names effect {}, which does not exist, skipping that effect", def.source(), entry.potion());
            return null;
        }
        return new MobEffectInstance(effect, entry.duration(), entry.amplifier(), entry.ambient(), entry.showParticles());
    }

    private static void restoreAll() {
        BLOCKS.forEach((block, held) -> held.restore(block));
        FIRE.forEach((block, held) -> held.restore(block));
        ITEMS.forEach((item, held) -> held.restore(item));
        POTIONS.forEach((potion, held) -> ((IPotion) potion).rdpl$setEffects(held));
        BLOCKS.clear();
        FIRE.clear();
        ITEMS.clear();
        POTIONS.clear();
        lightBlocks = Collections.emptyMap();
    }

    private static FireBlock fire() { return (FireBlock) Blocks.FIRE; }

    @Nullable private static Float floatOrNull(JsonObject json, String key) { return json.has(key) ? GsonHelper.getAsFloat(json, key) : null; }

    @Nullable private static Integer intOrNull(JsonObject json, String key) { return json.has(key) ? GsonHelper.getAsInt(json, key) : null; }

    @Nullable private static String stringOrNull(JsonObject json, String key) { return json.has(key) ? GsonHelper.getAsString(json, key) : null; }

    private record BlockSnapshot(float resistance, float friction, SoundType sound, float[] speeds, int[] lights, boolean[] tools) {
        static BlockSnapshot of(Block block) {
            IBlockBehaviour inside = (IBlockBehaviour) block;
            List<BlockState> states = block.getStateDefinition().getPossibleStates();
            float[] speeds = new float[states.size()];
            int[] lights = new int[states.size()];
            boolean[] tools = new boolean[states.size()];
            for (int i = 0; i < states.size(); i++) {
                IBlockStateBase state = (IBlockStateBase) states.get(i);
                speeds[i] = state.rdpl$getDestroySpeed();
                lights[i] = state.rdpl$getLightEmission();
                tools[i] = states.get(i).requiresCorrectToolForDrops();
            }
            return new BlockSnapshot(inside.rdpl$getExplosionResistance(), inside.rdpl$getFriction(), inside.rdpl$getSoundType(), speeds, lights, tools);
        }

        void restore(Block block) {
            IBlockBehaviour inside = (IBlockBehaviour) block;
            inside.rdpl$setExplosionResistance(resistance);
            inside.rdpl$setFriction(friction);
            inside.rdpl$setSoundType(sound);
            List<BlockState> states = block.getStateDefinition().getPossibleStates();
            for (int i = 0; i < states.size(); i++) {
                IBlockStateBase state = (IBlockStateBase) states.get(i);
                state.rdpl$setDestroySpeed(speeds[i]);
                state.rdpl$setLightEmission(lights[i]);
                state.rdpl$setRequiresCorrectToolForDrops(tools[i]);
            }
        }
    }

    private record FireSnapshot(@Nullable Integer ignite, @Nullable Integer burn) {
        static FireSnapshot of(Block block) {
            IFireBlock fire = (IFireBlock) fire();
            return new FireSnapshot(fire.rdpl$getIgniteOdds().containsKey(block) ? fire.rdpl$getIgniteOdds().getInt(block) : null, fire.rdpl$getBurnOdds().containsKey(block) ? fire.rdpl$getBurnOdds().getInt(block) : null);
        }

        void restore(Block block) {
            IFireBlock fire = (IFireBlock) fire();
            if (ignite == null || burn == null) {
                fire.rdpl$getIgniteOdds().removeInt(block);
                fire.rdpl$getBurnOdds().removeInt(block);
                return;
            }
            fire().setFlammable(block, ignite, burn);
        }
    }

    private record ItemSnapshot(DataComponentMap components, @Nullable Item remaining) {
        static ItemSnapshot of(Item item) { return new ItemSnapshot(item.components(), ((IItem) item).rdpl$getCraftingRemainingItem()); }

        void restore(Item item) {
            IItem inside = (IItem) item;
            inside.rdpl$setComponents(components);
            inside.rdpl$setCraftingRemainingItem(remaining);
        }
    }
}
