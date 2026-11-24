package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SneakGrowDef;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.common.util.PlayerLogout;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.common.util.plant.DynamicTrees;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeEvents;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeStore;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.LineCompat;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ContentSneakGrow {
    private static final String CHANCE = "chance";
    private static final String RADIUS = "radius";
    private static final String HEIGHT = "height";
    private static final String TICKS = "ticks";
    private static final String HUNGER = "hunger";
    private static final String COOLDOWN = "cooldown";
    private static final String DAMAGE = "damage";
    private static final List<String> KEYS = List.of(CHANCE, RADIUS, HEIGHT, "blocks", "mode", TICKS, HUNGER, COOLDOWN, "items", "equipment", "enchantment", DAMAGE, "except", "grass", "sound", "toggle", "most", "particles", "gameModes");
    private static final List<String> MODES = List.of("bonemeal", "tick", "auto");
    private static final List<String> GAME_MODES = List.of("survival", "creative", "adventure");
    private static final List<String> SLOTS = List.of("head", "chest", "legs", "feet", "mainhand", "offhand");
    private static final EquipmentSlot[] SLOT_VALUES = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final EquipmentSlot[] HANDS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final GameType[] GAME_TYPES = {GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE};
    private static final TagKey<Block> SAPLING_TAG = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("saplings"));
    private static final String PART = "growth.sneak";
    private static final String OFF = "rdplSneakOff";
    private static final int BONEMEAL_PARTICLES = 15;
    private static final long NEVER = Long.MIN_VALUE / 2;
    private static final Map<UUID, long[]> STATE = PlayerLogout.track(new ConcurrentHashMap<>());
    private static boolean any;

    private ContentSneakGrow() {}

    private static void bad(String owner, String key, JsonElement value, String expected) { ContentLog.LOGGER.error("{} gives {}.{} as {}, which is not {}, ignoring it", owner, PART, key, value, expected); }

    private static int whole(String owner, String key, JsonElement value, int low, int high, int fallback) {
        Float number = ContentGrowSwitch.within(owner, PART + "." + key, value, true, low, high);
        return number == null ? fallback : number.intValue();
    }

    private static boolean bool(String owner, String key, JsonElement value, boolean fallback) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        bad(owner, key, value, "true or false");
        return fallback;
    }

    @Nullable private static String text(JsonElement value) { return Json.isString(value) ? value.getAsString() : null; }

    private static List<BlockMatch> blockList(String owner, String key, JsonElement value) {
        List<BlockMatch> found = new ArrayList<>();
        if (!value.isJsonArray()) {
            bad(owner, key, value, "a list of block ids or #tags");
            return found;
        }
        for (JsonElement entry : value.getAsJsonArray()) {
            String name = text(entry);
            BlockMatch match = name == null ? null : ContentGrowSwitch.match(owner, name);
            if (match != null) { found.add(match); }
            else if (name == null) { bad(owner, key, entry, "a block id or #tag"); }
        }
        return found;
    }

    private static Identifier itemKey() { return ResourceDataPackLoader.id(PART); }

    private static void items(String owner, JsonElement value, SneakGrowDef.Builder builder) {
        if (!value.isJsonArray()) {
            bad(owner, "items", value, "a list of item ids or #tags");
            return;
        }
        for (JsonElement entry : value.getAsJsonArray()) {
            String name = text(entry);
            ItemMatch match = name == null ? null : ItemMatch.parse(itemKey(), name);
            if (match != null) { builder.items.add(match); }
            else if (name == null) { bad(owner, "items", entry, "an item id or #tag"); }
        }
    }

    private static void equipment(String owner, JsonElement value, SneakGrowDef.Builder builder) {
        if (!value.isJsonObject()) {
            bad(owner, "equipment", value, "an object of slots to item ids or #tags");
            return;
        }
        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
            int slot = SLOTS.indexOf(entry.getKey());
            String name = text(entry.getValue());
            if (slot < 0) { ContentLog.LOGGER.error("{} gives {}.equipment.{}, which is not one of {}, ignoring it", owner, PART, entry.getKey(), SLOTS); }
            else if (name == null) { bad(owner, "equipment." + entry.getKey(), entry.getValue(), "an item id or #tag"); }
            else {
                ItemMatch match = ItemMatch.parse(itemKey(), name);
                if (match != null) { builder.equipment.put(SLOT_VALUES[slot], match); }
            }
        }
    }

    private static Set<GameType> gameModes(String owner, JsonElement value, Set<GameType> fallback) {
        Set<GameType> modes = EnumSet.noneOf(GameType.class);
        if (value.isJsonArray()) {
            for (JsonElement entry : value.getAsJsonArray()) {
                String name = text(entry);
                int mode = name == null ? -1 : GAME_MODES.indexOf(name);
                if (mode >= 0) { modes.add(GAME_TYPES[mode]); }
                else { bad(owner, "gameModes", entry, "one of " + GAME_MODES); }
            }
        }
        else { bad(owner, "gameModes", value, "a list of " + GAME_MODES); }
        return modes.isEmpty() ? fallback : modes;
    }

    private static void numbers(String owner, String key, JsonElement value, SneakGrowDef.Builder builder) {
        switch (key) {
            case CHANCE -> {
                Float chance = ContentGrowSwitch.within(owner, PART + ".chance", value, false, 0.0D, 1.0D);
                if (chance != null && chance > 0.0F) { builder.chance = chance; }
                else if (chance != null) { bad(owner, key, value, "above 0"); }
            }
            case HUNGER -> {
                Float hunger = ContentGrowSwitch.within(owner, PART + ".hunger", value, false, 0.0D, 20.0D);
                if (hunger != null) { builder.hunger = hunger; }
            }
            case RADIUS -> builder.radius = whole(owner, key, value, 1, 16, builder.radius);
            case HEIGHT -> builder.height = whole(owner, key, value, 0, 2, builder.height);
            case TICKS -> builder.ticks = whole(owner, key, value, 1, 16, builder.ticks);
            case COOLDOWN -> builder.cooldown = whole(owner, key, value, 4, 600, builder.cooldown);
            case DAMAGE -> builder.damage = whole(owner, key, value, 0, 64, builder.damage);
            default -> builder.most = whole(owner, key, value, 1, 256, builder.most);
        }
    }

    private static void apply(String owner, String key, JsonElement value, SneakGrowDef.Builder builder) {
        switch (key) {
            case "blocks" -> {
                List<BlockMatch> blocks = blockList(owner, key, value);
                if (blocks.isEmpty()) { ContentLog.LOGGER.error("{} gives {}.blocks with no usable blocks, using the default plants", owner, PART); }
                else { builder.blocks = blocks; }
            }
            case "except" -> builder.except = blockList(owner, key, value);
            case "mode" -> {
                String name = text(value);
                int mode = name == null ? -1 : MODES.indexOf(name);
                if (mode < 0) { bad(owner, key, value, "one of " + MODES); }
                else { builder.mode = mode; }
            }
            case "items" -> items(owner, value, builder);
            case "equipment" -> equipment(owner, value, builder);
            case "enchantment" -> {
                String name = text(value);
                if (name == null) { bad(owner, key, value, "an enchantment id"); }
                else { builder.enchantment = name; }
            }
            case "gameModes" -> builder.gameModes = gameModes(owner, value, builder.gameModes);
            case "particles" -> builder.particles = bool(owner, key, value, builder.particles);
            case "sound" -> builder.sound = bool(owner, key, value, builder.sound);
            case "grass" -> builder.grass = bool(owner, key, value, builder.grass);
            case "toggle" -> builder.toggle = bool(owner, key, value, builder.toggle);
            default -> numbers(owner, key, value, builder);
        }
    }

    @Nullable public static SneakGrowDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth.sneak that is not an object of sneak keys, ignoring it", owner);
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        SneakGrowDef.Builder builder = new SneakGrowDef.Builder();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (KEYS.contains(entry.getKey())) { apply(owner, entry.getKey(), entry.getValue(), builder); }
            else { ContentLog.LOGGER.error("{} gives growth.sneak.{}, which is not a sneak key, ignoring it", owner, entry.getKey()); }
        }
        any = true;
        return builder.build();
    }

    public static boolean muted(Player player) { return PlayerPersisted.of(player, OFF).getBooleanOr(OFF, false); }

    public static void mute(Player player, boolean muted) {
        if (muted) { PlayerPersisted.of(player, OFF).putBoolean(OFF, true); }
        else { PlayerPersisted.of(player, OFF).remove(OFF); }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!any || !(event.getEntity() instanceof ServerPlayer player) || player instanceof FakePlayer) { return; }
        boolean sneaking = player.isShiftKeyDown();
        long[] state = STATE.get(player.getUUID());
        if (state == null) {
            STATE.put(player.getUUID(), new long[] {sneaking ? 1 : 0, NEVER});
            return;
        }
        if ((state[0] == 1) == sneaking) { return; }
        state[0] = sneaking ? 1 : 0;
        if (sneaking && player.onGround() && !player.isPassenger()) { crouch(player, state); }
    }

    private static boolean sapling(BlockState state) { return state.getBlock() instanceof SaplingBlock || state.getBlock() instanceof ContentSaplingBlock || state.is(SAPLING_TAG) || DynamicTrees.isSapling(state.getBlock()); }

    private static boolean plant(BlockState state) {
        Block block = state.getBlock();
        return block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof NetherWartBlock || sapling(state) || state.is(BlockTags.CROPS);
    }

    private static boolean listed(List<BlockMatch> matches, Block block) {
        for (BlockMatch match : matches) {
            if (match.matches(block)) { return true; }
        }
        return false;
    }

    @Nullable private static Map<EquipmentSlot, ItemStack> required(ServerPlayer player, SneakGrowDef def) {
        Map<EquipmentSlot, ItemStack> matched = new EnumMap<>(EquipmentSlot.class);
        if (!def.items.isEmpty()) {
            for (EquipmentSlot hand : HANDS) {
                ItemStack stack = player.getItemBySlot(hand);
                for (ItemMatch match : def.items) {
                    if (match.matches(stack)) {
                        matched.put(hand, stack);
                        break;
                    }
                }
            }
            if (matched.isEmpty()) { return null; }
        }
        for (Map.Entry<EquipmentSlot, ItemMatch> entry : def.equipment.entrySet()) {
            ItemStack stack = player.getItemBySlot(entry.getKey());
            if (!entry.getValue().matches(stack)) { return null; }
            matched.put(entry.getKey(), stack);
        }
        if (def.enchantment == null) { return matched; }
        Identifier id = Identifier.tryParse(def.enchantment);
        Holder<Enchantment> enchantment = id == null ? null : player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(ResourceKey.create(Registries.ENCHANTMENT, id)).orElse(null);
        for (ItemStack stack : matched.values()) {
            if (enchantment == null || stack.getEnchantmentLevel(enchantment) <= 0) { return null; }
        }
        return matched;
    }

    private static void crouch(ServerPlayer player, long[] state) {
        ServerLevel level = player.level();
        SneakGrowDef def = ContentEnvironments.growth(level, player.blockPosition()).sneak();
        long now = level.getServer().getTickCount();
        if (def == null || !def.gameModes.contains(player.gameMode.getGameModeForPlayer()) || now - state[1] < def.cooldown || def.toggle && muted(player)) { return; }
        Map<EquipmentSlot, ItemStack> matched = required(player, def);
        boolean creative = player.getAbilities().instabuild;
        FoodData food = player.getFoodData();
        if (matched == null || !creative && food.getFoodLevel() < def.hunger) { return; }
        state[1] = now;
        int grown = scan(player, level, def, matched);
        if (grown > 0 && def.hunger > 0.0F && !creative) { food.setFoodLevel(Math.max(0, food.getFoodLevel() - (int) Math.ceil(def.hunger))); }
    }

    private static int scan(ServerPlayer player, ServerLevel level, SneakGrowDef def, Map<EquipmentSlot, ItemStack> matched) {
        BlockPos feet = player.blockPosition();
        boolean dirt = def.grass && player.getOffhandItem().is(Items.GRASS_BLOCK);
        int grown = 0;
        for (int ring = 0; ring <= def.radius; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                int step = Math.abs(dx) == ring ? 1 : 2 * ring;
                for (int dz = -ring; dz <= ring; dz += step) {
                    for (int dy = -def.height; dy <= def.height; dy++) {
                        if (grown >= def.most) { return grown; }
                        BlockPos pos = feet.offset(dx, dy, dz);
                        if (level.isLoaded(pos) && tryGrow(level, pos, def, dirt)) {
                            grown++;
                            if (broke(player, level, pos, def, matched)) { return grown; }
                        }
                    }
                }
            }
        }
        return grown;
    }

    private static boolean tryGrow(ServerLevel level, BlockPos pos, SneakGrowDef def, boolean dirt) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (state.isAir() || listed(def.except, block)) { return false; }
        boolean plain = dirt && block == Blocks.DIRT;
        if (!plain && ContentTreeStore.get(level, pos.asLong()) == null && !(def.blocks == null ? plant(state) : listed(def.blocks, block))) { return false; }
        if (level.getRandom().nextFloat() >= def.chance) { return false; }
        if (plain) { return level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState()); }
        if (def.mode == SneakGrowDef.TICK) { return tick(level, pos, state, def.ticks); }
        return bonemeal(level, pos, state) || def.mode == SneakGrowDef.AUTO && tick(level, pos, state, def.ticks);
    }

    private static boolean bonemeal(ServerLevel level, BlockPos pos, BlockState state) {
        if (ContentTreeEvents.sneak(level, pos, state, true)) { return true; }
        Block block = state.getBlock();
        if (!(block instanceof BonemealableBlock) || ContentGrowSwitch.stopped(level, pos, block, sapling(state) ? GrowSwitchDef.SAPLINGS : GrowSwitchDef.CROPS)) { return false; }
        return LineCompat.bonemeal(level, pos, state);
    }

    private static boolean tick(ServerLevel level, BlockPos pos, BlockState state, int ticks) {
        Block block = state.getBlock();
        if (!state.isRandomlyTicking()) { return false; }
        for (int index = 0; index < ticks; index++) {
            BlockState now = level.getBlockState(pos);
            if (now.getBlock() != block) { break; }
            if (!ContentTreeEvents.sneak(level, pos, now, false)) { now.randomTick(level, pos, level.getRandom()); }
        }
        return true;
    }

    private static boolean broke(ServerPlayer player, ServerLevel level, BlockPos pos, SneakGrowDef def, Map<EquipmentSlot, ItemStack> matched) {
        if (def.particles) { level.levelEvent(def.sound ? LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH : LevelEvent.PARTICLES_BEE_GROWTH, pos, BONEMEAL_PARTICLES); }
        else if (def.sound) { level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F); }
        boolean broke = false;
        for (Map.Entry<EquipmentSlot, ItemStack> entry : matched.entrySet()) {
            ItemStack stack = entry.getValue();
            if (def.damage > 0 && !stack.isEmpty()) { stack.hurtAndBreak(def.damage, player, entry.getKey()); }
            if (stack.isEmpty()) { broke = true; }
        }
        return broke;
    }

    public static void forget() {
        STATE.clear();
        any = false;
    }
}
