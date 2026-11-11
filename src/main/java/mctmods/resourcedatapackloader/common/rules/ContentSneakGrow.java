package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SneakGrowDef;
import mctmods.resourcedatapackloader.common.util.BlockMatches;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentSneakGrow {
    private static final List<String> KEYS = Arrays.asList("chance", "radius", "height", "blocks", "mode", "ticks", "hunger", "cooldown", "items", "equipment", "enchantment", "damage", "except", "grass", "sound", "toggle", "most", "particles", "gameModes");
    private static final List<String> MODES = Arrays.asList("bonemeal", "tick", "auto");
    private static final List<String> GAME_MODES = Arrays.asList("survival", "creative", "adventure");
    private static final List<String> SLOTS = Arrays.asList("head", "chest", "legs", "feet", "mainhand", "offhand");
    private static final EntityEquipmentSlot[] SLOT_VALUES = {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND};
    private static final GameType[] GAME_TYPES = {GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE};
    private static final String PART = "growth.sneak";
    private static final String DYNAMIC_SAPLING = "com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling";
    private static final String OFF = "rdplSneakOff";
    private static final int BONEMEAL_EVENT = 2005;
    private static final long NEVER = Long.MIN_VALUE / 2;
    private static final BlockMatch TREE_SAPLING = BlockMatch.group("treeSapling");
    private static final Map<UUID, long[]> STATE = new ConcurrentHashMap<>();
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

    @Nullable private static String text(JsonElement value) { return value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? value.getAsString() : null; }

    private static List<BlockMatch> blockList(String owner, String key, JsonElement value) {
        List<BlockMatch> found = new ArrayList<>();
        if (!value.isJsonArray()) {
            bad(owner, key, value, "a list of block ids or #names");
            return found;
        }
        for (JsonElement entry : value.getAsJsonArray()) {
            String name = text(entry);
            BlockMatch match = name == null ? null : BlockMatches.parse(owner, name, PART + "." + key);
            if (match != null) { found.add(match); }
            else if (name == null) { bad(owner, key, entry, "a block id or #name"); }
        }
        return found;
    }

    private static ResourceLocation itemKey() { return new ResourceLocation(ResourceDataPackLoader.MOD_ID, PART); }

    private static void items(String owner, JsonElement value, SneakGrowDef.Builder builder) {
        if (!value.isJsonArray()) {
            bad(owner, "items", value, "a list of item ids or #names");
            return;
        }
        for (JsonElement entry : value.getAsJsonArray()) {
            String name = text(entry);
            ItemMatch match = name == null ? null : ItemMatch.parse(itemKey(), name);
            if (match != null) { builder.items.add(match); }
            else if (name == null) { bad(owner, "items", entry, "an item id or #name"); }
        }
    }

    private static void equipment(String owner, JsonElement value, SneakGrowDef.Builder builder) {
        if (!value.isJsonObject()) {
            bad(owner, "equipment", value, "an object of slots to item ids or #names");
            return;
        }
        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
            int slot = SLOTS.indexOf(entry.getKey());
            String name = text(entry.getValue());
            if (slot < 0) { ContentLog.LOGGER.error("{} gives {}.equipment.{}, which is not one of {}, ignoring it", owner, PART, entry.getKey(), SLOTS); }
            else if (name == null) { bad(owner, "equipment." + entry.getKey(), entry.getValue(), "an item id or #name"); }
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
            case "chance": {
                Float chance = ContentGrowSwitch.within(owner, PART + ".chance", value, false, 0.0F, 1.0F);
                if (chance != null && chance > 0.0F) { builder.chance = chance; }
                else if (chance != null) { bad(owner, key, value, "above 0"); }
                break;
            }
            case "hunger": {
                Float hunger = ContentGrowSwitch.within(owner, PART + ".hunger", value, false, 0.0F, 20.0F);
                if (hunger != null) { builder.hunger = hunger; }
                break;
            }
            case "radius": builder.radius = whole(owner, key, value, 1, 16, builder.radius); break;
            case "height": builder.height = whole(owner, key, value, 0, 2, builder.height); break;
            case "ticks": builder.ticks = whole(owner, key, value, 1, 16, builder.ticks); break;
            case "cooldown": builder.cooldown = whole(owner, key, value, 4, 600, builder.cooldown); break;
            case "damage": builder.damage = whole(owner, key, value, 0, 64, builder.damage); break;
            default: builder.most = whole(owner, key, value, 1, 256, builder.most); break;
        }
    }

    private static void apply(String owner, String key, JsonElement value, SneakGrowDef.Builder builder) {
        switch (key) {
            case "blocks": {
                List<BlockMatch> blocks = blockList(owner, key, value);
                if (blocks.isEmpty()) { ContentLog.LOGGER.error("{} gives {}.blocks with no usable blocks, using the default plants", owner, PART); }
                else { builder.blocks = blocks; }
                break;
            }
            case "except": builder.except = blockList(owner, key, value); break;
            case "mode": {
                String name = text(value);
                int mode = name == null ? -1 : MODES.indexOf(name);
                if (mode < 0) { bad(owner, key, value, "one of " + MODES); }
                else { builder.mode = mode; }
                break;
            }
            case "items": items(owner, value, builder); break;
            case "equipment": equipment(owner, value, builder); break;
            case "enchantment": {
                String name = text(value);
                if (name == null) { bad(owner, key, value, "an enchantment id"); }
                else { builder.enchantment = name; }
                break;
            }
            case "gameModes": builder.gameModes = gameModes(owner, value, builder.gameModes); break;
            case "particles": builder.particles = bool(owner, key, value, builder.particles); break;
            case "sound": builder.sound = bool(owner, key, value, builder.sound); break;
            case "grass": builder.grass = bool(owner, key, value, builder.grass); break;
            case "toggle": builder.toggle = bool(owner, key, value, builder.toggle); break;
            default: numbers(owner, key, value, builder); break;
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

    public static boolean muted(EntityPlayer player) { return PlayerPersisted.of(player).getBoolean(OFF); }

    public static void mute(EntityPlayer player, boolean muted) {
        if (muted) { PlayerPersisted.of(player).setBoolean(OFF, true); }
        else { PlayerPersisted.of(player).removeTag(OFF); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { STATE.remove(event.player.getUniqueID()); }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!any || event.phase != TickEvent.Phase.END || player.world.isRemote || !(player instanceof EntityPlayerMP) || player instanceof FakePlayer || ((EntityPlayerMP) player).connection == null) { return; }
        boolean sneaking = player.isSneaking();
        long[] state = STATE.get(player.getUniqueID());
        if (state == null) {
            STATE.put(player.getUniqueID(), new long[] {sneaking ? 1 : 0, NEVER});
            return;
        }
        if ((state[0] == 1) == sneaking) { return; }
        state[0] = sneaking ? 1 : 0;
        if (sneaking && player.onGround && !player.isRiding()) { crouch((EntityPlayerMP) player, state); }
    }

    private static boolean sapling(Block block, WorldServer world, BlockPos pos) {
        return block instanceof BlockSapling || block instanceof ContentSaplingBlock || TREE_SAPLING.matches(block, world, pos) || block.getClass().getName().startsWith(DYNAMIC_SAPLING);
    }

    private static boolean plant(Block block, WorldServer world, BlockPos pos) {
        if (block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockCocoa || block instanceof BlockNetherWart || sapling(block, world, pos)) { return true; }
        return block instanceof IPlantable && block instanceof IGrowable && ((IPlantable) block).getPlantType(world, pos) == EnumPlantType.Crop;
    }

    private static boolean listed(List<BlockMatch> matches, Block block, WorldServer world, BlockPos pos) {
        for (BlockMatch match : matches) {
            if (match.matches(block, world, pos)) { return true; }
        }
        return false;
    }

    @Nullable private static List<ItemStack> required(EntityPlayerMP player, SneakGrowDef def) {
        List<ItemStack> matched = new ArrayList<>();
        if (!def.items.isEmpty()) {
            boolean held = false;
            for (EnumHand hand : EnumHand.values()) {
                ItemStack stack = player.getHeldItem(hand);
                for (ItemMatch match : def.items) {
                    if (match.matches(stack)) {
                        held = true;
                        matched.add(stack);
                        break;
                    }
                }
            }
            if (!held) { return null; }
        }
        for (Map.Entry<EntityEquipmentSlot, ItemMatch> entry : def.equipment.entrySet()) {
            ItemStack stack = player.getItemStackFromSlot(entry.getKey());
            if (!entry.getValue().matches(stack)) { return null; }
            if (!matched.contains(stack)) { matched.add(stack); }
        }
        if (def.enchantment == null) { return matched; }
        Enchantment enchantment = Enchantment.getEnchantmentByLocation(def.enchantment);
        for (ItemStack stack : matched) {
            if (enchantment == null || EnchantmentHelper.getEnchantmentLevel(enchantment, stack) <= 0) { return null; }
        }
        return matched;
    }

    private static void crouch(EntityPlayerMP player, long[] state) {
        WorldServer world = player.getServerWorld();
        SneakGrowDef def = ContentEnvironments.growth(world, player.getPosition()).sneak();
        long now = player.server.getTickCounter();
        if (def == null || !def.gameModes.contains(player.interactionManager.getGameType()) || now - state[1] < def.cooldown || def.toggle && muted(player)) { return; }
        List<ItemStack> matched = required(player, def);
        boolean creative = player.capabilities.isCreativeMode;
        if (matched == null || !creative && player.getFoodStats().getFoodLevel() < def.hunger) { return; }
        state[1] = now;
        int grown = scan(player, world, def, matched);
        if (grown > 0 && def.hunger > 0.0F && !creative) { player.getFoodStats().setFoodLevel(Math.max(0, player.getFoodStats().getFoodLevel() - (int) Math.ceil(def.hunger))); }
    }

    private static int scan(EntityPlayerMP player, WorldServer world, SneakGrowDef def, List<ItemStack> matched) {
        BlockPos feet = player.getPosition();
        boolean dirt = def.grass && player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.GRASS);
        int grown = 0;
        for (int ring = 0; ring <= def.radius; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                int step = Math.abs(dx) == ring ? 1 : 2 * ring;
                for (int dz = -ring; dz <= ring; dz += step) {
                    for (int dy = -def.height; dy <= def.height; dy++) {
                        if (grown >= def.most) { return grown; }
                        BlockPos pos = feet.add(dx, dy, dz);
                        if (world.isBlockLoaded(pos) && tryGrow(world, pos, def, dirt)) {
                            grown++;
                            if (broke(player, world, pos, def, matched)) { return grown; }
                        }
                    }
                }
            }
        }
        return grown;
    }

    private static boolean tryGrow(WorldServer world, BlockPos pos, SneakGrowDef def, boolean dirt) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.AIR || listed(def.except, block, world, pos)) { return false; }
        boolean plain = dirt && block == Blocks.DIRT && state.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT;
        if (!plain && !(def.blocks == null ? plant(block, world, pos) : listed(def.blocks, block, world, pos))) { return false; }
        if (world.rand.nextFloat() >= def.chance) { return false; }
        if (plain) { return world.setBlockState(pos, Blocks.GRASS.getDefaultState()); }
        if (def.mode == SneakGrowDef.TICK) { return tick(world, pos, state, def.ticks); }
        return bonemeal(world, pos, state, sapling(block, world, pos)) || def.mode == SneakGrowDef.AUTO && tick(world, pos, state, def.ticks);
    }

    private static boolean bonemeal(WorldServer world, BlockPos pos, IBlockState state, boolean sapling) {
        Block block = state.getBlock();
        if (!(block instanceof IGrowable) || ContentGrowSwitch.stopped(world, pos, block, sapling ? GrowSwitchDef.SAPLINGS : GrowSwitchDef.CROPS)) { return false; }
        IGrowable growable = (IGrowable) block;
        if (!growable.canGrow(world, pos, state, false) || !growable.canUseBonemeal(world, world.rand, pos, state)) { return false; }
        growable.grow(world, world.rand, pos, state);
        return true;
    }

    private static boolean tick(WorldServer world, BlockPos pos, IBlockState state, int ticks) {
        Block block = state.getBlock();
        if (!block.getTickRandomly()) { return false; }
        for (int index = 0; index < ticks; index++) {
            IBlockState now = world.getBlockState(pos);
            if (now.getBlock() != block) { break; }
            block.randomTick(world, pos, now, world.rand);
        }
        return true;
    }

    private static boolean broke(EntityPlayerMP player, WorldServer world, BlockPos pos, SneakGrowDef def, List<ItemStack> matched) {
        if (def.particles) { world.playEvent(BONEMEAL_EVENT, pos, 0); }
        if (def.sound) { world.playSound(null, pos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F); }
        boolean broke = false;
        for (ItemStack stack : matched) {
            if (def.damage > 0 && !stack.isEmpty()) { stack.damageItem(def.damage, player); }
            if (stack.isEmpty()) { broke = true; }
        }
        return broke;
    }

    public static void forget() {
        STATE.clear();
        any = false;
    }
}
