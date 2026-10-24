package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.HeartReward;
import mctmods.resourcedatapackloader.content.def.HeartsDef;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.VanillaClients;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentHearts {
    public static final int[] DEFAULT_COLORS = {0xE8C33A, 0x3AA8E8, 0x5FD35F, 0xB05FE8};
    private static final String SECTION = "rdplHearts";
    private static final String NAME = "RDPL hearts";
    private static final UUID ID = UUID.nameUUIDFromBytes("rdpl:hearts".getBytes(StandardCharsets.UTF_8));
    private static final int POLL = 100;
    private static final String CHAT = "chat";
    private static final List<HeartReward> REWARDS = new ArrayList<>();
    private static final Map<String, ResourceLocation> OWNERS = new LinkedHashMap<>();
    private static final Map<UUID, Set<String>> MET = new HashMap<>();
    private static float start = HeartsDef.VANILLA;
    @Nullable private static Float cap;
    private static boolean keep = true;
    private static String style = "bar";
    private static String sound = "";
    private static String particle = "";
    private static int[] colors = DEFAULT_COLORS;
    private static boolean polled;

    private ContentHearts() {}

    public static void load() {
        REWARDS.clear();
        OWNERS.clear();
        MET.clear();
        start = HeartsDef.VANILLA;
        cap = null;
        keep = true;
        style = "bar";
        sound = "";
        particle = "";
        colors = DEFAULT_COLORS;
        Json.eachObject(PackManager.HEARTS, "hearts file", ContentHearts::read);
        if (cap != null && cap < start) {
            ContentLog.LOGGER.error("Hearts cap {} is below startHearts {}, so there is no cap", cap, start);
            cap = null;
        }
        polled = false;
        for (HeartReward reward : REWARDS) { polled |= reward.trigger == HeartReward.Trigger.CONDITION; }
        if (!REWARDS.isEmpty() || start != HeartsDef.VANILLA) { Summary.info("hearts", "Loaded " + REWARDS.size() + " hearts reward(s), starting at " + start + " heart(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        HeartsDef def = HeartsDef.parse(key, json);
        if (def.startHearts != null && first("startHearts", key)) { start = def.startHearts; }
        if (def.cap != null && first("cap", key)) { cap = def.cap; }
        if (def.keepOnDeath != null && first("keepOnDeath", key)) { keep = def.keepOnDeath; }
        if (def.style != null && first("style", key)) { style = def.style; }
        if (def.sound != null && first("sound", key)) { sound = def.sound; }
        if (def.particle != null && first("particle", key)) { particle = def.particle; }
        if (def.colors != null && first("heartColors", key)) {
            colors = def.colors;
            VanillaClients.clientDrawn("Hearts file " + key, "heartColors");
        }
        for (HeartReward reward : def.rewards) {
            if (ContentRegistry.available(reward.requires, key)) { REWARDS.add(reward); }
        }
    }

    private static boolean first(String name, ResourceLocation file) {
        ResourceLocation had = OWNERS.putIfAbsent(name, file);
        if (had == null) { return true; }
        ContentLog.LOGGER.error("Hearts file {} gives {}, but {} already did, so the first one is kept", file, name, had);
        return false;
    }

    public static int[] colors() { return colors.clone(); }

    public static void refresh(MinecraftServer server) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { sync(player); }
    }

    private static void sync(EntityPlayerMP player) {
        catchUp(player);
        apply(player);
        RDPLNetwork.sendHeartColors(player);
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.player instanceof EntityPlayerMP) { sync((EntityPlayerMP) event.player); } }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { MET.remove(event.player.getUniqueID()); }

    @SubscribeEvent public static void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer next = event.getEntityPlayer();
        if (!(next instanceof EntityPlayerMP)) { return; }
        if (event.isWasDeath() && !keep) { forget(next); }
        apply(next);
        if (!event.isWasDeath()) { next.setHealth(Math.min(event.getOriginal().getHealth(), next.getMaxHealth())); }
    }

    @SubscribeEvent public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { if (!event.isEndConquered()) { event.player.setHealth(event.player.getMaxHealth()); } }

    @SubscribeEvent public static void onAdvancement(AdvancementEvent event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        ResourceLocation earned = event.getAdvancement().getId();
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.ADVANCEMENT && earned.equals(reward.advancement)) { grant((EntityPlayerMP) event.getEntityPlayer(), reward); }
        }
    }

    @SubscribeEvent public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (REWARDS.isEmpty() || !(event.getEntityLiving() instanceof EntityPlayerMP) || !(event.getItem().getItem() instanceof ItemFood)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.FOODS && mark(player, reward, event.getItem())) { grant(player, reward); }
        }
    }

    @SubscribeEvent public static void onTick(TickEvent.PlayerTickEvent event) {
        if (!polled || event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP) || event.player.ticksExisted % POLL != 0) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Set<String> met = MET.computeIfAbsent(player.getUniqueID(), id -> new HashSet<>());
        for (HeartReward reward : REWARDS) {
            if (reward.trigger != HeartReward.Trigger.CONDITION) { continue; }
            if (!reward.standing.earnedBy(player)) { met.remove(reward.key); }
            else if (met.add(reward.key)) { grant(player, reward); }
        }
    }

    static void onMeal(EntityPlayerMP player, int distinctBefore, int distinctAfter) {
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.DIET && distinctBefore < reward.diet && distinctAfter >= reward.diet) { grant(player, reward); }
        }
    }

    private static boolean mark(EntityPlayerMP player, HeartReward reward, ItemStack eaten) {
        NBTTagCompound section = PlayerPersisted.section(player, SECTION);
        Set<String> marked = marked(section, reward);
        boolean changed = false;
        for (int i = 0; i < reward.foods.size(); i++) {
            if (reward.matches.get(i).matches(eaten) && marked.add(reward.foods.get(i))) { changed = true; }
        }
        if (!changed) { return false; }
        NBTTagList written = new NBTTagList();
        for (String entry : marked) { written.appendTag(new NBTTagString(entry)); }
        section.setTag(reward.eatenKey, written);
        return marked.size() >= reward.need;
    }

    private static Set<String> marked(NBTTagCompound section, HeartReward reward) {
        NBTTagList stored = section.getTagList(reward.eatenKey, Constants.NBT.TAG_STRING);
        Set<String> marked = new LinkedHashSet<>();
        for (int i = 0; i < stored.tagCount(); i++) { marked.add(stored.getStringTagAt(i)); }
        return marked;
    }

    private static void forget(EntityPlayer player) {
        NBTTagCompound section = PlayerPersisted.read(player, SECTION);
        for (String name : new ArrayList<>(section.getKeySet())) {
            if (name.startsWith(HeartReward.EATEN)) { section.removeTag(name); }
            else { section.setInteger(name, 0); }
        }
    }

    private static void catchUp(EntityPlayerMP player) {
        Set<String> met = MET.computeIfAbsent(player.getUniqueID(), id -> new HashSet<>());
        for (HeartReward reward : REWARDS) {
            NBTTagCompound section = PlayerPersisted.read(player, SECTION);
            boolean absent = !section.hasKey(reward.key);
            boolean reached;
            switch (reward.trigger) {
                case ADVANCEMENT: reached = done(player, reward.advancement); break;
                case DIET: reached = ContentDiet.distinct(player) >= reward.diet; break;
                case FOODS: reached = marked(section, reward).size() >= reward.need; break;
                default:
                    reached = reward.standing.earnedBy(player);
                    if (reached) { met.add(reward.key); }
                    else { met.remove(reward.key); }
                    break;
            }
            if (absent && reached) { award(player, reward); }
        }
    }

    private static boolean done(EntityPlayerMP player, @Nullable ResourceLocation id) {
        MinecraftServer server = player.getServer();
        Advancement advancement = server == null || id == null ? null : server.getAdvancementManager().getAdvancement(id);
        return advancement != null && player.getAdvancements().getProgress(advancement).isDone();
    }

    private static boolean award(EntityPlayerMP player, HeartReward reward) {
        NBTTagCompound section = PlayerPersisted.section(player, SECTION);
        int held = section.getInteger(reward.key);
        if (held >= reward.limit) { return false; }
        section.setInteger(reward.key, held + 1);
        return true;
    }

    private static void grant(EntityPlayerMP player, HeartReward reward) {
        if (!award(player, reward)) { return; }
        apply(player);
        if (!reward.message.isEmpty()) { player.sendStatusMessage(new TextComponentString(Lang.tr(player, reward.message)), !CHAT.equals(style)); }
        SoundEvent heard = sound.isEmpty() ? null : Registries.find(ForgeRegistries.SOUND_EVENTS, new ResourceLocation(sound));
        if (heard != null) { player.world.playSound(null, player.posX, player.posY, player.posZ, heard, SoundCategory.PLAYERS, 1.0F, 1.0F); }
        EnumParticleTypes shown = particle.isEmpty() ? null : EnumParticleTypes.getByName(particle);
        if (shown != null) { ((WorldServer) player.world).spawnParticle(shown, player.posX, player.posY + 1.0D, player.posZ, 8, 0.4D, 0.5D, 0.4D, 0.0D); }
    }

    private static double extra(EntityPlayer player) {
        NBTTagCompound section = PlayerPersisted.read(player, SECTION);
        double hearts = start;
        for (HeartReward reward : REWARDS) { hearts += section.getInteger(reward.key) * (double) reward.hearts; }
        if (cap != null) { hearts = Math.min(cap, hearts); }
        return 2.0D * (hearts - HeartsDef.VANILLA);
    }

    private static void apply(EntityPlayer player) {
        IAttributeInstance max = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        double amount = extra(player);
        AttributeModifier held = max.getModifier(ID);
        if (held == null ? amount == 0.0D : held.getAmount() == amount) { return; }
        float oldMax = player.getMaxHealth();
        if (held != null) { max.removeModifier(ID); }
        if (amount != 0.0D) { max.applyModifier(new AttributeModifier(ID, NAME, amount, 0)); }
        float newMax = player.getMaxHealth();
        float health = player.getHealth();
        if (newMax > oldMax) { player.setHealth(health * newMax / oldMax); }
        else if (health > newMax) { player.setHealth(newMax); }
    }
}
