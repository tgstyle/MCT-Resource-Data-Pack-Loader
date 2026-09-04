package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.OverrideDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlock;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockFire;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IItem;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IItemFood;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IPotionType;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public final class ContentOverrides {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, OverrideDef> DEFS = new LinkedHashMap<>();
    private static final Map<Block, BlockSnapshot> BLOCKS = new IdentityHashMap<>();
    private static final Map<Block, FireSnapshot> FIRE = new IdentityHashMap<>();
    private static final Map<Item, ItemSnapshot> ITEMS = new IdentityHashMap<>();
    private static final Map<Item, FoodSnapshot> FOODS = new IdentityHashMap<>();
    private static final Map<PotionType, ImmutableList<PotionEffect>> TYPES = new IdentityHashMap<>();
    private static final Map<Item, OverrideDef.FoodDef> EDIBLE = new IdentityHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentOverrides() {}

    @Nullable public static OverrideDef.FoodDef edible(Item item) { return EDIBLE.isEmpty() ? null : EDIBLE.get(item); }

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        restoreAll();
        DEFS.clear();
        EDIBLE.clear();
        if (!Config.content.overrides) { return; }
        PackManager.get().forEach(PackManager.OVERRIDES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation source = new ResourceLocation(namespace, PackManager.OVERRIDES + "/" + path);
            int split = path.indexOf('/');
            if (split <= 0 || split == path.length() - 1) {
                ContentLog.LOGGER.error("Override file {} does not name a target. The path must be overrides/<namespace>/<name>.json, such as overrides/minecraft/stone.json", source);
                return;
            }
            ResourceLocation target = new ResourceLocation(path.substring(0, split), path.substring(split + 1));
            try {
                OverrideDef def = read(target, source, contents);
                if (def == null) { return; }
                OverrideDef previous = DEFS.put(target, def);
                if (previous != null) { ContentLog.LOGGER.debug("Override for {} from {} replaces the one from {}", target, source, previous.source); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in override file {}, ignoring it: {}", source, ex.getMessage()); }
        });
        int applied = 0;
        for (OverrideDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires, def.target)) { continue; }
            if (apply(def)) { applied++; }
        }
        if (applied > 0) { Summary.info("overrides", "Applied " + applied + " override(s) to existing blocks, items and potion types"); }
    }

    @Nullable private static OverrideDef read(ResourceLocation target, ResourceLocation source, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Override file {} is empty, ignoring it", source);
            return null;
        }
        OverrideDef def = new OverrideDef(target, source,
                floatOrNull(json, "hardness"),
                floatOrNull(json, "resistance"),
                floatOrNull(json, "slipperiness"),
                intOrNull(json, "light"),
                intOrNull(json, "lightOpacity"),
                stringOrNull(json, "soundType"),
                stringOrNull(json, "harvestTool"),
                json.has("harvestTool") ? JsonUtils.getInt(json, "harvestToolLevel", 0) : null,
                intOrNull(json, "flammability"),
                json.has("flammability") ? JsonUtils.getInt(json, "fireSpread", 5) : null,
                intOrNull(json, "maxStackSize"),
                intOrNull(json, "maxDamage"),
                stringOrNull(json, "containerItem"),
                json.has("effects") ? effects(source, json) : null,
                json.has("food") ? food(source, JsonUtils.getJsonObject(json, "food")) : null,
                Json.strings(json, "requires"));
        if (!def.touchesBlock() && !def.touchesItem() && !def.touchesPotionType()) {
            ContentLog.LOGGER.error("Override file {} changes nothing it knows how to change, ignoring it", source);
            return null;
        }
        return def;
    }

    private static boolean apply(OverrideDef def) {
        boolean block = def.touchesBlock() && applyBlock(def);
        boolean item = def.touchesItem() && applyItem(def);
        boolean type = def.touchesPotionType() && applyPotionType(def);
        if (!block && !item && !type) {
            ContentLog.LOGGER.error("Override {} found nothing named {} it could change. Check the name and whether the mod that owns it is installed", def.source, def.target);
            return false;
        }
        return true;
    }

    private static boolean applyBlock(OverrideDef def) {
        if (!ForgeRegistries.BLOCKS.containsKey(def.target)) { return false; }
        Block block = ForgeRegistries.BLOCKS.getValue(def.target);
        if (block == null) { return false; }
        IBlock inside = (IBlock) block;
        if (!BLOCKS.containsKey(block)) { BLOCKS.put(block, BlockSnapshot.of(block)); }
        if (def.hardness != null) { block.setHardness(def.hardness); }
        if (def.resistance != null) { block.setResistance(def.resistance); }
        if (def.slipperiness != null) { block.setDefaultSlipperiness(def.slipperiness); }
        if (def.light != null) { block.setLightLevel(MathHelper.clamp(def.light, 0, 15) / 15.0F); }
        if (def.lightOpacity != null) { block.setLightOpacity(MathHelper.clamp(def.lightOpacity, 0, 255)); }
        if (def.soundType != null) { inside.rdpl$setSoundType(ContentTypes.soundType(def.soundType, inside.rdpl$getSoundType(), def.source.toString())); }
        if (def.harvestTool != null) { block.setHarvestLevel(def.harvestTool, def.harvestToolLevel == null ? 0 : def.harvestToolLevel); }
        if (def.flammability != null) {
            if (!FIRE.containsKey(block)) { FIRE.put(block, FireSnapshot.of(block)); }
            fire().setFireInfo(block, def.fireSpread == null ? 5 : def.fireSpread, def.flammability);
        }
        return true;
    }

    private static BlockFire fire() { return Objects.requireNonNull(Blocks.FIRE); }

    private static boolean applyItem(OverrideDef def) {
        if (!ForgeRegistries.ITEMS.containsKey(def.target)) { return false; }
        Item item = ForgeRegistries.ITEMS.getValue(def.target);
        if (item == null) { return false; }
        if (!ITEMS.containsKey(item)) { ITEMS.put(item, ItemSnapshot.of(item)); }
        if (def.maxStackSize != null) { item.setMaxStackSize(MathHelper.clamp(def.maxStackSize, 1, 64)); }
        if (def.maxDamage != null) { item.setMaxDamage(Math.max(0, def.maxDamage)); }
        if (def.containerItem != null) {
            ItemStack container = ContentStacks.parse(def.source, def.containerItem, 1);
            if (container.isEmpty()) { ContentLog.LOGGER.error("Override {} names container item {}, which does not exist", def.source, def.containerItem); }
            else { item.setContainerItem(container.getItem()); }
        }
        if (def.food != null) { applyFood(def, def.food, item); }
        return true;
    }

    private static void applyFood(OverrideDef def, OverrideDef.FoodDef food, Item item) {
        if (item instanceof ItemFood) {
            if (!FOODS.containsKey(item)) { FOODS.put(item, FoodSnapshot.of((ItemFood) item)); }
            IItemFood inside = (IItemFood) item;
            inside.rdpl$setHealAmount(food.heal);
            inside.rdpl$setSaturationModifier(food.saturation);
            inside.rdpl$setAlwaysEdible(food.alwaysEdible);
            if (!food.effects.isEmpty()) { ContentLog.LOGGER.error("Override {} puts effects on {}, which is already food. Effects on existing food are not supported, only heal, saturation and alwaysEdible were applied", def.source, def.target); }
            return;
        }
        EDIBLE.put(item, food);
    }

    private static boolean applyPotionType(OverrideDef def) {
        List<PotionEffectDef> effects = def.effects;
        if (effects == null || !ForgeRegistries.POTION_TYPES.containsKey(def.target)) { return false; }
        PotionType type = ForgeRegistries.POTION_TYPES.getValue(def.target);
        if (type == null) { return false; }
        List<PotionEffect> resolved = new ArrayList<>(effects.size());
        for (PotionEffectDef effect : effects) {
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect.potion));
            if (potion == null) {
                ContentLog.LOGGER.error("Override {} names effect {}, which does not exist, skipping that effect", def.source, effect.potion);
                continue;
            }
            resolved.add(new PotionEffect(potion, effect.duration, effect.amplifier, effect.ambient, effect.showParticles));
        }
        IPotionType inside = (IPotionType) type;
        if (!TYPES.containsKey(type)) { TYPES.put(type, ImmutableList.copyOf(inside.rdpl$getEffects())); }
        inside.rdpl$setEffects(ImmutableList.copyOf(resolved));
        return true;
    }

    private static void restoreAll() {
        for (Map.Entry<Block, BlockSnapshot> entry : BLOCKS.entrySet()) { entry.getValue().restore(entry.getKey()); }
        for (Map.Entry<Block, FireSnapshot> entry : FIRE.entrySet()) { entry.getValue().restore(entry.getKey()); }
        for (Map.Entry<Item, ItemSnapshot> entry : ITEMS.entrySet()) { entry.getValue().restore(entry.getKey()); }
        for (Map.Entry<Item, FoodSnapshot> entry : FOODS.entrySet()) { entry.getValue().restore((ItemFood) entry.getKey()); }
        for (Map.Entry<PotionType, ImmutableList<PotionEffect>> entry : TYPES.entrySet()) { ((IPotionType) entry.getKey()).rdpl$setEffects(entry.getValue()); }
        BLOCKS.clear();
        FIRE.clear();
        ITEMS.clear();
        FOODS.clear();
        TYPES.clear();
    }

    @Nullable private static List<PotionEffectDef> effects(ResourceLocation source, JsonObject json) {
        List<PotionEffectDef> effects = readEffects(source, json);
        if (effects.isEmpty()) {
            ContentLog.LOGGER.error("Override file {} has an effects list with no usable effects. To empty a potion type, that is not supported; remove the key instead", source);
            return null;
        }
        return effects;
    }

    private static OverrideDef.FoodDef food(ResourceLocation source, JsonObject json) {
        return new OverrideDef.FoodDef(
                Math.max(0, JsonUtils.getInt(json, "heal", 1)),
                Math.max(0.0F, JsonUtils.getFloat(json, "saturation", 0.6F)),
                JsonUtils.getBoolean(json, "alwaysEdible", false),
                readEffects(source, json));
    }

    private static List<PotionEffectDef> readEffects(ResourceLocation source, JsonObject json) {
        if (!json.has("effects")) { return Collections.emptyList(); }
        List<PotionEffectDef> effects = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(json, "effects")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", source);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String potion = JsonUtils.getString(entry, "potion", "");
            if (potion.isEmpty()) {
                ContentLog.LOGGER.error("An effect in {} names no potion, skipping it", source);
                continue;
            }
            effects.add(new PotionEffectDef(potion,
                    Math.max(1, JsonUtils.getInt(entry, "duration", 3600)),
                    Math.max(0, JsonUtils.getInt(entry, "amplifier", 0)),
                    JsonUtils.getBoolean(entry, "ambient", false),
                    JsonUtils.getBoolean(entry, "showParticles", true)));
        }
        return Collections.unmodifiableList(effects);
    }


    @Nullable private static Float floatOrNull(JsonObject json, String key) { return json.has(key) ? JsonUtils.getFloat(json, key) : null; }

    @Nullable private static Integer intOrNull(JsonObject json, String key) { return json.has(key) ? JsonUtils.getInt(json, key) : null; }

    @Nullable private static String stringOrNull(JsonObject json, String key) { return json.has(key) ? JsonUtils.getString(json, key) : null; }

    private static final class BlockSnapshot {
        private final float hardness;
        private final float resistance;
        private final float slipperiness;
        private final int light;
        private final int lightOpacity;
        private final SoundType soundType;
        private final String[] harvestTools;
        private final int[] harvestLevels;
        private final List<IBlockState> states;

        private BlockSnapshot(float hardness, float resistance, float slipperiness, int light, int lightOpacity, SoundType soundType, String[] harvestTools, int[] harvestLevels, List<IBlockState> states) {
            this.hardness = hardness;
            this.resistance = resistance;
            this.slipperiness = slipperiness;
            this.light = light;
            this.lightOpacity = lightOpacity;
            this.soundType = soundType;
            this.harvestTools = harvestTools;
            this.harvestLevels = harvestLevels;
            this.states = states;
        }

        static BlockSnapshot of(Block block) {
            IBlock inside = (IBlock) block;
            List<IBlockState> states = new ArrayList<>(block.getBlockState().getValidStates());
            String[] tools = new String[states.size()];
            int[] levels = new int[states.size()];
            for (int i = 0; i < states.size(); i++) {
                tools[i] = block.getHarvestTool(states.get(i));
                levels[i] = block.getHarvestLevel(states.get(i));
            }
            return new BlockSnapshot(inside.rdpl$getHardness(), inside.rdpl$getResistance(), inside.rdpl$getSlipperiness(), inside.rdpl$getLightValue(), inside.rdpl$getLightOpacity(), inside.rdpl$getSoundType(), tools, levels, states);
        }

        void restore(Block block) {
            IBlock inside = (IBlock) block;
            inside.rdpl$setHardness(hardness);
            inside.rdpl$setResistance(resistance);
            inside.rdpl$setSlipperiness(slipperiness);
            inside.rdpl$setLightValue(light);
            block.setLightOpacity(lightOpacity);
            inside.rdpl$setSoundType(soundType);
            for (int i = 0; i < states.size(); i++) { block.setHarvestLevel(harvestTools[i], harvestLevels[i], states.get(i)); }
        }
    }

    private static final class FireSnapshot {
        @Nullable private final Integer encouragement;
        @Nullable private final Integer flammability;

        private FireSnapshot(@Nullable Integer encouragement, @Nullable Integer flammability) {
            this.encouragement = encouragement;
            this.flammability = flammability;
        }

        static FireSnapshot of(Block block) {
            IBlockFire fire = (IBlockFire) fire();
            return new FireSnapshot(fire.rdpl$getEncouragements().get(block), fire.rdpl$getFlammabilities().get(block));
        }

        void restore(Block block) {
            IBlockFire fire = (IBlockFire) fire();
            if (encouragement == null || flammability == null) {
                fire.rdpl$getEncouragements().remove(block);
                fire.rdpl$getFlammabilities().remove(block);
                return;
            }
            fire().setFireInfo(block, encouragement, flammability);
        }
    }

    private static final class ItemSnapshot {
        private final int maxStackSize;
        private final int maxDamage;
        @Nullable private final Item containerItem;

        private ItemSnapshot(int maxStackSize, int maxDamage, @Nullable Item containerItem) {
            this.maxStackSize = maxStackSize;
            this.maxDamage = maxDamage;
            this.containerItem = containerItem;
        }

        static ItemSnapshot of(Item item) {
            IItem inside = (IItem) item;
            return new ItemSnapshot(inside.rdpl$getMaxStackSize(), inside.rdpl$getMaxDamage(), item.getContainerItem());
        }

        void restore(Item item) {
            item.setMaxStackSize(maxStackSize);
            item.setMaxDamage(maxDamage);
            ((IItem) item).rdpl$setContainerItem(containerItem);
        }
    }

    private static final class FoodSnapshot {
        private final int heal;
        private final float saturation;
        private final boolean alwaysEdible;

        private FoodSnapshot(int heal, float saturation, boolean alwaysEdible) {
            this.heal = heal;
            this.saturation = saturation;
            this.alwaysEdible = alwaysEdible;
        }

        static FoodSnapshot of(ItemFood item) {
            IItemFood inside = (IItemFood) item;
            return new FoodSnapshot(inside.rdpl$getHealAmount(), inside.rdpl$getSaturationModifier(), inside.rdpl$getAlwaysEdible());
        }

        void restore(ItemFood item) {
            IItemFood inside = (IItemFood) item;
            inside.rdpl$setHealAmount(heal);
            inside.rdpl$setSaturationModifier(saturation);
            inside.rdpl$setAlwaysEdible(alwaysEdible);
        }
    }
}
