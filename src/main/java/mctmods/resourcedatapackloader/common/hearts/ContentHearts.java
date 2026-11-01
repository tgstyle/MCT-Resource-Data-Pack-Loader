package mctmods.resourcedatapackloader.common.hearts;

import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.HeartReward;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.HeartsDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import com.google.gson.JsonObject;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
    private static float floor = 0.5F;
    private static boolean keep = true;
    private static boolean debt;
    private static boolean kills;
    private static boolean scale;
    private static String style = "bar";
    private static String sound = "";
    private static String particle = "";
    private static String lossSound = "";
    private static String lossParticle = "";
    private static int[] colors = DEFAULT_COLORS;
    private static boolean polled;

    private ContentHearts() {}

    public static void load() {
        REWARDS.clear();
        OWNERS.clear();
        MET.clear();
        start = HeartsDef.VANILLA;
        cap = null;
        floor = 0.5F;
        keep = true;
        debt = false;
        kills = false;
        scale = false;
        style = "bar";
        sound = "";
        particle = "";
        lossSound = "";
        lossParticle = "";
        colors = DEFAULT_COLORS;
        Json.eachObject(PackManager.HEARTS, "hearts file", ContentHearts::read);
        if (cap != null && cap < start) {
            ContentLog.LOGGER.error("Hearts cap {} is below startHearts {}, so there is no cap", cap, start);
            cap = null;
        }
        if (cap != null && floor > cap) {
            ContentLog.LOGGER.error("Hearts floor {} is above cap {}, so the floor is 0.5", floor, cap);
            floor = 0.5F;
        }
        polled = false;
        int losses = 0;
        for (HeartReward reward : REWARDS) {
            polled |= reward.trigger == HeartReward.Trigger.CONDITION;
            if (reward.loss) { losses++; }
        }
        if (!REWARDS.isEmpty() || start != HeartsDef.VANILLA) { Summary.info("hearts", "Loaded " + (REWARDS.size() - losses) + " hearts reward(s), starting at " + start + " heart(s)" + (losses > 0 ? ", with " + losses + " way(s) to lose them" : "")); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        HeartsDef def = HeartsDef.parse(key, json);
        if (def.startHearts != null && first("startHearts", key)) { start = def.startHearts; }
        if (def.cap != null && first("cap", key)) { cap = def.cap; }
        if (def.floor != null && first("floor", key)) { floor = def.floor; }
        if (def.keepOnDeath != null && first("keepOnDeath", key)) { keep = def.keepOnDeath; }
        if (def.floorDebt != null && first("floorDebt", key)) { debt = def.floorDebt; }
        if (def.floorKills != null && first("floorKills", key)) { kills = def.floorKills; }
        if (def.lossHealth != null && first("lossHealth", key)) { scale = "scale".equals(def.lossHealth); }
        if (def.style != null && first("style", key)) { style = def.style; }
        if (def.sound != null && first("sound", key)) { sound = def.sound; }
        if (def.particle != null && first("particle", key)) { particle = def.particle; }
        if (def.lossSound != null && first("lossSound", key)) { lossSound = def.lossSound; }
        if (def.lossParticle != null && first("lossParticle", key)) { lossParticle = def.lossParticle; }
        if (def.colors != null && first("heartColors", key)) {
            colors = def.colors;
            VanillaClients.clientDrawn("Hearts file " + key, "heartColors");
        }
        for (HeartReward reward : def.rewards) {
            if (ContentRegistry.available(reward.requires, key)) { REWARDS.add(reward); }
        }
        for (HeartReward reward : def.losses) {
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
        apply(player, REWARDS.stream().noneMatch(r -> r.whileMet));
        RDPLNetwork.sendHeartColors(player);
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.player instanceof EntityPlayerMP) { sync((EntityPlayerMP) event.player); } }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { MET.remove(event.player.getUniqueID()); }

    @SubscribeEvent public static void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer next = event.getEntityPlayer();
        if (!(next instanceof EntityPlayerMP)) { return; }
        if (event.isWasDeath()) {
            if (!keep) { forget(next); }
            clearLosses(next);
        }
        apply(next, true);
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

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onDeath(LivingDeathEvent event) {
        if (event.isCanceled() || !(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.DEATH && reward.diedTo(player, event.getSource())) { grant(player, reward); }
        }
    }

    @SubscribeEvent public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (REWARDS.isEmpty() || !(event.getEntityLiving() instanceof EntityPlayerMP) || !(event.getItem().getItem() instanceof ItemFood)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.FOODS && mark(player, reward, event.getItem())) {
                grant(player, reward);
                if (reward.limit > 1) { PlayerPersisted.section(player, SECTION).removeTag(reward.eatenKey); }
            }
        }
    }

    @SubscribeEvent public static void onTick(TickEvent.PlayerTickEvent event) {
        if (!polled || event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP) || event.player.ticksExisted % POLL != 0) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Set<String> met = MET.computeIfAbsent(player.getUniqueID(), id -> new HashSet<>());
        for (HeartReward reward : REWARDS) {
            if (reward.trigger != HeartReward.Trigger.CONDITION) { continue; }
            if (reward.invert && !reward.whileMet && reward.standing.unreadable(player)) {
                met.add(reward.key);
                continue;
            }
            if (!reward.met(player)) {
                if (met.remove(reward.key) && reward.whileMet) { apply(player, false); }
            }
            else if (!met.contains(reward.key)) {
                double before = hearts(player);
                met.add(reward.key);
                if (!reward.whileMet || hearts(player) != before) { grant(player, reward); }
            }
        }
    }

    public static void onMeal(EntityPlayerMP player, int distinctBefore, int distinctAfter) {
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
            if (name.startsWith(HeartReward.LOST) || name.startsWith(HeartReward.EATEN + HeartReward.LOST)) { continue; }
            if (name.startsWith(HeartReward.EATEN)) { section.removeTag(name); }
            else { section.setInteger(name, 0); }
        }
    }

    private static void clearLosses(EntityPlayer player) {
        NBTTagCompound section = PlayerPersisted.read(player, SECTION);
        for (HeartReward reward : REWARDS) {
            if (reward.clearFoodsOnDeath) { section.removeTag(reward.eatenKey); }
            if (!reward.clearOnDeath) { continue; }
            if (section.hasKey(reward.key)) { section.setInteger(reward.key, 0); }
            section.removeTag(reward.eatenKey);
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
                case DEATH: reached = false; break;
                default:
                    reached = reward.met(player);
                    if (reached) { met.add(reward.key); }
                    else { met.remove(reward.key); }
                    break;
            }
            if (absent && reached && !reward.invert && !reward.whileMet) { award(player, reward); }
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
        if (reward.loss && !debt && hearts(player) <= floor) {
            section.setInteger(reward.key, held);
            return false;
        }
        section.setInteger(reward.key, held + 1);
        return true;
    }

    private static boolean fatal(EntityPlayerMP player, HeartReward reward) {
        return kills && reward.loss && !reward.whileMet && reward.trigger != HeartReward.Trigger.DEATH
            && PlayerPersisted.read(player, SECTION).getInteger(reward.key) < reward.limit
            && hearts(player) - reward.hearts < floor;
    }

    private static void grant(EntityPlayerMP player, HeartReward reward) {
        boolean fatal = fatal(player, reward);
        if (!reward.whileMet && !award(player, reward)) {
            if (fatal) { player.onKillCommand(); }
            return;
        }
        apply(player, true);
        if (!reward.message.isEmpty()) { player.sendStatusMessage(new TextComponentString(Lang.tr(player, reward.message)), !CHAT.equals(style)); }
        String heardName = reward.loss ? lossSound : sound;
        String shownName = reward.loss ? lossParticle : particle;
        SoundEvent heard = heardName.isEmpty() ? null : Registries.find(ForgeRegistries.SOUND_EVENTS, new ResourceLocation(heardName));
        if (heard != null) { player.world.playSound(null, player.posX, player.posY, player.posZ, heard, SoundCategory.PLAYERS, 1.0F, 1.0F); }
        EnumParticleTypes shown = shownName.isEmpty() ? null : EnumParticleTypes.getByName(shownName);
        if (shown != null) { ((WorldServer) player.world).spawnParticle(shown, player.posX, player.posY + 1.0D, player.posZ, 8, 0.4D, 0.5D, 0.4D, 0.0D); }
        if (fatal) { player.onKillCommand(); }
    }

    private static double hearts(EntityPlayer player) {
        NBTTagCompound section = PlayerPersisted.read(player, SECTION);
        Set<String> met = MET.get(player.getUniqueID());
        double earned = start;
        double lost = 0.0D;
        for (HeartReward reward : REWARDS) {
            if (!reward.loss) { earned += section.getInteger(reward.key) * (double) reward.hearts; }
            else if (reward.whileMet) { lost += met != null && met.contains(reward.key) ? reward.hearts : 0.0D; }
            else { lost += section.getInteger(reward.key) * (double) reward.hearts; }
        }
        if (cap != null) { earned = Math.min(cap, earned); }
        return Math.max(Math.min(floor, earned), earned - lost);
    }

    private static double extra(EntityPlayer player) { return 2.0D * (hearts(player) - HeartsDef.VANILLA); }

    private static void apply(EntityPlayer player, boolean lift) {
        IAttributeInstance max = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        double amount = extra(player);
        AttributeModifier held = max.getModifier(ID);
        if (held == null ? amount == 0.0D : held.getAmount() == amount) { return; }
        float oldMax = player.getMaxHealth();
        if (held != null) { max.removeModifier(ID); }
        if (amount != 0.0D) { max.applyModifier(new AttributeModifier(ID, NAME, amount, 0)); }
        float newMax = player.getMaxHealth();
        float health = player.getHealth();
        if (lift && newMax > oldMax || scale && newMax < oldMax) { player.setHealth(health * newMax / oldMax); }
        else if (health > newMax) { player.setHealth(newMax); }
    }
}
