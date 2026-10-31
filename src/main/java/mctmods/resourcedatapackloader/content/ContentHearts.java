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
import mctmods.resourcedatapackloader.util.VanillaClients;

import com.google.gson.JsonObject;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentHearts {
    public static final int[] DEFAULT_COLORS = {0xE8C33A, 0x3AA8E8, 0x5FD35F, 0xB05FE8};
    private static final String SECTION = "rdplHearts";
    private static final Identifier ID = ResourceDataPackLoader.id("hearts");
    private static final int POLL = 100;
    private static final String CHAT = "chat";
    private static final List<HeartReward> REWARDS = new ArrayList<>();
    private static final Map<String, Identifier> OWNERS = new LinkedHashMap<>();
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

    private static void read(Identifier key, JsonObject json) {
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

    private static boolean first(String name, Identifier file) {
        Identifier had = OWNERS.putIfAbsent(name, file);
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
        apply(player, REWARDS.stream().noneMatch(r -> r.whileMet));
        RDPLNetwork.sendHeartColors(player);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.getEntity() instanceof ServerPlayer player) { sync(player); } }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { MET.remove(event.getEntity().getUUID()); }

    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer next)) { return; }
        if (event.isWasDeath()) {
            if (!keep) { forget(next); }
            clearLosses(next);
        }
        apply(next, true);
        if (!event.isWasDeath()) { next.setHealth(Math.min(event.getOriginal().getHealth(), next.getMaxHealth())); }
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { if (!event.isEndConquered()) { event.getEntity().setHealth(event.getEntity().getMaxHealth()); } }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        Identifier earned = event.getAdvancement().id();
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.ADVANCEMENT && Objects.equals(earned, reward.advancement)) { grant(player, reward); }
        }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.DEATH && reward.diedTo(player, event.getSource())) { grant(player, reward); }
        }
    }

    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (REWARDS.isEmpty() || !(event.getEntity() instanceof ServerPlayer player) || !event.getItem().has(DataComponents.FOOD)) { return; }
        for (HeartReward reward : REWARDS) {
            if (reward.trigger == HeartReward.Trigger.FOODS && mark(player, reward, event.getItem())) {
                grant(player, reward);
                if (reward.limit > 1) { PlayerPersisted.section(player, SECTION).remove(reward.eatenKey); }
            }
        }
    }

    public static void onTick(PlayerTickEvent.Post event) {
        if (!polled || !(event.getEntity() instanceof ServerPlayer player) || player.tickCount % POLL != 0) { return; }
        Set<String> met = MET.computeIfAbsent(player.getUUID(), _ -> new HashSet<>());
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
        ListTag stored = section.getListOrEmpty(reward.eatenKey);
        Set<String> marked = new LinkedHashSet<>();
        for (int i = 0; i < stored.size(); i++) { marked.add(stored.getStringOr(i, "")); }
        return marked;
    }

    private static void forget(Player player) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        for (String name : new ArrayList<>(section.keySet())) {
            if (name.startsWith(HeartReward.LOST) || name.startsWith(HeartReward.EATEN + HeartReward.LOST)) { continue; }
            if (name.startsWith(HeartReward.EATEN)) { section.remove(name); }
            else { section.putInt(name, 0); }
        }
    }

    private static void clearLosses(Player player) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        for (HeartReward reward : REWARDS) {
            if (reward.clearFoodsOnDeath) { section.remove(reward.eatenKey); }
            if (!reward.clearOnDeath) { continue; }
            if (section.contains(reward.key)) { section.putInt(reward.key, 0); }
            section.remove(reward.eatenKey);
        }
    }

    private static void catchUp(ServerPlayer player) {
        Set<String> met = MET.computeIfAbsent(player.getUUID(), _ -> new HashSet<>());
        for (HeartReward reward : REWARDS) {
            CompoundTag section = PlayerPersisted.read(player, SECTION);
            boolean absent = !section.contains(reward.key);
            boolean reached = switch (reward.trigger) {
                case ADVANCEMENT -> reward.advancement != null && Advancements.has(player, reward.advancement.toString());
                case DIET -> ContentDiet.distinct(player) >= reward.diet;
                case FOODS -> marked(section, reward).size() >= reward.need;
                case CONDITION -> reward.met(player);
                case DEATH -> false;
            };
            if (reward.trigger == HeartReward.Trigger.CONDITION) {
                if (reached) { met.add(reward.key); }
                else { met.remove(reward.key); }
            }
            if (absent && reached && !reward.invert && !reward.whileMet) { award(player, reward); }
        }
    }

    private static boolean award(ServerPlayer player, HeartReward reward) {
        CompoundTag section = PlayerPersisted.section(player, SECTION);
        int held = section.getIntOr(reward.key, 0);
        if (held >= reward.limit) { return false; }
        if (reward.loss && !debt && hearts(player) <= floor) {
            section.putInt(reward.key, held);
            return false;
        }
        section.putInt(reward.key, held + 1);
        return true;
    }

    private static boolean fatal(ServerPlayer player, HeartReward reward) {
        return kills && reward.loss && !reward.whileMet && reward.trigger != HeartReward.Trigger.DEATH
            && PlayerPersisted.read(player, SECTION).getIntOr(reward.key, 0) < reward.limit
            && hearts(player) - reward.hearts < floor;
    }

    private static void grant(ServerPlayer player, HeartReward reward) {
        boolean fatal = fatal(player, reward);
        if (!reward.whileMet && !award(player, reward)) {
            if (fatal) { player.kill(player.level()); }
            return;
        }
        apply(player, true);
        if (!reward.message.isEmpty()) {
            Component text = Component.literal(Lang.tr(player, reward.message));
            if (CHAT.equals(style)) { player.sendSystemMessage(text); }
            else { player.sendOverlayMessage(text); }
        }
        String heardName = reward.loss ? lossSound : sound;
        String shownName = reward.loss ? lossParticle : particle;
        SoundEvent heard = heardName.isEmpty() ? null : ContentSounds.find(heardName);
        if (heard != null) { player.level().playSound(null, player.getX(), player.getY(), player.getZ(), heard, SoundSource.PLAYERS, 1.0F, 1.0F); }
        if (!shownName.isEmpty() && Registered.find(BuiltInRegistries.PARTICLE_TYPE, Identifier.tryParse(shownName)) instanceof SimpleParticleType shown && player.level() instanceof ServerLevel level) {
            level.sendParticles(shown, player.getX(), player.getY() + 1.0D, player.getZ(), 8, 0.4D, 0.5D, 0.4D, 0.0D);
        }
        if (fatal) { player.kill(player.level()); }
    }

    private static double hearts(Player player) {
        CompoundTag section = PlayerPersisted.read(player, SECTION);
        Set<String> met = MET.get(player.getUUID());
        double earned = start;
        double lost = 0.0D;
        for (HeartReward reward : REWARDS) {
            if (!reward.loss) { earned += section.getIntOr(reward.key, 0) * (double) reward.hearts; }
            else if (reward.whileMet) { lost += met != null && met.contains(reward.key) ? reward.hearts : 0.0D; }
            else { lost += section.getIntOr(reward.key, 0) * (double) reward.hearts; }
        }
        if (cap != null) { earned = Math.min(cap, earned); }
        return Math.clamp(floor, earned - lost, earned);
    }

    private static double extra(Player player) { return 2.0D * (hearts(player) - HeartsDef.VANILLA); }

    private static void apply(Player player, boolean lift) {
        AttributeInstance max = player.getAttribute(Attributes.MAX_HEALTH);
        if (max == null) { return; }
        double amount = extra(player);
        AttributeModifier held = max.getModifier(ID);
        if (held == null ? amount == 0.0D : held.amount() == amount) { return; }
        float oldMax = player.getMaxHealth();
        if (held != null) { max.removeModifier(ID); }
        if (amount != 0.0D) { max.addOrReplacePermanentModifier(new AttributeModifier(ID, amount, AttributeModifier.Operation.ADD_VALUE)); }
        float newMax = player.getMaxHealth();
        float health = player.getHealth();
        if (lift && newMax > oldMax || scale && newMax < oldMax) { player.setHealth(health * newMax / oldMax); }
        else if (health > newMax) { player.setHealth(newMax); }
    }
}
