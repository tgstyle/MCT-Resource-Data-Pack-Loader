package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.HeartReward;
import mctmods.resourcedatapackloader.content.def.HeartsDef;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
    private static final ResourceLocation ID = ResourceDataPackLoader.id("hearts");
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
        if (def.colors != null && first("heartColors", key)) { colors = def.colors; }
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
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { sync(player); }
    }

    private static void sync(ServerPlayer player) {
        catchUp(player);
        apply(player);
        RDPLNetwork.sendHeartColors(player);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.getEntity() instanceof ServerPlayer player) { sync(player); } }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { MET.remove(event.getEntity().getUUID()); }

    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer next)) { return; }
        if (event.isWasDeath() && !keep) { forget(next); }
        apply(next);
        if (!event.isWasDeath()) { next.setHealth(Math.min(event.getOriginal().getHealth(), next.getMaxHealth())); }
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { if (!event.isEndConquered()) { event.getEntity().setHealth(event.getEntity().getMaxHealth()); } }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        ResourceLocation earned = event.getAdvancement().id();
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.ADVANCEMENT && earned.equals(reward.advancement)) { grant(player, reward); }
        }
    }

    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (REWARDS.isEmpty() || !(event.getEntity() instanceof ServerPlayer player) || event.getItem().getFoodProperties(player) == null) { return; }
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.FOODS && mark(player, reward, event.getItem())) { grant(player, reward); }
        }
    }

    public static void onTick(PlayerTickEvent.Post event) {
        if (!polled || !(event.getEntity() instanceof ServerPlayer player) || player.tickCount % POLL != 0) { return; }
        Set<String> met = MET.computeIfAbsent(player.getUUID(), id -> new HashSet<>());
        for (HeartReward reward : REWARDS) {
            if (reward.trigger != HeartReward.Trigger.CONDITION) { continue; }
            if (!reward.standing.earnedBy(player)) { met.remove(reward.key); }
            else if (met.add(reward.key)) { grant(player, reward); }
        }
    }

    static void onMeal(ServerPlayer player, int distinctBefore, int distinctAfter) {
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.DIET && distinctBefore < reward.diet && distinctAfter >= reward.diet) { grant(player, reward); }
        }
    }

    private static boolean mark(ServerPlayer player, HeartReward reward, ItemStack eaten) {
        CompoundTag section = PlayerPersisted.section(player, SECTION);
        Set<String> marked = marked(section, reward);
        boolean changed = false;
        for (int i = 0; i < reward.foods.size(); i++) {
            if (reward.matches.get(i).matches(eaten) && marked.add(reward.foods.get(i))) { changed = true; }
        }
        if (!changed) { return false; }
        ListTag written = new ListTag();
        for (String entry : marked) { written.add(StringTag.valueOf(entry)); }
        section.put(reward.eatenKey, written);
        return marked.size() >= reward.need;
    }

    private static Set<String> marked(CompoundTag section, HeartReward reward) {
        ListTag stored = section.getList(reward.eatenKey, Tag.TAG_STRING);
        Set<String> marked = new LinkedHashSet<>();
        for (int i = 0; i < stored.size(); i++) { marked.add(stored.getString(i)); }
        return marked;
    }

    private static void forget(Player player) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        for (String name : new ArrayList<>(section.getAllKeys())) {
            if (name.startsWith(HeartReward.EATEN)) { section.remove(name); }
            else { section.putInt(name, 0); }
        }
    }

    private static void catchUp(ServerPlayer player) {
        Set<String> met = MET.computeIfAbsent(player.getUUID(), id -> new HashSet<>());
        for (HeartReward reward : REWARDS) {
            CompoundTag section = PlayerPersisted.read(player, SECTION);
            boolean absent = !section.contains(reward.key);
            boolean reached;
            switch (reward.trigger) {
                case ADVANCEMENT -> reached = reward.advancement != null && Advancements.has(player, reward.advancement.toString());
                case DIET -> reached = ContentDiet.distinct(player) >= reward.diet;
                case FOODS -> reached = marked(section, reward).size() >= reward.need;
                default -> {
                    reached = reward.standing.earnedBy(player);
                    if (reached) { met.add(reward.key); }
                    else { met.remove(reward.key); }
                }
            }
            if (absent && reached) { award(player, reward); }
        }
    }

    private static boolean award(ServerPlayer player, HeartReward reward) {
        CompoundTag section = PlayerPersisted.section(player, SECTION);
        int held = section.getInt(reward.key);
        if (held >= reward.limit) { return false; }
        section.putInt(reward.key, held + 1);
        return true;
    }

    private static void grant(ServerPlayer player, HeartReward reward) {
        if (!award(player, reward)) { return; }
        apply(player);
        if (!reward.message.isEmpty()) { player.displayClientMessage(Component.literal(Lang.tr(player, reward.message)), !CHAT.equals(style)); }
        SoundEvent heard = sound.isEmpty() ? null : ContentSounds.find(sound);
        if (heard != null) { player.level().playSound(null, player.getX(), player.getY(), player.getZ(), heard, SoundSource.PLAYERS, 1.0F, 1.0F); }
        if (!particle.isEmpty() && Registered.find(BuiltInRegistries.PARTICLE_TYPE, ContentParser.location(particle)) instanceof SimpleParticleType shown) {
            ((ServerLevel) player.level()).sendParticles(shown, player.getX(), player.getY() + 1.0D, player.getZ(), 8, 0.4D, 0.5D, 0.4D, 0.0D);
        }
    }

    private static double extra(Player player) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        double hearts = start;
        for (HeartReward reward : REWARDS) { hearts += section.getInt(reward.key) * (double) reward.hearts; }
        if (cap != null) { hearts = Math.min(cap, hearts); }
        return 2.0D * (hearts - HeartsDef.VANILLA);
    }

    private static void apply(Player player) {
        AttributeInstance max = player.getAttribute(Attributes.MAX_HEALTH);
        if (max == null) { return; }
        double amount = extra(player);
        AttributeModifier held = max.getModifier(ID);
        if (held == null ? amount == 0.0D : held.amount() == amount) { return; }
        float oldMax = player.getMaxHealth();
        if (held != null) { max.removeModifier(ID); }
        if (amount != 0.0D) { max.addPermanentModifier(new AttributeModifier(ID, amount, AttributeModifier.Operation.ADD_VALUE)); }
        float newMax = player.getMaxHealth();
        float health = player.getHealth();
        if (newMax > oldMax) { player.setHealth(health * newMax / oldMax); }
        else if (health > newMax) { player.setHealth(newMax); }
    }
}
