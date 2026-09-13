package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AnvilDef;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentAnvils {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ItemStack, AnvilDef> OFFERED = Collections.synchronizedMap(new WeakHashMap<>());
    private static final int TOLD_EVERY = 40;
    private static final float BREAK_CHANCE = 0.12F;
    private static final List<AnvilDef> DEFS = new ArrayList<>();
    private static final Map<AnvilDef, ItemStack> ITEMS = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> WITH = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> RESULTS = new IdentityHashMap<>();
    private static final Map<AnvilDef, Map<Enchantment, Integer>> ENCHANTMENTS = new IdentityHashMap<>();
    private static final Map<String, Long> TOLD = new HashMap<>();

    private ContentAnvils() {}

    public static boolean load() {
        DEFS.clear();
        ITEMS.clear();
        WITH.clear();
        RESULTS.clear();
        ENCHANTMENTS.clear();
        if (!Config.data.anvils) { return false; }
        Json.eachFile(PackManager.ANVILS, "anvil file", (key, contents) -> {
            AnvilDef def = read(key, contents);
            if (def == null) { return; }
            ItemStack stack = ContentStacks.parse(key, def.item, 1);
            ItemStack with = ContentStacks.parse(key, def.with, 1);
            if (stack.isEmpty() || with.isEmpty()) { return; }
            if (!def.result.isEmpty()) {
                ItemStack result = ContentStacks.parse(key, def.result, def.resultCount);
                if (result.isEmpty()) { return; }
                RESULTS.put(def, result);
            }
            Map<Enchantment, Integer> found = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : def.enchantments.entrySet()) {
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(entry.getKey()));
                if (enchantment == null) {
                    ContentLog.LOGGER.error("Anvil work {} names the enchantment {}, which nothing registers, skipping it", key, entry.getKey());
                    continue;
                }
                found.put(enchantment, entry.getValue());
            }
            DEFS.add(def);
            ITEMS.put(def, stack);
            WITH.put(def, with);
            ENCHANTMENTS.put(def, found);
        });
        if (!DEFS.isEmpty()) { Summary.info("anvils", "Loaded " + DEFS.size() + " piece(s) of anvil work"); }
        return !DEFS.isEmpty();
    }

    @Nullable private static AnvilDef read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Anvil work {} is empty, ignoring it", key);
            return null;
        }
        String item = itemOf(json, "item");
        if (item.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names no item, ignoring it", key);
            return null;
        }
        String with = itemOf(json, "with");
        if (with.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names nothing for the right slot under with, and an anvil only answers to a pair, ignoring it", key);
            return null;
        }
        Map<String, Integer> enchantments = new LinkedHashMap<>();
        if (json.has("enchantments")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "enchantments").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Anvil work {} gives the enchantment {} something that is not a level, skipping it", key, entry.getKey());
                    continue;
                }
                enchantments.put(entry.getKey(), Math.max(1, entry.getValue().getAsInt()));
            }
        }
        return new AnvilDef(key, item, countOf(json, "item"), with, countOf(json, "with"), itemOf(json, "result"), countOf(json, "result"), Math.max(1, JsonUtils.getInt(json, "levels", 1)), enchantments, JsonUtils.getString(json, "grants", "").trim(), JsonUtils.getBoolean(json, "locks", false));
    }

    private static String itemOf(JsonObject json, String name) {
        if (json.has(name) && json.get(name).isJsonObject()) { return JsonUtils.getString(JsonUtils.getJsonObject(json, name), "item", "").trim(); }
        return JsonUtils.getString(json, name, "").trim();
    }

    private static int countOf(JsonObject json, String name) {
        if (!json.has(name) || !json.get(name).isJsonObject()) { return 1; }
        return Math.max(1, JsonUtils.getInt(JsonUtils.getJsonObject(json, name), "count", 1));
    }

    @Nullable private static AnvilDef holding(ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            ItemStack wanted = ITEMS.get(def);
            if (ContentStacks.matches(held, wanted.getItem(), wanted.getMetadata())) { return def; }
        }
        return null;
    }

    private static boolean pairs(AnvilDef def, ItemStack left, ItemStack right) {
        ItemStack wanted = ITEMS.get(def);
        ItemStack with = WITH.get(def);
        return ContentStacks.matches(left, wanted.getItem(), wanted.getMetadata()) && left.getCount() >= def.itemCount && ContentStacks.matches(right, with.getItem(), with.getMetadata()) && right.getCount() >= def.withCount;
    }

    private static ItemStack worked(AnvilDef def, ItemStack left) {
        ItemStack output = left.copy();
        output.setCount(def.itemCount);
        ItemStack result = RESULTS.get(def);
        if (result != null) {
            output = result.copy();
            NBTTagCompound carried = left.getTagCompound();
            if (carried != null) { output.setTagCompound(carried.copy()); }
        }
        Map<Enchantment, Integer> held = EnchantmentHelper.getEnchantments(output);
        boolean rises = false;
        for (Map.Entry<Enchantment, Integer> entry : ENCHANTMENTS.get(def).entrySet()) {
            Integer already = held.get(entry.getKey());
            if (already != null && already >= entry.getValue()) { continue; }
            held.put(entry.getKey(), entry.getValue());
            rises = true;
        }
        if (!rises && result == null) { return ItemStack.EMPTY; }
        if (rises) { EnchantmentHelper.setEnchantments(held, output); }
        return output;
    }

    @SubscribeEvent public static void onAnvil(AnvilUpdateEvent event) {
        if (DEFS.isEmpty() || event.getRight().isEmpty()) { return; }
        AnvilDef def = holding(event.getLeft());
        if (def == null || !pairs(def, event.getLeft(), event.getRight())) { return; }
        ItemStack output = worked(def, event.getLeft());
        if (output.isEmpty()) {
            if (!ENCHANTMENTS.get(def).isEmpty() || def.grants.isEmpty()) { return; }
            output = event.getLeft().copy();
            output.setCount(def.itemCount);
        }
        OFFERED.put(output, def);
        event.setOutput(output);
        event.setCost(def.levels);
        event.setMaterialCost(def.withCount);
    }

    @Nullable public static AnvilDef affordable(EntityLiving mob, int level) {
        ItemStack left = mob.getHeldItemMainhand();
        ItemStack right = mob.getHeldItemOffhand();
        if (DEFS.isEmpty() || left.isEmpty() || right.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            if (def.levels <= level && pairs(def, left, right) && !worked(def, left).isEmpty()) { return def; }
        }
        return null;
    }

    public static void gather(EntityLiving mob) {
        if (DEFS.isEmpty()) { return; }
        for (EntityItem dropped : mob.world.getEntitiesWithinAABB(EntityItem.class, mob.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D))) {
            if (dropped.isDead || dropped.cannotPickup()) { continue; }
            ItemStack stack = dropped.getItem();
            if (!paysFor(stack)) { continue; }
            ItemStack right = mob.getHeldItemOffhand();
            int room = right.isEmpty() ? stack.getMaxStackSize() : ItemHandlerHelper.canItemStacksStack(right, stack) ? right.getMaxStackSize() - right.getCount() : 0;
            int taken = Math.min(room, stack.getCount());
            if (taken <= 0) { continue; }
            mob.onItemPickup(dropped, taken);
            if (right.isEmpty()) { mob.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemHandlerHelper.copyStackWithSize(stack, taken)); }
            else { right.grow(taken); }
            stack.shrink(taken);
            if (stack.isEmpty()) { dropped.setDead(); }
            else { dropped.setItem(stack); }
        }
    }

    private static boolean paysFor(ItemStack stack) {
        for (AnvilDef def : DEFS) {
            ItemStack with = WITH.get(def);
            if (ContentStacks.matches(stack, with.getItem(), with.getMetadata())) { return true; }
        }
        return false;
    }

    public static void work(EntityLiving mob, AnvilDef def, BlockPos anvil) {
        ItemStack left = mob.getHeldItemMainhand();
        ItemStack right = mob.getHeldItemOffhand();
        IBlockState state = mob.world.getBlockState(anvil);
        if (state.getBlock() != Blocks.ANVIL || def.levels > ContentMobExperience.level(mob) || !pairs(def, left, right)) { return; }
        ItemStack output = worked(def, left);
        if (output.isEmpty()) { return; }
        ContentMobExperience.addLevels(mob, -def.levels);
        ItemStack kept = left.getCount() > def.itemCount ? ItemHandlerHelper.copyStackWithSize(left, left.getCount() - def.itemCount) : ItemStack.EMPTY;
        mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, output);
        if (!kept.isEmpty()) { mob.entityDropItem(kept, 0.5F); }
        right.shrink(def.withCount);
        if (right.isEmpty()) { mob.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY); }
        if (mob.getRNG().nextFloat() < BREAK_CHANCE) {
            int damage = state.getValue(BlockAnvil.DAMAGE) + 1;
            if (damage > 2) {
                mob.world.setBlockToAir(anvil);
                mob.world.playEvent(1029, anvil, 0);
            }
            else {
                mob.world.setBlockState(anvil, state.withProperty(BlockAnvil.DAMAGE, damage), 2);
                mob.world.playEvent(1030, anvil, 0);
            }
        }
        else { mob.world.playEvent(1030, anvil, 0); }
        ContentLog.LOGGER.info("{} worked {} at the anvil at {}, {}, {}: spent {} level(s) and {} {}, and holds {}", mob.getName(), def.registryName, anvil.getX(), anvil.getY(), anvil.getZ(), def.levels, def.withCount, def.with, output);
        if (!def.grants.isEmpty()) { ContentLog.LOGGER.debug("Anvil work {} also grants {}, which only a player can earn, so {} earns nothing more", def.registryName, def.grants, mob.getName()); }
    }

    @SubscribeEvent public static void onTaken(AnvilRepairEvent event) {
        AnvilDef def = OFFERED.get(event.getItemResult());
        if (def == null || def.grants.isEmpty() || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        grant((EntityPlayerMP) event.getEntityPlayer(), def);
    }

    public static ItemStack leftAfterWork(EntityPlayer player, ItemStack taken) {
        AnvilDef def = OFFERED.get(taken);
        if (def == null || !(player.openContainer instanceof ContainerRepair)) { return ItemStack.EMPTY; }
        ItemStack left = player.openContainer.getSlot(0).getStack();
        if (left.getCount() <= def.itemCount) { return ItemStack.EMPTY; }
        ItemStack kept = left.copy();
        kept.shrink(def.itemCount);
        return kept;
    }

    private static void grant(EntityPlayerMP player, AnvilDef def) {
        MinecraftServer server = player.getServer();
        if (server == null) { return; }
        Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(def.grants));
        if (advancement == null) {
            ContentLog.LOGGER.error("Anvil work {} grants the advancement {}, which no pack provides, so nothing is earned", def.registryName, def.grants);
            return;
        }
        PlayerAdvancements progress = player.getAdvancements();
        for (String criterion : advancement.getCriteria().keySet()) { progress.grantCriterion(advancement, criterion); }
    }

    @SubscribeEvent public static void onHeld(LivingEquipmentChangeEvent event) {
        if (DEFS.isEmpty() || event.getSlot() != EntityEquipmentSlot.MAINHAND || !(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        AnvilDef def = holding(event.getTo());
        if (def == null || !def.locks || def.grants.isEmpty() || Advancements.has((EntityPlayer) event.getEntityLiving(), def.grants)) { return; }
        tell((EntityPlayerMP) event.getEntityLiving(), def);
    }

    @SubscribeEvent public static void onAttack(AttackEntityEvent event) {
        if (locked(event.getEntityPlayer(), event.getEntityPlayer().getHeldItemMainhand())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUse(PlayerInteractEvent.RightClickItem event) {
        if (locked(event.getEntityPlayer(), event.getItemStack())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUseOn(PlayerInteractEvent.RightClickBlock event) {
        if (locked(event.getEntityPlayer(), event.getItemStack())) { event.setUseItem(Event.Result.DENY); }
    }

    @SubscribeEvent public static void onDig(PlayerInteractEvent.LeftClickBlock event) {
        if (locked(event.getEntityPlayer(), event.getItemStack())) { event.setCanceled(true); }
    }

    private static boolean locked(EntityPlayer player, ItemStack held) {
        if (DEFS.isEmpty()) { return false; }
        AnvilDef def = holding(held);
        if (def == null || !def.locks || def.grants.isEmpty() || Advancements.has(player, def.grants)) { return false; }
        if (player instanceof EntityPlayerMP) { tell((EntityPlayerMP) player, def); }
        return true;
    }

    private static void tell(EntityPlayerMP player, AnvilDef def) {
        long now = player.world.getTotalWorldTime();
        Long last = TOLD.get(player.getName());
        if (last != null && now - last < TOLD_EVERY) { return; }
        TOLD.put(player.getName(), now);
        Says.tell(player, "That waits on " + title(player, def.grants), TextFormatting.RED);
    }

    private static String title(EntityPlayerMP player, String name) {
        MinecraftServer server = player.getServer();
        Advancement advancement = server == null ? null : server.getAdvancementManager().getAdvancement(new ResourceLocation(name));
        return advancement == null || advancement.getDisplay() == null ? name : advancement.getDisplay().getTitle().getUnformattedText();
    }
}
